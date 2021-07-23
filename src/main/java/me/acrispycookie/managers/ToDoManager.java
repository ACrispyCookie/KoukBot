package me.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.acrispycookie.utility.ToDo;
import me.acrispycookie.utility.ToDoChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class ToDoManager extends ListenerAdapter {

    JsonObject object;

    public ToDoManager(JsonObject object) {
        this.object = object;
        if(object != null){
            loadToDos();
        }
    }

    private void loadToDos(){
        for(String obj : object.keySet()){
            JsonObject json = object.getAsJsonObject(obj);
            long userId = json.get("userId").getAsLong();
            JsonArray toDos = json.get("toDos").getAsJsonArray();
            ArrayList<ToDo> toDoList = new ArrayList<>();
            for(int i = 0; i < toDos.size(); i++){
                ToDo s = new ToDo(userId, Long.parseLong(obj), toDos.get(i).getAsLong());
                toDoList.add(s);
            }
            new ToDoChannel(userId, Long.parseLong(obj), toDoList);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        ToDoChannel channel = ToDoChannel.getByChannelId(e.getTextChannel().getIdLong());
        if(channel != null) {
            ToDo todo = channel.getByMessageId(e.getMessageIdLong());
            if(todo != null){
                if(e.getUserIdLong() == todo.getUserId()){
                    if(e.getReactionEmote().getIdLong() == 801012687806529556L){
                        todo.remove();
                    }
                }
                else if(!e.getUser().equals(e.getJDA().getSelfUser())){
                    e.getReaction().removeReaction(e.getUser()).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()) != null) {
            if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()).getByMessageId(e.getMessageIdLong()) != null){
                if(e.getReactionEmote().getIdLong() == 801012687806529556L){
                    e.getTextChannel().addReactionById(e.getMessageIdLong(), e.getGuild().getEmoteById(801012687806529556L)).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent e) {
        if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()) != null) {
            if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()).getByMessageId(e.getMessageIdLong()) != null){
                e.getTextChannel().addReactionById(e.getMessageIdLong(), e.getGuild().getEmoteById(801012687806529556L)).queue();
            }
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent e) {
        if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()) != null) {
            if(ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()).getByMessageId(e.getMessageIdLong()) != null){
                ToDo todo = ToDoChannel.getByChannelId(e.getTextChannel().getIdLong()).getByMessageId(e.getMessageIdLong());
                todo.delete();
            }
        }
    }

    public JsonObject getJson(){
        return object;
    }
}
