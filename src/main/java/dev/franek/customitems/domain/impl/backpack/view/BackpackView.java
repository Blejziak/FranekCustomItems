package dev.franek.customitems.domain.impl.backpack.view;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataRepository;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataStorage;
import dev.franek.customitems.domain.impl.backpack.view.BackpackViewHolder;
import dev.franek.customitems.shared.util.ColorUtils;

public class BackpackView {
    public static final int INVENTORY_SIZE = 9;
    public static final int CONTENT_SLOTS = 5;
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final BackpackDataStorage backpackDataStorage;
    private final BackpackDataRepository backpackDataRepository;

    public BackpackView(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, BackpackDataStorage backpackDataStorage, BackpackDataRepository backpackDataRepository) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.backpackDataStorage = backpackDataStorage;
        this.backpackDataRepository = backpackDataRepository;
    }

    public Inventory getInventory(BackpackData backpackData) {
        BackpackViewHolder inventoryHolder = new BackpackViewHolder(backpackData.getUniqueId());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)inventoryHolder, (int)9, (String)ColorUtils.fixColors(this.configuration.getBackpackItem().getViewTitle()));
        inventoryHolder.setInventory(inventory);
        for (int index = 0; index < Math.min(backpackData.getContent().size(), 5); ++index) {
            inventory.setItem(index, backpackData.getContent().get(index));
        }
        for (int i = 5; i < 9; ++i) {
            inventory.setItem(i, this.configuration.getBackpackItem().getFillerItem());
        }
        return inventory;
    }
}

