package dev.acrispycookie.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.FeatureManager;
import dev.acrispycookie.utility.ConfigurationFile;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class ToDoManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.TODO;

    public ToDoManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        bot.getBot().addEventListener(new ToDoListener());
        loadToDos();
    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {
        file.reloadJson();
    }

    private void loadToDos() {
        ArrayList<String> channelsToRemove = new ArrayList<>();
        JsonObject object = file.getJson();
        for (String obj : object.keySet()) {
            ArrayList<Integer> messagesToRemove = new ArrayList<>();
            if (!channelExists(Long.parseLong(obj))
                    || !isMember(object.getAsJsonObject(obj).get("userId").getAsLong())) {
                channelsToRemove.add(obj);
                continue;
            }
            JsonObject json = object.getAsJsonObject(obj);
            long userId = json.get("userId").getAsLong();
            JsonArray toDos = json.get("toDos").getAsJsonArray();
            ArrayList<ToDo> toDoList = new ArrayList<>();
            for (int i = 0; i < toDos.size(); i++) {
                if (!messageExists(Long.parseLong(obj), toDos.get(i).getAsLong())) {
                    messagesToRemove.add(i);
                    continue;
                }
                ToDo s = new ToDo(bot, userId, Long.parseLong(obj), toDos.get(i).getAsLong());
                toDoList.add(s);
            }
            new ToDoChannel(bot, userId, Long.parseLong(obj), toDoList);
            for (int i : messagesToRemove) {
                toDos.remove(i);
            }
        }

        for (String s : channelsToRemove) {
            if (!isMember(object.getAsJsonObject(s).get("userId").getAsLong()) && channelExists(Long.parseLong(s))) {
                bot.getGuild().getTextChannelById(Long.parseLong(s)).delete().queue();
            }
            object.remove(s);
        }

        file.setJson(object);
    }

    private boolean isMember(long id) {
        try {
            bot.getGuild().isMember(bot.getDiscordUser(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean channelExists(long id) {
        return bot.getGuild().getTextChannelById(id) != null;
    }

    private boolean messageExists(long channelId, long messageId) {
        try {
            bot.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JsonObject getJson() {
        return file.getJson();
    }

    private class ToDoListener extends ListenerAdapter {
        @Override
        public void onButtonInteraction(ButtonInteractionEvent e) {
            ToDoChannel channel = ToDoChannel.getByChannelId(e.getChannel().getIdLong());
            if (channel != null) {
                ToDo todo = channel.getByMessageId(e.getMessageIdLong());
                if (todo != null) {
                    if (e.getUser().getIdLong() == todo.getUserId()) {
                        if (e.getComponentId().equalsIgnoreCase("success")) {
                            e.replyEmbeds(new EmbedMessage(bot, e.getUser(),
                                            bot.getLanguageManager().get("todo.completed.title"),
                                            bot.getLanguageManager().get("todo.completed.description"),
                                            bot.getBotColor()).build())
                                    .setEphemeral(true).queue((q) -> {
                                        todo.delete();
                                        e.getMessage().delete().queue();
                                    });
                        }
                    }
                    else {
                        e.replyEmbeds(new EmbedMessage(bot, e.getUser(),
                                        bot.getLanguageManager().get("todo.not-your-task.title"),
                                        bot.getLanguageManager().get("todo.not-your-task.description"),
                                        bot.getBotColor()).build())
                                .setEphemeral(true).queue();
                    }
                }
            }
        }

        @Override
        public void onMessageDelete(MessageDeleteEvent e) {
            if (ToDoChannel.getByChannelId(e.getChannel().getIdLong()) != null) {
                if (ToDoChannel.getByChannelId(e.getChannel().getIdLong()).getByMessageId(e.getMessageIdLong()) != null) {
                    ToDo todo = ToDoChannel.getByChannelId(e.getChannel().getIdLong()).getByMessageId(e.getMessageIdLong());
                    todo.delete();
                }
            }
        }
    }
}
