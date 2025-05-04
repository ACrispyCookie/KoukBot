package dev.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.ConfigurationFile;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;
import java.util.Comparator;

public class LeaderboardManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.LEADERBOARD;
    private final ArrayList<Long> leaderboard = new ArrayList<>();

    public LeaderboardManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        bot.getUserDataManager().checkData();
        setup();
        for (VoiceChannel v : bot.getGuild().getVoiceChannels()) {
            for (Member m : v.getMembers()) {
                LevelUser.getByDiscordId(m.getIdLong()).joinChannel();
            }
        }
    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {

    }

    public void setup() {
        ArrayList<LevelUser> loaded = LevelUser.getLoadedUsers();
        for (String key : bot.getUserDataManager().getUserData().keySet()) {
            LevelUser user = LevelUser.getByDiscordId(Long.parseLong(key));
            leaderboard.add(user.getDiscordUser().getIdLong());
        }
        leaderboard.sort(Comparator.comparingInt(o -> -LevelUser.getByDiscordId(o).getTotalXp()));
        for (String key : bot.getUserDataManager().getUserData().keySet()) {
            LevelUser user = LevelUser.getByDiscordId(Long.parseLong(key));
            if (!loaded.contains(user)) {
                LevelUser.unload(user.getDiscordUser().getIdLong());
            }
        }
        file.setElement("board", jsonArray());
    }

    public void xpChanged(LevelUser currentUser, boolean added) {
        int currentPlace = getPlace(currentUser.getDiscordUser().getIdLong());
        if (added) {
            if (currentPlace > 0) {
                moveUp(currentUser, currentPlace);
            }
        } else {
            if (currentPlace < leaderboard.size() - 1) {
                moveDown(currentUser, currentPlace);
            }
        }
    }

    private void moveUp(LevelUser currentUser, int currentPlace) {
        if (currentPlace > 0) {
            boolean isLoaded = LevelUser.isLoaded(leaderboard.get(currentPlace - 1));
            LevelUser user = LevelUser.getByDiscordId(leaderboard.get(currentPlace - 1));
            if (currentUser.getTotalXp() > user.getTotalXp()) {
                moveUp(currentUser, currentPlace - 1);
            } else {
                leaderboard.remove(currentUser.getDiscordUser().getIdLong());
                leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
                file.setElement("board", jsonArray());
            }
            if (!isLoaded) LevelUser.unload(user.getDiscordUser().getIdLong());
        } else {
            leaderboard.remove(currentUser.getDiscordUser().getIdLong());
            leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
            file.setElement("board", jsonArray());
        }
    }

    private void moveDown(LevelUser currentUser, int currentPlace) {
        if (currentPlace < leaderboard.size() - 1) {
            boolean isLoaded = LevelUser.isLoaded(leaderboard.get(currentPlace + 1));
            LevelUser user = LevelUser.getByDiscordId(leaderboard.get(currentPlace - 1));
            if (currentUser.getTotalXp() < user.getTotalXp()) {
                moveDown(currentUser, currentPlace + 1);
            } else {
                leaderboard.remove(currentUser.getDiscordUser().getIdLong());
                leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
                file.setElement("board", jsonArray());
            }
            if (!isLoaded) LevelUser.unload(user.getDiscordUser().getIdLong());
        } else {
            leaderboard.remove(currentUser.getDiscordUser().getIdLong());
            leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
            file.setElement("board", jsonArray());
        }
    }

    private JsonArray jsonArray() {
        JsonArray array = new JsonArray();
        for (long l : leaderboard) {
            array.add(new JsonPrimitive(l));
        }
        return array;
    }

    public long getUser(int place) {
        return leaderboard.get(place);
    }

    public int getPlace(long discordId) {
        for (int i = 0; i < leaderboard.size(); i++) {
            if (discordId == leaderboard.get(i)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Long> getLeaderboard() {
        return leaderboard;
    }
}
