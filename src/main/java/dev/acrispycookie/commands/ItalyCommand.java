package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItalyCommand extends BotCommand{

    public ItalyCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        String featureEnabled = bot.getConfigManager().get("features.italy.enabled");
        if (Boolean.parseBoolean(featureEnabled)) {
            sendMessage(m, e);
        }
    }

    private void sendMessage(Member m, SlashCommandInteractionEvent e) {
        Role role = bot.getGuild().getRoleById(bot.getConfigManager().get("features.italy.role"));
        e.reply(role.getAsMention() + ", ο " + m.getAsMention() + " ήθελε να υπενθυμίσει σε όλους οτι πετάτε για Ιταλία σε **" + getCountdown() + "**").queue();

    }

    private long parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getCountdown() {
        long premiereDate = parseDate(bot.getConfigManager().get("features.italy.date"));
        long timeLeft = premiereDate - System.currentTimeMillis();
        return Time.formatted(timeLeft/1000);
    }
}
