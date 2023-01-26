package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.replyEmbeds(new EmbedMessage(m.getUser(),
                Main.getInstance().getLanguageManager().get("commands.success.title.stop"),
                Main.getInstance().getLanguageManager().get("commands.success.description.stop"),
                Main.getInstance().getBotColor()).build()).queue();
        Main.getInstance().getMusicManager().stop();
    }
}
