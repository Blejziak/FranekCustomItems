package dev.franek.customitems.shared.bossbar;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarService {
    private final Map<UUID, Map<String, BossBar>> bossBars = new ConcurrentHashMap<UUID, Map<String, BossBar>>();

    public void sendBossBar(Player player, String bossBarCustomKey, BossBar bossBar) {
        if (this.hasBossBar(player.getUniqueId(), bossBarCustomKey)) {
            this.getBossBar(player.getUniqueId(), bossBarCustomKey).removePlayer(player);
            this.removeBossBar(player.getUniqueId(), bossBarCustomKey);
        }
        this.putBossBar(player.getUniqueId(), bossBarCustomKey, bossBar);
        bossBar.addPlayer(player);
    }

    public void updateBossBar(Player player, String bossBarCustomKey, String newTitle, double progress) {
        if (!this.hasBossBar(player.getUniqueId(), bossBarCustomKey)) {
            return;
        }
        BossBar bossBar = this.getBossBar(player.getUniqueId(), bossBarCustomKey);
        bossBar.setTitle(newTitle);
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
    }

    public void removeBossBar(Player player, String bossBarCustomKey) {
        if (!this.bossBars.containsKey(player.getUniqueId())) {
            return;
        }
        BossBar bossBar = this.getBossBar(player.getUniqueId(), bossBarCustomKey);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
        this.bossBars.get(player.getUniqueId()).remove(bossBarCustomKey);
    }

    public void removeAllBossBars(Player player) {
        if (!this.bossBars.containsKey(player.getUniqueId())) {
            return;
        }
        this.bossBars.get(player.getUniqueId()).values().forEach(bossBar -> bossBar.removePlayer(player));
        this.bossBars.get(player.getUniqueId()).clear();
    }

    public boolean hasBossBar(UUID uniqueId, String bossBarCustomKey) {
        if (!this.bossBars.containsKey(uniqueId)) {
            return false;
        }
        return this.bossBars.get(uniqueId).containsKey(bossBarCustomKey);
    }

    public BossBar getBossBar(UUID uniqueId, String bossBarCustomKey) {
        return this.bossBars.get(uniqueId).get(bossBarCustomKey);
    }

    private void putBossBar(UUID uniqueId, String bossBarCustomKey, BossBar bossBar) {
        this.bossBars.computeIfAbsent(uniqueId, ignored -> new ConcurrentHashMap()).put(bossBarCustomKey, bossBar);
    }

    private void removeBossBar(UUID uniqueId, String bossBarCustomKey) {
        this.bossBars.get(uniqueId).remove(bossBarCustomKey);
    }
}

