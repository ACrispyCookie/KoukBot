package dev.acrispycookie.school.managers;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.FeatureManager;
import dev.acrispycookie.utility.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PanellhniesManager extends FeatureManager {

    private long secondsLeft;
    private long premiereDate;
    private TextChannel channel;
    private Role role;

    public PanellhniesManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        this.secondsLeft = getSecondsLeft();
        this.premiereDate = parseDate(bot.getConfigManager().get("features.panellhnies.date"));
        this.channel = bot.getGuild().getTextChannelById(bot.getConfigManager().get("features.panellhnies.channel"));
        this.role = bot.getGuild().getRoleById(bot.getConfigManager().get("features.panellhnies.3hLykeiouRole"));
        if (Boolean.parseBoolean(bot.getConfigManager().get("features.panellhnies.scheduler-enabled"))) {
            schedule();
        }
    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {

    }

    public void sendMessage(Member m, SlashCommandInteractionEvent e) {
        if (m == null) {
            this.channel.sendMessage("Λοιπόν παιδιά, " + role.getAsMention() + ", ήρθε η ώρα να σας υπνεθυμίσω οτι γράφετε σε **" + getCountdown() + "**").queue();
        }
        else {
            e.reply("Λοιπόν παιδιά, " + role.getAsMention() + ", ο " + m.getAsMention() + " ήθελε να υπενθυμίσει σε όλους οτι γράφετε σε **" + getCountdown() + "**").queue();
        }
    }

    private void schedule() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(() -> {
            sendMessage(null, null);
            PanellhniesManager.this.secondsLeft = getSecondsLeft();
            PanellhniesManager.this.schedule();
        }, secondsLeft, TimeUnit.SECONDS);
    }

    private long getSecondsLeft() {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        if (now.get(Calendar.HOUR_OF_DAY) > 12) {
            then.add(Calendar.DATE, 1);
            then.set(Calendar.HOUR_OF_DAY, 12);
        }
        else {
            then.set(Calendar.HOUR_OF_DAY, 12);
        }
        return then.getTimeInMillis() - now.getTimeInMillis();
    }

    private long parseDate(String date) {
        String[] split = date.split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Integer.parseInt(split[2]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[0]));
        return calendar.getTimeInMillis();
    }

    private String getCountdown() {
        long timeLeft = premiereDate - System.currentTimeMillis();
        return Time.formatted(timeLeft/1000);
    }
}
