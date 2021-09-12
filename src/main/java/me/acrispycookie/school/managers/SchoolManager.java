package me.acrispycookie.school.managers;

import me.acrispycookie.Console;
import me.acrispycookie.Main;
import me.acrispycookie.school.classes.Lesson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchoolManager {

    long channelId;
    Main main;

    public SchoolManager(long channelId, Main main){
        this.channelId = channelId;
        this.main = main;
        start();
    }

    public void start(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(day != Calendar.SUNDAY && day != Calendar.SATURDAY){
            if(hour < 13 || (hour == 13 && minute < 25)){
                ArrayList<Lesson> announcements = new ArrayList<>();
                addAll(announcements, day, hour, minute);
                startAnnouncements(announcements);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Console.println("The next lesson is at " + simpleDateFormat.format(new Date(announcements.get(0).getTimeToAnnouce())));
            }
            else{
                Console.println("There is no lesson left today! Will try again tomorrow...");
                scheduleNextDay();
            }
        }
        else{
            Console.println("There is no lesson left today! Will try again tomorrow...");
            scheduleNextDay();
        }
    }

    private void scheduleNextDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(() -> {
            Console.println("NEW DAY!");
            start();
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void startAnnouncements(ArrayList<Lesson> announcements){
        for(Lesson lesson : announcements){
            lesson.startAnnouncements();
        }
        scheduleNextDay();
    }

    public void addAll(ArrayList<Lesson> announcements, int day, int hour, int minute){
        int schoolHour = getNextSchoolHour(hour, minute);
        for(int i = schoolHour; i < 8; i++){
            Lesson l = Main.getInstance().getProgramManager().getByDate(day, i, getTimeToAnnounce(i));
            announcements.add(l);
        }
    }

    private long getTimeToAnnounce(int schoolHour) {
        Calendar calendar = Calendar.getInstance();
        switch (schoolHour){
            case 1:
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 25);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 2:
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 15);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 3:
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                calendar.set(Calendar.MINUTE, 5);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 4:
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                calendar.set(Calendar.MINUTE, 55);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 5:
                calendar.set(Calendar.HOUR_OF_DAY, 11);
                calendar.set(Calendar.MINUTE, 45);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 6:
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 35);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
            case 7:
                calendar.set(Calendar.HOUR_OF_DAY, 13);
                calendar.set(Calendar.MINUTE, 25);
                calendar.set(Calendar.SECOND, 0);
                return calendar.getTimeInMillis();
        }
        return -1;
    }

    private int getNextSchoolHour(int hour, int minute){
        if(hour < 8){
            return 1;
        }
        else if(hour == 8){
            if(minute < 25){
                return 1;
            }
            else{
                return 2;
            }
        }
        else if(hour == 9){
            if(minute < 15){
                return 2;
            }
            else{
                return 3;
            }
        }
        else if(hour == 10){
            if(minute < 5){
                return 3;
            }
            else{
                return 4;
            }
        }
        else if(hour == 11){
            if(minute < 45){
                return 5;
            }
            else {
                return 6;
            }
        }
        else if(hour == 12) {
            if(minute < 35){
                return 6;
            }
            else {
                return 7;
            }
        }
        else if(hour == 13 && minute < 25) {
            return 7;
        }
        return -1;
    }
}
