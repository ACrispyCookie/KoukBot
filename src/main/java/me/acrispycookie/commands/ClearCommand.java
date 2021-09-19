package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends BotCommand {


    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(Perm.hasPermission(m, Perm.CLEAR)){
            if(args.length == 1){
                if(Utils.isInt(args[0])){
                    t.getHistory().retrievePast(Integer.parseInt(args[0]) + 1).complete().forEach(ms -> ms.delete().queue());
                    t.sendMessage(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.success.title.clear"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.clear", args[0]),
                            Main.getInstance().getBotColor()).build()).queue(after -> {
                        after.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                }
                else{
                    t.sendMessage(new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                            Main.getInstance().getLanguageManager().get("commands.invalid.description.clear"),
                            Main.getInstance().getErrorColor()).build()).queue();
                }
            }
            else{
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                        Main.getInstance().getLanguageManager().get("commands.invalid.description.clear"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
