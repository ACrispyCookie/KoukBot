package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        t.sendMessage(new EmbedMessage(m.getUser(),
                "Leaderboard - Top 10", getDescription()).build()).queue();
    }

    private String getDescription(){
        StringBuilder s = new StringBuilder();
        ArrayList<Long> ids = Main.getInstance().getLeaderboardManager().getLeaderboard();
        for(int i = 0; i < 10; i++){
            LevelUser u = LevelUser.getByDiscordId(ids.get(i));
            s.append("Rank #").append(i + 1).append(": ").append(u.getDiscordUser().getName()).append(" - ").append(u.getLevel()).append(" Level, ").append(u.getXp()).append(" XP");
            s.append(i < 9 ? "\n" : "");
        }
        return s.toString();
    }
}
