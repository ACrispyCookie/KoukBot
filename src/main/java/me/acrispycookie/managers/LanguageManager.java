package me.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Random;

public class LanguageManager {

    JsonObject language;

    public LanguageManager(JsonObject language){
        this.language = language;
    }

    public String get(String key, String... replace) {
        String string = get(key)
                .replaceAll("%perms", getAvailablePerms())
                .replaceAll("%prefix", Main.getInstance().getPrefix());
        for(int i = 0; i < replace.length; i++){
            string = string.replaceAll("%" + (i + 1), replace[i]);
        }
        return string;
    }

    public String get(String key) {
        JsonElement element = null;
        String[] parents = key.split("\\.");
        for(String s : parents){
            if(element == null){
                element = language.get(s);
            }
            else{
                element = element.getAsJsonObject().get(s);
            }
        }
        if(!element.isJsonArray()){
            return element.getAsString()
                    .replaceAll("%perms", getAvailablePerms())
                    .replaceAll("%prefix", Main.getInstance().getPrefix());
        }
        else{
            StringBuilder content = new StringBuilder();
            for(int i = 0; i < element.getAsJsonArray().size(); i++){
                content.append(element.getAsJsonArray().get(i)
                        .getAsString()
                        .replaceAll("%perms", getAvailablePerms())
                        .replaceAll("%prefix", Main.getInstance().getPrefix())).append("\n");
            }
            return content.toString();
        }
    }

    public String getRandomLevelUp(ArrayList<String> special){
        Random random = new Random();
        JsonArray array = language.getAsJsonArray("level-up");
        for(String s : special){
            array.add(new JsonPrimitive(s));
        }
        int index = random.nextInt(array.size());
        return array.get(index).getAsString();
    }

    private String getAvailablePerms(){
        StringBuilder s = null;
        for(Perm p : Perm.values()){
            if(s == null){
                s = new StringBuilder(p.name());
            }
            else{
                s.append(", ").append(p.name());
            }
        }
        return s.toString();
    }
}
