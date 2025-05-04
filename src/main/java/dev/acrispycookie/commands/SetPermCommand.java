package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.EmbedMessage;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SetPermCommand extends BotCommand {

    public SetPermCommand(KoukBot bot) {
        super(bot);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        if (Perm.hasPermission(bot, m, Perm.SET_PERMISSION)) {
            String perm = e.getOption("permission").getAsString();
            Role role = e.getOption("role").getAsRole();
            if (Perm.getByName(perm) != null && e.getOption("role") != null) {
                EmbedMessage msg = null;
                OptionMapping action = e.getOption("action");
                if (action == null) {
                    msg = new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.failed.title.set-perm.invalid-action"),
                            bot.getLanguageManager().get("commands.failed.description.set-perm.invalid-action"),
                            bot.getErrorColor());
                }
                else if (action.getAsString().equalsIgnoreCase("allow")) {
                    if (!bot.getPermissionManager().getAllowedRoleList(Perm.getByName(perm)).contains(role.getIdLong())) {
                        bot.getPermissionManager().addRoleToPermission(role, Perm.getByName(perm));
                        msg = new EmbedMessage(bot, m.getUser(),
                                bot.getLanguageManager().get("commands.success.title.set-perm"),
                                bot.getLanguageManager().get("commands.success.description.set-perm.allow", Perm.getByName(perm).name(), role.getAsMention()),
                                bot.getBotColor());
                    }
                    else {
                        msg = new EmbedMessage(bot, m.getUser(),
                                bot.getLanguageManager().get("commands.failed.title.set-perm.already-has"),
                                bot.getLanguageManager().get("commands.failed.description.set-perm.already-has"),
                                bot.getBotColor());
                    }
                }
                else if (action.getAsString().equalsIgnoreCase("deny")) {
                    bot.getPermissionManager().removeRoleFromPermission(role, Perm.getByName(perm));
                    msg = new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.success.title.set-perm"),
                            bot.getLanguageManager().get("commands.success.description.set-perm.deny", Perm.getByName(perm).name(), role.getAsMention()),
                            bot.getBotColor());
                }
                else if (action.getAsString().equalsIgnoreCase("clear")) {
                    bot.getPermissionManager().removeRoleFromPermission(role, Perm.getByName(perm));
                    msg = new EmbedMessage(bot, m.getUser(),
                            bot.getLanguageManager().get("commands.success.title.set-perm"),
                            bot.getLanguageManager().get("commands.success.description.set-perm.clear", Perm.getByName(perm).name(), role.getAsMention()),
                            bot.getBotColor());
                }

                e.replyEmbeds(msg.build()).queue();
            }
            else {
                EmbedMessage msg = new EmbedMessage(bot, m.getUser(),
                        bot.getLanguageManager().get("commands.failed.title.set-perm.invalid-permission"),
                        bot.getLanguageManager().get("commands.failed.description.set-perm.invalid-permission"),
                        bot.getErrorColor());
                e.replyEmbeds(msg.build()).queue();
            }
        }
        else {
            e.replyEmbeds(new EmbedMessage(bot, m.getUser(),
                    bot.getLanguageManager().get("no-perm.title"),
                    bot.getLanguageManager().get("no-perm.description", Perm.SET_PERMISSION.name()),
                    bot.getErrorColor()).build()).queue();
        }
    }
}
