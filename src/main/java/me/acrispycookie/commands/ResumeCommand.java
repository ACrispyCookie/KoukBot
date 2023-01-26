package me.acrispycookie.commands;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ResumeCommand extends BotCommand{

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        e.deferReply().setEphemeral(true).queue();
        Main.getInstance().getMusicManager().resume(m.getUser(), e);
    }
}
