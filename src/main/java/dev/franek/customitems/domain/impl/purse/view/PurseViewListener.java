package dev.franek.customitems.domain.impl.purse.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.impl.purse.data.PurseData;
import dev.franek.customitems.domain.impl.purse.data.PurseDataRepository;
import dev.franek.customitems.domain.impl.purse.data.PurseDataStorage;
import dev.franek.customitems.domain.impl.purse.view.PurseViewHolder;

public class PurseViewListener
implements Listener {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final PurseDataStorage purseDataStorage;
    private final PurseDataRepository purseDataRepository;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder;
        Inventory inventory = event.getInventory();
        if (inventory.getLocation() != null || !((inventoryHolder = inventory.getHolder()) instanceof PurseViewHolder)) {
            return;
        }
        PurseViewHolder inventoryHolder2 = (PurseViewHolder)inventoryHolder;
        switch (event.getClick()) {
            case SHIFT_LEFT: 
            case SHIFT_RIGHT: 
            case NUMBER_KEY: 
            case DOUBLE_CLICK: {
                event.setCancelled(true);
                return;
            }
        }
        ItemStack clickedItem = event.getCurrentItem();
        int clickedSlot = event.getRawSlot();
        if (clickedSlot < 0 || clickedSlot >= inventory.getSize() || clickedItem == null || clickedItem.getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        Player player = (Player)event.getWhoClicked();
        PurseData purseData = this.purseDataStorage.get(inventoryHolder2.getPurseUniqueId());
        if (purseData == null) {
            return;
        }
        if (clickedSlot == 49) {
            this.withdrawAll(player, inventory, purseData);
            return;
        }
        if (clickedSlot >= 45) {
            return;
        }
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(new ItemStack[]{clickedItem});
        inventory.setItem(clickedSlot, leftovers.isEmpty() ? null : (ItemStack)leftovers.values().iterator().next());
        this.save(purseData, inventory, false);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder;
        Inventory inventory = event.getInventory();
        if (inventory.getLocation() != null || !((inventoryHolder = inventory.getHolder()) instanceof PurseViewHolder)) {
            return;
        }
        PurseViewHolder holder = (PurseViewHolder)inventoryHolder;
        PurseData purseData = this.purseDataStorage.get(holder.getPurseUniqueId());
        if (purseData == null) {
            return;
        }
        this.save(purseData, inventory, false);
    }

    private void withdrawAll(Player player, Inventory inventory, PurseData purseData) {
        for (int i = 0; i < 45; ++i) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(new ItemStack[]{item});
            if (leftovers.isEmpty()) {
                inventory.setItem(i, null);
                continue;
            }
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            inventory.setItem(i, null);
        }
        this.save(purseData, inventory, false);
    }

    public void save(PurseData purseData, Inventory inventory, boolean priority) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < 45; ++i) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR || item.isSimilar(this.configuration.getPurseItem().getWithdrawItem())) continue;
            items.add(item);
        }
        purseData.getContent().clear();
        purseData.getContent().addAll(items);
        this.purseDataRepository.save(purseData, priority);
    }
    public PurseViewListener(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, PurseDataStorage purseDataStorage, PurseDataRepository purseDataRepository) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.purseDataStorage = purseDataStorage;
        this.purseDataRepository = purseDataRepository;
    }
}

