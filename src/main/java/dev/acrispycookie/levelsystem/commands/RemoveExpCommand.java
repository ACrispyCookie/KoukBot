package dev.acrispycookie.levelsystem.commands;

import dev.acrispycookie.Main;
import dev.acrispycookie.commands.BotCommand;
import dev.acrispycookie.levelsystem.LevelUser;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RemoveExpCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        if(Perm.hasPermission(m, Perm.REMOVE_XP)){
            LevelUser levelUser;
            int expToRemove = e.getOption("xp").getAsInt();
            if(e.getOption("user") == null) {
                levelUser = LevelUser.getByDiscordId(m.getIdLong());
            }
            else {
                levelUser = LevelUser.getByDiscordId(e.getOption("user").getAsUser().getIdLong());
            }
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("commands.success.title.remove-xp"),
                    Main.getInstance().getLanguageManager().get("commands.success.description.remove-xp", String.valueOf(expToRemove), levelUser.getDiscordUser().getAsMention())).build()).queue();
            levelUser.removeExp(expToRemove);
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.REMOVE_XP.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
