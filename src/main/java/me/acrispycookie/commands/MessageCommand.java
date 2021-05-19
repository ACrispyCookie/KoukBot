package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(Perm.hasPermission(m, Perm.MESSAGE)){
            String msg = Arrays.stream(args).map(Objects::toString).collect(Collectors.joining("\n"));
            t.sendMessage(msg).queue();
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
