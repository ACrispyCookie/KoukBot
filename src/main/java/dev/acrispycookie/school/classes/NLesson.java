package dev.acrispycookie.school.classes;

import dev.acrispycookie.KoukBot;

public class NLesson extends Lesson {

    private final int[] id1;
    private final int[] id2;

    public NLesson(KoukBot bot, int[] id1, int[] id2, long timeToAnnounce) {
        super(bot, timeToAnnounce);
        this.id1 = id1;
        this.id2 = id2;
    }

    public int[] getId1() {
        return id1;
    }

    public int[] getId2() {
        return id2;
    }

    @Override
    public void startAnnouncements() {
        if (id1[0] != 15) {
            Announcement g1 = new Announcement(bot, id1, timeToAnnouce,
                    bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.0")));
            g1.start();
        }
        if (id2[0] != 15) {
            Announcement g2 = new Announcement(bot, id2, timeToAnnouce,
                    bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.1")));
            g2.start();
        }
        if (id1[0] != 15 || id2[0] != 15) {
            Announcement dialeima = new Announcement(bot, timeToAnnouce +
                    (1000L * 60L * Integer.parseInt(bot.getConfigManager().get("features.announcer.classDuration"))),
                    bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.0")),
                    bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.1")));
            dialeima.start();
        }
    }
}
