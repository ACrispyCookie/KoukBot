package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.LanguageManager;
import dev.acrispycookie.music.MusicManager;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlayCommand extends BotCommand {

    public PlayCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().setEphemeral(true).queue();
        String search = e.getOption("search").getAsString();

        MusicManager musicManager = bot.getMusicManager();
        LanguageManager languageManager = bot.getLanguageManager();

        if (m.getVoiceState() == null || (!musicManager.isPlaying() && !m.getVoiceState().inAudioChannel())) {
            e.getHook().sendMessageEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("commands.failed.title.play.not-in-channel"),
                    languageManager.get("commands.failed.description.play.not-in-channel"),
                    bot.getErrorColor()).build()).queue();
            return;
        }

        if (m.getVoiceState().inAudioChannel() && musicManager.isPlaying() && (musicManager.getActiveChannel() != e.getChannel().asTextChannel()
                || musicManager.getVoiceChannel() != m.getVoiceState().getChannel())) {
            e.getHook().sendMessageEmbeds(new EmbedMessage(bot, m.getUser(),
                    languageManager.get("commands.failed.title.play.not-in-your-channel"),
                    languageManager.get("commands.failed.description.play.not-in-your-channel"),
                    bot.getErrorColor()).build()).queue();
            return;
        } else if (!musicManager.isPlaying()) {
            musicManager.initialize(e.getChannel().asTextChannel(), m.getVoiceState().getChannel().asVoiceChannel());
        }

        musicManager.add(e.getHook(), m.getUser(), search);
    }
}
