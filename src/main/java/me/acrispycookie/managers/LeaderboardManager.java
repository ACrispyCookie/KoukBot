package me.acrispycookie.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import me.acrispycookie.levelsystem.LevelUser;

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
        setup();
    }

    private void setup(){
        if(data.getAsJsonArray("board").size() == 0){
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
    }

    public void save(){
        data.add("board", jsonArray());
        try {
            FileWriter file = new FileWriter("./data/leaderboard.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(Main.getInstance().getUserDataManager().getUserData()));
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

    public ArrayList<Long> getLeaderboard(){
        return leaderboard;
    }
}
