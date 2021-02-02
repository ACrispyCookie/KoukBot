package me.acrispycookie.utility;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Emote;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ToDo {

    String content;
    long userId;
    long channelId;
    long messageId;

    public ToDo(long userId, long channelId, long messageId){
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        if(Main.getInstance().getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete() != null){
            Main.getInstance().getGuild().getTextChannelById(channelId).addReactionById(messageId, Main.getInstance().getGuild().getEmoteById(801012687806529556L)).queue();
        }
        else{
            delete();
        }
    }

    public ToDo(String content, long userId, long channelId){
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void send(){
        EmbedMessage msg = new EmbedMessage(Main.getInstance().getDiscordUser(userId), "**NEW TO-DO FOR:** __" + Main.getInstance().getDiscordUser(userId).getAsTag() + "__", "Task: " + content +
                "\n\nReact with " + Main.getInstance().getGuild().getEmoteById(801012687806529556L).getAsMention() + " to complete this task!");
        Emote emote = Main.getInstance().getGuild().retrieveEmoteById(801012687806529556L).complete();
        Main.getInstance().getGuild().getTextChannelById(channelId).sendMessage(msg.build()).queue((q) -> {
            q.addReaction(emote).queue();
            this.messageId = q.getIdLong();
            save();
        });
    }

    public void remove(){
        EmbedMessage msg = new EmbedMessage(Main.getInstance().getDiscordUser(userId), "**TO-DO TASK COMPLETED FOR:** __" + Main.getInstance().getDiscordUser(userId).getAsTag() + "__",
                Main.getInstance().getDiscordUser(userId).getAsMention() + " you have completed your task! Deleting...");
        Main.getInstance().getGuild().getTextChannelById(channelId).editMessageById(messageId, msg.build()).queue((q) -> {
            q.delete().queueAfter(10, TimeUnit.SECONDS);
        });
        removeFromConfig();
    }

    public void delete(){
        removeFromConfig();
    }

    private void save(){
        JsonObject userObject = Main.getInstance().getToDoManager().getJson();
        JsonArray toAdd = userObject.getAsJsonObject(String.valueOf(channelId)).getAsJsonArray("toDos");
        toAdd.add(messageId);
        try {
            FileWriter file = new FileWriter("./data/todo.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(userObject));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromConfig(){
        JsonObject object = Main.getInstance().getToDoManager().getJson();
        JsonArray list = object.getAsJsonObject(String.valueOf(channelId)).getAsJsonArray("toDos");
        list.remove(new JsonPrimitive(messageId));
        try {
            FileWriter file = new FileWriter("./data/todo.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getContent(){
        return content;
    }

    public long getUserId(){
        return userId;
    }

    public long getMessageId(){
        return messageId;
    }
}
