package dev.acrispycookie.commands;

import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;

public abstract class BotCommand extends ListenerAdapter {

    protected final KoukBot bot;

    public BotCommand(KoukBot bot) {
        this.bot = bot;
    }

    public abstract void execute(SlashCommandInteractionEvent e, String label, Member m);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        Member m = e.getMember();
        if (!m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())) {
            Command command = Command.getByAlias(e.getName());
            command.getBotCommand().execute(e, e.getName(), e.getMember());
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent e) {
        if (!Boolean.parseBoolean(bot.getConfigManager().get("settings.commandsRegistered"))) {
            ArrayList<CommandData> data = new ArrayList<>();
            for (Command command : Command.values()) {
                data.add(command.getData());
            }
            e.getJDA().updateCommands().addCommands(data).queue();
            bot.getConfigManager().set("settings.commandsRegistered", new JsonPrimitive(true));
        }
    }

}
