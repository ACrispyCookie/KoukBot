package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.managers.todo.ToDo;
import me.acrispycookie.managers.todo.ToDoChannel;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collection;

public class ToDoCommand extends BotCommand {

    public ToDoCommand(){
        super();
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        String task = e.getOption("task").getAsString();
        e.deferReply().queue();
        if(ToDoChannel.isToDoChannel(e.getChannel().getIdLong())) {
            ToDo toDo = new ToDo(task, m.getUser().getIdLong(), e.getChannel().getIdLong());
            toDo.send();
        }
        else if(ToDoChannel.getChannelByUser(m.getUser().getIdLong()) != null){
            ToDo toDo = new ToDo(task, m.getUser().getIdLong(), ToDoChannel.getChannelByUser(m.getUser().getIdLong()).getChannelId());
            toDo.send();
        }
        else{
            Collection<Permission> allowed = new ArrayList<>();
            allowed.add(Permission.MESSAGE_SEND);
            allowed.add(Permission.VIEW_CHANNEL);
            Collection<Permission> denied = new ArrayList<>();
            denied.add(Permission.MESSAGE_SEND);
            denied.add(Permission.VIEW_CHANNEL);
            Main.getInstance().getGuild().createTextChannel(
                            Main.getInstance().getDiscordUser(m.getIdLong()).getName())
                    .setParent(Main.getInstance().getGuild().getCategoryById(803289238498050058L))
                    .addMemberPermissionOverride(m.getIdLong(), allowed, null)
                    .addPermissionOverride(Main.getInstance().getGuild().getPublicRole(), null, denied).
                    queue((q) -> {
                        ToDoChannel channel = new ToDoChannel(m.getIdLong(), q.getIdLong());
                        ToDo toDo = new ToDo(task, m.getUser().getIdLong(), q.getIdLong());
                        toDo.send();
                        channel.addToDo(toDo);
                    });
        }
        e.getHook().sendMessageEmbeds(new EmbedMessage(m.getUser(),
                Main.getInstance().getLanguageManager().get("commands.success.title.todo"),
                Main.getInstance().getLanguageManager().get("commands.success.description.todo"),
                Main.getInstance().getBotColor()).build()).queue();
    }
}
