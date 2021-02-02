package me.acrispycookie.commands;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BotCommand extends ListenerAdapter {

    public abstract void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message);

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        Member m = e.getMember();
        if(!m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())){
            if(e.getMessage().getContentRaw().startsWith(Main.getInstance().getPrefix()) && e.getMessage().getAttachments().size() == 0){
                String msg = e.getMessage().getContentRaw();
                String noPrefixMsg = msg.substring(Main.getInstance().getPrefix().length());
                String commandBody = noPrefixMsg.contains(" ") ? noPrefixMsg.substring(0, noPrefixMsg.indexOf(' ')) : noPrefixMsg;
                String[] args = noPrefixMsg.contains(" ") ? findArguments(noPrefixMsg.substring(noPrefixMsg.indexOf(' '))) : new String[] {};
                Command command = Command.getByAlias(commandBody);
                if(command != null){
                    command.getBotCommand().execute(args, commandBody, e.getMember(), e.getChannel(), e.getMessage().getMentionedMembers(), e.getMessage().getMentionedRoles(), e.getMessage().getAttachments(), e.getMessage());
                }
            }
        }
    }

    private static String[] findArguments(String msg){
        ArrayList<String> stringArrayList = new ArrayList<>();
        for(int i = 0; i < msg.length(); i++){
            char c = msg.charAt(i);
            if(c != ' '){
                String argument = "";
                while(true){
                    c = msg.charAt(i);
                    argument = argument.concat(String.valueOf(c));
                    i++;
                    if(i >= msg.length()){
                        break;
                    }
                    c = msg.charAt(i);
                    if(c == ' '){
                        break;
                    }
                }
                stringArrayList.add(argument);
            }
        }
        return stringArrayList.toArray(new String[] {});
    }

}
