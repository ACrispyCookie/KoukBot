package me.acrispycookie;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Console {

    public static void println(Object s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String prefix = sdf.format(new Date(System.currentTimeMillis()));
        System.out.println("[" + prefix + "] " + s);
    }

    public static void start(){
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()){
                try {
                    String s = scanner.next();
                    if(s != null && ConsoleCommand.getByName(s) != null){
                        Console.runCommand(ConsoleCommand.getByName(s));
                    }
                    else{
                        println("Unknown command! Available commands: stop, restart, reload");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void runCommand(ConsoleCommand cmd){
        if(cmd == ConsoleCommand.RELOAD){
            Main.getInstance().reload();
        }
        else if(cmd == ConsoleCommand.STOP){
            Main.getInstance().disable();
        }
        else if(cmd == ConsoleCommand.RESTART){
            try {
                System.out.println("Restarting...");
                Desktop.getDesktop().open(new File("start.bat"));
                Main.getInstance().disable();
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

        public static ConsoleCommand getByName(String s){
            for(ConsoleCommand cmd : ConsoleCommand.values()){
                if(cmd.name().equalsIgnoreCase(s)){
                    return cmd;
                }
            }
            return null;
        }
    }
}
