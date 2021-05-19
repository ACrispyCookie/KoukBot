package me.acrispycookie;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.MessageSentEvent;
import me.acrispycookie.managers.*;
import me.acrispycookie.managers.MoviePollManager;
import me.acrispycookie.managers.school.SchoolManager;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    private JDA bot;
    private LanguageManager languageManager;
    private PermissionManager permissionManager;
    private ConfigManager configManager;
    private UserDataManager userDataManager;
    private SchoolManager schoolManager;
    private ProgramManager programManager;
    private ToDoManager toDoManager;
    private MoviePollManager moviePollManager;
    private static Main instance;

    public Main(){
        instance = this;
    }

    public static void main(String[] args) {
        Main main = new Main();
        long startingLoadingTime = System.currentTimeMillis();
        main.loadFont();
        main.loadConfig();
        main.loadPermissions();
        main.loadLanguage();
        main.loadUserData();
        main.startBot();
        main.loadSchoolManager();
        main.loadToDoManager();
        //main.loadMoviePollManager();
        Console.println("Total loading time: " + (System.currentTimeMillis() - startingLoadingTime) + "ms");
        Console.start();
    }

    public void disable(){
        Main.getInstance().getUserDataManager().save();
        bot.shutdown();
        Console.println("Άντε παιδιά.. Καλη όρεξη");
        System.exit(-1);
    }

    private void loadToDoManager(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading To-Dos...");
        try {
            bot.addEventListener(new ToDoManager(null));
            File todoFile = new File("./data/todo.json");
            if(!todoFile.exists()){
                todoFile = getResource("todo.json");
                todoFile.createNewFile();
            }
            toDoManager = new ToDoManager(new Gson().fromJson(new FileReader(todoFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the permission file!");
            System.exit(-1);
        }
        Console.println("To-Dos have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadFont(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading fonts...");
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Sarine-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/PrimaSansBT-Roman.otf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./images/Cera-Pro-Light.otf")));
        } catch (IOException|FontFormatException e) {
            e.printStackTrace();
            Console.println("Couldn't load the fonts!");
        }
        Console.println("Fonts have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadConfig(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading config...");
        try {
            File configFile = new File("./data/config.json");
            if(!configFile.getParentFile().exists()){
                configFile.getParentFile().mkdirs();
            }
            if(!configFile.exists()){
                configFile = getResource("config.json");
                configFile.createNewFile();
            }
            configManager = new ConfigManager(new Gson().fromJson(new FileReader(configFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the config file!");
            System.exit(-1);
        }
        Console.println("Configuration settings have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadPermissions(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading permissions...");
        try {
            File permissionFile = new File("./data/permissions.json");
            if(!permissionFile.exists()){
                permissionFile = getResource("permissions.json");
                permissionFile.createNewFile();
            }
            permissionManager = new PermissionManager(new Gson().fromJson(new FileReader(permissionFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the permission file!");
            System.exit(-1);
        }
        Console.println("Permission settings have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadLanguage(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading language files...");
        try {
            File languageFile = new File("./data/language.json");
            if(!languageFile.getParentFile().exists()){
                languageFile.getParentFile().mkdirs();
            }
            if(!languageFile.exists()){
                languageFile = getResource("language.json");
                languageFile.createNewFile();
            }
            languageManager = new LanguageManager(new Gson().fromJson(new FileReader(languageFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the language file!");
            System.exit(-1);
        }
        Console.println("Language files have been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadUserData(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading user data...");
        try {
            File dataFile = new File("./data/data.json");
            if(!dataFile.exists()){
                dataFile = getResource("data.json");
                dataFile.createNewFile();
            }
            userDataManager = new UserDataManager(new Gson().fromJson(new FileReader(dataFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the user data file!");
            System.exit(-1);
        }
        Console.println("User data has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadMoviePollManager(){
        long startTime = System.currentTimeMillis();
        Console.println("Loading movie poll manager...");
        try {
            File dataFile = new File("./data/movie_polls.json");
            if(!dataFile.exists()){
                dataFile = getResource("movie_polls.json");
                dataFile.createNewFile();
            }
            moviePollManager = new MoviePollManager(this, new Gson().fromJson(new FileReader(dataFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the user data file!");
            System.exit(-1);
        }
        Console.println("Movie poll manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void startBot(){
        long startTime = System.currentTimeMillis();
        Console.println("Starting discord bot...");
        try {
            JDABuilder builder = JDABuilder.createDefault(configManager.get("bot.botToken"));
            builder.setActivity(Activity.of(Activity.ActivityType.valueOf(configManager.get("bot.activity.type")), configManager.get("bot.activity.status")));
            builder.addEventListeners(new BotCommand() { @Override  public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) { } });
            builder.addEventListeners(new MessageSentEvent());
            builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            bot = builder.build();
            bot.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to start the discord bot!");
            System.exit(-1);
        }
        Console.println("Bot has started successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadSchoolManager(){
        long startTime = System.currentTimeMillis();
        Console.println("Starting school announcement manager...");
        loadProgramManager();
        long channelId = Long.parseLong(configManager.get("settings.announcementChannel"));
        schoolManager = new SchoolManager(channelId, instance);
        Console.println("Announcement manager has started successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadProgramManager(){
        try {
            File dataFile = new File("./data/program.json");
            if(!dataFile.exists()){
                dataFile = getResource("program.json");
                dataFile.createNewFile();
            }
            programManager = new ProgramManager(new Gson().fromJson(new FileReader(dataFile), JsonObject.class));
        } catch (IOException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to load the program file!");
            System.exit(-1);
        }
    }

    public void reload(){
        loadLanguage();
        loadPermissions();
        Main.getInstance().getUserDataManager().save();
        loadUserData();
        bot.getPresence().setActivity(Activity.of(Activity.ActivityType.valueOf(configManager.get("bot.activity.type")), configManager.get("bot.activity.status")));
        Console.println("Configuration files and user data have been reloaded successfully!");
    }

    public SchoolManager getSchoolManager(){
        return schoolManager;
    }

    public LanguageManager getLanguageManager(){
        return languageManager;
    }

    public PermissionManager getPermissionManager(){
        return permissionManager;
    }

    public ConfigManager getConfigManager(){
        return configManager;
    }

    public UserDataManager getUserDataManager(){
        return userDataManager;
    }

    public ProgramManager getProgramManager(){
        return programManager;
    }

    public ToDoManager getToDoManager(){
        return toDoManager;
    }

    public MoviePollManager getMoviePollManager() {
        return moviePollManager;
    }

    public User getDiscordUser(long discordId){
        return bot.retrieveUserById(discordId).complete();
    }

    public Member getDiscordMember(User user){
        return getGuild().retrieveMember(user).complete();
    }

    public Color getBotColor(){
        return getGuild().getMember(bot.getSelfUser()).getColor();
    }

    public Color getErrorColor(){
        return Utils.hex2Rgb(configManager.get("settings.errorColor"));
    }

    public String getPrefix(){
        return getConfigManager().get("bot.prefix");
    }

    public Guild getGuild(){
        return bot.getGuildById(Long.parseLong(configManager.get("bot.guildId")));
    }

    public static Main getInstance(){
        return instance;
    }

    private File getResource(String resourceName){
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        File file = new File("./data/" + resourceName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
