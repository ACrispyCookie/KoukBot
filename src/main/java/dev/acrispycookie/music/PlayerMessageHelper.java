package dev.acrispycookie.music;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.LanguageManager;
import dev.acrispycookie.utility.EmbedMessage;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerMessageHelper {

    private static KoukBot bot;
    private final MusicManager musicManager;
    private final TrackScheduler scheduler;
    private Message statusMessage = null;

    public PlayerMessageHelper(KoukBot bot, MusicManager manager, TrackScheduler scheduler) {
        PlayerMessageHelper.bot = bot;
        this.musicManager = manager;
        this.scheduler = scheduler;
    }

    public void sendQueuedMessage(InteractionHook hook, User user, Track track) {
        Messages.QUEUED.send(hook, user, getTrackInfo(track), user.getAsMention());
    }

    public void deleteStatusMessage() {
        if (statusMessage != null) {
            statusMessage.delete().complete();
            statusMessage = null;
        }
    }

    public void updateStatusMessage(User user) {
        MessageEmbed embed;
        if (musicManager.isPlaying()) {
            embed = Messages.NOW_PLAYING.get(user, getTrackInfo(scheduler.getCurrent().getTrack()), scheduler.getCurrent().getUser().getAsMention());
        } else {
            embed = Messages.PAUSE.get(user, scheduler.getCurrent().getUser().getAsMention());
        }

        String currentQueueTitle = bot.getLanguageManager().get("commands.success.title.play.current-queue");
        String description = embed.getDescription() +
                "\n\n\n" +
                "**" + currentQueueTitle + "**\n" +
                createQueueString(scheduler, true);

        EmbedBuilder finalEmbed = new EmbedBuilder();
        finalEmbed.copyFrom(embed);
        finalEmbed.setDescription(description);
        finalEmbed.setImage(scheduler.getCurrent().getTrack().getInfo().getArtworkUrl());

        if (statusMessage == null) {
            MessageCreateData message = new MessageCreateBuilder()
                    .addEmbeds(finalEmbed.build())
                    .setComponents(getActionRows())
                    .build();
            musicManager.getActiveChannel().sendMessage(message).queue((m) -> statusMessage = m);
        } else {
            MessageEditData message = new MessageEditBuilder()
                    .setEmbeds(finalEmbed.build())
                    .setComponents(getActionRows())
                    .build();
            statusMessage.editMessage(message).queue();
        }
    }

    public long getStatusMessageId() {
        return statusMessage == null ? -1 : statusMessage.getIdLong();
    }

    private String createQueueString(TrackScheduler scheduler, boolean message) {
        StringBuilder queue = new StringBuilder();
        List<MusicEntry> tracks = scheduler.isShuffle() ? scheduler.getShuffled() : scheduler.getTracks();

        for (int i = 0; i < tracks.size(); i++) {
            MusicEntry entry = tracks.get(i);
            queue.append(i + 1).append(". ");
            if (message) {
                boolean isCurrent = entry == scheduler.getCurrent();
                if (isCurrent)
                    queue.append("**");
                queue.append(getTrackInfo(entry.getTrack()));
                queue.append(" [").append(entry.getUser().getAsMention()).append("]");
                if (isCurrent)
                    queue.append("**");
            } else {
                queue.append(entry.getTrack().getInfo().getTitle());
            }
            queue.append("\n");
        }

        return queue.toString();
    }

    private String getTrackInfo(Track track) {
        return "[" + track.getInfo().getTitle() + "](" + track.getInfo().getUri() + ")";
    }

    private List<ActionRow> getActionRows() {
        return List.of(
                ActionRow.of(getSongSelectMenu()),
                ActionRow.of(getPlayingNowButtons()),
                ActionRow.of(getSettingsButton())
        );
    }

    private Collection<ItemComponent> getPlayingNowButtons() {
        Button playButton;
        if (!musicManager.isPlaying())
            playButton = Button.primary("play", "▶️");
        else
            playButton = Button.primary("pause", "⏸️");
        Button stopButton = Button.danger("stop", "⏹️");
        Button previousButton = Button.primary("previous", "⏮️");
        Button nextButton = Button.primary("next", "⏭️");
        Button addSongButton = Button.primary("addSong", "➕");
        return List.of(playButton, stopButton, previousButton, nextButton, addSongButton);
    }

    private Collection<ItemComponent> getSettingsButton() {
        Button shuffleButton = scheduler.isShuffle() ? Button.success("shuffle", "🔀") : Button.primary("shuffle", "🔀");
        Button repeatButton;
        switch (scheduler.getRepeatMode()) {
            case REPEAT -> repeatButton = Button.success("repeat", "🔁");
            case REPEAT_ONE -> repeatButton = Button.success("repeat", "🔂");
            default -> repeatButton = Button.primary("repeat", "🔁");
        }
        return List.of(shuffleButton, repeatButton);
    }

    private StringSelectMenu getSongSelectMenu() {
        List<SelectOption> options = Arrays.stream(createQueueString(scheduler, false)
                .split("\n"))
                .map((s) -> SelectOption.of(s, s))
                .collect(Collectors.toList());
        return StringSelectMenu.create("songSelect").setPlaceholder("Select a song...").setMaxValues(1).addOptions(options).build();
    }

    public enum Messages {
        NOT_PLAYING("commands.failed.title.play.not-playing", "commands.failed.description.play.not-playing", true),
        ALREADY_PLAYING("commands.failed.title.play.already-playing", "commands.failed.description.play.already-playing", true),
        ERROR_PLAYING("commands.failed.title.play.error-playing", "commands.failed.description.play.error-playing", true),
        NO_MATCHES("commands.failed.title.play.no-matches", "commands.failed.description.play.no-matches", true),
        NOW_PLAYING("commands.success.title.play.now-playing", "commands.success.description.play.now-playing", false),
        QUEUED("commands.success.title.play.queued", "commands.success.description.play.queued", false),
        PAUSE("commands.success.title.pause", "commands.success.description.pause", false),
        STOP("commands.success.title.stop", "commands.success.description.stop", false),
        SONG_NOT_FOUND("commands.failed.title.play.song-not-found", "commands.failed.description.play.song-not-found", true),
        TOP_OF_QUEUE("commands.failed.title.previous.top-of-queue", "commands.failed.description.previous.top-of-queue", true),
        END_OF_QUEUE("commands.failed.title.next.end-of-queue", "commands.failed.description.next.end-of-queue", true);

        private final String title;
        private final String description;
        private final boolean error;

        Messages(String title, String description, boolean error) {
            this.title = title;
            this.description = description;
            this.error = error;
        }

        public MessageEmbed get(User user, String... replace) {
            LanguageManager manager = bot.getLanguageManager();
            return new EmbedMessage(bot, user,
                    manager.get(title),
                    manager.get(description, replace),
                    error ? bot.getErrorColor() : bot.getBotColor()).build();
        }

        public void send(InteractionHook hook, User user, String... replace) {
            hook.sendMessageEmbeds(get(user, replace)).setEphemeral(true).queue();
        }
    }
}
