package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class AppeasementTotemItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<UUID, ItemStack[]>();

    public AppeasementTotemItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public String getItemKey() {
        return "appeasement_totem";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getAppeasementTotemItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        if (!this.isThisItem(player.getInventory().getItemInMainHand()) && !this.isThisItem(player.getInventory().getItemInOffHand())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!this.isThisItem(itemInMainHand) && !this.isThisItem(itemInOffHand)) {
            return;
        }
        if (Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) {
            return;
        }
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] savedContents = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; ++i) {
            if (contents[i] == null) continue;
            savedContents[i] = contents[i].clone();
        }
        if (this.isThisItem(itemInMainHand)) {
            int slot = player.getInventory().getHeldItemSlot();
            if (itemInMainHand.getAmount() <= 1) {
                savedContents[slot] = null;
            } else {
                savedContents[slot] = itemInMainHand.clone();
                savedContents[slot].setAmount(itemInMainHand.getAmount() - 1);
            }
        } else {
            int offhandSlot = 40;
            if (itemInOffHand.getAmount() <= 1) {
                savedContents[40] = null;
            } else {
                savedContents[40] = itemInOffHand.clone();
                savedContents[40].setAmount(itemInOffHand.getAmount() - 1);
            }
        }
        this.savedInventories.put(player.getUniqueId(), savedContents);
        event.getDrops().clear();
        event.setDroppedExp(0);
        this.configuration.getAppeasementTotemItem().getOnUse().send((CommandSender)player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ItemStack[] savedContents = this.savedInventories.remove(player.getUniqueId());
        if (savedContents == null) {
            return;
        }
        TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> {
            player.getInventory().setContents(savedContents);
            player.setFoodLevel(20);
            player.setSaturation(20.0f);
            Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "untag " + player.getName());
        });
    }
}

