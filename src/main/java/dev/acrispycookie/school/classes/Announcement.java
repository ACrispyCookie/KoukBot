package dev.acrispycookie.school.classes;

import dev.acrispycookie.Main;
import dev.acrispycookie.school.enums.EnumLesson;
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
        if(id != null){
            EnumLesson l = EnumLesson.values()[id[0]];
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("features.announcer.announcementChannel"));
                Main.getInstance().getGuild().getTextChannelById(channelId)
                        .sendMessage(Main.getInstance().getLanguageManager().get("announcer.lesson",
                                toMention.getAsMention(), l.getName(), l.getUrl(id[1]))).queue();
            }, delay, TimeUnit.MILLISECONDS);
        }
        else{
            ses.schedule(() -> {
                long channelId = Long.parseLong(Main.getInstance().getConfigManager().get("features.announcer.announcementChannel"));

                String msg = Main.getInstance().getLanguageManager().get("announcer.break",
                        mentions[0].getAsMention(),
                        mentions.length > 1 ? mentions[1].getAsMention() : "",
                        mentions.length > 2 ? mentions[2].getAsMention() : "",
                        mentions.length > 3 ? mentions[3].getAsMention() : "");
                Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage(msg).queue();
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
}
