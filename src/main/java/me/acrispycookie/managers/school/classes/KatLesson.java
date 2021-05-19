package me.acrispycookie.managers.school.classes;

import me.acrispycookie.Main;
import me.acrispycookie.managers.school.Announcement;
import me.acrispycookie.managers.school.enums.EnumLesson;

public class KatLesson extends Lesson{

    EnumLesson bthet1;
    EnumLesson bthet2;
    EnumLesson anth;

    public KatLesson(EnumLesson bthet1, EnumLesson bthet2, EnumLesson anth, long timeToAnnounce) {
        super("kat", timeToAnnounce);
        this.bthet1 = bthet1;
        this.bthet2 = bthet2;
        this.anth = anth;
    }

    public EnumLesson getBthet1(){
        return  bthet1;
    }

    public EnumLesson getBthet2(){
        return bthet2;
    }

    public EnumLesson getAnth(){
        return anth;
    }

    @Override
    public void startAnnouncements() {
        Announcement bthet1 = new Announcement(this.bthet1, timeToAnnouce, Main.getInstance().getGuild().getRoleById(787997819889516554L));
        Announcement bthet2 = new Announcement(this.bthet2, timeToAnnouce, Main.getInstance().getGuild().getRoleById(788351437540687913L));
        Announcement anth = new Announcement(this.anth, timeToAnnouce, Main.getInstance().getGuild().getRoleById(787997917541433385L));
        Announcement dialeima = new Announcement(timeToAnnouce + 2400000L, Main.getInstance().getGuild().getRoleById(787997917541433385L), Main.getInstance().getGuild().getRoleById(788351437540687913L), Main.getInstance().getGuild().getRoleById(787997819889516554L));
        bthet1.start();
        bthet2.start();
        anth.start();
        dialeima.start();
    }
}
