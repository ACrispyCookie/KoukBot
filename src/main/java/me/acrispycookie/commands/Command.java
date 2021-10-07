package me.acrispycookie.commands;

import me.acrispycookie.levelsystem.commands.GiveExpCommand;
import me.acrispycookie.levelsystem.commands.RankCommand;
import me.acrispycookie.levelsystem.commands.RemoveExpCommand;
import me.acrispycookie.levelsystem.commands.SetColorCommand;

public enum Command {
    SET_PREFIX(new SetPrefixCommand(), new String[]{"setprefix", "set-prefix"}),
    SET_PERM(new SetPermCommand(), new String[] {"setperm", "set-perm"}),
    SET_COLOR(new SetColorCommand(), new String[] {"setcolor", "set-color"}),
    GIVE_XP(new GiveExpCommand(), new String[] {"give-xp"}),
    REMOVE_XP(new RemoveExpCommand(), new String[] {"remove-xp"}),
    RANK(new RankCommand(), new String[] {"rank"}),
    LEVELS(new RankCommand(), new String[] {"levels", "leaderboard"}),
    HELP(new HelpCommand(), new String[]{"help", "?"}),
    TODO(new ToDoCommand(), new String[]{"todo", "task"}),
    NOTIFY(new NotifyCommand(), new String[]{"notify", "noti"}),
    MESSAGE(new MessageCommand(), new String[]{"message", "msg"}),
    SAY(new SayCommand(), new String[]{"say"}),
    CLEAR(new ClearCommand(), new String[]{"clear"}),
    PANELLHNIES(new PanellhniesCommand(), new String[]{"panellhnies", "πανελλήνιες", "πανελλήνιεσ", "panell"}),
    PLAY(new PlayCommand(), new String[]{"play", "queue", "q"}),
    PAUSE(new PauseCommand(), new String[]{"pause"}),
    RESUME(new ResumeCommand(), new String[]{"resume", "res"}),
    NEXT(new NextCommand(), new String[]{"next", "skip"}),
    PREVIOUS(new PreviousCommand(), new String[]{"previous", "prev"}),
    STOP(new StopCommand(), new String[]{"stop"});
    BotCommand botCommand;
    String[] aliases;

    Command(BotCommand command, String[] aliases){
        this.botCommand = command;
        this.aliases = aliases;
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
