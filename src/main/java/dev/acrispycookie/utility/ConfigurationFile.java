package dev.acrispycookie.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.acrispycookie.Console;

import java.io.*;

public enum ConfigurationFile {
    CONFIG("config.json"),
    DATA("data.json"),
    LANGUAGE("language.json"),
    LEADERBOARD("leaderboard.json"),
    PERMISSION("permissions.json"),
    PROGRAM("program.json"),
    TODO("todo.json"),
    PROGRAM_CREATOR("program_creator.json");

    private final String path;
    private JsonObject json;
    private boolean loaded = false;

    ConfigurationFile(String path) {
        this.path = path;
    }

    public void reloadJson() {
        loaded = false;
    }

    public JsonElement getElement(String path) {
        if (!loaded)
            load();

        String[] parents = path.split("\\.");
        JsonElement element = json.get(parents[0]);
        for (int i = 1; i < parents.length; i++)
            element = element.getAsJsonObject().get(parents[i]);
        return element;
    }

    public void setElement(String path, JsonElement element) {
        if (!loaded)
            load();

        String[] parents = path.split("\\.");
        JsonObject finalParent = json;
        for (int i = 0; i < parents.length - 1; i++)
            finalParent = finalParent.getAsJsonObject(parents[i]);
        finalParent.add(parents[parents.length - 1], element);
        save();
    }

    public void removeElement(String path) {
        json.remove(path);
        save();
    }

    public JsonObject getJson() {
        if (!loaded)
            load();
        return json;
    }

    public void setJson(JsonObject object) {
        json = object;
        save();
    }

    public String getFullPath() {
        return getParentDir() + path;
    }

    private String getParentDir() {
        return "./data/";
    }

    private void load() {
        File configFile = new File(getFullPath());
        configFile.getParentFile().mkdirs();
        try {
            if (!configFile.exists()) {
                configFile = getResource(path);
                if (configFile != null)
                    configFile.createNewFile();
                else
                    return;
            }

            loaded = true;
            json = new Gson().fromJson(new FileReader(configFile), JsonObject.class);
        } catch (IOException e) {
            Console.error("Error loading the configuration file with path " + getFullPath(), e);
        }
    }

    private void save() {
        try {
            FileWriter file = new FileWriter(getFullPath());
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
            file.flush();
            file.close();
        } catch (IOException e) {
            Console.error("Error saving the configuration file with path " + getFullPath(), e);
        }
    }

    private File getResource(String resourceName) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        if (inputStream == null) {
            Console.println("Error fetching default configuration file with path " + getFullPath());
            return null;
        }

        File file = new File("./data/" + resourceName);
        FileOutputStream outputStream = new FileOutputStream(file);
        int read;
        byte[] bytes = new byte[1024];
        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        outputStream.close();

        return file;
    }
}
