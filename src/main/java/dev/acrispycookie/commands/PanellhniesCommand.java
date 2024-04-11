package dev.acrispycookie.commands;

import dev.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class PanellhniesCommand extends BotCommand{

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        Main.getInstance().getPanellhniesManager().sendMessage(m, e);
    }
}
