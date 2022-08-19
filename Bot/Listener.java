import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Listener implements EventListener {

    private final MessageChannel updatesChannel;
    private MessageChannel channel;

    public Listener(MessageChannel updatesChannel) {
        this.updatesChannel = updatesChannel;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        MessageReceivedEvent event;
        String message;
        if (genericEvent instanceof MessageReceivedEvent) {
            event = (MessageReceivedEvent) genericEvent;
            if (!event.getAuthor().isBot()) {
                message = event.getMessage().getContentRaw();
                channel = event.getChannel();
                if (message.charAt(0) == '!') {
                    if ("assignments".equalsIgnoreCase(message.substring(1))) {
                        getDueAssignments();
                    } else {
                        sendMessage("Command not recognised");
                    }
                }
            }
        }
    }

    public void getDueAssignments() {
        String timeNow = LocalDateTime.now().plus(Duration.ofHours(1)).toString();
        List<Assignment> assignments = DatabaseConnection.selectAssignments(List.of(String.format("dueDate > '%s'", timeNow)));
        if (!assignments.isEmpty()) {
            updatesChannel.sendMessageEmbeds(assignments.stream().map(assignment -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(new Color(36, 138, 46))
                            .setAuthor("SulisBot")
                            .setTitle(assignment.getModule() + " - " + assignment.getTitle(), assignment.getLink())
                            .addField(new MessageEmbed.Field("Module", assignment.getModule(), true))
                            .addField(new MessageEmbed.Field("Due in", getTimeUntilDue(assignment.getDueDate()), true));

                return embedBuilder.build();
            }).toList()).queue();
        } else {
            sendMessage("No due assignments");
        }
    }

    private void sendMessage(String text) {
        Message message = new MessageBuilder(text).build();
        channel.sendMessage(message).queue();
    }

    private String getTimeUntilDue(String dueDateString) {
        long time = LocalDateTime.now().until(LocalDateTime.parse(dueDateString.replace(" ", "T")), ChronoUnit.MILLIS);
        long seconds = time / 1000 % 60;
        long minutes = time / (1000 * 60) % 60;
        long hours = time / (1000 * 60 * 60) % 24;
        long days = time / (1000 * 60 * 60 * 24);
        return String.format(
                "%s %s %s %s",
                days > 0 ? days + "d" : "",
                hours > 0 ? hours + "h" : "",
                minutes > 0 ? minutes + "m" : "",
                seconds > 0 ? seconds + "s" : ""
        );
    }
}
