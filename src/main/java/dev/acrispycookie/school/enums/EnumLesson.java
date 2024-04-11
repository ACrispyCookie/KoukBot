package dev.acrispycookie.school.enums;

public enum EnumLesson {
    GREEK("Έκθεση",
            new String[] {"https://minedu-secondary.webex.com/meet/karatzidou",
                    "https://minedu-secondary.webex.com/meet/ekartsiouk"}, "📖"),
    LITERATURE("Λογοτεχνία",
            new String[] {"https://minedu-secondary.webex.com/meet/karatzidou",
                    "https://minedu-secondary.webex.com/meet/ekartsiouk"}, "📚"),
    MATHS("Μαθηματικά",
            new String[] {"https://minedu-secondary.webex.com/meet/tenia84",
                    "https://minedu-secondary.webex.com/meet/skukiotis",
                    "https://minedu-secondary.webex.com/meet/parasiri"}, "📐"),
    PHYSICS("Φυσική",
            new String[] {"https://minedu-secondary.webex.com/meet/gbatziak"}, "👨‍🔬"),
    CHEMISTRY("Χημεία",
            new String[] {"https://minedu-secondary.webex.com/meet/ekamvisi"}, "🧪"),
    BIOLOGY("Βιολογία",
            new String[] {"https://minedu-secondary.webex.com/meet/gbatziak"}, "🧬"),
    ECONOMICS("Οικονομικά",
            new String[] {"https://minedu-secondary.webex.com/meet/geodypap"}, "📊"),
    COMPUTER_SCIENCE("Πληροφορική",
            new String[] {""}, "💻"),
    HISTORY("Ιστορία",
            new String[] {"https://minedu-secondary.webex.com/meet/davanouf",
                    "https://minedu-secondary.webex.com/meet/ekartsiouk",
                    "https://minedu-secondary.webex.com/meet/karatzidou"}, "🏛"),
    LATIN("Λατινικά",
            new String[] {"https://minedu-secondary.webex.com/meet/ekartsiouk"}, "🔡"),
    ANCIENT_GREEK_G("Αρχαία διδαγμένο",
            new String[] {"https://minedu-secondary.webex.com/meet/akaliakouda"}, "📃"),
    ANCIENT_GREEK_A("Αρχαία αδίδακτο",
            new String[] {"https://minedu-secondary.webex.com/meet/akaliakouda"}, "📜"),
    GYMNASTICS("Γυμναστική",
            new String[] {"https://minedu-secondary.webex.com/meet/grammato"}, "🤸‍♂️"),
    ENGLISH("Αγγλικά",
            new String[] {"https://minedu-secondary.webex.com/meet/georgbesas"}, "🔠"),
    RELIGIOUS_EDUCATION("Θρησκευτικά",
            new String[] {"https://minedu-secondary.webex.com/meet/asmitroula"}, "✝"),
    NOTHING("Τίποτα",
            new String[] {""}, "🆓");

    String name;
    String url[];
    String emoji;

    EnumLesson(String name, String[] url, String emoji){
        this.name = name;
        this.url = url;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getUrl(int index){
        return this.url[index];
    }

    public static EnumLesson get(int index){
        return values()[index];
    }
}
