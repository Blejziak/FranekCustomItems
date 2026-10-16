package dev.franek.customitems.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import dev.franek.customitems.player.PlayerData;

public class PlayerDataStorage {
    private final Map<UUID, PlayerData> playerDataByUniqueId = new ConcurrentHashMap<UUID, PlayerData>();
    private final Map<String, PlayerData> playerDataByName = new ConcurrentHashMap<String, PlayerData>();

    public void add(PlayerData playerData) {
        this.playerDataByUniqueId.put(playerData.getUniqueId(), playerData);
        this.playerDataByName.put(playerData.getName(), playerData);
    }

    public boolean contains(UUID uniqueId) {
        return this.playerDataByUniqueId.containsKey(uniqueId);
    }

    public boolean contains(String name) {
        return this.playerDataByName.containsKey(name);
    }

    public PlayerData get(UUID uniqueId) {
        return this.playerDataByUniqueId.get(uniqueId);
    }

    public PlayerData get(String name) {
        return this.playerDataByName.get(name);
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return this.playerDataByUniqueId;
    }
}

