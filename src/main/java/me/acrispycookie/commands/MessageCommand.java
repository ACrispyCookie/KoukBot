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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(Perm.hasPermission(m, Perm.MESSAGE)){
            message.delete().queue();
            if(Utils.isInt(args[0]) && m.getGuild().getTextChannelById(args[0]) != null){
                TextChannel textChannel = m.getGuild().getTextChannelById(args[0]);
                ArrayList<String> newArgs = new ArrayList<>(Arrays.asList(args));
                newArgs.remove(0);
                String msg = newArgs.stream().map(Objects::toString).collect(Collectors.joining(" "));
                textChannel.sendMessage(msg).queue();
            }
            else{
                String msg = Arrays.stream(args).map(Objects::toString).collect(Collectors.joining(" "));
                t.sendMessage(msg).queue();
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
