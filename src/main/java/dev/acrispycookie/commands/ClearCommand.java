package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.LanguageManager;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends BotCommand {

    public ClearCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        LanguageManager languageManager = bot.getLanguageManager();
        int count = e.getOption("count").getAsInt();

        if (Perm.hasPermission(bot, m, Perm.CLEAR)) {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("commands.success.title.clear"),
                    languageManager.get("commands.success.description.clear", String.valueOf(count), (count != 1 ? "s" : "")),
                    bot.getBotColor()).build()).setEphemeral(true).queue();

            e.getChannel().asTextChannel().getHistory().retrievePast(count).queue((l) -> {
                List<Message> newerMessages = new ArrayList<>();
                List<Message> olderMessages = new ArrayList<>();
                OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minusWeeks(2);
                for (Message message : l) {
                    if (message.getTimeCreated().isBefore(twoWeeksAgo))
                        olderMessages.add(message);
                    else
                        newerMessages.add(message);
                }

                if (!newerMessages.isEmpty())
                    e.getChannel().asTextChannel().deleteMessages(newerMessages).queue();
                for (Message message : olderMessages) {
                    message.delete().queue();
                }
            });
        } else {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("no-perm.title"),
                    languageManager.get("no-perm.description", Perm.CLEAR.name()),
                    bot.getErrorColor()).build()).queue();
        }
    }
}
