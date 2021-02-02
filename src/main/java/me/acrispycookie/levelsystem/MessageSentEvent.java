package me.acrispycookie.levelsystem;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;

public class MessageSentEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        Member m = e.getMember();
        if(!m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())){
            if(!e.getMessage().getContentRaw().startsWith(Main.getInstance().getPrefix())){
                LevelUser levelUser = LevelUser.getByDiscordId(m.getIdLong());
                if(levelUser.getNextValidMessage() <= System.currentTimeMillis()){
                    Random random = new Random();
                    int exp = random.nextInt(11);
                    levelUser.addExp(15 + exp, e.getChannel().getIdLong(), true);
                    levelUser.save();
                }
            }
        }
    }
}
