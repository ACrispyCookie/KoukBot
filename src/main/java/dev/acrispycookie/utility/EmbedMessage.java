package dev.acrispycookie.utility;

import dev.acrispycookie.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class EmbedMessage {

    User user;
    String title;
    String content;
    Color color;
    String imageUrl;

    public EmbedMessage(User user, String title, String content, Color color) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public EmbedMessage(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.color = Main.getInstance().getBotColor();
    }

    public void setImage(String url) {
        this.imageUrl = url;
    }

    public MessageEmbed build(){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(content);
        builder.setColor(color);
        builder.setFooter("Command executed by " + Main.getInstance().getDiscordMember(user).getEffectiveName());
        if(imageUrl != null) {
            builder.setThumbnail(imageUrl);
        }
        return builder.build();
    }

}
