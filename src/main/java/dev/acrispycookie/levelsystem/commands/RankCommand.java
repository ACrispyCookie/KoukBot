package dev.acrispycookie.levelsystem.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RankCommand extends BotCommand {

    public RankCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().queue();
        LevelUser levelUser;
        if (e.getOption("user") == null) {
            levelUser = LevelUser.getByDiscordId(m.getIdLong());
        }
        else {
            levelUser = LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong());
        }
        levelUser.sendCard(e);
    }
}
