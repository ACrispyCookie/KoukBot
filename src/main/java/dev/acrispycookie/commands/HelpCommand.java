package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.LanguageManager;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends BotCommand {

    public HelpCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        LanguageManager languageManager = bot.getLanguageManager();
        EmbedMessage msg = new EmbedMessage(bot, m.getUser(),
                languageManager.get("commands.success.title.help"),
                languageManager.get("commands.success.description.help"),
                bot.getBotColor());
        e.replyEmbeds(msg.build()).queue();
    }
}
