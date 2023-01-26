package me.acrispycookie.commands;

import me.acrispycookie.Main;
import me.acrispycookie.utility.EmbedMessage;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SetPermCommand extends BotCommand {

    @Override
    public void execute(SlashCommandInteractionEvent e, String label, Member m) {
        if(Perm.hasPermission(m, Perm.SET_PERMISSION)){
            String perm = e.getOption("perm").getAsString();
            Role role = e.getOption("role").getAsRole();
            if(Perm.getByName(perm) != null && e.getOption("role") != null){
                EmbedMessage msg = null;
                OptionMapping action = e.getOption("action");
                if(action == null) {
                    msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.failed.title.setperm.invalid-action"),
                            Main.getInstance().getLanguageManager().get("commands.failed.description.setperm.invalid-action"),
                            Main.getInstance().getErrorColor());
                }
                else if(action.getAsString().equalsIgnoreCase("allow")){
                    if(!Main.getInstance().getPermissionManager().getAllowedRoleList(Perm.getByName(perm)).contains(role.getIdLong())){
                        Main.getInstance().getPermissionManager().addRoleToPermission(role, Perm.getByName(perm));
                        msg = new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                                Main.getInstance().getLanguageManager().get("commands.success.description.setperm.allow", Perm.getByName(perm).name(), role.getAsMention()),
                                Main.getInstance().getBotColor());
                    }
                    else{
                        msg = new EmbedMessage(m.getUser(),
                                Main.getInstance().getLanguageManager().get("commands.failed.title.setperm.already-has"),
                                Main.getInstance().getLanguageManager().get("commands.failed.description.setperm.already-has"),
                                Main.getInstance().getBotColor());
                    }
                }
                else if(action.getAsString().equalsIgnoreCase("deny")) {
                    Main.getInstance().getPermissionManager().removeRoleFromPermission(role, Perm.getByName(perm));
                    msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.setperm.deny", Perm.getByName(perm).name(), role.getAsMention()),
                            Main.getInstance().getBotColor());
                }
                else if(action.getAsString().equalsIgnoreCase("clear")){
                    Main.getInstance().getPermissionManager().removeRoleFromPermission(role, Perm.getByName(perm));
                    msg = new EmbedMessage(m.getUser(),
                            Main.getInstance().getLanguageManager().get("commands.success.title.setperm"),
                            Main.getInstance().getLanguageManager().get("commands.success.description.setperm.clear", Perm.getByName(perm).name(), role.getAsMention()),
                            Main.getInstance().getBotColor());
                }

                e.replyEmbeds(msg.build()).queue();
            }
            else{
                EmbedMessage msg = new EmbedMessage(m.getUser(),
                        Main.getInstance().getLanguageManager().get("commands.failed.title.setperm.invalid-permission"),
                        Main.getInstance().getLanguageManager().get("commands.failed.description.setperm.invalid-permission"),
                        Main.getInstance().getErrorColor());
                e.replyEmbeds(msg.build()).queue();
            }
        }
        else{
            e.replyEmbeds(new EmbedMessage(m.getUser(),
                    Main.getInstance().getLanguageManager().get("no-perm.title"),
                    Main.getInstance().getLanguageManager().get("no-perm.description", Perm.SET_PERMISSION.name()),
                    Main.getInstance().getErrorColor()).build()).queue();
        }
    }
}
