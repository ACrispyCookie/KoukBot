package dev.acrispycookie.school.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.managers.FeatureManager;
import dev.acrispycookie.school.classes.ProgramCreatorChannel;
import dev.acrispycookie.utility.ConfigurationFile;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class ProgramCreatorManager extends FeatureManager {

    private Message message;
    private final ConfigurationFile file = ConfigurationFile.PROGRAM_CREATOR;

    public ProgramCreatorManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {
        bot.getBot().addEventListener(new ButtonListener());
        start();
        loadChannels();
    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {
        file.reloadJson();
    }

    private void start() {
        TextChannel channel = bot.getGuild().getTextChannelById(Long.parseLong(bot.getConfigManager().get("features.program-creator.channel")));
        long messageId = Long.parseLong(bot.getConfigManager().get("features.program-creator.messageId"));
        if (channel != null) {
            channel.retrieveMessageById(messageId).queue((success) -> {
                ProgramCreatorManager.this.message = success;
            }, (failure) -> {
                if (failure instanceof ErrorResponseException) {
                    EmbedMessage msg = new EmbedMessage(bot, bot.getGuild().getJDA().getSelfUser(),
                            bot.getLanguageManager().get("program-creator.start-message.title"),
                            bot.getLanguageManager().get("program-creator.start-message.description"));
                    channel.sendMessageEmbeds(msg.build())
                            .addActionRow(Button.primary("start",
                                            bot.getLanguageManager().get("program-creator.start-message.button.text")))
                            .queue((m) -> {
                        ProgramCreatorManager.this.message = m;
                        bot.getConfigManager().set("features.program-creator.messageId", new JsonPrimitive(m.getIdLong()));
                    });
                }
            });
        }
        else {
            bot.getGuild().createTextChannel(bot.getConfigManager().get("features.program-creator.channelName"), bot.getGuild().getCategoryById(bot.getConfigManager().get("features.announcer.schoolCategory"))).queue((q) -> {
                EmbedMessage msg = new EmbedMessage(bot, bot.getGuild().getJDA().getSelfUser(),
                        bot.getLanguageManager().get("program-creator.start-message.title"),
                        bot.getLanguageManager().get("program-creator.start-message.description"));
                q.sendMessageEmbeds(msg.build())
                        .addActionRow(Button.primary("start",
                                        bot.getLanguageManager().get("program-creator.start-message.button.text")))
                        .queue((m) -> {
                    bot.getConfigManager().set("features.program-creator.channel", new JsonPrimitive(q.getIdLong()));
                    bot.getConfigManager().set("features.program-creator.messageId", new JsonPrimitive(m.getIdLong()));
                    ProgramCreatorManager.this.message = m;
                });
            });
        }
    }

    private void loadChannels() {
        JsonObject object = file.getJson();
        for (String key : file.getJson().keySet()) {
            new ProgramCreatorChannel(bot, Long.parseLong(key),
                    object.getAsJsonObject(key).get("channel").getAsLong(),
                    object.getAsJsonObject(key).get("message").getAsLong(),
                    object.getAsJsonObject(key).get("stage").getAsInt(),
                    object.getAsJsonObject(key).get("maxStageReached").getAsInt(),
                    object.getAsJsonObject(key).getAsJsonObject("data"));
        }
    }

    public void saveChannel(ProgramCreatorChannel channel) {
        JsonObject data = channel.getData();
        JsonObject finished = new JsonObject();
        finished.add("channel", new JsonPrimitive(channel.getMessage().getChannel().getIdLong()));
        finished.add("message", new JsonPrimitive(channel.getMessage().getIdLong()));
        finished.add("maxStageReached", new JsonPrimitive(channel.getMaxStage()));
        finished.add("stage", new JsonPrimitive(channel.getStage()));
        finished.add("data", data);
        file.setElement(String.valueOf(channel.getUser().getIdLong()), finished);
    }

    public void deleteChannel(ProgramCreatorChannel channel) {
        file.removeElement(channel.getUser().getId());
    }

    private class ButtonListener extends ListenerAdapter {
        @Override
        public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
            if (e.getMessageIdLong() == message.getIdLong() && e.getComponentId().equalsIgnoreCase("start")) {
                ProgramCreatorChannel channel = ProgramCreatorChannel.getChannelByUser(e.getUser().getIdLong());
                if (channel == null) {
                    e.deferReply().setEphemeral(true).queue();
                    new ProgramCreatorChannel(bot, e.getUser(), e.getHook());
                }
                else {
                    EmbedMessage msg = new EmbedMessage(bot, bot.getGuild().getJDA().getSelfUser(),
                            bot.getLanguageManager().get("program-creator.already-creating.title"),
                            bot.getLanguageManager().get("program-creator.already-creating.description"), bot.getErrorColor());
                    e.replyEmbeds(msg.build()).setEphemeral(true).queue();
                }
            }
        }
    }
}
