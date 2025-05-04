package dev.acrispycookie.levelsystem.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import dev.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class SetColorCommand extends BotCommand {

    public SetColorCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        if (e.getOption("color") != null) {
            String colorCode = e.getOption("color").getAsString();
            if (e.getOption("user") == null && Utils.isColor(colorCode)) {
                LevelUser.getByDiscordId(m.getIdLong()).setCardColor(colorCode);
                e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                        bot.getLanguageManager().get("commands.success.title.set-color"),
                        bot.getLanguageManager().get("commands.success.description.set-color.own", colorCode),
                        Utils.hex2Rgb(colorCode)).build()).queue();
            }
            else if (e.getOption("user") != null && Utils.isColor(colorCode)) {
                if (Perm.hasPermission(bot, m, Perm.SET_COLOR_OTHER)) {
                    LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong()).setCardColor(colorCode);
                    e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.success.title.set-color"),
                            bot.getLanguageManager().get("commands.success.description.set-color.other", e.getOption("user").getAsUser().getAsMention(), colorCode),
                            Utils.hex2Rgb(colorCode)).build()).queue();
                }
                else {
                    e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.noperm.title"),
                            bot.getLanguageManager().get("commands.noperm.description", Perm.SET_COLOR_OTHER.name()),
                            bot.getBotColor()).build()).queue();
                }
            }
            else {
                e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                        bot.getLanguageManager().get("commands.failed.title.set-color.not-a-color"),
                        bot.getLanguageManager().get("commands.failed.description.set-color.not-a-color"),
                        bot.getErrorColor()).build()).queue();
            }
        } else {
            Color c = bot.getBotColor();
            String hexColor = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            LevelUser.getByDiscordId(m.getIdLong()).setCardColor(hexColor);
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("commands.success.title.set-color"),
                    bot.getLanguageManager().get("commands.success.description.set-color.own", hexColor),
                    bot.getBotColor()).build()).queue();
        }
    }
}
