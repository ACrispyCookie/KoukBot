package dev.acrispycookie.levelsystem;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class XPGainEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        Member m = e.getMember();
        if(m != null && !m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())){
            LevelUser levelUser = LevelUser.getByDiscordId(m.getIdLong());
            if(levelUser.getNextValidMessage() <= System.currentTimeMillis()){
                Random random = new Random();
                int exp = random.nextInt(11);
                levelUser.addExp(15 + exp, e.getChannel().asTextChannel());
                levelUser.save();
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent e) {
        Member m = e.getMember();
        if(!m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())){
            if(e.getChannelJoined() != null && e.getChannelLeft() == null) {
                LevelUser levelUser = LevelUser.getByDiscordId(m.getIdLong());
                levelUser.joinChannel();
            } else if(e.getChannelLeft() != null && e.getChannelJoined() == null) {
                LevelUser levelUser = LevelUser.getByDiscordId(m.getIdLong());
                levelUser.leaveChannel();
            }
        }
    }

}
