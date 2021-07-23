package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PanellhniesCommand extends BotCommand{

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        Main.getInstance().getPanellhniesManager().sendMessage(m, t);
        message.delete().queue();
        t.sendMessage(new EmbedMessage(m.getUser(), Main.getInstance().getLanguageManager().get("commands.success.title.panellhnies"),
                Main.getInstance().getLanguageManager().get("commands.success.description.panellhnies",
                        t.getGuild().getTextChannelById(Main.getInstance().getConfigManager().get("settings.panellhnies.channel")).getAsMention()),
                Main.getInstance().getBotColor()).build()).queue(after -> {
                    after.delete().queueAfter(5, TimeUnit.SECONDS);
        });
    }
}
