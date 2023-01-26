package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.concurrent.TimeUnit;

public class NotifyCommand extends BotCommand{

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        Member toNotify = e.getOption("user").getAsMember();
        if(m.getVoiceState().getChannel() != null){
            if(toNotify.getVoiceState().getChannel() != null){
                if(m.getVoiceState().getChannel().equals(toNotify.getVoiceState().getChannel())){
                    if(toNotify.getVoiceState().isDeafened()){
                        notify(toNotify);
                        e.replyEmbeds(new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.success.title.notify"),
                                Main.getInstance().getLanguageManager().get("commands.success.description.notify"),
                                Main.getInstance().getBotColor()).build()).queue();
                    }
                    else{
                        e.replyEmbeds(new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-deafened"),
                                Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-deafened"),
                                Main.getInstance().getErrorColor()).build()).queue();
                    }
                }
                else{
                    e.replyEmbeds(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-your-channel"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-in-your-channel"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
            else{
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-in-channel"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.notify.user-not-in-channel"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    private void notify(Member m){
        if(m.getVoiceState() != null){
            VoiceChannel channel = m.getVoiceState().getChannel().asVoiceChannel();
            VoiceChannel random = null;
            for(VoiceChannel channel1 : Main.getInstance().getGuild().getVoiceChannels()){
                if(!channel.equals(channel1)){
                    random = channel1;
                }
            }
            if(random != null){
                Main.getInstance().getGuild().moveVoiceMember(m, random).queue();
                Main.getInstance().getGuild().moveVoiceMember(m, channel).queueAfter(2, TimeUnit.SECONDS);
            }
        }
    }
}
