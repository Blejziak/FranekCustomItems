package dev.franek.customitems.domain.impl.purse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import dev.franek.customitems.domain.impl.purse.data.PurseData;
import dev.franek.customitems.domain.impl.purse.data.PurseDataRepository;
import dev.franek.customitems.domain.impl.purse.data.PurseDataStorage;
import dev.franek.customitems.domain.impl.purse.view.PurseView;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.util.PDCUtils;

public class PurseItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final PurseDataStorage purseDataStorage;
    private final PurseDataRepository purseDataRepository;
    private final PurseView purseView;
    private final Map<UUID, UUID> lastDamager;
    private final NamespacedKey purseUuidKey;

    public PurseItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, PurseDataStorage purseDataStorage, PurseDataRepository purseDataRepository, PurseView purseView) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.purseDataStorage = purseDataStorage;
        this.purseDataRepository = purseDataRepository;
        this.purseView = purseView;
        this.lastDamager = new HashMap<UUID, UUID>();
        this.purseUuidKey = new NamespacedKey((Plugin)this.plugin, "purse_uuid");
    }

    @Override
    public String getItemKey() {
        return "purse";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getPurseItem().getItem().clone();
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
        Player player = event.getPlayer();
        if (clickedItem.getAmount() > 1) {
            this.configuration.getPurseItem().getCannotOpenStacked().send((CommandSender)player);
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        if (PDCUtils.getString(clickedItemMeta, this.purseUuidKey) == null) {
            UUID newUuid = UUID.randomUUID();
            PurseData newData = new PurseData(newUuid, new ArrayList<ItemStack>());
            this.purseDataStorage.add(newData);
            this.purseDataRepository.save(newData, true);
            ItemMeta meta = clickedItem.getItemMeta();
            PDCUtils.setString(meta, this.purseUuidKey, newUuid.toString());
            clickedItem.setItemMeta(meta);
            Inventory inventory = this.purseView.openInventory(newData);
            if (inventory != null) {
                player.openInventory(inventory);
            }
            return;
        }
        UUID purseUniqueId = UUID.fromString(PDCUtils.getString(clickedItemMeta, this.purseUuidKey));
        PurseData purseData = this.purseDataStorage.get(purseUniqueId);
        if (purseData == null) {
            this.configuration.getPurseItem().getCannotOpenUnlinked().send((CommandSender)player);
            return;
        }
        Inventory inventory = this.purseView.openInventory(purseData);
        if (inventory != null) {
            player.openInventory(inventory);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID lastDamagerId;
        Player player = event.getEntity();
        if (event.getKeepInventory()) {
            return;
        }
        if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) {
            return;
        }
        Player killer = player.getKiller();
        if (killer == null && (lastDamagerId = this.lastDamager.get(player.getUniqueId())) != null) {
            killer = this.plugin.getServer().getPlayer(lastDamagerId);
        }
        this.lastDamager.remove(player.getUniqueId());
        if (killer == null || !killer.isOnline()) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(killer)) {
            return;
        }
        List<ItemStack> purseItems = this.getPursesFromInventory(killer);
        if (purseItems.isEmpty()) {
            return;
        }
        ItemStack[] drops = event.getDrops().toArray(new ItemStack[0]);
        event.getDrops().clear();
        event.setDroppedExp(0);
        this.distributeDrops(player, purseItems, drops);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player victim = (Player)entity;
        Entity entity2 = event.getDamager();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player damager = (Player)entity2;
        this.lastDamager.put(victim.getUniqueId(), damager.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.lastDamager.remove(event.getPlayer().getUniqueId());
    }

    private void distributeDrops(Player deadPlayer, List<ItemStack> purseItems, ItemStack[] drops) {
        LinkedHashMap<UUID, PurseData> loadedPurses = new LinkedHashMap<UUID, PurseData>();
        for (ItemStack purseItem : purseItems) {
            String uuidString;
            if (!purseItem.hasItemMeta() || (uuidString = PDCUtils.getString(purseItem.getItemMeta(), this.purseUuidKey)) == null) continue;
            try {
                UUID purseUuid = UUID.fromString(uuidString);
                PurseData purseData = this.purseDataStorage.get(purseUuid);
                if (purseData == null) continue;
                loadedPurses.put(purseUuid, purseData);
            }
            catch (IllegalArgumentException purseUuid) {}
        }
        if (loadedPurses.isEmpty()) {
            for (ItemStack drop : drops) {
                if (drop == null || drop.getType() == Material.AIR) continue;
                deadPlayer.getWorld().dropItemNaturally(deadPlayer.getLocation(), drop);
            }
            return;
        }
        for (ItemStack drop : drops) {
            if (drop == null || drop.getType() == Material.AIR || drop.containsEnchantment(Enchantment.VANISHING_CURSE)) continue;
            boolean collected = false;
            for (PurseData purseData : loadedPurses.values()) {
                List<ItemStack> content = purseData.getContent();
                if (content.size() >= 45) continue;
                content.add(drop.clone());
                collected = true;
                break;
            }
            if (collected) continue;
            deadPlayer.getWorld().dropItemNaturally(deadPlayer.getLocation(), drop);
        }
        for (PurseData purseData : loadedPurses.values()) {
            this.purseDataRepository.save(purseData, false);
        }
    }

    private List<ItemStack> getPursesFromInventory(Player player) {
        return Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR && this.isThisItem((ItemStack)item)).collect(Collectors.toList());
    }
}

