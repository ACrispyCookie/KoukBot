package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        EmbedMessage msg = new EmbedMessage(m.getUser(),
                Main.getInstance().getLanguageManager().get("commands.success.title.help"),
                Main.getInstance().getLanguageManager().get("commands.success.description.help"),
                Main.getInstance().getBotColor());
        t.sendMessage(msg.build()).queue();
    }
}
