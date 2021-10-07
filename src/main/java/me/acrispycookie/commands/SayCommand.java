package me.acrispycookie.commands;

import me.acrispycookie.Console;
import me.acrispycookie.Main;
import me.acrispycookie.levelsystem.LevelUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SayCommand extends BotCommand {

    @Override
    public void execute(String[] args, String label, Member m, TextChannel t, List<Member> mentions, List<Role> mentionedRoles, List<Message.Attachment> attachments, Message message) {
        if(args.length > 0){
            String msg = Arrays.stream(args).map(Objects::toString).collect(Collectors.joining(" "));
            t.sendFile(getImage(msg), "kouk.png").queue();
        }
        else{
            t.sendFile(getImage(Main.getInstance().getLanguageManager().getRandomLevelUp(LevelUser.getByDiscordId(m.getIdLong()).getSpecialLevelUp())), "kouk.png").queue();
        }
    }

    private byte[] getImage(String msg) {
        final int X_CALLOUT = 190;
        final int Y_CALLOUT = 19;
        final int X_TEXT = 244;
        final int Y_TEXT = 42;
        try {
            Font font = new Font("Cera Pro", Font.PLAIN, 70);
            BufferedImage image = ImageIO.read(new File("./images/kouk.png"));
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setFont(font);
            graphics.setColor(Color.BLACK);
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(ImageIO.read(new File("./images/callout.png")), X_CALLOUT, Y_CALLOUT, null);

            //DRAW TEXT
            HashMap<TextAttribute, Object> map = new HashMap<TextAttribute, Object>() {{
                put(TextAttribute.FAMILY, "Cera Pro");
                put(TextAttribute.SIZE, 70f);
            }};
            AttributedCharacterIterator paragraph = new AttributedString(msg, map).getIterator();
            int paragraphStart = paragraph.getBeginIndex();
            int paragraphEnd = paragraph.getEndIndex();
            FontRenderContext frc = graphics.getFontRenderContext();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
            float breakWidth = (float) 800 - 2 * (X_TEXT - X_CALLOUT);
            float drawPosY = Y_TEXT;
            lineMeasurer.setPosition(paragraphStart);
            while (lineMeasurer.getPosition() < paragraphEnd) {
                TextLayout layout = lineMeasurer.nextLayout(breakWidth);
                float drawPosX = layout.isLeftToRight()
                        ? X_TEXT : breakWidth - layout.getAdvance();
                drawPosY += layout.getAscent();
                layout.draw(graphics, drawPosX, drawPosY);
                drawPosY += layout.getDescent() + layout.getLeading();
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes);
            bytes.flush();
            return bytes.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[] {};
        }
    }
}
