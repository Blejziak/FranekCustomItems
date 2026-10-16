package dev.franek.customitems.cooldown;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import dev.franek.customitems.shared.util.TimeUtils;

public class CooldownController {
    private final Map<UUID, HashMap<String, Instant>> cooldowns = new HashMap<UUID, HashMap<String, Instant>>();

    public boolean canUse(Player player, String itemKey, int cooldownInSeconds) {
        Map playerCooldowns = this.cooldowns.get(player.getUniqueId());
        if (playerCooldowns != null && playerCooldowns.containsKey(itemKey)) {
            if (!TimeUtils.hasPassed((Instant)playerCooldowns.get(itemKey))) {
                return false;
            }
            playerCooldowns.remove(itemKey);
        }
        this.cooldowns.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap()).put(itemKey, Instant.now().plusSeconds(cooldownInSeconds));
        return true;
    }

    public String getFormattedRemainingCooldown(Player player, String itemKey) {
        return this.getFormattedRemainingCooldown(player, itemKey, false);
    }

    public String getFormattedRemainingCooldown(Player player, String itemKey, boolean shortForm) {
        Map playerCooldowns = this.cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || !playerCooldowns.containsKey(itemKey)) {
            return "teraz";
        }
        return TimeUtils.getFormattedTime(TimeUtils.getRemainingDuration((Instant)playerCooldowns.get(itemKey)), shortForm);
    }

    public boolean hasCooldown(Player player, String itemKey) {
        Map playerCooldowns = this.cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || !playerCooldowns.containsKey(itemKey)) {
            return false;
        }
        return !TimeUtils.hasPassed((Instant)playerCooldowns.get(itemKey));
    }

    public void clearPlayer(UUID uuid) {
        this.cooldowns.remove(uuid);
    }
}

