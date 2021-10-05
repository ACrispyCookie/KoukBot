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
        Announcement ann = new Announcement(id, timeToAnnouce, role);
        ann.start();
        if(announceBreak){
            boolean shouldAnnounceBreak = id[0] != 15;
            if(shouldAnnounceBreak){
                Announcement dialeima = new Announcement(timeToAnnouce + 2400000L, Main.getInstance().getGuild().getRoleById(890980670569074739L), Main.getInstance().getGuild().getRoleById(890980375910809630L));
                dialeima.start();
            }
        }
    }
}
