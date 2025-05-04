package dev.acrispycookie.school.classes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.acrispycookie.KoukBot;
import dev.acrispycookie.school.enums.EnumColor;
import dev.acrispycookie.school.enums.EnumLesson;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;
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

    private final KoukBot bot;
    private final User user;
    private final JsonObject data;
    private final static ArrayList<ProgramCreatorChannel> channels = new ArrayList<>();
    private final int STROKE = 14;
    private final int PIXELS_X = (210 * 6) + (STROKE + 1) * 7;
    private final int MORNING_PIXELS_Y = (70 * 8) + (STROKE + 1) * (7 + 2);
    private final int AFTERNOON_PIXELS_Y = (70 * 13) + (STROKE + 1) * (12 + 2);
    private int stage;
    private int maxStage;
    private Message message;
    private int dayIndex = -1;
    private long time;
    private int totalStages;
    private int stagesPerDay;
    private long startingTime;
    private Color color = Color.BLACK;

    public ProgramCreatorChannel(KoukBot bot, User user, InteractionHook hook) {
        this.bot = bot;
        this.user = user;
        this.stage = 0;
        this.maxStage = 0;
        this.data = new JsonObject();
        channels.add(this);
        create(hook);
        bot.getGuild().getJDA().addEventListener(this);
    }

    public ProgramCreatorChannel(KoukBot bot, long userId, long channelId, long messageId, int stage, int maxStage, JsonObject data) {
        this.bot = bot;
        this.user = bot.getDiscordUser(userId);
        this.message = bot.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
        this.stage = stage;
        this.maxStage = maxStage;
        this.data = data;
        channels.add(this);
        setVars();
        bot.getGuild().getJDA().addEventListener(this);
    }

    private void create(InteractionHook hook) {
        Collection<Permission> allowed = new ArrayList<>();
        allowed.add(Permission.VIEW_CHANNEL);
        Collection<Permission> denied = new ArrayList<>();
        denied.add(Permission.MESSAGE_SEND);
        denied.add(Permission.VIEW_CHANNEL);
        bot.getGuild().createTextChannel(user.getName().toLowerCase(), bot.getGuild().getCategoryById(886033717733244979L))
                .addMemberPermissionOverride(user.getIdLong(), allowed, null)
                .addPermissionOverride(bot.getGuild().getPublicRole(), null, denied).queue((q) -> {
            q.sendMessage(user.getAsMention()).queue();
            EmbedMessage msg = new EmbedMessage(bot, user, bot.getLanguageManager().get("program-creator.stages.first.title"),
                    bot.getLanguageManager().get("program-creator.stages.first.description"));
            q.sendMessageEmbeds(msg.build())
                    .addActionRow(getChoices())
                    .addActionRow(getNavigation())
                    .queue((m) -> {
                ProgramCreatorChannel.this.message = m;
                save();
                hook.sendMessage(bot.getLanguageManager().get("program-creator.started",
                                user.getAsMention(), m.getChannel().getAsMention()))
                        .setEphemeral(true).queue();
            });
        });
    }

    private void nextStage(ButtonInteractionEvent e) {
        e.deferReply().queue();
        stage++;
        setVars();
        if (stage == totalStages) {
            complete(e);
        }
        else {
            e.getHook().editOriginalEmbeds(getNextMessage())
                    .setActionRow(getChoices()).setActionRow(getNavigation())
                    .queue();
        }
    }

    private void nextStage(StringSelectInteractionEvent e) {
        e.deferReply().queue();
        stage++;
        setVars();
        if (stage == totalStages) {
            complete(e);
        }
        else {
            e.editSelectMenu(getChoices()).queue();
            e.getHook().editOriginalEmbeds(getNextMessage())
                    .setActionRow(getNavigation())
                    .queue();
        }
    }

    public void previousStage(ButtonInteractionEvent e) {
        e.deferReply().queue();
        stage--;
        setVars();
        e.getHook().editOriginalEmbeds(getNextMessage())
                .setActionRow(getChoices()).setActionRow(getNavigation())
                .queue();
    }

    private void complete(ButtonInteractionEvent e) {
        e.editMessageEmbeds(new EmbedMessage(bot, message.getJDA().getSelfUser(),
                        bot.getLanguageManager().get("program-creator.stages.completed.title"),
                        bot.getLanguageManager().get("program-creator.stages.completed.description"))
                        .build()).setActionRow(getChoices()).setActionRow(getNavigation())
                .queue((m) -> {
                    delete(340);
                    sendFinished();
                });
    }

    private void complete(StringSelectInteractionEvent e) {
        e.editSelectMenu(getChoices()).queue();
        e.editMessageEmbeds(new EmbedMessage(bot, message.getJDA().getSelfUser(),
                        bot.getLanguageManager().get("program-creator.stages.completed.title"),
                        bot.getLanguageManager().get("program-creator.stages.completed.description"))
                        .build()).setActionRow(getNavigation())
                .queue((m) -> {
                    delete(340);
                    sendFinished();
                });
    }

    private void cancel(ButtonInteractionEvent e) {
        e.editMessageEmbeds(new EmbedMessage(bot, message.getJDA().getSelfUser(),
                        bot.getLanguageManager().get("program-creator.stages.canceled.title"),
                        bot.getLanguageManager().get("program-creator.stages.canceled.description"))
                        .build()).setActionRow(getChoices()).setActionRow(getNavigation())
                .queue((m) -> delete(10));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        if (e.getMessageIdLong() == message.getIdLong() && e.getUser().equals(user)) {
            if (e.getComponentId().equals("cancel")) {
                cancel(e);
            }
            else if (e.getComponentId().equals("prev")) {
                previousStage(e);
            }
            else if (e.getComponentId().equals("next")) {
                nextStage(e);
            }
            save();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent e) {
        if (e.getMessageIdLong() == message.getIdLong() && e.getUser().equals(user)) {
            ArrayList<String> options = getOptions();
            if (options.contains(e.getValues().get(0))) {
                int lesson = options.indexOf(e.getValues().get(0));
                if (stage == maxStage) {
                    maxStage++;
                }
                if (stage == 0) {
                    data.add(String.valueOf(stage), new JsonPrimitive(lesson == 0 ? "m" : "a"));
                }
                else if (stage + 1 == totalStages) {
                    color = EnumColor.values()[lesson].getColor();
                }
                else {
                    data.add(String.valueOf(stage), new JsonPrimitive(String.valueOf(lesson)));
                }
                nextStage(e);
            }
            save();
        }
    }

    private void save() {
        bot.getProgramCreator().saveChannel(this);
    }

    private void delete(int after) {
        message.getChannel().delete().queueAfter(after, TimeUnit.SECONDS);
        bot.getGuild().getJDA().removeEventListener(this);
        channels.remove(this);
        bot.getProgramCreator().deleteChannel(this);
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

    public static ProgramCreatorChannel getChannelByUser(long userId) {
        for (ProgramCreatorChannel channel : channels) {
            if (channel.user.getIdLong() == userId) {
                return channel;
            }
        }
        return null;
    }

    private MessageEmbed getNextMessage() {
        if (stage + 1 == totalStages) {
            return new EmbedMessage(bot, this.message.getJDA().getSelfUser(),
                    bot.getLanguageManager().get("program-creator.stages.color.title"),
                    bot.getLanguageManager().get("program-creator.stages.color.description")).build();
        }
        else {
            String[] days = bot.getLanguageManager().get("program-creator.days").split("\n");
            return new EmbedMessage(bot, this.message.getJDA().getSelfUser(), 
                    bot.getLanguageManager().get("program-creator.stages.lesson.title", getTime(), days[getDay()]),
                    bot.getLanguageManager().get("program-creator.stages.lesson.description", getTime(), days[getDay()]))
                    .build();
        }
    }

    private ArrayList<String> getOptions() {
        ArrayList<String> list = new ArrayList<>();
        if (stage + 1 == totalStages) {
            for (EnumColor l : EnumColor.values()) {
                list.add(l.name());
            }
        }
        else if (stage == 0) {
            list.add("morning");
            list.add("afternoon");
        }
        else {
            for (EnumLesson l : EnumLesson.values()) {
                list.add(l.name());
            }
        }
        return list;
    }

    private ArrayList<Button> getNavigation() {
        Button prev = Button.primary("prev", bot.getLanguageManager().get("program-creator.buttons.previous"));
        Button next = Button.primary("next", bot.getLanguageManager().get("program-creator.buttons.next"));
        Button cancel = Button.danger("cancel", bot.getLanguageManager().get("program-creator.buttons.cancel"));

        ArrayList<Button> buttons = new ArrayList<>();
        if (stage + 2 >= totalStages && stage != 0) {
            prev = prev.asDisabled();
            next = next.asDisabled();
            cancel = cancel.asDisabled();
        } else if (stage == 0) {
            prev = prev.asDisabled();
            if (maxStage == 0) {
                next = next.asDisabled();
            }
        }
        buttons.add(cancel);
        buttons.add(prev);
        buttons.add(next);
        return buttons;
    }

    public StringSelectMenu getChoices() {
        StringSelectMenu.Builder builder = StringSelectMenu.create("choices");
        if (stage == 0) {
            builder.addOption(bot.getLanguageManager().get("program-creator.buttons.morning.text"),
                    "morning", bot.getLanguageManager().get("program-creator.buttons.morning.emoji"));
            builder.addOption(bot.getLanguageManager().get("program-creator.buttons.afternoon.text"),
                    "afternoon", bot.getLanguageManager().get("program-creator.buttons.afternoon.emoji"));
        } else if (stage + 1 == totalStages) {
            for (EnumColor c : EnumColor.values()) {
                builder.addOption(c.getName(), c.name(), Emoji.fromUnicode(c.getEmote()));
            }
        } else {
            for (EnumLesson c : EnumLesson.values()) {
                builder.addOption(c.getName(), c.name(), Emoji.fromUnicode(c.getEmoji()));
            }
        }
        if (stage + 2 >= totalStages && stage != 0) {
            builder.setDisabled(true);
        }
        return builder.build();
    }

    private int getDay() {
        if (dayIndex != Math.floorDiv((stage-1), stagesPerDay)) {
            this.dayIndex = Math.floorDiv((stage-1), stagesPerDay);
        }
        return dayIndex;
    }

    private String getTime() {
        dayIndex = getDay();
        int timeIndex = stage- 1 - dayIndex * stagesPerDay;
        this.time = startingTime + (timeIndex * 45L * 60L * 1000L);
        return new SimpleDateFormat("HH:mm").format(new Date(time));
    }

    private void setVars() {
        if ((totalStages == 0 || stagesPerDay == 0) && stage > 0) {
            String typeOfProgram = data.getAsJsonPrimitive("0").getAsString();
            if (typeOfProgram.equals("m")) {
                totalStages = 5 * 7 + 2;
                stagesPerDay = 7;
                startingTime = Long.parseLong(bot.getConfigManager().get("features.program-creator.startingTimeMorning"));
            }
            else if (typeOfProgram.equals("a")) {
                totalStages = 5 * 12 + 2;
                stagesPerDay = 12;
                startingTime = Long.parseLong(bot.getConfigManager().get("features.program-creator.startingTimeAfternoon"));
            }
        }
    }

    private void sendFinished() {
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
        for (int i = 1; i < 6; i++) {
            int offset = i * (finalImage.getWidth() - 7 * (STROKE + 1))/6 + (i - 1) * (STROKE + 1);
            graphics.drawLine(3 * STROKE/2 + 1 + offset, STROKE, 3 * STROKE/2 + 1 + offset, finalImage.getHeight() - STROKE + 1);
        }
        //make horizontal lines
        int countOfLines = stagesPerDay + 1;
        for (int i = 1; i < countOfLines; i++) {
            int offset = i * (finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines + (i - 1) * (STROKE + 1);
            graphics.drawLine(STROKE, 3 * STROKE/2 + 1 + offset, finalImage.getWidth() - STROKE + 1, 3 * STROKE/2 + 1 + offset);
        }
        graphics.setColor(Color.BLACK);

        //write days
        String[] days = bot.getLanguageManager().get("program-creator.days").split("\n");
        for (int i = 1; i < 6; i++) {
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
        String[] times = (stagesPerDay == 7) ? new String[] {"8:15", "9:00", "9:55", "10:55", "11:50", "12:45", "13:30"}
        : new String[] {"15:00", "15:45", "16:30", "17:15", "18:00", "18:45", "19:30", "20:15", "21:00", "21:45", "22:30", "23:15"};
        for (int i = 1; i < countOfLines; i++) {
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
        for (int i = 1; i <= 5 * stagesPerDay; i++) {
            String lesson = EnumLesson.values()[Integer.parseInt(data.get(String.valueOf(i)).getAsString())].getName();
            if (Integer.parseInt(data.get(String.valueOf(i)).getAsString()) == 15) continue;
            int x = (int) ((STROKE + 1 + ((finalImage.getWidth() - 7 * (STROKE + 1))/6)) * ( Math.floorDiv(i - 1, stagesPerDay) + 1.5));
            int y = (int) ((STROKE + 1 + ((finalImage.getHeight() - (countOfLines + 1) * (STROKE + 1))/countOfLines)) * ((i - 1) % stagesPerDay + 1.5));
            if (lesson.contains(" ")) {
                String[] lessons = lesson.split(" ");
                for (int c = 0; c < lessons.length; c++) {
                    graphics.drawString(lessons[c], x - (graphics.getFontMetrics().stringWidth(lessons[c]))/2, (int) (y + graphics.getFontMetrics().getStringBounds(lesson, graphics).getHeight()/2) - 15 + c * 30);
                }
            }
            else {
                graphics.drawString(lesson, x - (graphics.getFontMetrics().stringWidth(lesson))/2, (int) (y + graphics.getFontMetrics().getStringBounds(lesson, graphics).getHeight()/2));
            }
        }

        //send image
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "png", bytes);
            bytes.flush();
            FileUtils.writeByteArrayToFile(new File("./saved-programs/" + user.getName() + "-" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()))), bytes.toByteArray());
            message.getChannel().sendFiles(FileUpload.fromData(bytes.toByteArray(), "program.png")).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
