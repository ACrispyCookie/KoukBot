package dev.acrispycookie.levelsystem.commands;

import dev.acrispycookie.Main;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;

public class LeaderboardCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.replyEmbeds(new EmbedMessage(m.getUser(),
                "Leaderboard - Top 10", getDescription()).build()).queue();
    }

    private String getDescription(){
        StringBuilder s = new StringBuilder();
        ArrayList<Long> ids = Main.getInstance().getLeaderboardManager().getLeaderboard();
        for(int i = 0; i < 10; i++){
            LevelUser u = LevelUser.getByDiscordId(ids.get(i));
            s.append("Rank #").append(i + 1).append(": ").append(Main.getInstance().getDiscordMember(u.getDiscordUser()).getEffectiveName()).append(" - ").append(u.getLevel()).append(" Level, ").append(u.getXp()).append(" XP");
            s.append(i < 9 ? "\n" : "");
        }
        return s.toString();
    }
}
