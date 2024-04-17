package dev.acrispycookie.levelsystem;

import dev.acrispycookie.Main;
import dev.acrispycookie.utility.Utils;
import net.dv8tion.jda.api.OnlineStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class UserCard {

    private final int NAME_TAG_SPACE_PIXELS = 8;
    private final int SPACE_BETWEEN_REQUIRED_AND_CURRENT_EXP = 4;

    private final int REQUIRED_XP_SPACE_PIXELS = 50;
    private final int LEVEL_SPACE_PIXELS = 55;

    private final int SMALL_FONT_SIZE = 24;
    private final int LARGE_FONT_SIZE = 40;
    private final int EXTRA_LARGE_FONT_SIZE = 60;

    private final Color WHITE = Utils.hex2Rgb("#ffffff");
    private final Color GRAY = Utils.hex2Rgb("#7F8384");
    private final Color ONLINE_GREEN = Utils.hex2Rgb("#43b581");
    private final Color BLACK = Utils.hex2Rgb("#000000");

    private Color mainColor;
    private LevelUser user;
    private Font font;
    private BufferedImage backround;
    private BufferedImage finalImage;
    private Graphics2D graphics;

    public UserCard(LevelUser user){
        this.user = user;
        try {
            this.mainColor = Utils.hex2Rgb(user.getCardColor());
            this.font = new Font("Sarine-Regular", Font.PLAIN, LARGE_FONT_SIZE);
            this.backround = ImageIO.read(new File("./images/template.png"));
            this.finalImage = new BufferedImage(934, 282, BufferedImage.TYPE_INT_ARGB);
            this.graphics = (Graphics2D) finalImage.getGraphics();
            graphics.setFont(font);
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserCard(LevelUser user, Font font, BufferedImage backround){
        this.user = user;
        this.font = font;
        this.backround = backround;
        this.finalImage = new BufferedImage(934, 282, BufferedImage.TYPE_INT_ARGB);
        this.graphics = (Graphics2D) finalImage.getGraphics();
        graphics.setFont(font);
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public BufferedImage build() throws IOException {
        addPfp();
        addStatus();
        addBar();
        addBackground();
        addTag();
        addExp();
        addRankLevel();
        return finalImage;
    }

    public void addPfp() throws IOException {
        graphics.setColor(Utils.hex2Rgb("#484B4E"));
        graphics.fillRect(0, 0, finalImage.getWidth(), finalImage.getHeight());
        HttpURLConnection connection = (HttpURLConnection) new URL(user.getDiscordUser().getAvatarUrl()).openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
        BufferedImage profilePicture = ImageIO.read(connection.getInputStream());
        connection.getInputStream().close();
        graphics.drawImage(profilePicture, 42, 62,160, 160, null);
    }

    private void addBackground(){
        graphics.drawImage(backround, 0, 0, null);
    }

    private void addStatus(){
        graphics.setColor(getColorByStatus());
        graphics.fillOval(162, 172,42,42);
    }

    private void addBar(){
        graphics.setColor(mainColor);
        graphics.fillRoundRect(256, 185, (int) Math.round((((double) user.getXp() / (double) user.getXpRequired()) * 633)), 35, 35, 35);
    }

    private void addTag(){
        font = new Font("Cera Pro", Font.PLAIN, LARGE_FONT_SIZE);
        graphics.setFont(font);
        int offsetX = width(getNameLine());
        graphics.setColor(WHITE);
        graphics.drawString(getNameLine(), 272, 165);
//        font = font.deriveFont(Font.PLAIN, SMALL_FONT_SIZE);
//        graphics.setFont(font);
//        graphics.setColor(GRAY);
//        graphics.drawString("#" + getTag(), 272 + offsetX + NAME_TAG_SPACE_PIXELS, 165);
    }

    private void addExp(){
        font = new Font("Sarine-Regular", Font.PLAIN, SMALL_FONT_SIZE);
        graphics.setFont(font);
        graphics.setColor(GRAY);
        graphics.drawString(getRequiredXp(), finalImage.getWidth() - width(getRequiredXp()) - REQUIRED_XP_SPACE_PIXELS, 165);
        graphics.setColor(WHITE);
        graphics.drawString(getXpLine(), finalImage.getWidth() - REQUIRED_XP_SPACE_PIXELS - SPACE_BETWEEN_REQUIRED_AND_CURRENT_EXP - width(getXpLine()) - width(getRequiredXp()), 165);
    }

    private void addRankLevel(){
        int levelWidth = width("LEVEL");
        int rankWidth = width("RANK");
        font = font.deriveFont(Font.PLAIN, EXTRA_LARGE_FONT_SIZE);
        graphics.setFont(font);
        int numberWidth = width("#" + user.getRank());
        int levelNumberWidth = width(String.valueOf(user.getLevel()));
        graphics.setColor(mainColor);
        graphics.drawString(String.valueOf(user.getLevel()), finalImage.getWidth() - levelNumberWidth - LEVEL_SPACE_PIXELS, 95);
        font = font.deriveFont(Font.PLAIN, SMALL_FONT_SIZE);
        graphics.setFont(font);
        graphics.drawString("LEVEL", finalImage.getWidth() - levelNumberWidth - LEVEL_SPACE_PIXELS - 6 - levelWidth, 95);
        font = font.deriveFont(Font.PLAIN, EXTRA_LARGE_FONT_SIZE);
        graphics.setFont(font);
        graphics.setColor(WHITE);
        graphics.drawString("#" + user.getRank(), finalImage.getWidth() - levelNumberWidth - LEVEL_SPACE_PIXELS - 6 - levelWidth - 15 - numberWidth, 95);
        font = font.deriveFont(Font.PLAIN, SMALL_FONT_SIZE);
        graphics.setFont(font);
        graphics.setColor(WHITE);
        graphics.drawString("RANK", finalImage.getWidth() - levelNumberWidth - LEVEL_SPACE_PIXELS - 6 - levelWidth - 15 - numberWidth - 6 - rankWidth, 95);
    }

    private String getXpLine(){
        String s = "";
        DecimalFormat d = new DecimalFormat("#.##");
        if(user.getXp() < 5000){
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            s = nf.format(user.getXp());
        }
        else{
            if(user.getXp() >= 1000000000) {
                s = s + (d.format((double) user.getXp() / 1000000000)) + "B";
            }
            else if(user.getXp() >= 1000000) {
                s = s + (d.format((double) user.getXp() / 1000000)) + "M";
            }
            else {
                s = s + (d.format((double) user.getXp() / 1000)) + "K";
            }
        }
        return s;
    }

    private String getRequiredXp(){
        String s = "/ ";
        DecimalFormat d = new DecimalFormat("#.##");
        if(user.getXpRequired() >= 1000000000) {
            s = s + (d.format((double) user.getXpRequired() / 1000000000)) + "B";
        }
        else if(user.getXpRequired() >= 1000000) {
            s = s + (d.format((double) user.getXpRequired() / 1000000)) + "M";
        }
        else if(user.getXpRequired() >= 1000) {
            s = s + (d.format((double) user.getXpRequired() / 1000)) + "K";
        }
        else {
            s = s + user.getXpRequired();
        }
        s = s + " XP";
        return s;
    }

    private Color getColorByStatus(){
        OnlineStatus status = Main.getInstance().getDiscordMember(user.getDiscordUser()).getOnlineStatus();
        if(status == OnlineStatus.ONLINE){
            return mainColor;
        }
        else if(status == OnlineStatus.OFFLINE || status == OnlineStatus.INVISIBLE || status == OnlineStatus.UNKNOWN){
            return Utils.hex2Rgb("#747f8d");
        }
        else if(status == OnlineStatus.IDLE){
            return Utils.hex2Rgb("#faa61a");
        }
        else if(status == OnlineStatus.DO_NOT_DISTURB){
            return Utils.hex2Rgb("#f04747");
        }
        return mainColor;
    }

    private String getNameLine(){
        return Main.getInstance().getDiscordMember(user.getDiscordUser()).getEffectiveName().length() > 16 ?
                Main.getInstance().getDiscordMember(user.getDiscordUser()).getEffectiveName().substring(0,16) :
                Main.getInstance().getDiscordMember(user.getDiscordUser()).getEffectiveName();
    }

    private String getTag(){
        return user.getDiscordUser().getDiscriminator();
    }

    private int width(String s){
        FontMetrics metrics = graphics.getFontMetrics();
        return metrics.stringWidth(s);
    }
}
