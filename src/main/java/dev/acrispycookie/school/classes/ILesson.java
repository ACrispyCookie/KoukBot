package dev.acrispycookie.school.classes;

import dev.acrispycookie.KoukBot;
import net.dv8tion.jda.api.entities.Role;

public class ILesson extends Lesson {

    private final int[] id;
    private final Role role;
    private final boolean announceBreak;

    public ILesson(KoukBot bot, int[] id1, long timeToAnnounce, Role role, boolean announceBreak) {
        super(bot, timeToAnnounce);
        this.id = id1;
        this.role = role;
        this.announceBreak = announceBreak;
    }

    public int[] getId() {
        return id;
    }

    @Override
    public void startAnnouncements() {
        boolean shouldAnnounce = id[0] != 15;
        if (shouldAnnounce) {
            Announcement ann = new Announcement(bot, id, timeToAnnouce, role);
            ann.start();
            if (announceBreak) {
                Announcement dialeima = new Announcement(bot, timeToAnnouce +
                        (1000L * 60L * Integer.parseInt(bot.getConfigManager().get("features.announcer.classDuration"))),
                        bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.0")),
                        bot.getGuild().getRoleById(bot.getConfigManager().get("features.announcer.roles.global.1")));
                dialeima.start();
            }
        }
    }
}
