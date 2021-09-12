package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class MovieCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        boolean isActive = Main.getInstance().getMoviePollManager().isActive();
        if(args.length == 1){
            if(args[0].equals("cancel")){
                if(isActive){
                    Main.getInstance().getMoviePollManager().getActive().end();
                }
                else{
                    EmbedMessage msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.invalid.failed.moviepoll.not-active"),
                            Main.getInstance().getLanguageManager().get("commands.invalid.description.moviepoll.not-active"),
                            Main.getInstance().getErrorColor());
                    t.sendMessage(msg.build()).queue();
                }
            }
            else{
                invalidUsage(m, t);
            }
        }
        else if(args.length >= 2){
            if(isActive){
                if(args[0].equals("search")){
                    String argumentAfterSearch = "";
                }
                else if(args[0].equals("id")){

                }
            }
            else{
                EmbedMessage msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.invalid.failed.moviepoll.not-active"),
                        Main.getInstance().getLanguageManager().get("commands.invalid.description.moviepoll.not-active"),
                        Main.getInstance().getErrorColor());
                t.sendMessage(msg.build()).queue();
            }
        }
    }

    private void invalidUsage(Member m, TextChannel t){
        EmbedMessage msg = new EmbedMessage(m.getUser(),
                Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                Main.getInstance().getLanguageManager().get("commands.invalid.description.moviepoll"),
                Main.getInstance().getErrorColor());
        t.sendMessage(msg.build()).queue();
    }
}
