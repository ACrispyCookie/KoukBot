package me.acrispycookie.commands;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NextCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().queue();
        Main.getInstance().getMusicManager().next(m.getUser(), e);
    }
}
