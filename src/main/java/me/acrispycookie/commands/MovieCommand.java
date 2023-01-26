package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class MovieCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        boolean isActive = Main.getInstance().getMoviePollManager().isActive();
        String action = e.getOption("action").getAsString();
        if(action.equalsIgnoreCase("cancel")){
            if(isActive){
                Main.getInstance().getMoviePollManager().getActive().end();
            }
            else{
                EmbedMessage msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.invalid.failed.moviepoll.not-active"),
                        Main.getInstance().getLanguageManager().get("commands.invalid.description.moviepoll.not-active"),
                        Main.getInstance().getErrorColor());
                e.replyEmbeds(msg.build()).queue();
            }
        }
        else if(isActive){
            if(action.equalsIgnoreCase("search")){
                String argumentAfterSearch = "";
            }
            else if(action.equals("id")){

            }
        }
        else{
            EmbedMessage msg = new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.invalid.failed.moviepoll.not-active"),
                    Main.getInstance().getLanguageManager().get("commands.invalid.description.moviepoll.not-active"),
                    Main.getInstance().getErrorColor());
            e.replyEmbeds(msg.build()).queue();
        }
    }
}
