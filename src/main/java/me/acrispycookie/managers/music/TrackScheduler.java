package me.acrispycookie.managers.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;

import java.util.ArrayList;

public class TrackScheduler extends AudioEventAdapter {

    AudioPlayer player;
    ArrayList<MusicEntry> tracks = new ArrayList<>();
    int index = 0;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
    }

    public void queue(MusicEntry track){
        tracks.add(track);
        if(tracks.size() == 1){
            sendOrChange(tracks.get(index));
            player.startTrack(tracks.get(index).getTrack(), true);
        }
    }

    public void nextTrack(){
        if(index + 1 < tracks.size()){
            index++;
            player.stopTrack();
            player.startTrack(tracks.get(index).getTrack().makeClone(), true);
        }
        else {
            Main.getInstance().getMusicManager().pause(null, null);
            player.setPaused(true);
        }
    }

    public void previousTrack(){
        if(index > 0){
            index--;
            player.stopTrack();
            player.startTrack(tracks.get(index).getTrack().makeClone(), true);
        }
    }

    public void clear(){
        index = 0;
        tracks.clear();
        player.destroy();
    }

    public ArrayList<MusicEntry> getTracks() {
        return tracks;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    private void sendOrChange(MusicEntry entry){
        Main.getInstance().getMusicManager().sendOrChange(new EmbedMessage(entry.getUser(),
                Main.getInstance().getLanguageManager().get("commands.success.title.play.now-playing"),
                Main.getInstance().getLanguageManager().get("commands.success.description.play.now-playing", "[" + entry.getTrack().getInfo().title + "](" + entry.getTrack().getInfo().uri + ")", entry.getUser().getAsMention()),
                Main.getInstance().getBotColor()).build());
    }
}

