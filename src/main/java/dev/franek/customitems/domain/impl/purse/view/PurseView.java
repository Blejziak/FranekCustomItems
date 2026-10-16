package dev.franek.customitems.domain.impl.purse.view;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.impl.purse.data.PurseData;
import dev.franek.customitems.domain.impl.purse.data.PurseDataRepository;
import dev.franek.customitems.domain.impl.purse.data.PurseDataStorage;
import dev.franek.customitems.domain.impl.purse.view.PurseViewHolder;
import dev.franek.customitems.shared.util.ColorUtils;

public class PurseView
implements Listener {
    public static final int INVENTORY_SIZE = 54;
    public static final int CONTENT_SLOTS = 45;
    public static final int WITHDRAW_SLOT = 49;
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final PurseDataStorage purseDataStorage;
    private final PurseDataRepository purseDataRepository;
    private final NamespacedKey purseUuidKey;

    public PurseView(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, PurseDataStorage purseDataStorage, PurseDataRepository purseDataRepository) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.purseDataStorage = purseDataStorage;
        this.purseDataRepository = purseDataRepository;
        this.purseUuidKey = new NamespacedKey((Plugin)plugin, "purse_uuid");
    }

    public Inventory openInventory(PurseData purseData) {
        PurseViewHolder inventoryHolder = new PurseViewHolder(purseData.getUniqueId());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)inventoryHolder, (int)54, (String)ColorUtils.fixColors(this.configuration.getPurseItem().getViewTitle()));
        inventoryHolder.setInventory(inventory);
        for (int index = 0; index < Math.min(purseData.getContent().size(), 45); ++index) {
            inventory.setItem(index, purseData.getContent().get(index));
        }
        inventory.setItem(49, this.configuration.getPurseItem().getWithdrawItem());
        return inventory;
    }
}

