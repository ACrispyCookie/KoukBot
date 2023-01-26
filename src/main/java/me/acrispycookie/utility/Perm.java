package me.acrispycookie.utility;

import me.acrispycookie.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.List;

public enum Perm {
    GIVE_XP,
    REMOVE_XP,
    SET_PERMISSION,
    MESSAGE,
    SET_COLOR_OTHER,
    SET_PREFIX,
    CLEAR;

    public static boolean hasPermission(Member user, Perm permission){
        if(user.getPermissions().contains(Permission.ADMINISTRATOR)){
            return true;
        }
        for(Role role : user.getRoles()){
            if(Main.getInstance().getPermissionManager().getAllowedRoleList(permission).contains(role.getIdLong())){
                return true;
            }
        }
        return false;
    }

    public static Perm getByName(String name){
        for(Perm perm : Perm.values()){
            if(perm.name().equalsIgnoreCase(name)){
                return perm;
            }
        }
        return null;
    }

    public static List<Command.Choice> getPermChoices(){
        List<Command.Choice> choices = new ArrayList<>();
        for(Perm perm : Perm.values()){
            choices.add(new Command.Choice(perm.name(), perm.name()));
        }
        return choices;
    }
}
