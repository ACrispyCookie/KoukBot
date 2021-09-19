package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class PlayCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(args.length > 0){
            String query = String.join(" ", args);
            if(Main.getInstance().getMusicManager().isPlaying()){
                if(m.getVoiceState().inVoiceChannel()){
                    if(m.getVoiceState().getChannel().equals(Main.getInstance().getMusicManager().getChannel())){
                        Main.getInstance().getMusicManager().add(query, m.getUser(), m.getVoiceState().getChannel(), t);
                    }
                    else{
                        t.sendMessage(new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-your-channel"),
                                Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-your-channel"),
                                Main.getInstance().getErrorColor()).build()).queue();
                    }
                }
                else{
                    t.sendMessage(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-channel"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-channel"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
            else{
                if(m.getVoiceState().inVoiceChannel()){
                    Main.getInstance().getMusicManager().add(query, m.getUser(), m.getVoiceState().getChannel(), t);
                }
                else{
                    t.sendMessage(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.play.not-in-channel"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.play.not-in-channel"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
        }
        else{
            Main.getInstance().getMusicManager().resume(m.getUser(), t);
        }
    }
}
