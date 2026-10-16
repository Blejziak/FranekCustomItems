package dev.franek.customitems.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.player.PlayerData;
import dev.franek.customitems.player.PlayerDataRepository;
import dev.franek.customitems.player.PlayerDataService;
import dev.franek.customitems.player.PlayerDataStorage;

public class PlayerDataListener
implements Listener {
    private final PlayerDataStorage playerDataStorage;
    private final PlayerDataRepository playerDataRepository;
    private final PlayerDataService playerDataService;
    private final CooldownController cooldownController;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.playerDataStorage.contains(player.getUniqueId())) {
            this.playerDataService.create(new PlayerData(player.getUniqueId(), player.getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.cooldownController.clearPlayer(player.getUniqueId());
        if (!this.playerDataStorage.contains(player.getUniqueId())) {
            return;
        }
        PlayerData playerData = this.playerDataStorage.get(player.getUniqueId());
        this.playerDataRepository.save(playerData, false);
    }
    public PlayerDataListener(PlayerDataStorage playerDataStorage, PlayerDataRepository playerDataRepository, PlayerDataService playerDataService, CooldownController cooldownController) {
        this.playerDataStorage = playerDataStorage;
        this.playerDataRepository = playerDataRepository;
        this.playerDataService = playerDataService;
        this.cooldownController = cooldownController;
    }
}

