package dev.acrispycookie.managers;

import com.google.gson.JsonObject;
import dev.acrispycookie.Console;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.ConfigurationFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UserDataManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.DATA;

    public UserDataManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        LevelUser.setBot(bot);
        long startTime = System.currentTimeMillis();
        Console.println("Loading fonts...");
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Sarine-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/PrimaSansBT-Roman.otf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Cera-Pro-Medium.otf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Cera-Pro-Light.otf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Cera-Pro-Bold.otf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Cera-Pro-Black.otf")));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            Console.println("Couldn't load the fonts!");
        }
        Console.println("Fonts have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {
        file.reloadJson();
    }

    public void checkData() {
        ArrayList<String> toRemove = new ArrayList<>();
        JsonObject userData = file.getJson();
        for (String id : userData.keySet()) {
            if (!bot.getGuild().isMember(bot.getDiscordUser(Long.parseLong(id)))) {
                toRemove.add(id);
            }
        }

        for (String id : toRemove) {
            userData.remove(id);
        }
        save();
    }

    public JsonObject getUserData() {
        return file.getJson();
    }

    public void save() {
        for (LevelUser u : LevelUser.getLoadedUsers()) {
            u.save();
        }
    }
}
