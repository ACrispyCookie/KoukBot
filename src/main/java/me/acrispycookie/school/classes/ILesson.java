package me.acrispycookie.school.classes;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Role;

public class ILesson extends Lesson {

    int[] id;
    Role role;
    boolean announceBreak;

    public ILesson(int[] id1, long timeToAnnounce, Role role, boolean announceBreak) {
        super(timeToAnnounce);
        this.id = id1;
        this.role = role;
        this.announceBreak = announceBreak;
    }

    public int[] getId(){
        return id;
    }

    @Override
    public void startAnnouncements() {
        boolean shouldAnnounce = id[0] != 15;
        if(shouldAnnounce){
            Announcement ann = new Announcement(id, timeToAnnouce, role);
            ann.start();
            if(announceBreak){
                Announcement dialeima = new Announcement(timeToAnnouce + 2400000L,
                        Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.global.0")),
                        Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.global.1")));
                dialeima.start();
            }
        }
    }
}
