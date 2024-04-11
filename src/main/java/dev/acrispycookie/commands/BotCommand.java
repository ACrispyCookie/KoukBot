package dev.acrispycookie.commands;

import com.google.gson.JsonPrimitive;
import dev.acrispycookie.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;

public abstract class BotCommand extends ListenerAdapter {

    public abstract void execute(SlashCommandInteractionEvent e, String label, Member m);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e){
        Member m = e.getMember();
        if(!m.getUser().isBot() && !m.getUser().equals(e.getJDA().getSelfUser())){
            Command command = Command.getByAlias(e.getName());
            command.getBotCommand().execute(e, e.getName(), e.getMember());
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent e) {
        if(!Boolean.parseBoolean(Main.getInstance().getConfigManager().get("settings.commandsRegistered"))) {
            ArrayList<CommandData> data = new ArrayList<>();
            for(Command command : Command.values()) {
                data.add(command.data);
            }
            e.getJDA().updateCommands().addCommands(data).queue();
            Main.getInstance().getConfigManager().set("settings.commandsRegistered", new JsonPrimitive(true));
        }
    }

    /*
    private static String[] findArguments(String msg){
        ArrayList<String> stringArrayList = new ArrayList<>();
        for(int i = 0; i < msg.length(); i++){
            char c = msg.charAt(i);
            if(c != ' '){
                String argument = "";
                while(true){
                    c = msg.charAt(i);
                    argument = argument.concat(String.valueOf(c));
                    i++;
                    if(i >= msg.length()){
                        break;
                    }
                    c = msg.charAt(i);
                    if(c == ' '){
                        break;
                    }
                }
                stringArrayList.add(argument);
            }
        }
        return stringArrayList.toArray(new String[] {});
    }
    */

}
