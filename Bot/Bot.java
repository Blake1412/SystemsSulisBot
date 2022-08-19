import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Bot {

    private static final MessageChannel updates;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("/home/ubuntu/SystemsSulisBot/config.properties"));

            JDA bot = JDABuilder.createDefault(properties.getProperty("BotAccessToken")).build().awaitReady();
            updates = bot.getTextChannelsByName("general", true).get(0);
            bot.addEventListener(new Listener(updates));
        } catch (InterruptedException | LoginException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkForNew() {
        List<Assignment> assignments = DatabaseConnection.selectAssignments(List.of("deployed = 'false'"));
        if (!assignments.isEmpty()) {
            updates.sendMessageEmbeds(assignments.stream().map(assignment -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(new Color(36, 138, 46))
                            .setAuthor("A new assignment has been posted for " + assignment.getModule())
                            .setTitle(assignment.getModule() + " - " + assignment.getTitle(), assignment.getLink())
                            .addField(new MessageEmbed.Field("Module", assignment.getModule(), true))
                            .addField(new MessageEmbed.Field("Open Date", assignment.getOpenDate(), true))
                            .addField(new MessageEmbed.Field("Due Date", assignment.getDueDate(), true));

                return embedBuilder.build();
            }).toList()).queue();
            assignments.forEach(assignment -> DatabaseConnection.update(assignment, Map.of("deployed", "true")));
        }

        List<Announcement> announcements = DatabaseConnection.selectAnnouncements(List.of("deployed = 'false'"));
        if (!announcements.isEmpty()) {
            updates.sendMessageEmbeds(announcements.stream().map(announcement -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(new Color(36, 138, 46))
                            .setAuthor("A new announcement has been posted for " + announcement.getModule())
                            .setTitle(announcement.getModule() + " - " + announcement.getTitle(), announcement.getLink())
                            .addField(new MessageEmbed.Field("Module", announcement.getModule(), true))
                            .addField(new MessageEmbed.Field("Posted At", announcement.getPostedAt(), true))
                            .addField(new MessageEmbed.Field("Posted By", announcement.getAuthor(), true));

                return embedBuilder.build();
            }).toList()).queue();
            announcements.forEach(announcement -> DatabaseConnection.update(announcement, Map.of("deployed", "true")));
        }
    }
}
