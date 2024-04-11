package dev.acrispycookie.commands;

import dev.acrispycookie.Main;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PlayCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().queue();
        String search = e.getOption("search").getAsString();
        if(Main.getInstance().getMusicManager().isPlaying()){
            if(m.getVoiceState().inAudioChannel()){
                if(m.getVoiceState().getChannel().asVoiceChannel().equals(Main.getInstance().getMusicManager().getChannel())){
                    Main.getInstance().getMusicManager().add(search, m.getUser(), m.getVoiceState().getChannel().asVoiceChannel(), e);
                }
                else{
                    e.replyEmbeds(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-your-channel"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-your-channel"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
            else{
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-channel"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-channel"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            if(m.getVoiceState().inAudioChannel()){
                Main.getInstance().getMusicManager().add(search, m.getUser(), m.getVoiceState().getChannel().asVoiceChannel(), e);
            }
            else{
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-channel"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-channel"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
    }
}
