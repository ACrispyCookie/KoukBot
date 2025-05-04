package dev.acrispycookie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Console {

    private static KoukBot bot;

    public static void println(Object s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String prefix = sdf.format(new Date(System.currentTimeMillis()));
        System.out.println("[" + prefix + "] " + s);
    }

    public static void error(Object msg, Exception e) {
        println(msg + " : " + e.getMessage());
        e.printStackTrace();
    }

    public static void start(KoukBot bot) {
        Console.bot = bot;
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                try {
                    String s = scanner.next();
                    if (s != null && ConsoleCommand.getByName(s) != null) {
                        Console.runCommand(ConsoleCommand.getByName(s));
                    }
                    else {
                        println("Unknown command! Available commands: stop, restart, reload");
                    }
                } catch (Exception e) {
                    error("Error occurred while executing a command", e);
                }
            }
        });
        thread.start();
    }

    public static void runCommand(ConsoleCommand cmd) {
        if (cmd == ConsoleCommand.RELOAD) {
            bot.reload();
        }
        else if (cmd == ConsoleCommand.STOP) {
            bot.unload();
        }
        else if (cmd == ConsoleCommand.RESTART) {
            try {
                System.out.println("Restarting...");
                String scriptPath = new File("start.sh").getAbsolutePath();
                String[] commands = new String[] {"C:\\Program Files\\Git\\git-bash.exe", scriptPath};
                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Start batch file not found! Exiting...");
                System.exit(-1);
            }
        }
    }

    enum ConsoleCommand{
        RELOAD,
        STOP,
        RESTART;

        public static ConsoleCommand getByName(String s) {
            for (ConsoleCommand cmd : ConsoleCommand.values()) {
                if (cmd.name().equalsIgnoreCase(s)) {
                    return cmd;
                }
            }
            return null;
        }
    }
}
