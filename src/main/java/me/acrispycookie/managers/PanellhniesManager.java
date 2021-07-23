package me.acrispycookie.managers;

import me.acrispycookie.Main;
import me.acrispycookie.utility.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PanellhniesManager {

    Main main;
    long secondsLeft;
    long premiereDate;
    TextChannel channel;
    Role role;

    public PanellhniesManager(Main main){
        this.main = main;
        this.secondsLeft = getSecondsLeft();
        this.premiereDate = parseDate(main.getConfigManager().get("settings.panellhnies.date"));
        this.channel = Main.getInstance().getGuild().getTextChannelById(Main.getInstance().getConfigManager().get("settings.panellhnies.channel"));
        this.role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("settings.panellhnies.3hLykeiouRole"));
        if(Main.getInstance().getConfigManager().get("settings.panellhnies.enabled").equals("true")){
            schedule();
        }
    }

    public void sendMessage(Member m, TextChannel channel){
        if(m == null){
            channel.sendMessage("Λοιπόν παιδιά, " + role.getAsMention() + ", ήρθε η ώρα να σας υπνεθυμίσω οτι γράφετε σε **" + getCountdown() + "**").queue();
        }
        else{
            this.channel.sendMessage("Λοιπόν παιδιά, " + role.getAsMention() + ", ο " + m.getAsMention() + " ήθελε να υπενθυμίσει σε όλους οτι γράφετε σε **" + getCountdown() + "**").queue();
        }
    }

    private void schedule(){
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(() -> {
            sendMessage(null, null);
            PanellhniesManager.this.secondsLeft = getSecondsLeft();
            PanellhniesManager.this.schedule();
        }, secondsLeft, TimeUnit.SECONDS);
    }

    private long getSecondsLeft(){
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        if(now.get(Calendar.HOUR_OF_DAY) > 12){
            then.add(Calendar.DATE, 1);
            then.set(Calendar.HOUR_OF_DAY, 12);
        }
        else{
            then.set(Calendar.HOUR_OF_DAY, 12);
        }
        return then.getTimeInMillis() - now.getTimeInMillis();
    }

    private long parseDate(String date){
        String[] split = date.split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Integer.parseInt(split[2]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[0]));
        return calendar.getTimeInMillis();
    }

    private String getCountdown(){
        long timeLeft = premiereDate - System.currentTimeMillis();
        return Time.formatted(timeLeft/1000);
    }
}
