package me.acrispycookie.school.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import me.acrispycookie.school.classes.ProgramCreatorChannel;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

public class ProgramCreatorManager extends ListenerAdapter {

    Main main;
    Message message;
    JsonObject object;

    public ProgramCreatorManager(Main main, JsonObject object){
        this.main = main;
        this.object = object;
        start();
        loadChannels();
    }

    private void start(){
        TextChannel channel = main.getGuild().getTextChannelById(Long.parseLong(main.getConfigManager().get("features.program-creator.channel")));
        long messageId = Long.parseLong(main.getConfigManager().get("features.program-creator.messageId"));
        if(channel != null){
            channel.retrieveMessageById(messageId).queue((success) -> {
                ProgramCreatorManager.this.message = success;
            }, (failure) -> {
                if (failure instanceof ErrorResponseException) {
                    EmbedMessage msg = new EmbedMessage(Main.getInstance().getGuild().getJDA().getSelfUser(),
                            Main.getInstance().getLanguageManager().get("program-creator.start-message.title"),
                            Main.getInstance().getLanguageManager().get("program-creator.start-message.description"));
                    channel.sendMessageEmbeds(msg.build())
                            .addActionRow(Button.primary("start",
                                            Main.getInstance().getLanguageManager().get("program-creator.start-message.button.text")))
                            .queue((m) -> {
                        ProgramCreatorManager.this.message = m;
                        main.getConfigManager().set("features.program-creator.messageId", new JsonPrimitive(m.getIdLong()));
                    });
                }
            });
        }
        else {
            main.getGuild().createTextChannel(Main.getInstance().getConfigManager().get("features.program-creator.channelName"), main.getGuild().getCategoryById(main.getConfigManager().get("features.announcer.schoolCategory"))).queue((q) -> {
                EmbedMessage msg = new EmbedMessage(Main.getInstance().getGuild().getJDA().getSelfUser(),
                        Main.getInstance().getLanguageManager().get("program-creator.start-message.title"),
                        Main.getInstance().getLanguageManager().get("program-creator.start-message.description"));
                q.sendMessageEmbeds(msg.build())
                        .addActionRow(Button.primary("start",
                                        Main.getInstance().getLanguageManager().get("program-creator.start-message.button.text")))
                        .queue((m) -> {
                    main.getConfigManager().set("features.program-creator.channel", new JsonPrimitive(q.getIdLong()));
                    main.getConfigManager().set("features.program-creator.messageId", new JsonPrimitive(m.getIdLong()));
                    ProgramCreatorManager.this.message = m;
                });
            });
        }
    }

    private void loadChannels(){
        for(String key : object.keySet()){
            new ProgramCreatorChannel(main, Long.parseLong(key),
                    object.getAsJsonObject(key).get("channel").getAsLong(),
                    object.getAsJsonObject(key).get("message").getAsLong(),
                    object.getAsJsonObject(key).get("stage").getAsInt(),
                    object.getAsJsonObject(key).get("maxStageReached").getAsInt(),
                    object.getAsJsonObject(key).getAsJsonObject("data"));
        }
    }

    public void saveChannel(ProgramCreatorChannel channel){
        JsonObject data = channel.getData();
        JsonObject finished = new JsonObject();
        finished.add("channel", new JsonPrimitive(channel.getMessage().getChannel().getIdLong()));
        finished.add("message", new JsonPrimitive(channel.getMessage().getIdLong()));
        finished.add("maxStageReached", new JsonPrimitive(channel.getMaxStage()));
        finished.add("stage", new JsonPrimitive(channel.getStage()));
        finished.add("data", data);
        object.add(String.valueOf(channel.getUser().getIdLong()), finished);
        save();
    }

    public void deleteChannel(ProgramCreatorChannel channel){
        object.remove(channel.getUser().getId());
        save();
    }

    private void save(){
        try {
            FileWriter file = new FileWriter("./data/program_creator.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        if(e.getMessageIdLong() == message.getIdLong() && e.getComponentId().equalsIgnoreCase("start")){
            ProgramCreatorChannel channel = ProgramCreatorChannel.getChannelByUser(e.getUser().getIdLong());
            if(channel == null){
                e.deferReply().setEphemeral(true).queue();
                new ProgramCreatorChannel(main, e.getUser(), e.getHook());
            }
            else {
                EmbedMessage msg = new EmbedMessage(Main.getInstance().getGuild().getJDA().getSelfUser(),
                        Main.getInstance().getLanguageManager().get("program-creator.already-creating.title"),
                        Main.getInstance().getLanguageManager().get("program-creator.already-creating.description"), Main.getInstance().getErrorColor());
                e.replyEmbeds(msg.build()).setEphemeral(true).queue();
            }
        }
    }
}
