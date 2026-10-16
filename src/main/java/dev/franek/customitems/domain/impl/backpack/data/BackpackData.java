package dev.franek.customitems.domain.impl.backpack.data;

import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class BackpackData {
    private final UUID uniqueId;
    private List<ItemStack> content;

    public BackpackData(UUID uniqueId, List<ItemStack> content) {
        this.uniqueId = uniqueId;
        this.content = content;
    }
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    public List<ItemStack> getContent() {
        return this.content;
    }
}

