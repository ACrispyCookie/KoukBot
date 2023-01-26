package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        EmbedMessage msg = new EmbedMessage(m.getUser(),
                Main.getInstance().getLanguageManager().get("commands.success.title.help"),
                Main.getInstance().getLanguageManager().get("commands.success.description.help"),
                Main.getInstance().getBotColor());
        e.replyEmbeds(msg.build()).queue();
    }
}
