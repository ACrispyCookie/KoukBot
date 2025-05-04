package dev.acrispycookie.levelsystem.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GiveExpCommand extends BotCommand {

    public GiveExpCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        if (Perm.hasPermission(bot, m, Perm.GIVE_XP)) {
            LevelUser levelUser;
            int expToAdd = e.getOption("xp").getAsInt();
            if (e.getOption("user") == null) {
                levelUser = LevelUser.getByDiscordId(m.getIdLong());
            }
            else {
                levelUser = LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong());
            }
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("commands.success.title.give-xp"),
                    bot.getLanguageManager().get("commands.success.description.give-xp", String.valueOf(expToAdd), levelUser.getDiscordUser().getAsMention())).build()).queue();
            levelUser.addExp(expToAdd, e.getChannel().getIdLong());
        }
        else {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("no-perm.title"),
                    bot.getLanguageManager().get("no-perm.description", Perm.GIVE_XP.name()),
                    bot.getErrorColor()).build()).queue();
        }
    }
}
