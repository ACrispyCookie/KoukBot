package me.acrispycookie.school.classes;

import me.acrispycookie.Main;
import me.acrispycookie.school.enums.EnumLesson;
import net.dv8tion.jda.api.entities.Role;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Announcement {

    int[] id;
    long time;
    Role toMention;
    Role[] mentions;

    public Announcement(int[] id, long timeToAnnounce, Role role){
        this.id = id;
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
        EnumLesson l = EnumLesson.get(id[0]);
        if(l != null){
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("features.announcer.announcementChannel"));
                Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage("Ει " + toMention.getAsMention() + ", έχεις μάθημα σε 5'\nΤο μάθημα σας είναι: " + l.getName() + "\nΆντε πάρε και το link: " + l.getUrl(id[1])).queue();
            }, delay, TimeUnit.MILLISECONDS);
        }
        else{
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("features.announcer.announcementChannel"));
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
