package dev.acrispycookie.managers.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;

public class MusicEntry {

    String query;
    User user;
    AudioTrack track;
    boolean voiceLine;

    public MusicEntry(String query, User user, boolean voiceLine){
        this.query = query;
        this.user = user;
        this.voiceLine = voiceLine;
    }

    public String resolveQuery(){
        if(voiceLine){
            return query;
        }
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!query.isEmpty() && query.matches(pattern)) {
            return query;
        }
        else{
            return "ytsearch:" + query;
        }
    }

    public User getUser() {
        return user;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public void setTrack(AudioTrack track){
        this.track = track;
    }

    public boolean isVoiceLine() {
        return voiceLine;
    }
}
