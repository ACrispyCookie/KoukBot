package me.acrispycookie.levelsystem.commands;

import me.acrispycookie.Main;
import me.acrispycookie.commands.BotCommand;
import me.acrispycookie.levelsystem.LevelUser;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import me.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SetColorCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        String colorCode = e.getOption("color").getAsString();
        if(e.getOption("user") == null && Utils.isColor(colorCode)){
            LevelUser.getByDiscordId(m.getIdLong()).setCardColor(colorCode);
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.success.title.setcolor"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.setcolor.own", colorCode),
                    Utils.hex2Rgb(colorCode)).build()).queue();
        }
        else if(e.getOption("user") != null && Utils.isColor(colorCode)){
            if(Perm.hasPermission(m, Perm.SET_COLOR_OTHER)){
                LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong()).setCardColor(colorCode);
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.success.title.setcolor"),
                        Main.getInstance().getLanguageManager().get("commands.success.description.setcolor.other", e.getOption("user").getAsUser().getAsMention(), colorCode),
                        Utils.hex2Rgb(colorCode)).build()).queue();
            }
            else{
                e.replyEmbeds(new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.noperm.title"),
                        Main.getInstance().getLanguageManager().get("commands.noperm.description", Perm.SET_COLOR_OTHER.name()),
                        Main.getInstance().getBotColor()).build()).queue();
            }
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.failed.title.setcolor.not-a-color"),
                    Main.getInstance().getLanguageManager().get("commands.failed.description.setcolor.not-a-color"),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
