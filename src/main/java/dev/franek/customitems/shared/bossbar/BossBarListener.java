package dev.franek.customitems.shared.bossbar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.franek.customitems.shared.bossbar.BossBarService;

public class BossBarListener
implements Listener {
    private final BossBarService bossBarService;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.bossBarService.removeAllBossBars(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.bossBarService.removeAllBossBars(event.getPlayer());
    }
    public BossBarListener(BossBarService bossBarService) {
        this.bossBarService = bossBarService;
    }
}

