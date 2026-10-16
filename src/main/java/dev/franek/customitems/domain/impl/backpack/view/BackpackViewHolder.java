package dev.franek.customitems.domain.impl.backpack.view;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BackpackViewHolder
implements InventoryHolder {
    private final UUID backpackUniqueId;
    private Inventory inventory;

    public Inventory getInventory() {
        return this.inventory;
    }
    public UUID getBackpackUniqueId() {
        return this.backpackUniqueId;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public BackpackViewHolder(UUID backpackUniqueId) {
        this.backpackUniqueId = backpackUniqueId;
    }
}

