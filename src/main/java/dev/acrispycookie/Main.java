package dev.acrispycookie;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.levelsystem.XPGainEvent;
import dev.acrispycookie.managers.*;
import dev.acrispycookie.managers.tracking.StatusListener;
import dev.acrispycookie.school.managers.PanellhniesManager;
import dev.acrispycookie.school.managers.ProgramCreatorManager;
import dev.acrispycookie.school.managers.ProgramManager;
import dev.acrispycookie.school.managers.SchoolManager;
import dev.acrispycookie.utility.Utils;
import dev.acrispycookie.managers.MoviePollManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Main {

    private JDA bot;
    private LanguageManager languageManager;
    private PermissionManager permissionManager;
    private ConfigManager configManager;
    private UserDataManager userDataManager;
    private LeaderboardManager leaderboardManager;
    private ToDoManager toDoManager;
    private MoviePollManager moviePollManager;
    private MusicManager musicManager;
    private SchoolManager schoolManager;
    private ProgramManager programManager;
    private PanellhniesManager panellhniesManager;
    private ProgramCreatorManager programCreator;
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
        main.loadLevelManager();
        main.loadAnnouncerManager();
        main.loadPanellhniesManager();
        main.loadProgramCreatorManager();
        main.loadToDoManager();
        main.loadMusicManager();
        main.loadMoviePollManager();
        Console.println("Total loading time: " + (System.currentTimeMillis() - startingLoadingTime) + "ms");
        Console.start();
    }

    public void disable(){
        Main.getInstance().getUserDataManager().save();
        Main.getInstance().getLeaderboardManager().save();
        Main.getInstance().getMusicManager().stop();
        bot.shutdown();
        Console.println("Άντε παιδιά.. Καλη όρεξη");
        System.exit(-1);
    }

    private void loadFont(){
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
            Console.println("Error occured while trying to read the config file!");
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
            Console.println("Error occured while trying to read the permission file!");
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
            Console.println("Error occured while trying to read the language file!");
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
            Console.println("Error occured while trying to read the user data file!");
            System.exit(-1);
        }
        Console.println("User data has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }


    private void startBot(){
        long startTime = System.currentTimeMillis();
        Console.println("Starting discord bot...");
        try {
            bot = JDABuilder.createDefault(configManager.get("bot.botToken"))
                    .addEventListeners(new BotCommand() { @Override public void execute(SlashCommandInteractionEvent e, String label, Member m) { } })
                    .addEventListeners(new XPGainEvent())
                    .addEventListeners(new StatusListener())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.ACTIVITY)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .setActivity(Activity.of(Activity.ActivityType.valueOf(configManager.get("bot.activity.type")), configManager.get("bot.activity.status")))
                    .build();
            bot.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Console.println("Error occured while trying to start the discord bot!");
            System.exit(-1);
        }
        Console.println("Bot has been started successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadLevelManager(){
        if(Boolean.parseBoolean(configManager.get("features.levels.enabled"))){
            long startTime = System.currentTimeMillis();
            Console.println("Loading level manager...");
            try {
                File dataFile = new File("./data/leaderboard.json");
                if(!dataFile.exists()){
                    dataFile = getResource("leaderboard.json");
                    dataFile.createNewFile();
                }
                JsonObject object = new Gson().fromJson(new FileReader(dataFile), JsonObject.class);
                leaderboardManager = new LeaderboardManager(object);
                leaderboardManager.setup();
                for(VoiceChannel v : Main.getInstance().getGuild().getVoiceChannels()){
                    for(Member m : v.getMembers()){
                        LevelUser.getByDiscordId(m.getIdLong()).joinChannel();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Console.println("Error occured while trying to read the leaderboard data file!");
                System.exit(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Console.println("Level manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    private void loadMusicManager(){
        if(Boolean.parseBoolean(configManager.get("features.music.enabled"))){
            long startTime = System.currentTimeMillis();
            Console.println("Loading music manager...");
            File folder = new File("./kouk_lines");
            ArrayList<File> files = new ArrayList<>();
            if(!folder.exists()){
                folder.mkdir();
            }
            else{
                for(File file : folder.listFiles()){
                    if(file.getName().endsWith(".mp3")){
                        files.add(file);
                    }
                }
            }
            musicManager = new MusicManager(this, files);
            Console.println("Music manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    private void loadMoviePollManager(){
        if(Boolean.parseBoolean(configManager.get("features.movies.enabled"))){
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
                Console.println("Error occured while trying to read the movie poll data file!");
                System.exit(-1);
            }
            Console.println("Movie poll manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
        }
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
        Console.println("To-Do manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void loadAnnouncerManager(){
        if(Boolean.parseBoolean(configManager.get("features.announcer.enabled"))){
            long startTime = System.currentTimeMillis();
            Console.println("Starting announcer manager...");
            long channelId = Long.parseLong(configManager.get("features.announcer.announcementChannel"));
            try {
                File dataFile = new File("./data/program.json");
                if(!dataFile.exists()){
                    dataFile = getResource("program.json");
                    dataFile.createNewFile();
                }
                programManager = new ProgramManager(new Gson().fromJson(new FileReader(dataFile), JsonObject.class));
                schoolManager = new SchoolManager(channelId, instance);
            } catch (IOException e) {
                e.printStackTrace();
                Console.println("Error occured while trying to read the program file!");
                System.exit(-1);
            }
            Console.println("Announcer manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    private void loadPanellhniesManager(){
        if(Boolean.parseBoolean(configManager.get("features.panellhnies.enabled"))) {
            long startTime = System.currentTimeMillis();
            Console.println("Loading panellhnies manager...");
            panellhniesManager = new PanellhniesManager(this);
            Console.println("Panellhnies manager has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    private void loadProgramCreatorManager(){
        if(Boolean.parseBoolean(configManager.get("features.program-creator.enabled"))) {
            try {
                long startTime = System.currentTimeMillis();
                Console.println("Loading program creator manager...");
                File programCreatorFile = new File("./data/program_creator.json");
                if(!programCreatorFile.exists()){
                    programCreatorFile = getResource("program_creator.json");
                    programCreatorFile.createNewFile();
                }
                programCreator = new ProgramCreatorManager(this, new Gson().fromJson(new FileReader(programCreatorFile), JsonObject.class));
                bot.addEventListener(programCreator);
                Console.println("Program creator has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e){
                e.printStackTrace();
                Console.println("Error occured while trying to read the program creator file!");
                System.exit(-1);
            }
        }
    }

    public void reload(){
        loadConfig();
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

    public PanellhniesManager getPanellhniesManager() {
        return panellhniesManager;
    }

    public ProgramCreatorManager getProgramCreator(){
        return programCreator;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public LeaderboardManager getLeaderboardManager() { return leaderboardManager; }

    public User getDiscordUser(long discordId){
        try {
            return bot.retrieveUserById(discordId).complete();
        } catch(Exception e) {
            return null;
        }
    }

    public Member getDiscordMember(User user){
        if(getGuild().isMember(user)){
            return getGuild().retrieveMember(user).complete();
        }
        return null;
    }

    public Color getBotColor(){
        return getGuild().getMember(bot.getSelfUser()).getColor();
    }

    public Color getErrorColor(){
        return Utils.hex2Rgb(configManager.get("settings.errorColor"));
    }

    public Guild getGuild(){
        return bot.getGuildById(Long.parseLong(configManager.get("settings.guildId")));
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
