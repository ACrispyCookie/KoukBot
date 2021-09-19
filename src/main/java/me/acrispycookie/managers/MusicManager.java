package me.acrispycookie.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import javafx.scene.layout.Pane;
import me.acrispycookie.Main;
import me.acrispycookie.managers.music.AudioPlayerSendHandler;
import me.acrispycookie.managers.music.MusicEntry;
import me.acrispycookie.managers.music.TrackScheduler;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Timer;
import java.util.TimerTask;

public class MusicManager {

    Main main;
    VoiceChannel channel;
    boolean isPlaying = false;
    boolean isConnected = false;
    TextChannel t;
    Message nowPlaying;
    AudioPlayerManager playerManager;
    AudioPlayer player;
    TrackScheduler scheduler;

    public MusicManager(Main main){
        this.main = main;

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public void add(String query, User user, VoiceChannel channel, TextChannel t){
        if(!isConnected){
            this.t = t;
            connect(channel);
            isPlaying = true;
        }
        MusicEntry entry = new MusicEntry(query, user);
        playerManager.loadItem(entry.resolveQuery(), new ResultHandler(user, t, entry));
    }

    public void next(User user, TextChannel channel){
        if(scheduler.getTracks().size() > 0){
            if(scheduler.getIndex() + 1 < scheduler.getTracks().size()){
                scheduler.nextTrack();
                t.sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.next"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.next", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
                sendOrChange(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build());
            }
            else{
                channel.sendMessage(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.next.end-of-queue"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.next.end-of-queue"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            channel.sendMessage(new EmbedMessage(user,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    public void prev(User user, TextChannel channel){
        if(scheduler.getTracks().size() > 0){
            if(scheduler.getIndex() > 0){
                scheduler.previousTrack();
                t.sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.previous"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.previous", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
                sendOrChange(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build());
            }
            else{
                channel.sendMessage(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.previous.top-of-queue"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.previous.top-of-queue"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            channel.sendMessage(new EmbedMessage(user,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    public void pause(User user, TextChannel channel){
        if(user == null){
            isPlaying = false;
            nowPlaying.delete().queue();
            player.setPaused(true);
        }
        else{
            if(isPlaying){
                isPlaying = false;
                sendOrChange(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.pause"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.pause", user.getAsMention()),
                        Main.getInstance().getBotColor()).build());
                player.setPaused(true);
            }
            else{
                channel.sendMessage(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
    }

    public void resume(User user, TextChannel channel){
        if(!isPlaying){
            if(scheduler.getTracks().size() > 0){
                isPlaying = true;
                sendOrChange(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build());
                player.setPaused(false);
            }
            else{
                channel.sendMessage(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            channel.sendMessage(new EmbedMessage(user,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.already-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.already-playing"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    public void stop(){
        if(isConnected){
            pause(null, null);
            disconnect();
            scheduler.clear();
            nowPlaying = null;
            t = null;
        }
    }

    private void disconnect(){
        channel = null;
        isConnected = false;
        main.getGuild().getAudioManager().closeAudioConnection();
    }

    private void connect(VoiceChannel channel){
        isConnected = true;
        this.channel = channel;
        AudioManager manager = main.getGuild().getAudioManager();
        manager.openAudioConnection(channel);
        this.player = playerManager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        main.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public VoiceChannel getChannel() {
        return channel;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public MusicEntry getCurrentSong(){
        return scheduler.getTracks().get(scheduler.getIndex());
    }

    public Message getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(Message nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public void sendOrChange(MessageEmbed message){
        if(nowPlaying != null){
            TextChannel channel = nowPlaying.getTextChannel();
            nowPlaying.delete().queue();
            channel.sendMessageEmbeds(message).queue((q) -> {
                nowPlaying = q;
            });
        }
        else{
            t.sendMessageEmbeds(message).queue((q) -> {
                nowPlaying = q;
            });
        }
    }

    class ResultHandler implements AudioLoadResultHandler {

        MusicEntry entry;
        User requested;
        TextChannel t;

        public ResultHandler(User user, TextChannel t, MusicEntry e){
            this.requested = user;
            this.t = t;
            this.entry = e;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            entry.setTrack(audioTrack);
            t.sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.success.title.play.queued"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.play.queued", "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")", requested.getAsMention()),
                    Main.getInstance().getBotColor()).build()).queue();
            scheduler.queue(entry);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
            entry.setTrack(audioTrack);
            t.sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.success.title.play.queued"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.play.queued", "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")", requested.getAsMention()),
                    Main.getInstance().getBotColor()).build()).queue();
            scheduler.queue(entry);
        }

        @Override
        public void noMatches() {
            t.sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.no-matches"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.no-matches"),
                    Main.getInstance().getBotColor()).build()).queue();
        }

        @Override
        public void loadFailed(FriendlyException e) {
            t.sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.error-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.error-playing"),
                    Main.getInstance().getBotColor()).build()).queue();
        }
    }
}
