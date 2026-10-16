package dev.franek.customitems.domain.impl.backpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataRepository;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataStorage;
import dev.franek.customitems.domain.impl.backpack.view.BackpackView;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class BackpackItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final BackpackDataStorage backpackDataStorage;
    private final BackpackDataRepository backpackDataRepository;
    private final BackpackView backpackView;
    private final NamespacedKey backpackUuidKey;
    private final Map<UUID, Long> combatTimers = new HashMap<UUID, Long>();

    public BackpackItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, BackpackDataStorage backpackDataStorage, BackpackDataRepository backpackDataRepository, BackpackView backpackView) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.backpackDataStorage = backpackDataStorage;
        this.backpackDataRepository = backpackDataRepository;
        this.backpackView = backpackView;
        this.backpackUuidKey = new NamespacedKey((Plugin)plugin, "backpack_uuid");
    }

    @Override
    public String getItemKey() {
        return "backpack";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getBackpackItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            if (this.isThisItem(event.getItem())) {
                event.setCancelled(true);
            }
            return;
        }
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (clickedItem.getAmount() > 1) {
            this.configuration.getBackpackItem().getCannotOpenStacked().send((CommandSender)player);
            return;
        }
        if (this.isInCombat(player) && !this.configuration.getBackpackItem().isCanOpenInCombat()) {
            this.configuration.getBackpackItem().getCannotOpenInCombat().send((CommandSender)player);
            return;
        }
        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        String uniqueIdString = PDCUtils.getString(clickedItemMeta, this.backpackUuidKey);
        if (uniqueIdString == null) {
            UUID newUuid = UUID.randomUUID();
            BackpackData newData = new BackpackData(newUuid, new ArrayList<ItemStack>());
            this.backpackDataStorage.add(newData);
            this.backpackDataRepository.save(newData, true);
            ItemMeta meta = clickedItem.getItemMeta();
            PDCUtils.setString(meta, this.backpackUuidKey, newUuid.toString());
            clickedItem.setItemMeta(meta);
            this.openBackpack(player, newData);
            return;
        }
        UUID backpackUniqueId = UUID.fromString(uniqueIdString);
        BackpackData backpackData = this.backpackDataStorage.get(backpackUniqueId);
        if (backpackData == null) {
            this.configuration.getBackpackItem().getCannotOpenUnlinked().send((CommandSender)player);
            return;
        }
        this.openBackpack(player, backpackData);
    }

    private void openBackpack(Player player, BackpackData backpackData) {
        player.playSound((Entity)player, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> {
            Inventory inventory = this.backpackView.getInventory(backpackData);
            if (inventory != null) {
                player.openInventory(inventory);
            }
        });
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player victim = (Player)entity;
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        int combatSeconds = this.configuration.getBackpackItem().getCombatCooldownSeconds();
        this.combatTimers.put(victim.getUniqueId(), System.currentTimeMillis() + (long)combatSeconds * 1000L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) {
            return;
        }
        ArrayList<ItemStack> toKeep = new ArrayList<>();
        event.getDrops().removeIf(drop -> {
            if (drop == null || !this.isThisItem((ItemStack)drop)) {
                return false;
            }
            String uuidString = PDCUtils.getString(drop.getItemMeta(), this.backpackUuidKey);
            if (uuidString == null) {
                return false;
            }
            toKeep.add(drop.clone());
            return true;
        });
        if (toKeep.isEmpty()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> {
            Player respawned = this.plugin.getServer().getPlayer(uuid);
            if (respawned == null) {
                return;
            }
            for (ItemStack item : toKeep) {
                Map<Integer, ItemStack> leftovers = respawned.getInventory().addItem(new ItemStack[]{item});
                leftovers.values().forEach(leftover -> respawned.getWorld().dropItemNaturally(respawned.getLocation(), leftover));
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.combatTimers.remove(event.getPlayer().getUniqueId());
    }

    public boolean isInCombat(Player player) {
        Long expiry = this.combatTimers.get(player.getUniqueId());
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiry) {
            this.combatTimers.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
}

