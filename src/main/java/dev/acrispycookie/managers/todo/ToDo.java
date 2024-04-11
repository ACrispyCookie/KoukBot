package dev.acrispycookie.managers.todo;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.Main;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.FileWriter;
import java.io.IOException;

public class ToDo {

    String content;
    long userId;
    long channelId;
    long messageId;

    public ToDo(long userId, long channelId, long messageId){
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        if(Main.getInstance().getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete() == null){
            delete();
        }
    }

    public ToDo(String content, long userId, long channelId){
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void send(SlashCommandInteractionEvent e, boolean reply){
        EmbedMessage msg = new EmbedMessage(Main.getInstance().getDiscordUser(userId),
                Main.getInstance().getLanguageManager().get("todo.new.title", Main.getInstance().getDiscordUser(userId).getAsTag()),
                Main.getInstance().getLanguageManager().get("todo.new.description", content), Main.getInstance().getBotColor());
        if(reply) {
            e.getHook().sendMessageEmbeds(msg.build())
                    .addActionRow(Button.success("success",
                                    Main.getInstance().getLanguageManager().get("todo.new.button.text"))
                            .withEmoji(Emoji.fromUnicode(Main.getInstance().getLanguageManager().get("todo.new.button.emoji"))))
                    .queue((q) -> {
                        this.messageId = q.getIdLong();
                        save();
                    });
        } else {
            Main.getInstance().getGuild().getTextChannelById(channelId).sendMessageEmbeds(msg.build())
                    .addActionRow(Button.success("success",
                                    Main.getInstance().getLanguageManager().get("todo.new.button.text"))
                            .withEmoji(Emoji.fromUnicode(Main.getInstance().getLanguageManager().get("todo.new.button.emoji"))))
                    .queue((q) -> {
                        this.messageId = q.getIdLong();
                        save();
                    });
        }
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
