package dev.acrispycookie.commands;

import dev.acrispycookie.levelsystem.commands.*;
import dev.acrispycookie.Main;
import me.acrispycookie.levelsystem.commands.*;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public enum Command {
    SET_PERM(new SetPermCommand(), new String[] {"set-perm"},
            Commands.slash("set-perm", Main.getInstance().getLanguageManager().get("commands.descriptions.set-perm.main"))
                    .addOptions(new OptionData(OptionType.STRING, "permission",
                            Main.getInstance().getLanguageManager().get("commands.descriptions.set-perm.permission"), true)
                            .addChoices(Perm.getPermChoices()))
                    .addOption(OptionType.ROLE, "role",
                            Main.getInstance().getLanguageManager().get("commands.descriptions.set-perm.role"), true)
                    .addOptions(new OptionData(OptionType.STRING, "action",
                            Main.getInstance().getLanguageManager().get("commands.descriptions.set-perm.action"), false)
                            .addChoice("allow", "allow").addChoice("deny", "deny")
                            .addChoice("clear", "clear"))),
    SET_COLOR(new SetColorCommand(), new String[] {"set-color"},
            Commands.slash("set-color", Main.getInstance().getLanguageManager().get("commands.descriptions.set-color.main"))
                    .addOption(OptionType.STRING, "color", Main.getInstance().getLanguageManager().get("commands.descriptions.set-color.main"), false)
                    .addOption(OptionType.USER, "user", Main.getInstance().getLanguageManager().get("commands.descriptions.set-color.user"), false)),
    GIVE_XP(new GiveExpCommand(), new String[] {"give-xp"},
            Commands.slash("give-xp", Main.getInstance().getLanguageManager().get("commands.descriptions.give-xp.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "xp", Main.getInstance().getLanguageManager().get("commands.descriptions.give-xp.xp"), true)
                                    .setMinValue(0)
                                    .setMaxValue(1000000))
                    .addOption(OptionType.USER, "user", Main.getInstance().getLanguageManager().get("commands.descriptions.give-xp.user"), false)),
    REMOVE_XP(new RemoveExpCommand(), new String[] {"remove-xp"},
            Commands.slash("remove-xp", Main.getInstance().getLanguageManager().get("commands.descriptions.remove-xp.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "xp", Main.getInstance().getLanguageManager().get("commands.descriptions.remove-xp.xp"), true)
                                    .setMinValue(0)
                                    .setMaxValue(1000000))
                    .addOption(OptionType.USER, "user", Main.getInstance().getLanguageManager().get("commands.descriptions.remove-xp.xp"), false)),
    RANK(new RankCommand(), new String[] {"rank"},
            Commands.slash("rank", Main.getInstance().getLanguageManager().get("commands.descriptions.rank.main"))
                    .addOption(OptionType.USER, "user", Main.getInstance().getLanguageManager().get("commands.descriptions.rank.user"), false)),
    LEADERBOARD(new LeaderboardCommand(), new String[] {"top", "leaderboard"},
            Commands.slash("top", Main.getInstance().getLanguageManager().get("commands.descriptions.top.main"))),
    /*HELP(new HelpCommand(), new String[]{"help", "?"},
            Commands.slash("help", "a")),*/
    TODO(new ToDoCommand(), new String[]{"todo", "task"},
            Commands.slash("todo", Main.getInstance().getLanguageManager().get("commands.descriptions.todo.main"))
                    .addOption(OptionType.STRING, "task", Main.getInstance().getLanguageManager().get("commands.descriptions.todo.task"), true)),
    NOTIFY(new NotifyCommand(), new String[]{"notify", "noti"},
            Commands.slash("notify", Main.getInstance().getLanguageManager().get("commands.descriptions.notify.main"))
                    .addOption(OptionType.USER, "user", Main.getInstance().getLanguageManager().get("commands.descriptions.notify.user"), true)),
    MESSAGE(new MessageCommand(), new String[]{"message", "msg"},
            Commands.slash("message", Main.getInstance().getLanguageManager().get("commands.descriptions.message.main"))
                    .addOption(OptionType.STRING, "message", Main.getInstance().getLanguageManager().get("commands.descriptions.message.message"), true)
                    .addOptions(new OptionData(OptionType.CHANNEL, "channel", Main.getInstance().getLanguageManager().get("commands.descriptions.message.channel"), false)
                            .setChannelTypes(ChannelType.TEXT))),
    SAY(new SayCommand(false), new String[]{"say"},
            Commands.slash("say", Main.getInstance().getLanguageManager().get("commands.descriptions.say.main"))
                    .addOption(OptionType.STRING, "message", Main.getInstance().getLanguageManager().get("commands.descriptions.say.message"), false)),
    EVILSAY(new SayCommand(true), new String[]{"evil-say", "e-say"},
            Commands.slash("evil-say", Main.getInstance().getLanguageManager().get("commands.descriptions.evil-say.main"))
                    .addOption(OptionType.STRING, "message", Main.getInstance().getLanguageManager().get("commands.descriptions.evil-say.message"), false)),
    CLEAR(new ClearCommand(), new String[]{"clear"},
            Commands.slash("clear", Main.getInstance().getLanguageManager().get("commands.descriptions.clear.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "count", Main.getInstance().getLanguageManager().get("commands.descriptions.clear.count"), true)
                                    .setMinValue(2)
                                    .setMaxValue(100)
                    )),
    PANELLHNIES(new PanellhniesCommand(), new String[]{"panellhnies", "πανελλήνιες", "πανελλήνιεσ", "panell"},
            Commands.slash("panellhnies", Main.getInstance().getLanguageManager().get("commands.descriptions.panellhnies.main"))),
    ITALY(new ItalyCommand(), new String[]{"italy", "italia"},
            Commands.slash("italy", Main.getInstance().getLanguageManager().get("commands.descriptions.italy.main"))),
    PLAY(new PlayCommand(), new String[]{"play", "queue", "q"},
            Commands.slash("play", Main.getInstance().getLanguageManager().get("commands.descriptions.play.main"))
                    .addOption(OptionType.STRING, "search", Main.getInstance().getLanguageManager().get("commands.descriptions.play.search"), true)),
    PAUSE(new PauseCommand(), new String[]{"pause"},
            Commands.slash("pause", Main.getInstance().getLanguageManager().get("commands.descriptions.pause.main"))),
    RESUME(new ResumeCommand(), new String[]{"resume", "res"},
            Commands.slash("resume", Main.getInstance().getLanguageManager().get("commands.descriptions.resume.main"))),
    NEXT(new NextCommand(), new String[]{"next", "skip"},
            Commands.slash("next", Main.getInstance().getLanguageManager().get("commands.descriptions.next.main"))),
    PREVIOUS(new PreviousCommand(), new String[]{"previous", "prev"},
            Commands.slash("previous", Main.getInstance().getLanguageManager().get("commands.descriptions.previous.main"))),
    STOP(new StopCommand(), new String[]{"stop"},
            Commands.slash("stop", Main.getInstance().getLanguageManager().get("commands.descriptions.stop.main")));
    BotCommand botCommand;
    String[] aliases;
    CommandData data;

    Command(BotCommand command, String[] aliases, CommandData data){
        this.botCommand = command;
        this.aliases = aliases;
        this.data = data;
    }

    public String[] getAliases(){
        return aliases;
    }

    public BotCommand getBotCommand(){
        return botCommand;
    }

    public static Command getByAlias(String alias){
        for(Command command : Command.values()){
            String[] args = command.getAliases();
            for(String s : args){
                if(s.equalsIgnoreCase(alias)){
                    return command;
                }
            }
        }
        return null;
    }
}
