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
        String hourString = Main.getInstance().getConfigManager().get("features.announcer.hours.5");
        if(day != Calendar.SUNDAY && day != Calendar.SATURDAY){
            if(hour < getHour(hourString) || (hour == getHour(hourString) && getMinute(hourString) < 25)){
                ArrayList<Lesson[]> announcements = getAll(day - 2, hour, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Console.println("The next lesson is at " + simpleDateFormat.format(new Date(getTimeToAnnounce(getNextSchoolHour(hour, minute)))));
                startAnnouncements(announcements);
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

    public void startAnnouncements(ArrayList<Lesson[]> announcements){
        for(Lesson[] lessons : announcements){
            for(Lesson l : lessons) {
                l.startAnnouncements();
            }
        }
        scheduleNextDay();
    }

    public ArrayList<Lesson[]> getAll(int day, int hour, int minute){
        ArrayList<Lesson[]> announcements = new ArrayList<>();
        int schoolHour = getNextSchoolHour(hour, minute);
        for(int i = schoolHour; i < 7; i++){
            Lesson[] l = Main.getInstance().getProgramManager().getByDate(day, i, getTimeToAnnounce(i));
            announcements.add(l);
        }
        return announcements;
    }

    private long getTimeToAnnounce(int schoolHour) {
        Calendar calendar = Calendar.getInstance();
        String hourString = Main.getInstance().getConfigManager().get("features.announcer.hours."
                + (schoolHour - 1));
        calendar.set(Calendar.HOUR_OF_DAY, getHour(hourString));
        calendar.set(Calendar.MINUTE, getMinute(hourString));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    private int getNextSchoolHour(int hour, int minute){
        for(int i = 1; i < 7; i++) {
            String hourString = Main.getInstance().getConfigManager().get("features.announcer.hours." + (i - 1));
            if(hour < getHour(hourString)){
                return i;
            } else if(hour == getHour(hourString)) {
                if(minute < getMinute(hourString)) {
                    return i;
                } else {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    private int getHour(String s) {
        return Integer.parseInt(s.substring(0, s.indexOf(':')));
    }

    private int getMinute(String s) {
        return Integer.parseInt(s.substring(s.indexOf(':') + 1));
    }
}
