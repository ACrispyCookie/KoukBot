package dev.acrispycookie.managers;

import dev.acrispycookie.Console;
import dev.acrispycookie.KoukBot;

public abstract class FeatureManager {

    protected final KoukBot bot;
    protected final String name;

    protected abstract void loadInternal();
    protected abstract void unloadInternal();
    protected abstract void reloadInternal();

    public FeatureManager(KoukBot bot, String name) {
        this.bot = bot;
        this.name = name;
    }

    public void load() {
        long startTime = System.currentTimeMillis();
        Console.println("Loading manager " + name + "...");
        try {
            loadInternal();
        } catch (Exception e) {
            Console.error("Error while loading manager " + name, e);
            System.exit(-1);
        }
        Console.println("Manager " + name + " has been loaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void unload() {
        long startTime = System.currentTimeMillis();
        Console.println("Unloading manager " + name + "...");
        try {
            unloadInternal();
        } catch (Exception e) {
            Console.error("Error while unloading manager " + name, e);
        }
        Console.println("Manager " + name + " has been unloaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void reload() {
        long startTime = System.currentTimeMillis();
        Console.println("Reloading manager " + name + "...");
        try {
            unloadInternal();
        } catch (Exception e) {
            Console.error("Error while reloading manager " + name, e);
        }
        Console.println("Manager " + name + " has been reloaded successfully! Took " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
