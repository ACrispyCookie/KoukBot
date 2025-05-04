package dev.acrispycookie.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.utility.ConfigurationFile;
import dev.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;

public class PermissionManager extends FeatureManager {

    private final ConfigurationFile file = ConfigurationFile.PERMISSION;

    public PermissionManager(KoukBot bot, String name) {
        super(bot, name);
    }

    @Override
    public void loadInternal() {

    }

    @Override
    public void unloadInternal() {

    }

    @Override
    public void reloadInternal() {
        file.reloadJson();
    }

    public ArrayList<Long> getAllowedRoleList(Perm permission) {
        JsonArray array = file.getElement("permissions").getAsJsonObject().getAsJsonArray(permission.name());
        ArrayList<Long> longs = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
            longs.add(array.get(i).getAsLong());
        return longs;
    }

    public void addRoleToPermission(Role role, Perm permission) {
        JsonArray userArray = get(permission);
        userArray.add(role.getIdLong());
        file.setElement("permissions" + permission.name(), userArray);
    }

    public void removeRoleFromPermission(Role role, Perm permission) {
        JsonArray userArray = get(permission);
        userArray.remove(new JsonPrimitive(role.getIdLong()));
        file.setElement("permissions" + permission.name(), userArray);
    }

    private JsonArray get(Perm perm) {
        return file.getElement("permissions").getAsJsonObject().getAsJsonArray(perm.name());
    }

}
