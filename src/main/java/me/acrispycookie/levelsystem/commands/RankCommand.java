package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class RankCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        LevelUser levelUser;
        if(args.length == 0){
            levelUser = LevelUser.getByDiscordId(m.getIdLong());
        }
        else if(args.length == 1 && mentions.size() > 0){
            levelUser = LevelUser.getByDiscordId(mentions.get(0).getIdLong());
        }
        else{
            t.sendMessage(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.title"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.rank"),
                    Main.getInstance().getErrorColor()).build()).queue();
            return;
        }
        levelUser.sendCard(t.getIdLong());
    }
}
