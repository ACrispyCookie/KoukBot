package dev.acrispycookie;

import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.XPGainEvent;
import dev.acrispycookie.managers.*;
import dev.acrispycookie.music.MusicManager;
import dev.acrispycookie.school.managers.PanellhniesManager;
import dev.acrispycookie.school.managers.ProgramCreatorManager;
import dev.acrispycookie.school.managers.ProgramManager;
import dev.acrispycookie.school.managers.SchoolManager;
import dev.acrispycookie.todo.ToDoManager;
import dev.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.awt.*;

public class KoukBot {

    private JDA bot;
    private final JDABuilder builder = JDABuilder.createDefault("");
    private static final KoukBot instance = new KoukBot();

    public static void main(String[] args) {
        long startingLoadingTime = System.currentTimeMillis();
        Console.println("Loading managers...");
        instance.load();
        Console.println("Total loading time: " + (System.currentTimeMillis() - startingLoadingTime) + "ms");
        Console.start(instance);
    }

    private void load() {
        for (Manager m : Manager.values()) {
            m.getFeatureManager().load();
            if (m == Manager.MUSIC_MANAGER)
                instance.startBot();
        }
    }

    public void unload() {
        for (Manager m : Manager.values()) {
            m.getFeatureManager().unload();
        }
        bot.shutdown();
        Console.println("Άντε παιδιά.. Καλη όρεξη");
        System.exit(-1);
    }

    public void reload() {
        for (Manager m : Manager.values()) {
            m.getFeatureManager().reload();
        }

        ConfigManager configManager = (ConfigManager) Manager.CONFIG_MANAGER.getFeatureManager();
        bot.getPresence().setActivity(
                Activity.of(
                        Activity.ActivityType.valueOf(configManager.get("bot.activity.type")),
                        configManager.get("bot.activity.status")
                )
        );
        Console.println("Bot has been reloaded successfully!");
    }


    private void startBot() {
        long startTime = System.currentTimeMillis();
        Console.println("Starting discord bot...");
        try {
            ConfigManager configManager = (ConfigManager) Manager.CONFIG_MANAGER.getFeatureManager();
            bot = builder.setToken(configManager.get("bot.botToken"))
                    .addEventListeners(new BotCommand(instance) { @Override public void execute(SlashCommandInteractionEvent e, String label, Member m) { } })
                    .addEventListeners(new XPGainEvent())
                    //.addEventListeners(new StatusListener(instance))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.ACTIVITY)
                    .enableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES)
                    .setActivity(Activity.of(Activity.ActivityType.valueOf(configManager.get("bot.activity.type")), configManager.get("bot.activity.status")))
                    .build();
            bot.awaitReady();
        } catch (InterruptedException e) {
            Console.error("Error occurred while trying to start the discord bot!", e);
            System.exit(-1);
        }
        Console.println("Bot has been started successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public LanguageManager getLanguageManager() {
        return (LanguageManager) Manager.LANGUAGE_MANAGER.getFeatureManager();
    }

    public PermissionManager getPermissionManager() {
        return (PermissionManager) Manager.PERMISSION_MANAGER.getFeatureManager();
    }

    public ConfigManager getConfigManager() {
        return (ConfigManager) Manager.CONFIG_MANAGER.getFeatureManager();
    }

    public UserDataManager getUserDataManager() {
        return (UserDataManager) Manager.USERDATA_MANAGER.getFeatureManager();
    }

    public ProgramManager getProgramManager() {
        return (ProgramManager) Manager.PROGRAM_MANAGER.getFeatureManager();
    }

    public ToDoManager getToDoManager() {
        return (ToDoManager) Manager.TODO_MANAGER.getFeatureManager();
    }

    public PanellhniesManager getPanellhniesManager() {
        return (PanellhniesManager) Manager.PANELLHNIES_MANAGER.getFeatureManager();
    }

    public ProgramCreatorManager getProgramCreator() {
        return (ProgramCreatorManager)  Manager.PROGRAM_CREATOR_MANAGER.getFeatureManager();
    }

    public MusicManager getMusicManager() {
        return (MusicManager) Manager.MUSIC_MANAGER.getFeatureManager();
    }

    public LeaderboardManager getLeaderboardManager() { return (LeaderboardManager) Manager.LEADERBOARD_MANAGER.getFeatureManager(); }

    public User getDiscordUser(long discordId) {
        try {
            return bot.retrieveUserById(discordId).complete();
        } catch (Exception e) {
            return null;
        }
    }

    public Member getDiscordMember(User user) {
        if (getGuild().isMember(user)) {
            return getGuild().retrieveMember(user).complete();
        }
        return null;
    }

    public Color getBotColor() {
        return getGuild().getMember(bot.getSelfUser()).getColor();
    }

    public Color getErrorColor() {
        return Utils.hex2Rgb(((ConfigManager) Manager.CONFIG_MANAGER.getFeatureManager()).get("settings.errorColor"));
    }

    public Guild getGuild() {
        return bot.getGuildById(Long.parseLong(((ConfigManager) Manager.CONFIG_MANAGER.getFeatureManager()).get("settings.guildId")));
    }

    public JDA getBot() {
        return bot;
    }

    public JDABuilder getBuilder() {
        return builder;
    }

    public static KoukBot getCmdInstance() {
        return instance;
    }

    public enum Manager {
        CONFIG_MANAGER(new ConfigManager(instance, "CONFIG_MANAGER")),
        LANGUAGE_MANAGER(new LanguageManager(instance, "LANGUAGE_MANAGER")),
        USERDATA_MANAGER(new UserDataManager(instance, "USERDATA_MANAGER")),
        MUSIC_MANAGER(new MusicManager(instance, "MUSIC_MANAGER")),
        LEADERBOARD_MANAGER(new LeaderboardManager(instance, "LEADERBOARD_MANAGER")),
        PERMISSION_MANAGER(new PermissionManager(instance, "PERMISSION_MANAGER")),
        TODO_MANAGER(new ToDoManager(instance, "TODO_MANAGER")),
        SCHOOL_MANAGER(new SchoolManager(instance, "SCHOOL_MANAGER")),
        PROGRAM_MANAGER(new ProgramManager(instance, "PROGRAM_MANAGER")),
        PROGRAM_CREATOR_MANAGER(new ProgramCreatorManager(instance, "PROGRAM_CREATOR_MANAGER")),
        PANELLHNIES_MANAGER(new PanellhniesManager(instance, "PANELLHNIES_MANAGER"));

        private final FeatureManager featureManager;

        Manager(FeatureManager featureManager) {
            this.featureManager = featureManager;
        }

        public FeatureManager getFeatureManager() {
            return featureManager;
        }
    }

}
