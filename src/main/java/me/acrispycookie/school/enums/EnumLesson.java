package me.acrispycookie.school.enums;

import net.dv8tion.jda.api.entities.Role;

public enum EnumLesson {
    GREEK("Έκθεση",
            "normal", "", "", "📖"),
    LITERATURE("Λογοτεχνία",
            "normal", "", "", "📚"),
    MATHS("Μαθηματικά",
            "normal", "", "", "📐"),
    PHYSICS("Φυσική",
            "normal", "", "", "👨‍🔬"),
    CHEMISTRY("Χημεία",
            "normal", "", "", "🧪"),
    BIOLOGY("Βιολογία",
            "normal", "", "", "🧬"),
    ECONOMICS("Οικονομικά",
            "normal", "", "", "📊"),
    COMPUTER_SCIENCE("Πληροφορική",
            "normal", "", "", "💻"),
    HISTORY("Ιστορία",
            "normal", "", "", "🏛"),
    LATIN("Λατινικά",
            "normal", "", "", "🔡"),
    ANCIENT_GREEK_G("Αρχαία διδαγμένο",
            "normal", "", "", "📃"),
    ANCIENT_GREEK_A("Αρχαία αδίδακτο",
            "normal", "", "", "📜"),
    GYMNASTICS("Γυμναστική",
            "normal", "", "", "🤸‍♂️"),
    ENGLISH("Αγγλικά",
            "normal", "", "", "🔠"),
    RELIGIOUS_EDUCATION("Θρησκευτικά",
            "normal", "", "", "✝"),
    NOTHING("Τίποτα",
                    "normal", "", "", "🆓");

    String name;
    String type;
    String b1url;
    String b2url;
    String emoji;

    EnumLesson(String name, String type, String b1url, String b2url, String emoji){
        this.name = name;
        this.type = type;
        this.b1url = b1url;
        this.b2url = b2url;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getUrl(Role role) {
        switch (type) {
            case "kat":
                if(this == EnumLesson.MATHS){
                    if (role.getIdLong() == 787997819889516554L) {
                        return b1url;
                    } else if (role.getIdLong() == 788351437540687913L) {
                        return b2url;
                    }
                }
            case "lang":
                return b1url;
            case "normal":
                if (role.getIdLong() == 786225886311481355L) {
                    return b1url;
                } else if (role.getIdLong() == 786225699668361238L) {
                    return b2url;
                }
                break;
        }
        return null;
    }
}
