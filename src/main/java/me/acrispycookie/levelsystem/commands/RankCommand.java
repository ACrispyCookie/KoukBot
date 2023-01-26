package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RankCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().queue();
        LevelUser levelUser;
        if(e.getOption("user") == null){
            levelUser = LevelUser.getByDiscordId(m.getIdLong());
        }
        else {
            levelUser = LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong());
        }
        levelUser.sendCard(e);
    }
}
