package me.acrispycookie.school.classes;

import me.acrispycookie.Main;
import me.acrispycookie.school.enums.EnumLesson;

public class NLesson extends Lesson {

    int[] id1;
    int[] id2;

    public NLesson(int[] id1, int[] id2, long timeToAnnounce) {
        super(timeToAnnounce);
        this.id1 = id1;
        this.id2 = id2;
    }

    public int[] getId1(){
        return id1;
    }

    public int[] getId2(){
        return id2;
    }

    @Override
    public void startAnnouncements() {
        if(id1[0] != 15){
            Announcement g1 = new Announcement(id1, timeToAnnouce, Main.getInstance().getGuild().getRoleById(890980670569074739L));
            g1.start();
        }
        if(id2[0] != 15){
            Announcement g2 = new Announcement(id2, timeToAnnouce, Main.getInstance().getGuild().getRoleById(890980375910809630L));
            g2.start();
        }
        if(id1[0] != 15 || id2[0] != 15){
            Announcement dialeima = new Announcement(timeToAnnouce + 2400000L, Main.getInstance().getGuild().getRoleById(890980670569074739L), Main.getInstance().getGuild().getRoleById(890980375910809630L));
            dialeima.start();
        }
    }
}
