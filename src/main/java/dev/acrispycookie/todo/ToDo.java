package dev.acrispycookie.todo;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.FileWriter;
import java.io.IOException;

public class ToDo {

    private final KoukBot bot;
    private final long userId;
    private final long channelId;
    private String content;
    private long messageId;

    public ToDo(KoukBot bot, long userId, long channelId, long messageId) {
        this.bot = bot;
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        if (bot.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete() == null) {
            delete();
        }
    }

    public ToDo(KoukBot bot, String content, long userId, long channelId) {
        this.bot = bot;
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void send(SlashCommandInteractionEvent e, boolean reply) {
        EmbedMessage msg = new EmbedMessage(bot, bot.getDiscordUser(userId),
                bot.getLanguageManager().get("todo.new.title", bot.getDiscordUser(userId).getAsTag()),
                bot.getLanguageManager().get("todo.new.description", content), bot.getBotColor());
        if (reply) {
            e.getHook().sendMessageEmbeds(msg.build())
                    .addActionRow(Button.success("success",
                                    bot.getLanguageManager().get("todo.new.button.text"))
                            .withEmoji(Emoji.fromUnicode(bot.getLanguageManager().get("todo.new.button.emoji"))))
                    .queue((q) -> {
                        this.messageId = q.getIdLong();
                        save();
                    });
        } else {
            bot.getGuild().getTextChannelById(channelId).sendMessageEmbeds(msg.build())
                    .addActionRow(Button.success("success",
                                    bot.getLanguageManager().get("todo.new.button.text"))
                            .withEmoji(Emoji.fromUnicode(bot.getLanguageManager().get("todo.new.button.emoji"))))
                    .queue((q) -> {
                        this.messageId = q.getIdLong();
                        save();
                    });
        }
    }

    public void delete() {
        removeFromConfig();
    }

    private void save() {
        JsonObject userObject = bot.getToDoManager().getJson();
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

    private void removeFromConfig() {
        JsonObject object = bot.getToDoManager().getJson();
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

    public String getContent() {
        return content;
    }

    public long getUserId() {
        return userId;
    }

    public long getMessageId() {
        return messageId;
    }
}
