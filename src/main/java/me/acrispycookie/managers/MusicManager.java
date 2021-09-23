package me.acrispycookie.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.acrispycookie.Main;
import me.acrispycookie.managers.music.AudioPlayerSendHandler;
import me.acrispycookie.managers.music.MusicEntry;
import me.acrispycookie.managers.music.TrackScheduler;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class MusicManager {

    Main main;
    ArrayList<File> voiceLines = new ArrayList<>();
    AudioPlayerManager playerManager;
    boolean isPlaying = false;
    boolean isConnected = false;
    boolean reachedEnd = false;
    VoiceChannel channel;
    TextChannel t;
    Message nowPlaying;
    AudioPlayer player;
    TrackScheduler scheduler;

    public MusicManager(Main main, ArrayList<File> voiceLines){
        this.main = main;
        this.voiceLines.addAll(voiceLines);
    }

    public void add(String query, User user, VoiceChannel channel, TextChannel t){
        if(!isConnected){
            this.t = t;
            this.channel = channel;
            connect();
            isPlaying = true;
        }
        File line = getRandomLine();
        if(line != null){
            MusicEntry voiceLine = new MusicEntry(getRandomLine().getAbsolutePath(), null, true);
            playerManager.loadItem(voiceLine.resolveQuery(), new ResultHandler(user, t, voiceLine));
        }
        MusicEntry entry = new MusicEntry(query, user, false);
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
            reachedEnd = true;
            if(nowPlaying != null){
                nowPlaying.delete().queue((m) -> {}, (f) -> {});
            }
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
                if(reachedEnd){
                    reachedEnd = false;
                    scheduler.restart();
                }
                else{
                    sendOrChange(new EmbedMessage(user,
                            Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + getCurrentSong().getTrack().getInfo().title + "](" + getCurrentSong().getTrack().getInfo().uri + ")", getCurrentSong().getUser().getAsMention()),
                            Main.getInstance().getBotColor()).build());
                }
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
            player.setPaused(true);
            playerManager.shutdown();
            scheduler.clear();
            disconnect();
            if(nowPlaying != null){
                nowPlaying.delete().queue((m) -> {}, (f) -> {});
            }

            isPlaying = false;
            isConnected = false;
            reachedEnd = false;

            playerManager = null;
            channel = null;
            t = null;
            nowPlaying = null;
            player = null;
            scheduler = null;
        }
    }

    private void disconnect(){
        main.getGuild().getAudioManager().closeAudioConnection();
    }

    private void connect(){
        isConnected = true;
        AudioManager manager = main.getGuild().getAudioManager();
        manager.openAudioConnection(channel);
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
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

    public boolean hasReachedEnd(){
        return reachedEnd;
    }

    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }

    public MusicEntry getCurrentSong(){
        return scheduler.getTracks().get(scheduler.getIndex());
    }

    public File getRandomLine(){
        if(voiceLines.size() > 0){
            return voiceLines.get(new Random().nextInt(voiceLines.size()));
        }
        return null;
    }

    public void sendOrChange(MessageEmbed message){
        if(nowPlaying != null){
            TextChannel channel = nowPlaying.getTextChannel();
            nowPlaying.delete().queue((m) -> {}, (f) -> {});
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
            if(!entry.isVoiceLine()){
                t.sendMessageEmbeds(new EmbedMessage(requested,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.queued"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.queued", "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")", requested.getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
            }
            scheduler.queue(entry);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
            entry.setTrack(audioTrack);
            if(!entry.isVoiceLine()){
                t.sendMessageEmbeds(new EmbedMessage(requested,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.queued"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.queued", "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")", requested.getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
            }
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
