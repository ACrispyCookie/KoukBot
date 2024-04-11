package dev.acrispycookie.school.classes;

public abstract class Lesson {

    long timeToAnnouce;
    public abstract void startAnnouncements();

    public Lesson(long timeToAnnounce){
        this.timeToAnnouce = timeToAnnounce;
    }

    public long getTimeToAnnouce(){
        return timeToAnnouce;
    }

}
