package me.acrispycookie.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Console;
import me.acrispycookie.Main;
import me.acrispycookie.managers.todo.ToDo;
import me.acrispycookie.managers.todo.ToDoChannel;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileWriter;
import java.io.IOException;
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
        ArrayList<String> channelsToRemove = new ArrayList<>();
        for(String obj : object.keySet()){
            ArrayList<Integer> messagesToRemove = new ArrayList<>();
            if(!channelExists(Long.parseLong(obj))
                    || !isMember(object.getAsJsonObject(obj).get("userId").getAsLong())) {
                channelsToRemove.add(obj);
                continue;
            }
            JsonObject json = object.getAsJsonObject(obj);
            long userId = json.get("userId").getAsLong();
            JsonArray toDos = json.get("toDos").getAsJsonArray();
            ArrayList<ToDo> toDoList = new ArrayList<>();
            for(int i = 0; i < toDos.size(); i++){
                if(!messageExists(Long.parseLong(obj), toDos.get(i).getAsLong())) {
                    messagesToRemove.add(i);
                    continue;
                }
                ToDo s = new ToDo(userId, Long.parseLong(obj), toDos.get(i).getAsLong());
                toDoList.add(s);
            }
            new ToDoChannel(userId, Long.parseLong(obj), toDoList);
            for(int i : messagesToRemove) {
                toDos.remove(i);
            }
        }
        for(String s : channelsToRemove) {
            if(!isMember(object.getAsJsonObject(s).get("userId").getAsLong()) && channelExists(Long.parseLong(s))) {
                Main.getInstance().getGuild().getTextChannelById(Long.parseLong(s)).delete().queue();
            }
            object.remove(s);
        }
        save();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        ToDoChannel channel = ToDoChannel.getByChannelId(e.getChannel().getIdLong());
        if(channel != null) {
            ToDo todo = channel.getByMessageId(e.getMessageIdLong());
            if(todo != null){
                if(e.getUser().getIdLong() == todo.getUserId()){
                    if(e.getComponentId().equalsIgnoreCase("success")){
                        e.replyEmbeds(new EmbedMessage(e.getUser(),
                                        Main.getInstance().getLanguageManager().get("todo.completed.title"),
                                        Main.getInstance().getLanguageManager().get("todo.completed.description"),
                                        Main.getInstance().getBotColor()).build())
                                .setEphemeral(true).queue((q) -> {
                                    todo.delete();
                                    e.getMessage().delete().queue();
                                });
                    }
                }
                else {
                    e.replyEmbeds(new EmbedMessage(e.getUser(),
                                    Main.getInstance().getLanguageManager().get("todo.not-your-task.title"),
                                    Main.getInstance().getLanguageManager().get("todo.not-your-task.description"),
                                    Main.getInstance().getBotColor()).build())
                            .setEphemeral(true).queue();
                }
            }
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent e) {
        if(ToDoChannel.getByChannelId(e.getChannel().getIdLong()) != null) {
            if(ToDoChannel.getByChannelId(e.getChannel().getIdLong()).getByMessageId(e.getMessageIdLong()) != null){
                ToDo todo = ToDoChannel.getByChannelId(e.getChannel().getIdLong()).getByMessageId(e.getMessageIdLong());
                todo.delete();
            }
        }
    }

    public JsonObject getJson(){
        return object;
    }

    private void save(){
        try {
            FileWriter file = new FileWriter("./data/todo.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isMember(long id) {
        try {
            Main.getInstance().getGuild().isMember(Main.getInstance().getDiscordUser(id));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private boolean channelExists(long id) {
        return Main.getInstance().getGuild().getTextChannelById(id) != null;
    }

    private boolean messageExists(long channelId, long messageId) {
        try {
            Main.getInstance().getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
