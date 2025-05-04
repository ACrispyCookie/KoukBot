package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.LanguageManager;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageCommand extends BotCommand {

    public MessageCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        LanguageManager languageManager = bot.getLanguageManager();
        String message = e.getOption("message").getAsString();
        OptionMapping channel = e.getOption("channel");
        String channelId = channel != null ? channel.getAsMentionable().getId() : null;

        if (!Perm.hasPermission(bot, m, Perm.MESSAGE)) {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("no-perm.title"),
                    languageManager.get("no-perm.description", Perm.MESSAGE.name()),
                    bot.getErrorColor()).build()).queue();
            return;
        }

        if (channelId == null) {
            e.reply(message).queue();
            return;
        }


        TextChannel textChannel = m.getGuild().getTextChannelById(channel.getAsMentionable().getId());
        if (m.getGuild().getTextChannelById(channel.getAsMentionable().getId()) != null) {
            textChannel.sendMessage(message).queue();
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("commands.success.title.message"),
                    languageManager.get("commands.success.description.message", Perm.MESSAGE.name()),
                    bot.getBotColor()).build()).setEphemeral(true).queue();
        } else {
            e.reply(message).queue();
        }
    }
}
