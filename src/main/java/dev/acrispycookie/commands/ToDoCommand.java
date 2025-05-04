package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.todo.ToDo;
import dev.acrispycookie.todo.ToDoChannel;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collection;

public class ToDoCommand extends BotCommand {

    public ToDoCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        String task = e.getOption("task").getAsString();
        if (ToDoChannel.isToDoChannel(e.getChannel().getIdLong())) {
            e.deferReply().queue();
            ToDo toDo = new ToDo(bot, task, m.getUser().getIdLong(), e.getChannel().getIdLong());
            toDo.send(e, true);
            ToDoChannel.getByChannelId(e.getChannel().getIdLong()).addToDo(toDo);
        }
        else if (ToDoChannel.getChannelByUser(m.getUser().getIdLong()) != null) {
            e.deferReply().setEphemeral(true).queue();
            ToDo toDo = new ToDo(bot, task, m.getUser().getIdLong(), ToDoChannel.getChannelByUser(m.getUser().getIdLong()).getChannelId());
            toDo.send(e, false);
            ToDoChannel.getChannelByUser(m.getUser().getIdLong()).addToDo(toDo);
            e.getHook().sendMessageEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("commands.success.title.todo"),
                    bot.getLanguageManager().get("commands.success.description.todo"),
                    bot.getBotColor()).build()).setEphemeral(true).queue();
        }
        else {
            e.deferReply().setEphemeral(true).queue();
            Collection<Permission> allowed = new ArrayList<>();
            allowed.add(Permission.MESSAGE_SEND);
            allowed.add(Permission.VIEW_CHANNEL);
            Collection<Permission> denied = new ArrayList<>();
            denied.add(Permission.MESSAGE_SEND);
            denied.add(Permission.VIEW_CHANNEL);
            bot.getGuild().createTextChannel(
                            bot.getDiscordUser(m.getIdLong()).getName())
                    .setParent(bot.getGuild().getCategoryById(803289238498050058L))
                    .addMemberPermissionOverride(m.getIdLong(), allowed, null)
                    .addPermissionOverride(bot.getGuild().getPublicRole(), null, denied).
                    queue((q) -> {
                        ToDoChannel channel = new ToDoChannel(bot, m.getIdLong(), q.getIdLong());
                        ToDo toDo = new ToDo(bot, task, m.getUser().getIdLong(), q.getIdLong());
                        toDo.send(e, false);
                        channel.addToDo(toDo);
                        e.getHook().sendMessageEmbeds(new EmbedMessage(bot, m.getUser(),
                                bot.getLanguageManager().get("commands.success.title.todo"),
                                bot.getLanguageManager().get("commands.success.description.todo"),
                                bot.getBotColor()).build()).setEphemeral(true).queue();
                    });
        }
    }
}
