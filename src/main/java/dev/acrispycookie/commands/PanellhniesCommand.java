package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class PanellhniesCommand extends BotCommand{

    public PanellhniesCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        bot.getPanellhniesManager().sendMessage(m, e);
    }
}
