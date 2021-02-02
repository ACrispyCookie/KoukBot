package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SetPermCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(args.length == 3 && Perm.getByName(args[0]) != null && mentionedRoles.size() > 0 && (args[2].equalsIgnoreCase("allow") || args[2].equalsIgnoreCase("deny") || args[2].equalsIgnoreCase("clear"))){
            EmbedMessage msg;
            if(args[2].equalsIgnoreCase("allow")){
                if(!Main.getInstance().getPermissionManager().getAllowedRoleList(Perm.getByName(args[0])).contains(mentionedRoles.get(0).getIdLong())){
                    Main.getInstance().getPermissionManager().addRoleToPermission(mentionedRoles.get(0), Perm.getByName(args[0]));
                    msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.setperm.allow", Perm.getByName(args[0]).name(), mentionedRoles.get(0).getAsMention()),
                            Main.getInstance().getBotColor());
                }
                else{
                    msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.setperm.alreadyHas"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.setperm.alreadyHas"),
                            Main.getInstance().getBotColor());
                }
            }
            else if(args[2].equalsIgnoreCase("deny")) {
                Main.getInstance().getPermissionManager().removeRoleFromPermission(mentionedRoles.get(0), Perm.getByName(args[0]));
                msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.setperm.deny", Perm.getByName(args[0]).name(), mentionedRoles.get(0).getAsMention()),
                        Main.getInstance().getBotColor());
            }
            else {
                Main.getInstance().getPermissionManager().removeRoleFromPermission(mentionedRoles.get(0), Perm.getByName(args[0]));
                msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.setperm.clear", Perm.getByName(args[0]).name(), mentionedRoles.get(0).getAsMention()),
                        Main.getInstance().getBotColor());
            }
            t.sendMessage(msg.build()).queue();
        }
        else{
            EmbedMessage msg = new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.setperm"),
                    Main.getInstance().getErrorColor());
            t.sendMessage(msg.build()).queue();
        }
    }
}
