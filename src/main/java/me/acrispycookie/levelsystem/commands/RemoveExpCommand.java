package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class RemoveExpCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        int expToRemove = 0;
        if(Perm.hasPermission(m, Perm.REMOVE_XP)){
            LevelUser levelUser;
            if(args.length == 1  && Utils.isInt(args[0]) && Integer.parseInt(args[0]) > 0) {
                levelUser = LevelUser.getByDiscordId(m.getIdLong());
                expToRemove = Integer.parseInt(args[0]);
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.remove-xp"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.remove-xp", String.valueOf(expToRemove), m.getAsMention())).build()).queue();
                levelUser.removeExp(expToRemove);
            }
            else if(args.length == 2 && mentions.size() > 0 && Utils.isInt(args[1]) && Integer.parseInt(args[1]) > 0) {
                levelUser = LevelUser.getByDiscordId(mentions.get(0).getIdLong());
                expToRemove = Integer.parseInt(args[1]);
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.remove-xp"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.remove-xp", String.valueOf(expToRemove), levelUser.getDiscordUser().getAsMention())).build()).queue();
                levelUser.removeExp(expToRemove);
            }
            else{
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                        Main.getInstance().getLanguageManager().get("commands.invalid.description.remove-xp"),
                        Main.getInstance().getErrorColor()).build()).queue();
            }
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.REMOVE_XP.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
