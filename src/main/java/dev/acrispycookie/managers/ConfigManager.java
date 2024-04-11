package dev.acrispycookie.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private JsonObject config;

    public ConfigManager(JsonObject config){
        this.config = config;
    }

    public String get(String key) {
        JsonElement element = null;
        String[] parents = key.split("\\.");
        for(String s : parents){
            if(element == null){
                element = config.get(s);
            }
            else{
                element = element.getAsJsonObject().get(s);
            }
        }
        return element.getAsString();
    }

    public void changePrefix(String prefix){
        JsonObject object = config.getAsJsonObject("settings");
        object.add("prefix", new JsonPrimitive(prefix));
        save();
    }

    public void set(String key, JsonElement value){
        String[] parents = key.split("\\.");
        JsonObject finalParent = null;
        for(int i = 0; i < parents.length - 1; i++){
            if(finalParent == null){
                finalParent = config.getAsJsonObject(parents[i]);
                continue;
            }
            finalParent = finalParent.getAsJsonObject(parents[i]);
        }
        finalParent.add(parents[parents.length - 1], value);
        save();
    }

    private void save(){
        try {
            FileWriter file = new FileWriter("./data/config.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
