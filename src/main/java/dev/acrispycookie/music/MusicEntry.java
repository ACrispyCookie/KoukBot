package dev.acrispycookie.music;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.User;

public class MusicEntry {

    private final User user;
    private final Track track;

    public MusicEntry(User user, Track track) {
        this.user = user;
        this.track = track;
    }

    public User getUser() {
        return user;
    }

    public Track getTrack() {
        return track;
    }
}
