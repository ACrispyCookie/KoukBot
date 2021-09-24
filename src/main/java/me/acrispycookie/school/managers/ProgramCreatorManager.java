package me.acrispycookie.school.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import me.acrispycookie.school.classes.ProgramCreatorChannel;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
            channel.retrieveMessageById(messageId).queue((m) -> {
                m.clearReactions().queue((mm) -> {
                    m.addReaction("📆").queue();
                });
                ProgramCreatorManager.this.message = m;
            }, (failure) -> {
                if (failure instanceof ErrorResponseException) {
                    EmbedMessage msg = new EmbedMessage(Main.getInstance().getDiscordUser(764939013777784843L), "Δημιούργησε το ψηφιακό σου πρόγραμμα!", main.getGuild().getRoleById(867487114508501022L).getAsMention() +
                            "! Αυτό είναι ειδικά για εσένα!\nΒάλε το εβδομαδιαίο σου πρόγραμμα και πάρε\nένα ψηφιακό αντίγραφο του!");
                    channel.sendMessageEmbeds(msg.build()).queue((m) -> {
                        m.addReaction("📆").queue();
                        ProgramCreatorManager.this.message = m;
                        main.getConfigManager().set("features.program-creator.messageId", new JsonPrimitive(m.getIdLong()));
                    });
                }
            });
        }
        else {
            main.getGuild().createTextChannel("get-your-program", main.getGuild().getCategoryById(main.getConfigManager().get("features.announcer.schoolCategory"))).queue((q) -> {
                EmbedMessage msg = new EmbedMessage(Main.getInstance().getDiscordUser(764939013777784843L), "Δημιούργησε το ψηφιακό σου πρόγραμμα!", main.getGuild().getRoleById(867487114508501022L).getAsMention() +
                        "! Αυτό είναι ειδικά για εσένα!\nΒάλε το εβδομαδιαίο σου πρόγραμμα και πάρε\nένα ψηφιακό αντίγραφο του!");
                q.sendMessageEmbeds(msg.build()).queue((m) -> {
                    m.addReaction("📆").queue();
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
        finished.add("channel", new JsonPrimitive(channel.getMessage().getTextChannel().getIdLong()));
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
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if(e.getMessageIdLong() == message.getIdLong() && !e.getUser().equals(e.getJDA().getSelfUser())){
            e.getReaction().removeReaction(e.getUser()).queue();
            ProgramCreatorChannel channel = ProgramCreatorChannel.getChannelByUser(e.getUserIdLong());
            if(channel == null){
                new ProgramCreatorChannel(main, e.getUser());
            }
            else {
                channel.getMessage().getTextChannel().sendMessage(e.getUser().getAsMention() + "! Δημιουργείς ήδη αυτό το πρόγραμμα!").queue((s) -> {
                    s.delete().queueAfter(15, TimeUnit.SECONDS);
                });
            }
        }
    }
}
