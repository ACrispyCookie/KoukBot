package dev.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.ConfigurationFile;
import dev.acrispycookie.utility.Perm;

import java.util.ArrayList;
import java.util.Random;

public class LanguageManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.LANGUAGE;

    public LanguageManager(KoukBot bot, String name) {
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

    public String get(String key, String... replace) {
        String string = get(key)
                .replaceAll("%perms", getAvailablePerms());
        for (int i = 0; i < replace.length; i++) {
            string = string.replaceAll("%" + (i + 1), replace[i]);
        }
        return string;
    }

    public String get(String key) {
        JsonElement element = null;
        String[] parents = key.split("\\.");
        for (String s : parents) {
            if (element == null) {
                element = file.getElement(s);
            }
            else {
                element = element.getAsJsonObject().get(s);
            }
        }
        if (!element.isJsonArray()) {
            return element.getAsString()
                    .replaceAll("%perms", getAvailablePerms());
        }
        else {
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                content.append(element.getAsJsonArray().get(i)
                        .getAsString()
                        .replaceAll("%perms", getAvailablePerms())).append("\n");
            }
            return content.toString();
        }
    }

    public String getRandomLevelUp(ArrayList<String> special) {
        Random random = new Random();
        JsonArray array = file.getElement("level-up").getAsJsonArray();
        if (random.nextBoolean() && !special.isEmpty()) {
            return special.get(random.nextInt(special.size()));
        }
        else {
            return array.get(random.nextInt(array.size())).getAsString();
        }
    }

    private String getAvailablePerms() {
        StringBuilder s = null;
        for (Perm p : Perm.values()) {
            if (s == null) {
                s = new StringBuilder(p.name());
            }
            else {
                s.append(", ").append(p.name());
            }
        }
        return s.toString();
    }
}
