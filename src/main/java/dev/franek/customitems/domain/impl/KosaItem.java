package dev.franek.customitems.domain.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class KosaItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public KosaItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "kosa_item";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getKosaItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        Player attacker = (Player)entity;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        ItemStack itemInMainHand = attacker.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = attacker.getInventory().getItemInOffHand();
        ItemStack item = this.isThisItem(itemInMainHand) ? itemInMainHand : (this.isThisItem(itemInOffHand) ? itemInOffHand : null);
        if (item == null) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            event.setCancelled(true);
            return;
        }
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getKosaItem().getCooldownInSeconds())) {
            return;
        }
        if (!this.configuration.getKosaItem().getEffects().isEmpty()) {
            target.addPotionEffects(this.configuration.getKosaItem().getEffects());
        }
        this.configuration.getKosaItem().getOnUse().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getKosaItem().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{PLAYER}", attacker.getName()));
    }
}

