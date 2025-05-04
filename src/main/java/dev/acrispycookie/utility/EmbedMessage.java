package dev.acrispycookie.utility;

import dev.acrispycookie.KoukBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class EmbedMessage {

    private final KoukBot bot;
    private final User user;
    private final String title;
    private final String content;
    private final Color color;
    private String imageUrl;

    public EmbedMessage(KoukBot bot, User user, String title, String content, Color color) {
        this.bot = bot;
        this.user = user;
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public EmbedMessage(KoukBot bot, User user, String title, String content) {
        this.bot = bot;
        this.user = user;
        this.title = title;
        this.content = content;
        this.color = bot.getBotColor();
    }

    public void setImage(String url) {
        this.imageUrl = url;
    }

    public MessageEmbed build() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(content);
        builder.setColor(color);
        builder.setFooter("Command executed by " + bot.getDiscordMember(user).getEffectiveName());
        if (imageUrl != null) {
            builder.setThumbnail(imageUrl);
        }
        return builder.build();
    }

}
