package dev.acrispycookie.managers;

import com.google.gson.JsonObject;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.Main;

import java.util.ArrayList;

public class UserDataManager {

    private JsonObject userData;

    public UserDataManager(JsonObject userData){
        this.userData = userData;
    }

    public void checkData() {
        ArrayList<String> toRemove = new ArrayList<>();
        for(String id : userData.keySet()) {
            if(!Main.getInstance().getGuild().isMember(Main.getInstance().getDiscordUser(Long.parseLong(id)))) {
                toRemove.add(id);
            }
        }
        for(String id : toRemove){
            userData.remove(id);
        }
        save();
    }

    public JsonObject getUserData(){
        return userData;
    }

    public void save(){
        for(LevelUser u : LevelUser.getLoadedUsers()){
            u.save();
        }
    }
}
