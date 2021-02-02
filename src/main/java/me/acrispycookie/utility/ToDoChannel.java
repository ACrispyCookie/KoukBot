package me.acrispycookie.utility;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ToDoChannel {

    long userId;
    long channelId;
    ArrayList<ToDo> toDos = new ArrayList<>();
    static ArrayList<ToDoChannel> channels = new ArrayList<>();

    public ToDoChannel(long userId, long channelId){
        this.userId = userId;
        this.channelId = channelId;
        saveChannel();
        channels.add(this);
    }

    public ToDoChannel(long userId, long channelId, ArrayList<ToDo> toDos){
        this.userId = userId;
        this.channelId = channelId;
        this.toDos = toDos;
        channels.add(this);
    }

    private void saveChannel(){
        JsonObject userObject = Main.getInstance().getToDoManager().getJson();
        JsonObject toAdd = new JsonObject();
        toAdd.add("userId", new JsonPrimitive(userId));
        toAdd.add("toDos", new JsonArray());
        userObject.add(String.valueOf(channelId), toAdd);
        try {
            FileWriter file = new FileWriter("./data/todo.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(userObject));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getChannelId(){
        return channelId;
    }

    public long getUserId(){
        return userId;
    }

    public ToDo getByMessageId(long messageId){
        for(ToDo toDo : toDos){
            if(toDo.getMessageId() == messageId){
                return toDo;
            }
        }
        return null;
    }

    public static ToDoChannel getByChannelId(long channelId){
        for(ToDoChannel channel : channels) {
            if(channel.getChannelId() == channelId){
                return channel;
            }
        }
        return null;
    }

    public static boolean isToDoChannel(long channelId){
        for(ToDoChannel channel : channels){
            if(channel.getChannelId() == channelId){
                return true;
            }
        }
        return false;
    }

    public static ToDoChannel getChannelByUser(long userId){
        for(ToDoChannel channel : channels){
            if(channel.getUserId() == userId){
                return channel;
            }
        }
        return null;
    }
}
