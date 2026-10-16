package dev.franek.customitems.cooldown;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItemStorage;
import dev.franek.customitems.shared.notification.NotificationDispatcher;
import dev.franek.customitems.shared.notification.NotificationType;

public class CooldownActionBarTask
implements Runnable {
    private final CustomItemStorage customItemStorage;
    private final CooldownController cooldownController;

    public CooldownActionBarTask(CustomItemStorage customItemStorage, CooldownController cooldownController) {
        this.customItemStorage = customItemStorage;
        this.cooldownController = cooldownController;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ArrayList<String> activeCooldowns = new ArrayList<String>();
            for (Map.Entry<String, String> entry : this.customItemStorage.getCustomItemDisplayNameMap().entrySet()) {
                String itemKey = entry.getKey();
                if (!this.cooldownController.hasCooldown(player, itemKey)) continue;
                String formattedTime = this.cooldownController.getFormattedRemainingCooldown(player, itemKey, true);
                activeCooldowns.add(entry.getValue() + " &8(&f" + formattedTime + "&8)");
            }
            if (activeCooldowns.isEmpty()) continue;
            NotificationDispatcher.sendNotification((CommandSender)player, NotificationType.ACTIONBAR, String.join((CharSequence)" &8| ", activeCooldowns));
        }
    }
}

