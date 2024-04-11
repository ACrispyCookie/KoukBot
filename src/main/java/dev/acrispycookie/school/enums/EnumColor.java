package dev.acrispycookie.school.enums;

import java.awt.*;

public enum EnumColor {
    RED("🟥", new Color(255, 65, 52)),
    ORANGE("🟧", new Color(251, 157, 52)),
    YELLOW("🟨", new Color(252, 252, 107)),
    GREEN("🟩", new Color(129, 252, 107)),
    BLUE("🟦", new Color(107, 183, 252)),
    PURPLE("🟪", new Color(207, 107, 252)),
    BROWN("🟫", new Color(89, 53, 28)),
    BLACK("⬛", new Color(90, 87, 85));

    String emote;
    Color color;

    EnumColor(String emote, Color color){
        this.emote = emote;
        this.color = color;
    }

    public String getEmote() {
        return emote;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name().charAt(1) + name().substring(2).toLowerCase();
    }
}
