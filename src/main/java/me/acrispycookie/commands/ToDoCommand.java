package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.ToDo;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.ToDoChannel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ToDoCommand extends BotCommand {

    public ToDoCommand(){
        super();
    }

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(args.length > 0){
            String st = message.getContentRaw().substring(1 + label.length());
            if(ToDoChannel.isToDoChannel(t.getIdLong())) {
                ToDo toDo = new ToDo(st, m.getUser().getIdLong(), t.getIdLong());
                toDo.send();
            }
            else if(ToDoChannel.getChannelByUser(m.getUser().getIdLong()) != null){
                ToDo toDo = new ToDo(st, m.getUser().getIdLong(), ToDoChannel.getChannelByUser(m.getUser().getIdLong()).getChannelId());
                toDo.send();
            }
            else{
                Collection<Permission> allowed = new ArrayList<>();
                allowed.add(Permission.MESSAGE_WRITE);
                allowed.add(Permission.VIEW_CHANNEL);
                Collection<Permission> denied = new ArrayList<>();
                denied.add(Permission.MESSAGE_WRITE);
                denied.add(Permission.VIEW_CHANNEL);
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                AtomicLong channelId = new AtomicLong(0);
                Main.getInstance().getGuild().createTextChannel(Main.getInstance().getDiscordUser(m.getIdLong()).getName()).setParent(Main.getInstance().getGuild().getCategoryById(803289238498050058L))
                        .addMemberPermissionOverride(m.getIdLong(), allowed, null).addPermissionOverride(Main.getInstance().getGuild().getPublicRole(), null, denied).queue((q) -> {
                    new ToDoChannel(m.getIdLong(), q.getIdLong());
                    channelId.set(q.getIdLong());
                    atomicBoolean.set(true);
                });
                while (true){
                    if(atomicBoolean.get()){
                        break;
                    }
                }
                ToDo toDo = new ToDo(st, m.getUser().getIdLong(), channelId.get());
                toDo.send();
            }
            message.delete().queue();
        }
        else{
            EmbedMessage msg = new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.todo"),
                    Main.getInstance().getErrorColor());
            t.sendMessage(msg.build()).queue();
        }
    }
}
