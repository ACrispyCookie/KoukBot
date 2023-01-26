package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class ClearCommand extends BotCommand {


    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        int count = e.getOption("count").getAsInt();
        if(Perm.hasPermission(m, Perm.CLEAR)){
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.success.title.clear"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.clear", String.valueOf(count), (count != 1 ? "s" : "")),
                    Main.getInstance().getBotColor()).build()).setEphemeral(true).queue();

            e.getChannel().asTextChannel().getHistory().retrievePast(count).queue((l) -> {
                e.getChannel().asTextChannel().deleteMessages(l).queue(null,
                        new ErrorHandler().ignore(ErrorResponse.INVALID_BULK_DELETE_MESSAGE_AGE)
                                .ignore(ErrorResponse.INVALID_BULK_DELETE));
            });
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.CLEAR.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
