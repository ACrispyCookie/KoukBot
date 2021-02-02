package me.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.acrispycookie.levelsystem.LevelUser;

public class UserDataManager {

    private JsonObject userData;

    public UserDataManager(JsonObject userData){
        this.userData = userData;
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
