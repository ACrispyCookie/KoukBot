package dev.acrispycookie.music;

import dev.acrispycookie.Console;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.FeatureManager;
import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.client.player.*;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class MusicManager extends FeatureManager {

    private LavalinkClient client;
    private TrackScheduler scheduler;
    private PlayerMessageHelper msgHelper;

    private TextChannel activeChannel;
    private VoiceChannel voiceChannel;
    private boolean isConnected = false;
    private boolean isPlaying = false;

    public MusicManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        long startTime = System.currentTimeMillis();
        this.scheduler = new TrackScheduler();
        this.msgHelper = new PlayerMessageHelper(bot, this, scheduler);
        bot.getBuilder().addEventListeners(new MusicPlayerListener(bot));

        Console.println("Starting lavalink client...");
        String token = bot.getConfigManager().get("bot.botToken");
        String uri = bot.getConfigManager().get("lavalink-client.uri");
        String password = bot.getConfigManager().get("lavalink-client.password");
        client = new LavalinkClient(
                Helpers.getUserIdFromToken(token)
        );
        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        LavalinkNode node = client.addNode(new NodeOptions.Builder().setName("KoukBot")
                .setServerUri(URI.create(uri))
                .setPassword(password)
                .setRegionFilter(RegionGroup.EUROPE)
                .setHttpTimeout(5000L)
                .build()
        );
        node.on(TrackEndEvent.class).subscribe((event) -> {
            if (event.getEndReason() == Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason.FINISHED)
                onTrackEnd();
        });
        Console.println("Lavalink client has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");

        bot.getBuilder().setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client));
    }

    @Override
    public void unloadInternal() {
        stop();
    }

    @Override
    public void reloadInternal() {

    }

    public void initialize(TextChannel activeChannel, VoiceChannel voiceChannel) {
        if (!isConnected) {
            this.activeChannel = activeChannel;
            this.voiceChannel = voiceChannel;
            connect();
        }
    }

    public void add(InteractionHook hook, User user, String query) {
        String identifier = query;
        try {
            new URL(identifier);
        } catch (MalformedURLException e) {
            identifier = "ytsearch:" + query;
        }

        client.getOrCreateLink(bot.getGuild().getIdLong())
                .loadItem(identifier)
                .subscribe(new ResultHandler(hook, user));
    }

    public void select(InteractionHook hook, User user, int selected) {
        boolean set = scheduler.trySet(selected);
        if (!set) {
            PlayerMessageHelper.Messages.SONG_NOT_FOUND.send(hook, user);
            return;
        }

        isPlaying = true;
        msgHelper.updateStatusMessage(user);
        updatePlayback(true);
    }

    public void prev(InteractionHook hook, User user) {
        if (scheduler.getTracks().isEmpty()) {
            PlayerMessageHelper.Messages.NOT_PLAYING.send(hook, user);
            return;
        }

        scheduler.previous();
        msgHelper.updateStatusMessage(user);
        updatePlayback(true);
    }

    public void next(InteractionHook hook, User user) {
        if (scheduler.getTracks().isEmpty()) {
            PlayerMessageHelper.Messages.NOT_PLAYING.send(hook, user);
            return;
        }

        scheduler.next(true);
        msgHelper.updateStatusMessage(user);
        updatePlayback(true);
    }

    public void pause(InteractionHook hook, User user) {
        if (!isPlaying) {
            PlayerMessageHelper.Messages.NOT_PLAYING.send(hook, user);
            return;
        }

        isPlaying = false;
        msgHelper.updateStatusMessage(user);
        updatePlayback(false);
    }

    public void resume(InteractionHook hook, User user) {
        if (isPlaying) {
            PlayerMessageHelper.Messages.ALREADY_PLAYING.send(hook, user);
            return;
        }

        if (scheduler.getTracks().isEmpty()) {
            PlayerMessageHelper.Messages.NOT_PLAYING.send(hook, user);
            return;
        }

        isPlaying = true;
        msgHelper.updateStatusMessage(user);
        updatePlayback(false);
    }

    public void stop(InteractionHook hook, User user) {
        if (!isConnected) {
            PlayerMessageHelper.Messages.NOT_PLAYING.send(hook, user);
            return;
        }

        PlayerMessageHelper.Messages.STOP.send(hook, user);

        disconnect();
        msgHelper.deleteStatusMessage();
        scheduler.clear();
        isPlaying = false;
    }

    public void stop() {
        if (!isConnected) {
            return;
        }

        disconnect();
        msgHelper.deleteStatusMessage();
        scheduler.clear();
        isPlaying = false;
    }

    public void onTrackEnd() {
        isPlaying = scheduler.next(false);
        msgHelper.updateStatusMessage(bot.getBot().getSelfUser());
        updatePlayback(true);
    }

    public void toggleShuffle(User user) {
        scheduler.setShuffle(!scheduler.isShuffle());
        msgHelper.updateStatusMessage(user);
    }

    public void toggleRepeatMode(User user) {
        switch (scheduler.getRepeatMode()) {
            case REPEAT -> scheduler.setRepeatMode(TrackScheduler.RepeatMode.REPEAT_ONE);
            case REPEAT_ONE -> scheduler.setRepeatMode(TrackScheduler.RepeatMode.NONE);
            case NONE -> scheduler.setRepeatMode(TrackScheduler.RepeatMode.REPEAT);
        }
        msgHelper.updateStatusMessage(user);
    }

    public TextChannel getActiveChannel() {
        return activeChannel;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getStatusMessageId() {
        return msgHelper.getStatusMessageId();
    }

    private void connect() {
        isConnected = true;
        bot.getBot().getDirectAudioController().connect(voiceChannel);
    }

    private void disconnect() {
        isConnected = false;
        bot.getBot().getDirectAudioController().disconnect(voiceChannel.getGuild());
    }

    private void updatePlayback(boolean updateTrack) {
        PlayerUpdateBuilder playerUpdate = client.getOrCreateLink(bot.getGuild().getIdLong()).createOrUpdatePlayer();
        if (updateTrack)
            playerUpdate.setTrack(scheduler.getCurrent().getTrack()).setVolume(35);

        playerUpdate.setPaused(!isPlaying);
        playerUpdate.subscribe();
    }

    class ResultHandler extends AbstractAudioLoadResultHandler {

        private final InteractionHook hook;
        private final User requested;

        public ResultHandler(InteractionHook hook, User user) {
            this.hook = hook;
            this.requested = user;
        }

        @Override
        public void ontrackLoaded(@NotNull TrackLoaded trackLoaded) {
            addNewEntry(trackLoaded.getTrack());
        }

        @Override
        public void onPlaylistLoaded(@NotNull PlaylistLoaded playlistLoaded) {
            addNewEntries(playlistLoaded.getTracks());
        }

        @Override
        public void onSearchResultLoaded(@NotNull SearchResult searchResult) {
            addNewEntry(searchResult.getTracks().get(0));
        }

        @Override
        public void noMatches() {
            PlayerMessageHelper.Messages.NO_MATCHES.send(hook, requested);
        }

        @Override
        public void loadFailed(@NotNull LoadFailed loadFailed) {
            PlayerMessageHelper.Messages.ERROR_PLAYING.send(hook, requested);
        }

        private void addNewEntries(Collection<Track> tracks) {
            int schedulerSize = scheduler.getTracks().size();
            tracks.stream()
                    .map(t -> new MusicEntry(requested, t))
                    .collect(Collectors.toCollection(ArrayList::new))
                    .forEach(scheduler::queue);
            msgHelper.sendQueuedMessage(hook, requested, tracks.iterator().next());

            if (schedulerSize == 0) {
                isPlaying = true;
                updatePlayback(true);
            }
            msgHelper.updateStatusMessage(requested);
        }

        private void addNewEntry(Track audioTrack) {
            MusicEntry entry = new MusicEntry(requested, audioTrack);
            scheduler.queue(entry);
            msgHelper.sendQueuedMessage(hook, requested, audioTrack);

            if (scheduler.getTracks().size() == 1) {
                isPlaying = true;
                updatePlayback(true);
            }
            msgHelper.updateStatusMessage(requested);
        }
    }
}
