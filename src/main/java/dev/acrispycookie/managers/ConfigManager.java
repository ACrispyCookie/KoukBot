package dev.acrispycookie.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.ConfigurationFile;


public class ConfigManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.CONFIG;

    public ConfigManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {

    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {
        file.reloadJson();
    }

    public void changePrefix(String prefix) {
        file.setElement("settings.prefix", new JsonPrimitive(prefix));
    }

    public String get(String path) {
        return file.getElement(path).getAsString();
    }

    public void set(String path, JsonElement value) {
        file.setElement(path, value);
    }
}
