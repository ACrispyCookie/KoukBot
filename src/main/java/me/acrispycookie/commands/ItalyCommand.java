package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.Time;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItalyCommand extends BotCommand{

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(Boolean.parseBoolean(Main.getInstance().getConfigManager().get("features.italy.enabled"))) {
            sendMessage(m, t);
            message.delete().queue();
        }
    }

    private void sendMessage(Member m, TextChannel t){
        Role role = Main.getInstance().getGuild().getRoleById(Main.getInstance().getConfigManager().get("features.italy.role"));
        t.sendMessage(role.getAsMention() + ", ο " + m.getAsMention() + " ήθελε να υπενθυμίσει σε όλους οτι πετάτε για Ιταλία σε **" + getCountdown() + "**").queue();

    }

    private long parseDate(String dateString){
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

    private String getCountdown(){
        long premiereDate = parseDate(Main.getInstance().getConfigManager().get("features.italy.date"));
        long timeLeft = premiereDate - System.currentTimeMillis();
        return Time.formatted(timeLeft/1000);
    }
}
