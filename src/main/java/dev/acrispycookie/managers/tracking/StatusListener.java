package dev.acrispycookie.managers.tracking;

import dev.acrispycookie.Main;
import dev.acrispycookie.utility.EmbedMessage;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StatusListener extends ListenerAdapter {

    final long ONLY_STATUS_ID = 1088520176565223504L;
    final long FULL_LOGS_ID = 1088308148072370236L;
    final long TO_TRACK_ID = 538435855589572609L;

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent e) {
        if(e.getMember().getIdLong() == TO_TRACK_ID) {
            StringBuilder content = new StringBuilder("``" + e.getOldOnlineStatus().name() + "`` -> ``" + e.getNewOnlineStatus().name() + "``\n\nCurrent statuses:");

            ClientType[] types = new ClientType[]{ClientType.WEB, ClientType.DESKTOP, ClientType.MOBILE};
            for (int i = 0; i < types.length; i++) {
                ClientType type = types[i];
                content.append("\n").append(type.name()).append(": ").append(e.getMember().getOnlineStatus(type).name());

            }
            EmbedMessage msg = new EmbedMessage(e.getJDA().getSelfUser(),
                    "Status change",
                    content.toString(),
                    Main.getInstance().getBotColor());
            e.getGuild().getTextChannelById(FULL_LOGS_ID).sendMessageEmbeds(msg.build()).queue();
            e.getGuild().getTextChannelById(ONLY_STATUS_ID).sendMessageEmbeds(msg.build()).queue();
        }
    }

    @Override
    public void onUserUpdateActivities(UserUpdateActivitiesEvent e) {
        if(e.getMember().getIdLong() == TO_TRACK_ID && e.getNewValue() != null && !e.getNewValue().equals(e.getOldValue())) {
            ArrayList<String> newA = buildActivityMessage(e.getNewValue());
            ArrayList<String> oldA = buildActivityMessage(e.getOldValue());
            String newActivityMsg = newA.get(0);
            String oldActivityMsg = oldA.get(0);
            String newStatus = newA.get(1);
            String oldStatus = oldA.get(1);
            String imageUrl = newA.get(2);

            if(!newActivityMsg.equals(oldActivityMsg)) {
                EmbedMessage msg = new EmbedMessage(e.getJDA().getSelfUser(),
                        "Activity change",
                        newActivityMsg,
                        Main.getInstance().getBotColor());
                msg.setImage(imageUrl);
                e.getGuild().getTextChannelById(FULL_LOGS_ID).sendMessageEmbeds(msg.build()).queue();
            }
            if(!newStatus.equals(oldStatus)) {
                EmbedMessage status = new EmbedMessage(e.getJDA().getSelfUser(),
                        "Custom status change",
                        "New custom status: ``" + newStatus + "``",
                        Main.getInstance().getBotColor());
                e.getGuild().getTextChannelById(ONLY_STATUS_ID).sendMessageEmbeds(status.build()).queue();
            }
        }
    }

    private ArrayList<String> buildActivityMessage(List<Activity> newAs) {
        StringBuilder newA = new StringBuilder("**New activities**\n");
        String newStatus = "";
        String imageUrl = null;
        for(Activity a : newAs) {
            newStatus = a.getType() == Activity.ActivityType.CUSTOM_STATUS ? a.getName() : newStatus;
            if(a.isRich()) {
                newA.append(" • Type: ``").append(a.getType().name()).append("``\nName: ``").append(a.getName());
                if(a.getType() == Activity.ActivityType.LISTENING) {
                    imageUrl = a.asRichPresence().getLargeImage().getUrl();
                    String artist = a.asRichPresence().getState();
                    String trackUrl = "https://open.spotify.com/track/" + a.asRichPresence().getSyncId();
                    long durationMillis = a.asRichPresence().getTimestamps().getEnd() - a.asRichPresence().getTimestamps().getStart();
                    long elapsedSeconds = a.asRichPresence().getTimestamps().getElapsedTime(ChronoUnit.SECONDS);
                    int durationSeconds = (int) (durationMillis / 1000);
                    Duration duration = Duration.ofSeconds(durationSeconds);
                    String formattedDuration = String.format("%d:%02d", duration.toMinutes(), durationSeconds % 60);
                    Duration elapsed = Duration.ofSeconds(elapsedSeconds);
                    String formattedElapsed = String.format("%d:%02d", elapsed.toMinutes(), elapsedSeconds % 60);

                    newA.append("``\nTrack: ``").append(a.asRichPresence().getDetails())
                            .append("``\nArtist: ``").append(artist)
                            .append("``\nElapsed: ``").append(formattedElapsed).append("/").append(formattedDuration)
                            .append("``\nURL: ``").append(trackUrl);
                } else {
                    newA.append("``\nDetails: ``").append(a.asRichPresence().getDetails());
                }
                newA.append("``\n\n");
            } else {
                newA.append(" • Type: ``").append(a.getType().name()).append("``\nName: ``").append(a.getName());
                if(a.getUrl() != null) {
                    newA.append("``\nURL: ``").append(a.getUrl());
                }
                newA.append("``\n\n");
            }
        }

        ArrayList<String> returns = new ArrayList<>();
        returns.add(newA.toString());
        returns.add(newStatus);
        returns.add(imageUrl);
        return returns;
    }
}
