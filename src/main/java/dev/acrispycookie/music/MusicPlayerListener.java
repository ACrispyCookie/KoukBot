package dev.acrispycookie.music;

import dev.acrispycookie.KoukBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class MusicPlayerListener extends ListenerAdapter {

    private final KoukBot bot;

    public MusicPlayerListener(KoukBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
        if (e.getMember().getUser() == e.getJDA().getSelfUser()) {
            if (e.getChannelJoined() == null)
                bot.getMusicManager().stop();
            else if (e.getChannelLeft() != null)
                bot.getMusicManager().setVoiceChannel(e.getChannelJoined().asVoiceChannel());
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if (e.getButton().getId() != null && e.getMessage().getIdLong() == bot.getMusicManager().getStatusMessageId()) {
            if (!e.getButton().getId().equals("addSong"))
                e.deferEdit().queue();

            switch (e.getButton().getId()) {
                case "pause" -> bot.getMusicManager().pause(e.getHook(), e.getUser());
                case "play" -> bot.getMusicManager().resume(e.getHook(), e.getUser());
                case "stop" -> bot.getMusicManager().stop(e.getHook(), e.getUser());
                case "next" -> bot.getMusicManager().next(e.getHook(), e.getUser());
                case "previous" -> bot.getMusicManager().prev(e.getHook(), e.getUser());
                case "repeat" -> bot.getMusicManager().toggleRepeatMode(e.getUser());
                case "shuffle" -> bot.getMusicManager().toggleShuffle(e.getUser());
                case "addSong" -> e.replyModal(Modal.create("addSong", "Add a song")
                        .addComponents(ActionRow.of(getSongInput())).build()).queue();
                default -> {
                    // Do nothing
                }
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent e) {
        if (e.getMessage().getIdLong() == bot.getMusicManager().getStatusMessageId()) {
            e.deferEdit().queue();

            int selected;
            try {
                String title = e.getSelectedOptions().get(0).getValue();
                selected = Integer.parseInt(title.substring(0, title.indexOf('.'))) - 1;
            } catch (NumberFormatException exception) {
                return;
            }

            if (e.getComponentId().equals("songSelect")) {
                bot.getMusicManager().select(e.getHook(), e.getUser(), selected);
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals("addSong") && e.getValue("songInput") != null) {
            String songName = e.getValue("songInput").getAsString();
            if (!songName.isEmpty()) {
                e.deferReply(true).queue();
                bot.getMusicManager().add(e.getHook(), e.getUser(), songName);
            } else {
                e.deferEdit().queue();
            }
        }
    }

    private TextInput getSongInput() {
        return TextInput.create("songInput", "Enter a song name or URL", TextInputStyle.SHORT)
                .setPlaceholder("e.g. https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .setRequired(false).build();
    }
}
