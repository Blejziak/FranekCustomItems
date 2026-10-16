package dev.franek.customitems.domain.impl.purse.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import dev.franek.customitems.domain.impl.purse.data.PurseData;

public class PurseDataStorage {
    private final Map<UUID, PurseData> purseDataByUniqueId = new ConcurrentHashMap<UUID, PurseData>();

    public void add(PurseData purseData) {
        this.purseDataByUniqueId.put(purseData.getUniqueId(), purseData);
    }

    public void remove(PurseData purseData) {
        this.remove(purseData.getUniqueId());
    }

    public void remove(UUID uniqueId) {
        this.purseDataByUniqueId.remove(uniqueId);
    }

    public boolean contains(UUID uniqueId) {
        return this.purseDataByUniqueId.containsKey(uniqueId);
    }

    public PurseData get(UUID uniqueId) {
        return this.purseDataByUniqueId.get(uniqueId);
    }

    public Map<UUID, PurseData> getPurseDataMap() {
        return this.purseDataByUniqueId;
    }
}

