package dev.franek.customitems.player;

import dev.franek.customitems.player.PlayerData;
import dev.franek.customitems.player.PlayerDataRepository;
import dev.franek.customitems.player.PlayerDataStorage;

public class PlayerDataService {
    private final PlayerDataStorage playerDataStorage;
    private final PlayerDataRepository playerDataRepository;

    public PlayerData create(PlayerData playerData) {
        this.playerDataStorage.add(playerData);
        this.playerDataRepository.save(playerData, true);
        return playerData;
    }
    public PlayerDataService(PlayerDataStorage playerDataStorage, PlayerDataRepository playerDataRepository) {
        this.playerDataStorage = playerDataStorage;
        this.playerDataRepository = playerDataRepository;
    }
}

