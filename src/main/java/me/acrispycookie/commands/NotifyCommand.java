package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotifyCommand extends BotCommand{

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(mentions.size() == 1){
            Member toNotify = mentions.get(0);
            if(m.getVoiceState().getChannel() != null){
                if(toNotify.getVoiceState().getChannel() != null){
                    if(m.getVoiceState().getChannel().equals(toNotify.getVoiceState().getChannel())){
                        if(toNotify.getVoiceState().isDeafened()){
                            notify(toNotify);
                            t.sendMessage(new EmbedMessage(m.getUser(),
                                    Main.getInstance().getLanguageManager().get("commands.success.title.notify"),
                                    Main.getInstance().getLanguageManager().get("commands.success.description.notify"),
                                    Main.getInstance().getBotColor()).build()).queue();
                        }
                        else{
                            t.sendMessage(new EmbedMessage(m.getUser(),
                                    Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-deafened"),
                                    Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-deafened"),
                                    Main.getInstance().getErrorColor()).build()).queue();
                        }
                    }
                    else{
                        t.sendMessage(new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-your-channel"),
                                Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-in-your-channel"),
                                Main.getInstance().getErrorColor()).build()).queue();
                    }
                }
                else{
                    t.sendMessage(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.notify.not-in-channel"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
            else{
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.failed.title.notify.not-in-channel"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.notify.user-not-in-channel"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.notify"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }

    private void notify(Member m){
        if(m.getVoiceState() != null){
            VoiceChannel channel = m.getVoiceState().getChannel();
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
