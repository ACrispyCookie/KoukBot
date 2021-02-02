package me.acrispycookie.levelsystem;

import com.google.gson.*;
import com.sun.javafx.binding.StringFormatter;
import me.acrispycookie.Console;
import me.acrispycookie.Main;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LevelUser {

    User discordUser;
    int level;
    int xp;
    int totalXp;
    int xpRequired;
    long nextValidMessage;
    String cardColor;
    ArrayList<String> specialLevelUp;
    static ArrayList<LevelUser> loadedUsers = new ArrayList<>();

    private LevelUser(User discordUser, int level, int xp, long nextValidMessage, ArrayList<String> specialLevelUp, String cardColor, boolean isSaved){
        this.discordUser = discordUser;
        this.level = level;
        this.cardColor = cardColor;
        this.xp = xp;
        this.totalXp = findTotalXP(level) + xp;
        this.xpRequired = getRequired(level);
        this.nextValidMessage = nextValidMessage;
        this.specialLevelUp = specialLevelUp;
        loadedUsers.add(this);
        if(!isSaved){
            save();
        }
    }

    public void addExp(int exp, long channelId, boolean updateMinute){
        totalXp = totalXp + exp;
        xp = xp + exp;
        while(xp >= xpRequired){
            levelUp(channelId);
        }
        if(updateMinute){
            nextValidMessage = System.currentTimeMillis() + 60 * 1000;
        }
    }

    public void removeExp(int exp){
        totalXp = Math.max(totalXp - exp, 0);
        level = findLevel(totalXp);
        xpRequired = getRequired(level);
        int previousLevelXp = findTotalXP(level);
        xp = totalXp - previousLevelXp;
    }

    public void setCardColor(String color){
        cardColor = color;
    }

    public void levelUp(long channelId){
        int newExp = xp - xpRequired;
        level++;
        xp = newExp;
        xpRequired = getRequired(level);
        Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage(Main.getInstance().getLanguageManager().getRandomLevelUp(getSpecialLevelUp()) + " Μπράβο " + discordUser.getAsMention() + ", έφτασες level " + level + "!").queue();
    }

    public void sendCard(long channelId){
        try {
            BufferedImage image = new UserCard(this).build();
            ImageIO.write(image, "png", new File("./images/" + discordUser.getId() + ".png"));
            Main.getInstance().getGuild().getTextChannelById(channelId).sendFile(new File("./images/" + discordUser.getId() + ".png")).queue((a) -> {
                new File("./images/" + discordUser.getId() + ".png").delete();
            });
        } catch (IOException e) {
            e.printStackTrace();
            Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage("There was a problem obtaining the card template! Please contact ACrispyCookie#4252").queue();
        }
    }

    public static LevelUser getByDiscordId(long discordId){
        for(LevelUser u : loadedUsers){
            if(u.getDiscordUser().getIdLong() == discordId){
                return u;
            }
        }
        return load(discordId);
    }

    private static LevelUser load(long discordId){
        JsonObject userData = Main.getInstance().getUserDataManager().getUserData();
        JsonObject object = userData.getAsJsonObject(String.valueOf(discordId));
        if(object != null){
            int level = object.get("level").getAsInt();
            int xp = object.get("xp").getAsInt();
            String cardColor = object.get("card-color") == null ? "#62D3F5" : object.get("card-color").getAsString();
            long nextMinute = object.get("nextMinute").getAsLong();
            ArrayList<String> specialLevelUp = new ArrayList<>();
            JsonArray array = object.getAsJsonArray("extra-level-up");
            if(array != null){
                for(int c = 0; c < array.size(); c++){
                    specialLevelUp.add(array.get(c).getAsString());
                }
            }
            return new LevelUser(Main.getInstance().getDiscordUser(discordId), level, xp, nextMinute, specialLevelUp, cardColor,true);
        }
        return new LevelUser(Main.getInstance().getDiscordUser(discordId), 0, 0, 0, new ArrayList<>(), "#62D3F5", false);
    }

    public int getRank(){
        int place = 1;
        int totalExp = findTotalXP(level) + xp;
        JsonObject object = Main.getInstance().getUserDataManager().getUserData();
        for(String s : object.keySet()){
            JsonObject userObject = object.getAsJsonObject(s);
            int theirTotalExp = findTotalXP(userObject.get("level").getAsInt()) + userObject.get("xp").getAsInt();
            if(theirTotalExp > totalExp){
                place++;
            }
        }
        return place;
    }

    private int findTotalXP(int level){
        int findTotalXP = 0;
        for(int i = 0; i < level; i++){
            findTotalXP = findTotalXP + getRequired(i);
        }
        return findTotalXP;
    }

    private int findLevel(int totalXp){
        int level = 0;
        while(totalXp >= 0){
            totalXp = totalXp - getRequired(level);
            level++;
        }
        return Math.max(level - 1, 0);
    }

    private int getRequired(int level){
        return 5 * (int) Math.pow(level, 2) + 50 * level + 100;
    }

    public void save() {
        JsonObject userObject = Main.getInstance().getUserDataManager().getUserData().getAsJsonObject(discordUser.getId()) == null ? new JsonObject() : Main.getInstance().getUserDataManager().getUserData().getAsJsonObject(discordUser.getId());
        userObject.add("nextMinute", new JsonPrimitive(nextValidMessage));
        userObject.add("level", new JsonPrimitive(level));
        userObject.add("xp", new JsonPrimitive(xp));
        userObject.add("card-color", new JsonPrimitive(cardColor));
        if(!this.specialLevelUp.isEmpty()){
            JsonArray extraLevelUps = new JsonArray();
            for(String s : this.specialLevelUp){
                extraLevelUps.add(new JsonPrimitive(s));
            }
            userObject.add("extra-level-up", extraLevelUps);
        }
        Main.getInstance().getUserDataManager().getUserData().add(discordUser.getId(), userObject);
        try {
            FileWriter file = new FileWriter("./data/data.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(Main.getInstance().getUserDataManager().getUserData()));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<LevelUser> getLoadedUsers(){
        return loadedUsers;
    }

    public User getDiscordUser(){
        return discordUser;
    }

    public int getLevel(){
        return level;
    }

    public int getXp(){
        return xp;
    }

    public int getTotalXp(){
        return totalXp;
    }

    public int getXpRequired(){
        return xpRequired;
    }

    public long getNextValidMessage(){
        return nextValidMessage;
    }

    public String getCardColor(){
        return cardColor;
    }

    public ArrayList<String> getSpecialLevelUp(){
        return specialLevelUp;
    }

}
