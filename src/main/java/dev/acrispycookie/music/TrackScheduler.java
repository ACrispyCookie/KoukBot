package dev.acrispycookie.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackScheduler  {

    List<MusicEntry> tracks = new ArrayList<>();
    List<MusicEntry> shuffled = new ArrayList<>();
    int current = 0;
    boolean shuffle = false;
    RepeatMode repeatMode = RepeatMode.NONE;

    public void queue(MusicEntry track) {
        tracks.add(track);
        shuffled.add(track);
    }

    public boolean trySet(int selected) {
        if (selected >= tracks.size())
            return false;

        current = selected;
        return true;
    }

    public void previous() {
        if (hasReachedTop()) {
            current = tracks.size() - 1;
            return;
        }

        current--;
    }

    public boolean next(boolean force) {
        if (repeatMode == RepeatMode.REPEAT_ONE && !force) {
            return true;
        } else if (hasReachedEnd()) {
            current = 0;
            return (force || repeatMode == RepeatMode.REPEAT);
        }

        current++;
        return true;
    }

    public void clear() {
        current = 0;
        tracks.clear();
    }

    public List<MusicEntry> getTracks() {
        return tracks;
    }

    public List<MusicEntry> getShuffled() {
        return shuffled;
    }

    public MusicEntry getCurrent() {
        return shuffle ? shuffled.get(current) : tracks.get(current);
    }

    private boolean hasReachedTop() {
        return current == 0;
    }

    private boolean hasReachedEnd() {
        return current == tracks.size() - 1;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;

        if (!shuffle) {
            for (int i = 0; i < tracks.size(); i++) {
                if (tracks.get(i) == shuffled.get(current)) {
                    current = i;
                    break;
                }
            }
            shuffled.clear();
            shuffled.addAll(tracks);
        } else {
            Collections.shuffle(shuffled);
            for (int i = 0; i < shuffled.size(); i++) {
                if (shuffled.get(i) == tracks.get(current)) {
                    current = i;
                    break;
                }
            }
        }
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public enum RepeatMode {
        REPEAT,
        REPEAT_ONE,
        NONE
    }
}

