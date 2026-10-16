package dev.franek.customitems.domain.impl.backpack.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;

public class BackpackDataStorage {
    private final Map<UUID, BackpackData> backpackDataByUniqueId = new ConcurrentHashMap<UUID, BackpackData>();

    public void add(BackpackData backpackData) {
        this.backpackDataByUniqueId.put(backpackData.getUniqueId(), backpackData);
    }

    public void remove(BackpackData backpackData) {
        this.remove(backpackData.getUniqueId());
    }

    public void remove(UUID uniqueId) {
        this.backpackDataByUniqueId.remove(uniqueId);
    }

    public boolean contains(UUID uniqueId) {
        return this.backpackDataByUniqueId.containsKey(uniqueId);
    }

    public BackpackData get(UUID uniqueId) {
        return this.backpackDataByUniqueId.get(uniqueId);
    }

    public Map<UUID, BackpackData> getBackpackDataMap() {
        return this.backpackDataByUniqueId;
    }
}

