package me.acrispycookie.managers.school.enums;

import net.dv8tion.jda.api.entities.Role;

public enum EnumLesson {
    ALGEBRA("Άλγεβρα", "normal", "https://minedu-secondary.webex.com/meet/skukiotis", "https://minedu-secondary.webex.com/meet/skukiotis"),
    GEOMETRY("Γεωμετρία", "normal", "https://minedu-secondary.webex.com/meet/skukiotis", "https://minedu-secondary.webex.com/meet/skukiotis"),
    PHYSICS("Φυσική", "normal", "https://minedu-secondary.webex.com/meet/chsaltas", "https://minedu-secondary.webex.com/meet/profountos"),
    CHEMISTRY("Χημεία", "normal", "https://minedu-secondary.webex.com/meet/chsaltas", "https://minedu-secondary.webex.com/meet/chsaltas"),
    BIOLOGY("Βιολογία", "normal", "https://minedu-secondary.webex.com/meet/chsaltas", "https://minedu-secondary.webex.com/meet/chsaltas"),
    COMPUTER_SCIENCE("Πληροφορική", "normal", "https://minedu-secondary.webex.com/meet/dinakis", "https://minedu-secondary.webex.com/meet/dinakis"),
    GYMNASTICS("Γυμναστική", "normal", "https://minedu-secondary.webex.com/meet/grammato", "https://minedu-secondary.webex.com/meet/grammato"),
    ENGLISH("Αγγλικά", "normal", "https://minedu-secondary.webex.com/meet/marinapap", "https://minedu-secondary.webex.com/meet/marinapap"),
    FRENCH("Γαλλικά", "lang", "https://minedu-secondary.webex.com/meet/psioumi", "https://minedu-secondary.webex.com/meet/psioumi"),
    GERMAN("Γερμανικά", "lang", "https://minedu-secondary.webex.com/meet/idrikoudi", "https://minedu-secondary.webex.com/meet/idrikoudi"),
    ANTIGONH("Αντιγόνη", "normal", "https://minedu-secondary.webex.com/meet/karatzidou", "https://minedu-secondary.webex.com/meet/karatzidou"),
    GREEK("Έκθεση", "normal", "https://minedu-secondary.webex.com/meet/akaliakouda", "https://minedu-secondary.webex.com/meet/karatzidou"),
    LITERATURE("Λογοτεχνία", "normal", "https://minedu-secondary.webex.com/meet/karatzidou", "https://minedu-secondary.webex.com/meet/karatzidou"),
    HISTORY("Ιστορία", "normal", "https://minedu-secondary.webex.com/meet/kardupap", "https://minedu-secondary.webex.com/meet/kardupap"),
    RELIGION("Θρησκευτικά", "normal", "https://minedu-secondary.webex.com/meet/asmitroula", "https://minedu-secondary.webex.com/meet/asmitroula"),
    PHILOSOPHY("Φιλοσοφία", "normal", "https://minedu-secondary.webex.com/meet/akaliakouda", "https://minedu-secondary.webex.com/meet/akaliakouda"),
    MATHS("Μαθηματικά κατεύθυνσης", "kat", "https://minedu-secondary.webex.com/meet/mpatsas", "https://minedu-secondary.webex.com/meet/gkdionysia"),
    FYSIKH_KAT("Φυσική κατεύθυνσης", "kat", "https://minedu-secondary.webex.com/meet/chsaltas", "https://minedu-secondary.webex.com/meet/chsaltas"),
    ANCIENT_GREEK("Αρχαία κατεύθυνσης", "kat", "https://minedu-secondary.webex.com/meet/ehrantou", "https://minedu-secondary.webex.com/meet/ehrantou"),
    LATIN("Λατινικά", "kat", "https://minedu-secondary.webex.com/meet/ehrantou", "https://minedu-secondary.webex.com/meet/ehrantou");

    String name;
    String type;
    String b1url;
    String b2url;

    EnumLesson(String name, String type, String b1url, String b2url){
        this.name = name;
        this.type = type;
        this.b1url = b1url;
        this.b2url = b2url;
    }

    public String getName() {
        return name;
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
