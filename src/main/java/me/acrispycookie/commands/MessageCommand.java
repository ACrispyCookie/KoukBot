package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        String message = e.getOption("message").getAsString();
        OptionMapping channel = e.getOption("channel");
        if(Perm.hasPermission(m, Perm.MESSAGE)){
            if(channel != null && m.getGuild().getTextChannelById(channel.getAsMentionable().getId()) != null){
                TextChannel textChannel = m.getGuild().getTextChannelById(channel.getAsMentionable().getId());
                textChannel.sendMessage(message).queue();
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.message"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.message", Perm.MESSAGE.name()),
                        Main.getInstance().getBotColor()).build()).setEphemeral(true).queue();
            }
            else{
                e.reply(message).queue();
            }
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.MESSAGE.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
