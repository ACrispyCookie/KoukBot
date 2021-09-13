package me.acrispycookie.school.classes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.acrispycookie.Main;
import me.acrispycookie.school.enums.EnumColor;
import me.acrispycookie.school.enums.EnumLesson;
import me.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ProgramCreatorChannel extends ListenerAdapter {

    Main main;
    User user;
    int stage;
    int maxStage;
    Message message;
    JsonObject data;
    static ArrayList<ProgramCreatorChannel> channels = new ArrayList<>();
    int dayIndex = -1;
    long time;
    int totalStages;
    int stagesPerDay;
    long startingTime;
    Color color = Color.BLACK;
    final int STROKE = 14;
    final int PIXELS_X = (210 * 6) + (STROKE + 1) * 7;
    final int MORNING_PIXELS_Y = (70 * 8) + (STROKE + 1) * (7 + 2);
    final int AFTERNOON_PIXELS_Y = (70 * 13) + (STROKE + 1) * (12 + 2);

    public ProgramCreatorChannel(Main main, User user){
        this.main = main;
        this.user = user;
        this.stage = 0;
        this.maxStage = 0;
        this.data = new JsonObject();
        channels.add(this);
        create();
        main.getGuild().getJDA().addEventListener(this);
    }

    public ProgramCreatorChannel(Main main, long userId, long channelId, long messageId, int stage, int maxStage, JsonObject data){
        this.main = main;
        this.user = main.getDiscordUser(userId);
        this.message = main.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
        this.stage = stage;
        this.maxStage = maxStage;
        this.data = data;
        channels.add(this);
        setVars();
        main.getGuild().getJDA().addEventListener(this);
    }

    private void create(){
        Collection<Permission> allowed = new ArrayList<>();
        allowed.add(Permission.VIEW_CHANNEL);
        Collection<Permission> denied = new ArrayList<>();
        denied.add(Permission.MESSAGE_WRITE);
        denied.add(Permission.VIEW_CHANNEL);
        main.getGuild().createTextChannel(user.getName().toLowerCase(), main.getGuild().getCategoryById(886033717733244979L))
                .addMemberPermissionOverride(user.getIdLong(), allowed, null)
                .addPermissionOverride(Main.getInstance().getGuild().getPublicRole(), null, denied).queue((q) -> {
            q.sendMessage(user.getAsMention()).queue();
            EmbedMessage msg = new EmbedMessage(user, "Καλώς ήρθες στην δημιουργία του προγράμματος σου!",
                    "Απάντησε σε κάθε ερώτηση κάνοντας react σε αυτό το μήνυμα κάθε φορά!\n\n**Τί είδους πρόγραμμα δημιουργείς?**\n1️⃣ - Πρωινό πρόγραμμα\n2️⃣ - Απογευματινό πρόγραμμα");
            q.sendMessageEmbeds(msg.build()).queue((m) -> {
                for(String s : getReactions()){
                    m.addReaction(s).queue();
                }
                ProgramCreatorChannel.this.message = m;
                save();
                m.getGuild().getTextChannelById(main.getConfigManager().get("settings.programCreator.channel")).sendMessage(user.getAsMention() + " πήγαινε στο κανάλι " + message.getTextChannel().getAsMention() + " για να ξεκινήσεις!").queue((s) -> {
                    s.delete().queueAfter(10, TimeUnit.SECONDS);
                });
            });
        });
    }

    private void nextStage(){
        stage++;
        setVars();
        if(stage == totalStages){
            complete();
        }
        else{
            message.editMessageEmbeds(getNextMessage()).queue((m) -> {
                if(stage == 1 || stage + 1 == totalStages){
                    m.clearReactions().queue();
                    for(String s : getReactions()){
                        m.addReaction(s).queue();
                    }
                }
                checkArrowReactions();
                ProgramCreatorChannel.this.message = m;
            });
        }
    }

    public void previousStage(){
        stage--;
        setVars();
        message.editMessageEmbeds(getNextMessage()).queue((m) -> {
            if(stage + 2 == totalStages){
                m.clearReactions().queue();
                for(String s : getReactions()){
                    m.addReaction(s).queue();
                }
            }
            checkArrowReactions();
            ProgramCreatorChannel.this.message = m;
        });
    }

    private void complete(){
        message.editMessageEmbeds(new EmbedMessage(message.getJDA().getSelfUser(), "Το πρόγραμμα σου ολοκληρώθηκε!", "Θα λάβεις το πρόγραμμα σου εδώ σε μορφή φωτογραφίας σε περίπου 5-10 δευτερόλεπτα. Έχεις 5 λεπτά να το αποθηκεύσεις πριν διαγραφεί").build())
                .queue((m) -> {
                    m.clearReactions().queue();
                    delete(340);
                    sendFinished();
                    m.getTextChannel().getGuild().getTextChannelById(783655696423714816L).sendMessage("Ο/Η " + user.getAsMention() + " έφτιαξε το δικό του ψηφιακό πρόγραμμα στο "
                            + main.getGuild().getTextChannelById(main.getConfigManager().get("settings.programCreator.channel")).getAsMention() + "!").queue();
                });
    }

    private void cancel(){
        message.editMessageEmbeds(new EmbedMessage(message.getJDA().getSelfUser(), "Το πρόγραμμα ακυρώθηκε", "Έχεις ακυρώσει την δημιουργία αυτόυ του προγράμματος!\nΑυτο το κανάλι θα διαγραφεί σε 10 δευτερόλεπτα!").build())
                .queue((m) -> {
                    m.clearReactions().queue();
                    delete(10);
                });
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent e) {
        if(e.getMessageIdLong() == message.getIdLong() && e.getUser().equals(user)){
            String reaction = e.getReaction().getReactionEmote().getName();
            ArrayList<String> reactions = getReactions();
            e.getReaction().removeReaction(e.getUser()).queue();
            if(reaction.equals("❌")){ //cancel emote
                cancel();
            }
            else if(reaction.equals("◀") && stage > 1){
                previousStage();
            }
            else if(reaction.equals("▶") && stage < maxStage){
                nextStage();
            }
            else if(reactions.contains(reaction)){
                int lesson = reactions.indexOf(reaction);
                if(stage == maxStage){
                    maxStage++;
                }
                if(stage == 0){
                    data.add(String.valueOf(stage), new JsonPrimitive(lesson == 0 ? "m" : "a"));
                }
                else if(stage + 1 == totalStages){
                    color = EnumColor.values()[lesson].getColor();
                }
                else{
                    data.add(String.valueOf(stage), new JsonPrimitive(String.valueOf(lesson)));
                }
                nextStage();
            }
            save();
        }
    }

    private void save(){
        main.getProgramCreator().saveChannel(this);
    }

    private void delete(int after){
        message.getTextChannel().delete().queueAfter(after, TimeUnit.SECONDS);
        main.getGuild().getJDA().removeEventListener(this);
        channels.remove(this);
        main.getProgramCreator().deleteChannel(this);
    }

    public User getUser() {
        return user;
    }

    public JsonObject getData() {
        return data;
    }

    public int getStage() {
        return stage;
    }

    public int getMaxStage() {
        return maxStage;
    }

    public Message getMessage() {
        return message;
    }

    public static ProgramCreatorChannel getChannelByUser(long userId){
        for(ProgramCreatorChannel channel : channels){
            if(channel.user.getIdLong() == userId){
                return channel;
            }
        }
        return null;
    }

    private MessageEmbed getNextMessage(){
        if(stage + 1 == totalStages){
            return new EmbedMessage(this.message.getJDA().getSelfUser(), "Χρώμα περιγράμματος",
                    "Τι χρώμα επιθυμείς να έχει το περίγραμμα το προγράμματος σου?" +
                            "\n(Κάνε react με το αντίστοιχο emoji)").build();
        }
        else{
            String[] days = new String[] {"Δευτέρα", "Τρίτη", "Τετάρτη", "Πέμπτη", "Παρασκευή"};
            return new EmbedMessage(this.message.getJDA().getSelfUser(), "Μάθημα " + getTime() + " την " + days[getDay()],
                    "Τι μάθημα έχεις " + getTime() + " την " + days[getDay()] + "?" +
                            "\n(Κάνε react με το αντίστοιχο emoji)" +
                            "\n\n" + getLessons()).build();
        }
    }

    private ArrayList<String> getReactions(){
        ArrayList<String> list = new ArrayList<>();
        if(stage == 0){
            list.add("1️⃣"); //1 button
            list.add("2️⃣"); //2 button
            list.add("❌"); //cancel button
        }
        else if(stage + 1 == totalStages){
            list.add("🟥");
            list.add("🟧");
            list.add("🟨");
            list.add("🟩");
            list.add("🟦");
            list.add("🟪");
            list.add("🟫");
            list.add("⬛");
        }
        else {
            for(EnumLesson l : EnumLesson.values()){
                list.add(l.getEmoji());
            }
            list.add("❌"); //cancel button
        }
        return list;
    }

    private void checkArrowReactions(){
        if(stage > 1){
            message.addReaction("◀").queue();
        }
        else{
            message.removeReaction("◀").queue();
        }
        if(stage < maxStage){
            message.addReaction("▶").queue();
        }
        else{
            message.removeReaction("▶").queue();
        }
    }

    private String getLessons(){
        StringBuilder s = new StringBuilder();
        EnumLesson[] values = EnumLesson.values();
        for(int i = 0; i < values.length; i++){
            if(i + 1 <= values.length){
                s.append("\n");
            }
            EnumLesson l = values[i];
            s.append(l.getEmoji()).append(" - ").append(l.getName());
        }
        return s.toString();
    }

    private int getDay(){
        if(dayIndex != Math.floorDiv((stage-1), stagesPerDay)) {
            this.dayIndex = Math.floorDiv((stage-1), stagesPerDay);
        }
        return dayIndex;
    }

    private String getTime(){
        dayIndex = getDay();
        int timeIndex = stage- 1 - dayIndex * stagesPerDay;
        this.time = startingTime + (timeIndex * 45L * 60L * 1000L);
        if(stagesPerDay == 7){
            return "την " + (timeIndex + 1) + "η ωρα";
        }
        else{
            return "στις " + new SimpleDateFormat("HH:mm").format(new Date(time));
        }
    }

    private void setVars(){
        if((totalStages == 0 || stagesPerDay == 0) && stage > 0){
            String typeOfProgram = data.getAsJsonPrimitive("0").getAsString();
            if(typeOfProgram.equals("m")){
                totalStages = 5 * 7 + 2;
                stagesPerDay = 7;
                startingTime = 22500000;
            }
            else if(typeOfProgram.equals("a")){
                totalStages = 5 * 12 + 2;
                stagesPerDay = 12;
                startingTime = 46800000;
            }
        }
    }

    private void sendFinished(){
        Font font = new Font("Cera Pro", Font.BOLD, 24);
        BufferedImage finalImage = new BufferedImage(PIXELS_X, stagesPerDay == 7 ? MORNING_PIXELS_Y : AFTERNOON_PIXELS_Y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) finalImage.getGraphics();
        graphics.setFont(font);
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(STROKE));

        //make border
        graphics.drawRoundRect(STROKE/2,STROKE/2, finalImage.getWidth() - STROKE - 1, finalImage.getHeight() - STROKE - 1, 10, 10);
        //make background
        graphics.setColor(Color.WHITE);
        graphics.fillRect(STROKE, STROKE, finalImage.getWidth() - 2 * STROKE, finalImage.getHeight() - 2 * STROKE);
        graphics.setColor(color);
        //make vertical lines
        for(int i = 1; i < 6; i++){
            int offset = i * (finalImage.getWidth() - 7 * (STROKE + 1))/6 + (i - 1) * (STROKE + 1);
            graphics.drawLine(3 * STROKE/2 + 1 + offset, STROKE, 3 * STROKE/2 + 1 + offset, finalImage.getHeight() - STROKE + 1);
        }
        //make horizontal lines
        int countOfLines = stagesPerDay + 1;
        for(int i = 1; i < countOfLines; i++){
            int offset = i * (finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines + (i - 1) * (STROKE + 1);
            graphics.drawLine(STROKE, 3 * STROKE/2 + 1 + offset, finalImage.getWidth() - STROKE + 1, 3 * STROKE/2 + 1 + offset);
        }
        graphics.setColor(Color.BLACK);

        //write days
        String[] days = new String[] {"Δευτέρα", "Τρίτη", "Τετάρτη", "Πέμπτη", "Παρασκευή"};
        for(int i = 1; i < 6; i++){
            int x = (int) ((STROKE + 1 + ((finalImage.getWidth() - 7 * (STROKE + 1))/6)) * (i + 0.5));
            int y = STROKE + 1 + ((finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines)/2;
            int finalX = x - graphics.getFontMetrics().stringWidth(days[i - 1])/2;
            int finalY = (int) (y + graphics.getFontMetrics().getStringBounds(days[i - 1], graphics).getHeight()/2);
            graphics.drawString(days[i - 1], finalX, finalY);
            graphics.setStroke(new BasicStroke(2));
            graphics.drawLine(finalX, finalY + 2, finalX + graphics.getFontMetrics().stringWidth(days[i - 1]), finalY + 2);
            graphics.setStroke(new BasicStroke(14));
        }

        //write timestamps
        String[] times = (stagesPerDay == 7) ? new String[] {"1η ώρα", "2η ώρα", "3η ώρα", "4η ώρα", "5η ώρα", "6η ώρα", "7η ώρα"}
        : new String[] {"15:00", "15:45", "16:30", "17:15", "18:00", "18:45", "19:30", "20:15", "21:00", "21:45", "22:30", "23:15"};
        for(int i = 1; i < countOfLines; i++){
            int x = STROKE + 1 + ((finalImage.getWidth() - 7 * (STROKE + 1))/6)/2;
            int y = (int) ((STROKE + 1 + ((finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines)) * (i + 0.5));
            int finalX = x - (graphics.getFontMetrics().stringWidth(times[i-1]))/2;
            int finalY = (int) (y + graphics.getFontMetrics().getStringBounds(times[i - 1], graphics).getHeight()/2);
            graphics.drawString(times[i - 1], finalX, finalY);
            graphics.setStroke(new BasicStroke(2));
            graphics.drawLine(finalX, finalY + 2, finalX + graphics.getFontMetrics().stringWidth(times[i - 1]), finalY + 2);
            graphics.setStroke(new BasicStroke(14));
        }

        //write lessons
        font = new Font("Cera Pro", Font.PLAIN, 24);
        graphics.setFont(font);
        for(int i = 1; i <= 5 * stagesPerDay; i++){
            String lesson = EnumLesson.values()[Integer.parseInt(data.get(String.valueOf(i)).getAsString())].getName();
            if(Integer.parseInt(data.get(String.valueOf(i)).getAsString()) == 15) continue;
            int x = (int) ((STROKE + 1 + ((finalImage.getWidth() - 7 * (STROKE + 1))/6)) * ( Math.floorDiv(i - 1, stagesPerDay) + 1.5));
            int y = (int) ((STROKE + 1 + ((finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines)) * ((i - 1) % stagesPerDay + 1.5));
            if(lesson.contains(" ")){
                String[] lessons = lesson.split(" ");
                for(int c = 0; c < lessons.length; c++){
                    graphics.drawString(lessons[c], x - (graphics.getFontMetrics().stringWidth(lessons[c]))/2, (int) (y + graphics.getFontMetrics().getStringBounds(lesson, graphics).getHeight()/2) - 15 + c * 30);
                }
            }
            else{
                graphics.drawString(lesson, x - (graphics.getFontMetrics().stringWidth(lesson))/2, (int) (y + graphics.getFontMetrics().getStringBounds(lesson, graphics).getHeight()/2));
            }
        }

        //send image
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "png", bytes);
            bytes.flush();
            FileUtils.writeByteArrayToFile(new File("./saved-programs/" + user.getName() + "-" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))), bytes.toByteArray());
            message.getTextChannel().sendFile(bytes.toByteArray(), "program.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
