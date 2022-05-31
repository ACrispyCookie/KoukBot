package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SetPrefixCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(Perm.hasPermission(m, Perm.SET_PREFIX)){
            if(args.length == 1 && mentionedRoles.size() == 0 && mentions.size() == 0){
                String prefix = args[0];
                Main.getInstance().getConfigManager().changePrefix(prefix);
                EmbedMessage msg = new EmbedMessage(m.getUser(),
                        "The prefix has been changed!",
                        "The new prefix will now be `" + prefix + "`",
                        Main.getInstance().getBotColor());
                t.sendMessage(msg.build()).queue();
            }
            else{
                EmbedMessage msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                        Main.getInstance().getLanguageManager().get("commands.invalid.description.setprefix"),
                        Main.getInstance().getErrorColor());
                t.sendMessage(msg.build()).queue();
            }
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.SET_PREFIX.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
