package dev.acrispycookie.school.classes;

import dev.acrispycookie.KoukBot;

public abstract class Lesson {

    protected final KoukBot bot;
    protected final long timeToAnnouce;
    public abstract void startAnnouncements();

    public Lesson(KoukBot bot, long timeToAnnounce) {
        this.bot = bot;
        this.timeToAnnouce = timeToAnnounce;
    }

    public long getTimeToAnnouce() {
        return timeToAnnouce;
    }

}
