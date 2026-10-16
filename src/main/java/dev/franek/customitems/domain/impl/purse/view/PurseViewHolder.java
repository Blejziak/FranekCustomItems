package dev.franek.customitems.domain.impl.purse.view;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PurseViewHolder
implements InventoryHolder {
    private final UUID purseUniqueId;
    private Inventory inventory;

    public Inventory getInventory() {
        return this.inventory;
    }
    public UUID getPurseUniqueId() {
        return this.purseUniqueId;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public PurseViewHolder(UUID purseUniqueId) {
        this.purseUniqueId = purseUniqueId;
    }
}

