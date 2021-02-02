package me.acrispycookie.managers;

import com.google.gson.*;
import me.acrispycookie.Main;
import me.acrispycookie.utility.Perm;
import net.dv8tion.jda.api.entities.Role;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PermissionManager {

    private JsonObject permissions;

    public PermissionManager(JsonObject permissions){
        this.permissions = permissions;
    }

    public ArrayList<Long> getAllowedRoleList(Perm permission) {
        JsonArray array = permissions.get("permissions").getAsJsonObject().getAsJsonArray(permission.name());
        ArrayList<Long> longs = new ArrayList<>();
        for(int i = 0; i < array.size(); i++){
            longs.add(array.get(i).getAsLong());
        }
        return longs;
    }

    public void addRoleToPermission(Role role, Perm permission){
        JsonArray userArray = get(permission);
        userArray.add(role.getIdLong());
        save();
    }

    public void removeRoleFromPermission(Role role, Perm permission){
        JsonArray userArray = get(permission);
        userArray.remove(new JsonPrimitive(role.getIdLong()));
        save();
    }

    private JsonArray get(Perm perm) {
        return permissions.getAsJsonObject("permissions").getAsJsonArray(perm.name());
    }

    private void save(){
        try {
            FileWriter file = new FileWriter("./data/permissions.json");
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(permissions));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
