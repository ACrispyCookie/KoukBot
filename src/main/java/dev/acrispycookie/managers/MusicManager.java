package dev.acrispycookie.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.acrispycookie.managers.music.AudioPlayerSendHandler;
import dev.acrispycookie.managers.music.MusicEntry;
import dev.acrispycookie.managers.music.TrackScheduler;
import dev.acrispycookie.Main;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

    public void add(String query, User user, VoiceChannel channel, SlashCommandInteractionEvent e){
        if(!isConnected){
            this.t = e.getChannel().asTextChannel();
            this.channel = channel;
            connect();
            isPlaying = true;
            File line = getRandomLine();
            if(line != null){
                MusicEntry voiceLine = new MusicEntry(getRandomLine().getAbsolutePath(), null, true);
                playerManager.loadItem(voiceLine.resolveQuery(), new ResultHandler(user, e, voiceLine));
            }
        }
        MusicEntry entry = new MusicEntry(query, user, false);
        playerManager.loadItem(entry.resolveQuery(), new ResultHandler(user, e, entry));
    }

    public void next(User user, SlashCommandInteractionEvent e){
        if(scheduler.getTracks().size() > 0){
            if(scheduler.getIndex() + 1 < scheduler.getTracks().size()){
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.next"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.next", "[" + getRelSong(1).getTrack().getInfo().title + "](" + getRelSong(1).getTrack().getInfo().uri + ")", getRelSong(1).getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
                scheduler.nextTrack();
            }
            else{
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.next.end-of-queue"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.next.end-of-queue"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    public void prev(User user, SlashCommandInteractionEvent e){
        if(scheduler.getTracks().size() > 0){
            if(scheduler.getIndex() > 0){
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.previous"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.previous", "[" +getRelSong(-1).getTrack().getInfo().title + "](" + getRelSong(-1).getTrack().getInfo().uri + ")", getRelSong(-1).getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
                scheduler.previousTrack();
            }
            else{
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.previous.top-of-queue"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.previous.top-of-queue"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    public void pause(User user, SlashCommandInteractionEvent e){
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
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.pause"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.pause", "[" + getRelSong(0).getTrack().getInfo().title + "](" + getRelSong(0).getTrack().getInfo().uri + ")", getRelSong(0).getUser().getAsMention()),
                        Main.getInstance().getBotColor()).build()).setEphemeral(true).queue();
                sendOrChange(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.success.title.pause"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.pause", user.getAsMention()),
                        Main.getInstance().getBotColor()).build());
                player.setPaused(true);
            }
            else{
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
    }

    public void resume(User user, SlashCommandInteractionEvent e){
        if(!isPlaying){
            if(scheduler.getTracks().size() > 0){
                isPlaying = true;
                if(reachedEnd){
                    reachedEnd = false;
                    scheduler.restart();
                }
                else{
                    e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                            Main.getInstance().getLanguageManager().get("commands.success.title.resume"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.resume", "[" + getRelSong(0).getTrack().getInfo().title + "](" + getRelSong(0).getTrack().getInfo().uri + ")", getRelSong(0).getUser().getAsMention()),
                            Main.getInstance().getBotColor()).build()).setEphemeral(true).queue();
                    sendOrChange(new EmbedMessage(user,
                            Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + getRelSong(0).getTrack().getInfo().title + "](" + getRelSong(0).getTrack().getInfo().uri + ")", getRelSong(0).getUser().getAsMention()),
                            Main.getInstance().getBotColor()).build());
                }
                player.setPaused(false);
            }
            else{
                e.getHook().sendMessageEmbeds(new EmbedMessage(user,
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-playing"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-playing"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            e.getHook().sendMessageEmbeds(new EmbedMessage(user,
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

    public MusicEntry getRelSong(int dIndex){
        return scheduler.getTracks().get(scheduler.getIndex() + dIndex);
    }

    public File getRandomLine(){
        if(voiceLines.size() > 0){
            return voiceLines.get(new Random().nextInt(voiceLines.size()));
        }
        return null;
    }

    public void sendOrChange(MessageEmbed message){
        if(nowPlaying != null){
            TextChannel channel = nowPlaying.getChannel().asTextChannel();
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
        SlashCommandInteractionEvent e;

        public ResultHandler(User user, SlashCommandInteractionEvent e, MusicEntry entry){
            this.requested = user;
            this.e = e;
            this.entry = entry;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            entry.setTrack(audioTrack);
            if(!entry.isVoiceLine()){
                e.getHook().sendMessageEmbeds(new EmbedMessage(requested,
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
                e.getHook().sendMessageEmbeds(new EmbedMessage(requested,
                        Main.getInstance().getLanguageManager().get("commands.success.title.play.queued"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.play.queued", "[" + audioTrack.getInfo().title + "](" + audioTrack.getInfo().uri + ")", requested.getAsMention()),
                        Main.getInstance().getBotColor()).build()).queue();
            }
            scheduler.queue(entry);
        }

        @Override
        public void noMatches() {
            e.getHook().sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.no-matches"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.no-matches"),
                    Main.getInstance().getBotColor()).build()).queue();
        }

        @Override
        public void loadFailed(FriendlyException exc) {
            e.getHook().sendMessageEmbeds(new EmbedMessage(requested,
                    Main.getInstance().getLanguageManager().get("commands.failed.title.play.error-playing"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.play.error-playing"),
                    Main.getInstance().getBotColor()).build()).queue();
        }
    }
}
