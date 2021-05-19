package me.acrispycookie.managers.school.classes;

import me.acrispycookie.Main;
import me.acrispycookie.managers.school.Announcement;
import me.acrispycookie.managers.school.enums.EnumLesson;

public class LangLesson extends Lesson {

    public LangLesson(long timeToAnnounce) {
        super("lang", timeToAnnounce);
    }

    @Override
    public void startAnnouncements() {
        Announcement gallika = new Announcement(EnumLesson.FRENCH, timeToAnnouce, Main.getInstance().getGuild().getRoleById(788336907070865440L));
        Announcement germanika = new Announcement(EnumLesson.GERMAN, timeToAnnouce, Main.getInstance().getGuild().getRoleById(788336947206291456L));
        Announcement dialeima = new Announcement(timeToAnnouce + 2400000L, Main.getInstance().getGuild().getRoleById(786225886311481355L), Main.getInstance().getGuild().getRoleById(786225699668361238L));
        gallika.start();
        germanika.start();
        dialeima.start();
    }
}
