package dev.acrispycookie.school.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.acrispycookie.Main;
import dev.acrispycookie.school.classes.ILesson;
import dev.acrispycookie.school.classes.Lesson;
import dev.acrispycookie.school.classes.NLesson;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;

public class ProgramManager {

    JsonObject program;

    public ProgramManager(JsonObject program){
        this.program = program;
    }

    public Lesson[] getByDate(int day, int schoolHour, long timeToAnnounce){
        ArrayList<Lesson> lesson = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            JsonElement dayObject = program.get(String.valueOf(i)).getAsJsonObject().get(String.valueOf(getIndex(schoolHour, day)));
            if(dayObject.isJsonArray()){
                JsonArray array = dayObject.getAsJsonArray();
                int lessonId = array.get(0).getAsInt();
                int urlId = array.get(1).getAsInt();
                lesson.add(new ILesson(new int[]{lessonId, urlId}, timeToAnnounce, getRole(i), i == 0));
            }
            else {
                JsonArray array1 = dayObject.getAsJsonObject().get("0").getAsJsonArray();
                JsonArray array2 = dayObject.getAsJsonObject().get("1").getAsJsonArray();
                lesson.add(new NLesson(new int[]{array1.get(0).getAsInt(), array1.get(1).getAsInt()}, new int[]{array2.get(0).getAsInt(), array2.get(1).getAsInt()}, timeToAnnounce));
                break;
            }
        }
        return lesson.toArray(new Lesson[lesson.size()]);
    }

    private int getIndex(int hourIndex, int dayIndex){
        return dayIndex * 7 + hourIndex;
    }

    private Role getRole(int i){
        Role role = null;
        if(i == 0){
            role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.perRole.0"));
        }
        else if(i == 1){
            role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.perRole.1"));
        }
        else if(i == 2){
            role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.perRole.2"));
        }
        else if(i == 3){
            role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.announcer.roles.perRole.3"));
        }
        return role;
    }
}
