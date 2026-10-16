package dev.franek.customitems.domain.impl;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.passive.PassiveEffectItem;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class CupidsRoseItem
extends CustomItem
implements PassiveEffectItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;

    public CupidsRoseItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public String getItemKey() {
        return "cupids_rose";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getCupidsRoseItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.updateEffects(event.getPlayer());
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> this.updateEffects(event.getPlayer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> this.updateEffects(player));
    }

    @Override
    public void updateEffects(Player player) {
        if (!this.isThisItem(player.getInventory().getItemInOffHand()) && !this.isThisItem(player.getInventory().getItemInMainHand())) {
            return;
        }
        for (PotionEffect effect : this.configuration.getCupidsRoseItem().getEffects()) {
            PotionEffect current = player.getPotionEffect(effect.getType());
            if (current != null && current.getDuration() > this.configuration.getCupidsRoseItem().getRefreshThreshold()) continue;
            player.addPotionEffect(effect);
        }
    }
}

