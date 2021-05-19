package me.acrispycookie.managers;

import com.google.gson.JsonObject;
import me.acrispycookie.managers.school.classes.KatLesson;
import me.acrispycookie.managers.school.classes.LangLesson;
import me.acrispycookie.managers.school.classes.Lesson;
import me.acrispycookie.managers.school.classes.NormalLesson;
import me.acrispycookie.managers.school.enums.EnumLesson;

import java.util.Calendar;

public class ProgramManager {

    JsonObject program;

    public ProgramManager(JsonObject program){
        this.program = program;
    }

    public Lesson getByDate(int day, int schoolHour, long timeToAnnounce){
        JsonObject dayObject = program.get(String.valueOf(schoolHour)).getAsJsonObject().get(getDayName(day)).getAsJsonObject();
        String type = dayObject.get("type").getAsString();
        switch (type) {
            case "normal":
                String b1 = dayObject.get("b1").getAsString();
                String b2 = dayObject.get("b2").getAsString();
                return new NormalLesson(EnumLesson.valueOf(b1), EnumLesson.valueOf(b2), timeToAnnounce);
            case "kat":
                String bthet1 = dayObject.get("bthet1").getAsString();
                String bthet2 = dayObject.get("bthet2").getAsString();
                String anth = dayObject.get("anth").getAsString();
                return new KatLesson(EnumLesson.valueOf(bthet1), EnumLesson.valueOf(bthet2), EnumLesson.valueOf(anth), timeToAnnounce);
            case "lang":
                return new LangLesson(timeToAnnounce);
        }
        return null;
    }

    private String getDayName(int day){
        if(day == Calendar.MONDAY){
            return "monday";
        }
        else if(day == Calendar.TUESDAY){
            return "tuesday";
        }
        else if(day == Calendar.WEDNESDAY){
            return "wednesday";
        }
        else if(day == Calendar.THURSDAY){
            return "thursday";
        }
        else if(day == Calendar.FRIDAY){
            return "friday";
        }
        return null;
    }
}
