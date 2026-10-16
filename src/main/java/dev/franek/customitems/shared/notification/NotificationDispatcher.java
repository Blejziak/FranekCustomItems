package dev.franek.customitems.shared.notification;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.franek.customitems.shared.notification.NotificationType;
import dev.franek.customitems.shared.util.ColorUtils;

public class NotificationDispatcher {
    public static void sendNotification(CommandSender sender, NotificationType type, String content) {
        if (!(sender instanceof Player) && type != NotificationType.CHAT) {
            sender.sendMessage(ColorUtils.fixColors(content));
            return;
        }
        Player player = (Player)sender;
        switch (type) {
            case CHAT: {
                sender.sendMessage(ColorUtils.fixColors(content));
                break;
            }
            case ACTIONBAR: {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((String)ColorUtils.fixColors(content)));
                break;
            }
            case TITLE: {
                player.sendTitle(ColorUtils.fixColors(content), "", 10, 30, 40);
                break;
            }
            case SUBTITLE: {
                player.sendTitle("", ColorUtils.fixColors(content), 10, 30, 40);
                break;
            }
            case TITLE_AND_SUBTITLE: {
                String[] contentSeparator = content.split("<N>");
                player.sendTitle(ColorUtils.fixColors(contentSeparator[0]), ColorUtils.fixColors(contentSeparator[1]), 10, 30, 40);
            }
        }
    }
}

