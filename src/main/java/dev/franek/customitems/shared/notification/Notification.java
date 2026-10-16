package dev.franek.customitems.shared.notification;

import org.bukkit.command.CommandSender;
import dev.franek.customitems.shared.notification.NotificationDispatcher;
import dev.franek.customitems.shared.notification.NotificationType;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;

public class Notification {
    private NotificationType type;
    private String content;

    public void send(CommandSender commandSender) {
        this.send(commandSender, null);
    }

    public void send(CommandSender commandSender, PlaceholderSet placeholderSet) {
        if (placeholderSet == null) {
            NotificationDispatcher.sendNotification(commandSender, this.type, this.content);
            return;
        }
        String preparedContent = placeholderSet.prepareAndGetResult(this.content);
        NotificationDispatcher.sendNotification(commandSender, this.type, preparedContent);
    }
    public NotificationType getType() {
        return this.type;
    }
    public String getContent() {
        return this.content;
    }
    public Notification(NotificationType type, String content) {
        this.type = type;
        this.content = content;
    }
}

