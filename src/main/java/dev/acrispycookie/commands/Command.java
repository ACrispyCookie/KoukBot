package dev.acrispycookie.commands;

import dev.acrispycookie.KoukBot;
import dev.acrispycookie.levelsystem.commands.*;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public enum Command {
    SET_PERM(new SetPermCommand(KoukBot.getCmdInstance()), new String[] {"set-perm"},
            Commands.slash("set-perm", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-perm.main"))
                    .addOptions(new OptionData(OptionType.STRING, "permission",
                            KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-perm.permission"), true)
                            .addChoices(Perm.getPermChoices()))
                    .addOption(OptionType.ROLE, "role",
                            KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-perm.role"), true)
                    .addOptions(new OptionData(OptionType.STRING, "action",
                            KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-perm.action"), false)
                            .addChoice("allow", "allow").addChoice("deny", "deny")
                            .addChoice("clear", "clear"))),
    SET_COLOR(new SetColorCommand(KoukBot.getCmdInstance()), new String[] {"set-color"},
            Commands.slash("set-color", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-color.main"))
                    .addOption(OptionType.STRING, "color", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-color.main"), false)
                    .addOption(OptionType.USER, "user", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.set-color.user"), false)),
    GIVE_XP(new GiveExpCommand(KoukBot.getCmdInstance()), new String[] {"give-xp"},
            Commands.slash("give-xp", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.give-xp.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "xp", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.give-xp.xp"), true)
                                    .setMinValue(0)
                                    .setMaxValue(1000000))
                    .addOption(OptionType.USER, "user", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.give-xp.user"), false)),
    REMOVE_XP(new RemoveExpCommand(KoukBot.getCmdInstance()), new String[] {"remove-xp"},
            Commands.slash("remove-xp", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.remove-xp.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "xp", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.remove-xp.xp"), true)
                                    .setMinValue(0)
                                    .setMaxValue(1000000))
                    .addOption(OptionType.USER, "user", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.remove-xp.xp"), false)),
    RANK(new RankCommand(KoukBot.getCmdInstance()), new String[] {"rank"},
            Commands.slash("rank", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.rank.main"))
                    .addOption(OptionType.USER, "user", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.rank.user"), false)),
    LEADERBOARD(new LeaderboardCommand(KoukBot.getCmdInstance()), new String[] {"top", "leaderboard"},
            Commands.slash("top", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.top.main"))),
    /*HELP(new HelpCommand(), new String[]{"help", "?"},
            Commands.slash("help", "a")),*/
    TODO(new ToDoCommand(KoukBot.getCmdInstance()), new String[]{"todo", "task"},
            Commands.slash("todo", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.todo.main"))
                    .addOption(OptionType.STRING, "task", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.todo.task"), true)),
    NOTIFY(new NotifyCommand(KoukBot.getCmdInstance()), new String[]{"notify", "noti"},
            Commands.slash("notify", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.notify.main"))
                    .addOption(OptionType.USER, "user", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.notify.user"), true)),
    MESSAGE(new MessageCommand(KoukBot.getCmdInstance()), new String[]{"message", "msg"},
            Commands.slash("message", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.message.main"))
                    .addOption(OptionType.STRING, "message", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.message.message"), true)
                    .addOptions(new OptionData(OptionType.CHANNEL, "channel", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.message.channel"), false)
                            .setChannelTypes(ChannelType.TEXT))),
    SAY(new SayCommand(KoukBot.getCmdInstance(), false), new String[]{"say"},
            Commands.slash("say", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.say.main"))
                    .addOption(OptionType.STRING, "message", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.say.message"), false)),
    EVILSAY(new SayCommand(KoukBot.getCmdInstance(), true), new String[]{"evil-say", "e-say"},
            Commands.slash("evil-say", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.evil-say.main"))
                    .addOption(OptionType.STRING, "message", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.evil-say.message"), false)),
    CLEAR(new ClearCommand(KoukBot.getCmdInstance()), new String[]{"clear"},
            Commands.slash("clear", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.clear.main"))
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "count", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.clear.count"), true)
                                    .setMinValue(2)
                                    .setMaxValue(100)
                    )),
    PANELLHNIES(new PanellhniesCommand(KoukBot.getCmdInstance()), new String[]{"panellhnies", "πανελλήνιες", "πανελλήνιεσ", "panell"},
            Commands.slash("panellhnies", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.panellhnies.main"))),
    ITALY(new ItalyCommand(KoukBot.getCmdInstance()), new String[]{"italy", "italia"},
            Commands.slash("italy", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.italy.main"))),
    PLAY(new PlayCommand(KoukBot.getCmdInstance()), new String[]{"play", "queue", "q"},
            Commands.slash("play", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.play.main"))
                    .addOption(OptionType.STRING, "search", KoukBot.getCmdInstance().getLanguageManager().get("commands.descriptions.play.search"), true));

    private final BotCommand botCommand;
    private final String[] aliases;
    private final CommandData data;

    Command(BotCommand command, String[] aliases, CommandData data) {
        this.botCommand = command;
        this.aliases = aliases;
        this.data = data;
    }

    public CommandData getData() {
        return data;
    }

    public String[] getAliases() {
        return aliases;
    }

    public BotCommand getBotCommand() {
        return botCommand;
    }

    public static Command getByAlias(String alias) {
        for (Command command : Command.values()) {
            String[] args = command.getAliases();
            for (String s : args) {
                if (s.equalsIgnoreCase(alias)) {
                    return command;
                }
            }
        }
        return null;
    }
}
