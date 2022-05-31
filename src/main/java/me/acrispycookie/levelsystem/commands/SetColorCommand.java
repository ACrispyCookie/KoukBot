package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SetColorCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(args.length == 1 && args[0].length() == 7 && Utils.isColor(args[0])){
            LevelUser.getByDiscordId(m.getIdLong()).setCardColor(args[0]);
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.success.title.setcolor"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.setcolor.own", args[0]),
                    Utils.hex2Rgb(args[0])).build()).queue();
        }
        else if(args.length == 2 && mentions.size() > 0 && Utils.isColor(args[1])){
            if(Perm.hasPermission(m, Perm.SET_COLOR_OTHER)){
                LevelUser.getByDiscordId(mentions.get(0).getIdLong()).setCardColor(args[1]);
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.setcolor"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.setcolor.other", mentions.get(0).getAsMention(), args[1]),
                        Utils.hex2Rgb(args[1])).build()).queue();
            }
            else{
                t.sendMessage(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.noperm.title"),
                        Main.getInstance().getLanguageManager().get("commands.noperm.description", Perm.SET_COLOR_OTHER.name()),
                        Main.getInstance().getBotColor()).build()).queue();
            }
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.setcolor"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
