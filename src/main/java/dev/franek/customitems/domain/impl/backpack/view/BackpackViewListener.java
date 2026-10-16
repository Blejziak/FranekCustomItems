package dev.franek.customitems.domain.impl.backpack.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataRepository;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataStorage;
import dev.franek.customitems.domain.impl.backpack.view.BackpackViewHolder;

public class BackpackViewListener
implements Listener {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final BackpackDataStorage backpackDataStorage;
    private final BackpackDataRepository backpackDataRepository;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder;
        Inventory inventory = event.getInventory();
        if (inventory.getLocation() != null || !((inventoryHolder = inventory.getHolder()) instanceof BackpackViewHolder)) {
            return;
        }
        BackpackViewHolder holder = (BackpackViewHolder)inventoryHolder;
        int rawSlot = event.getRawSlot();
        if (rawSlot >= 9) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        if (rawSlot < 0) {
            event.setCancelled(true);
            return;
        }
        if (rawSlot >= 5) {
            event.setCancelled(true);
            return;
        }
        switch (event.getClick()) {
            case DOUBLE_CLICK: 
            case NUMBER_KEY: {
                event.setCancelled(true);
                return;
            }
        }
        Player player = (Player)event.getWhoClicked();
        BackpackData backpackData = this.backpackDataStorage.get(holder.getBackpackUniqueId());
        if (backpackData == null) {
            event.setCancelled(true);
            return;
        }
        ItemStack currentItem = inventory.getItem(rawSlot);
        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && cursorItem.getType() != Material.AIR) {
            event.setCancelled(true);
            ItemStack toPlace = cursorItem.clone();
            ItemStack toPickup = currentItem != null && currentItem.getType() != Material.AIR ? currentItem.clone() : null;
            inventory.setItem(rawSlot, toPlace);
            event.getWhoClicked().setItemOnCursor(toPickup);
            this.save(backpackData, inventory, false);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            return;
        }
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        HashMap leftovers = player.getInventory().addItem(new ItemStack[]{currentItem.clone()});
        inventory.setItem(rawSlot, leftovers.isEmpty() ? null : (ItemStack)leftovers.values().iterator().next());
        this.save(backpackData, inventory, false);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getLocation() != null || !(inventory.getHolder() instanceof BackpackViewHolder)) {
            return;
        }
        Iterator iterator = event.getRawSlots().iterator();
        while (iterator.hasNext()) {
            int slot = (Integer)iterator.next();
            if (slot >= 9) continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder;
        Inventory inventory = event.getInventory();
        if (inventory.getLocation() != null || !((inventoryHolder = inventory.getHolder()) instanceof BackpackViewHolder)) {
            return;
        }
        BackpackViewHolder holder = (BackpackViewHolder)inventoryHolder;
        BackpackData backpackData = this.backpackDataStorage.get(holder.getBackpackUniqueId());
        if (backpackData == null) {
            return;
        }
        this.save(backpackData, inventory, false);
        Player player = (Player)event.getPlayer();
        player.playSound((Entity)player, Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
    }

    public void save(BackpackData backpackData, Inventory inventory, boolean priority) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < 5; ++i) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR || item.isSimilar(this.configuration.getBackpackItem().getFillerItem())) continue;
            items.add(item);
        }
        backpackData.getContent().clear();
        backpackData.getContent().addAll(items);
        this.backpackDataRepository.save(backpackData, priority);
    }
    public BackpackViewListener(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, BackpackDataStorage backpackDataStorage, BackpackDataRepository backpackDataRepository) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.backpackDataStorage = backpackDataStorage;
        this.backpackDataRepository = backpackDataRepository;
    }
}

