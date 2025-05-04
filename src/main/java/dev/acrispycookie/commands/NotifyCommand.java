package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.TimeUnit;

public class NotifyCommand extends BotCommand{

    public NotifyCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        Member toNotify = e.getOption("user").getAsMember();
        if (m.getVoiceState().getChannel() != null) {
            if (toNotify.getVoiceState().getChannel() != null) {
                if (m.getVoiceState().getChannel().equals(toNotify.getVoiceState().getChannel())) {
                    if (toNotify.getVoiceState().isDeafened()) {
                        notify(toNotify);
                        e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                                bot.getLanguageManager().get("commands.success.title.notify"),
                                bot.getLanguageManager().get("commands.success.description.notify"),
                                bot.getBotColor()).build()).queue();
                    }
                    else {
                        e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                                bot.getLanguageManager().get("commands.failed.title.notify.not-deafened"),
                                bot.getLanguageManager().get("commands.failed.description.notify.not-deafened"),
                                bot.getErrorColor()).build()).queue();
                    }
                }
                else {
                    e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.failed.title.notify.not-in-your-channel"),
                            bot.getLanguageManager().get("commands.failed.description.notify.not-in-your-channel"),
                            bot.getErrorColor()).build()).queue();
                }
            }
            else {
                e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                        bot.getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                        bot.getLanguageManager().get("commands.failed.description.notify.not-in-channel"),
                        bot.getErrorColor()).build()).queue();
            }
        }
        else {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                    bot.getLanguageManager().get("commands.failed.description.notify.user-not-in-channel"),
                    bot.getErrorColor()).build()).queue();
        }
    }

    private void notify(Member m) {
        if (m.getVoiceState() != null) {
            VoiceChannel channel = m.getVoiceState().getChannel().asVoiceChannel();
            VoiceChannel random = null;
            for (VoiceChannel channel1 : bot.getGuild().getVoiceChannels()) {
                if (!channel.equals(channel1)) {
                    random = channel1;
                }
            }
            if (random != null) {
                bot.getGuild().moveVoiceMember(m, random).queue();
                bot.getGuild().moveVoiceMember(m, channel).queueAfter(2, TimeUnit.SECONDS);
            }
        }
    }
}
