package dev.acrispycookie.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class LeaderboardManager {

    JsonObject data;
    ArrayList<Long> leaderboard;

    public LeaderboardManager(JsonObject object){
        this.data = object;
        this.leaderboard = new ArrayList<>();
        Main.getInstance().getUserDataManager().checkData();
    }

    public void setup(){
        ArrayList<LevelUser> loaded = LevelUser.getLoadedUsers();
        for(String key : Main.getInstance().getUserDataManager().getUserData().keySet()){
            LevelUser user = LevelUser.getByDiscordId(Long.parseLong(key));
            leaderboard.add(user.getDiscordUser().getIdLong());
        }
        leaderboard.sort(Comparator.comparingInt(o -> -LevelUser.getByDiscordId(o).getTotalXp()));
        for(String key : Main.getInstance().getUserDataManager().getUserData().keySet()){
            LevelUser user = LevelUser.getByDiscordId(Long.parseLong(key));
            if(!loaded.contains(user)){
                LevelUser.unload(user.getDiscordUser().getIdLong());
            }
        }
        save();
    }

    public void xpChanged(LevelUser currentUser, boolean added) {
        int currentPlace = getPlace(currentUser.getDiscordUser().getIdLong());
        if(added) {
            if(currentPlace > 0) {
                moveUp(currentUser, currentPlace);
            }
        } else {
            if(currentPlace < leaderboard.size() - 1) {
                moveDown(currentUser, currentPlace);
            }
        }
    }

    private void moveUp(LevelUser currentUser, int currentPlace) {
        if(currentPlace > 0) {
            boolean isLoaded = LevelUser.isLoaded(leaderboard.get(currentPlace - 1));
            LevelUser user = LevelUser.getByDiscordId(leaderboard.get(currentPlace - 1));
            if(currentUser.getTotalXp() > user.getTotalXp()) {
                moveUp(currentUser, currentPlace - 1);
            } else {
                leaderboard.remove(currentUser.getDiscordUser().getIdLong());
                leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
                save();
            }
            if(!isLoaded) LevelUser.unload(user.getDiscordUser().getIdLong());
        } else {
            leaderboard.remove(currentUser.getDiscordUser().getIdLong());
            leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
            save();
        }
    }

    private void moveDown(LevelUser currentUser, int currentPlace) {
        if(currentPlace < leaderboard.size() - 1) {
            boolean isLoaded = LevelUser.isLoaded(leaderboard.get(currentPlace + 1));
            LevelUser user = LevelUser.getByDiscordId(leaderboard.get(currentPlace - 1));
            if(currentUser.getTotalXp() < user.getTotalXp()) {
                moveDown(currentUser, currentPlace + 1);
            } else {
                leaderboard.remove(currentUser.getDiscordUser().getIdLong());
                leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
                save();
            }
            if(!isLoaded) LevelUser.unload(user.getDiscordUser().getIdLong());
        } else {
            leaderboard.remove(currentUser.getDiscordUser().getIdLong());
            leaderboard.add(currentPlace, currentUser.getDiscordUser().getIdLong());
            save();
        }
    }

    public void save(){
        data.add("board", jsonArray());
        try {
            FileWriter file = new FileWriter("./data/leaderboard.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(data));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonArray jsonArray(){
        JsonArray array = new JsonArray();
        for(long l : leaderboard){
            array.add(new JsonPrimitive(l));
        }
        return array;
    }

    public long getUser(int place) {
        return leaderboard.get(place);
    }

    public int getPlace(long discordId) {
        for(int i = 0; i < leaderboard.size(); i++) {
            if(discordId == leaderboard.get(i)){
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Long> getLeaderboard(){
        return leaderboard;
    }
}
