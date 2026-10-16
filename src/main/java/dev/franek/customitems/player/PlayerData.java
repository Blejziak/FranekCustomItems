package dev.franek.customitems.player;

import java.util.UUID;

public class PlayerData {
    private final UUID uniqueId;
    private String name;

    public PlayerData(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

