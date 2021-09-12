package me.acrispycookie.school;

import me.acrispycookie.Main;
import me.acrispycookie.school.enums.EnumLesson;
import net.dv8tion.jda.api.entities.Role;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Announcement {

    EnumLesson l;
    long time;
    Role toMention;
    Role[] mentions;

    public Announcement(EnumLesson l, long timeToAnnounce, Role role){
        this.l = l;
        this.time = timeToAnnounce;
        this.toMention = role;
    }

    public Announcement(long timeToAnnounce, Role... role){
        this.time = timeToAnnounce;
        this.mentions = role;
    }

    public void start(){
        long delay = time - System.currentTimeMillis();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        if(l != null){
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("settings.announcementChannel"));
                Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage("Ει " + toMention.getAsMention() + ", έχεις μάθημα σε 5'\nΤο μάθημα σας είναι: " + l.getName() + "\nΆντε πάρε και το link: " + l.getUrl(toMention)).queue();
            }, delay, TimeUnit.MILLISECONDS);
        }
        else{
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("settings.announcementChannel"));
                StringBuilder msg = null;
                for(Role mention : mentions){
                    if(msg == null){
                        msg = new StringBuilder("Έλα " + mention.getAsMention());
                    }
                    else{
                        msg.append(", ").append(mention.getAsMention());
                    }
                }
                msg.append(", άντε παιδιά τελειώνετε σε 5'");
                Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage(msg).queue();
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
}
