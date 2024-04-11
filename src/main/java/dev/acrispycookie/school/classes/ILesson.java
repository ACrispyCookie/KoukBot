package dev.acrispycookie.school.classes;

import dev.acrispycookie.Main;
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
                Announcement dialeima = new Announcement(timeToAnnouce +
                        (1000L * 60L * Integer.parseInt(Main.getInstance().getConfigManager().get("features.announcer.classDuration"))),
                        Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.global.0")),
                        Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.global.1")));
                dialeima.start();
            }
        }
    }
}
