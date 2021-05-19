package me.acrispycookie.managers.school.classes;

import me.acrispycookie.Main;
import me.acrispycookie.managers.school.Announcement;
import me.acrispycookie.managers.school.enums.EnumLesson;

public class NormalLesson extends Lesson{

    EnumLesson b1;
    EnumLesson b2;

    public NormalLesson(EnumLesson b1, EnumLesson b2, long timeToAnnounce) {
        super("normal", timeToAnnounce);
        this.b1 = b1;
        this.b2 = b2;
    }

    public EnumLesson getB1(){
        return b1;
    }

    public EnumLesson getB2(){
        return b2;
    }

    @Override
    public void startAnnouncements() {
        Announcement b1 = new Announcement(this.b1, timeToAnnouce, Main.getInstance().getGuild().getRoleById(786225699668361238L));
        Announcement b2 = new Announcement(this.b2, timeToAnnouce, Main.getInstance().getGuild().getRoleById(786225886311481355L));
        Announcement dialeima = new Announcement(timeToAnnouce + 2400000L, Main.getInstance().getGuild().getRoleById(786225886311481355L), Main.getInstance().getGuild().getRoleById(786225699668361238L));
        b1.start();
        b2.start();
        dialeima.start();
    }
}
