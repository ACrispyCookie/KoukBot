package me.acrispycookie.school.classes;

public abstract class Lesson {

    String type;
    long timeToAnnouce;
    public abstract void startAnnouncements();

    public Lesson(String type, long timeToAnnounce){
        this.type = type;
        this.timeToAnnouce = timeToAnnounce;
    }

    public long getTimeToAnnouce(){
        return timeToAnnouce;
    }

}
