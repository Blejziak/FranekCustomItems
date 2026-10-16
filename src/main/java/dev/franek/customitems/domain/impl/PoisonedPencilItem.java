package dev.franek.customitems.domain.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
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

public class PoisonedPencilItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public PoisonedPencilItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "poisoned_pencil";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getPoisonedPencilItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isThisItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
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
        if (!this.isThisItem(attacker.getInventory().getItemInMainHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker) || this.worldGuardHook.isInRestrictedRegion(target)) {
            event.setCancelled(true);
            return;
        }
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getPoisonedPencilItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(attacker, this.getItemKey())));
            return;
        }
        this.configuration.getPoisonedPencilItem().getEffects().forEach(arg_0 -> ((Player)target).addPotionEffect(arg_0));
        this.configuration.getPoisonedPencilItem().getOnUseAttacker().send((CommandSender)attacker);
        this.configuration.getPoisonedPencilItem().getOnUseTarget().send((CommandSender)target);
    }
}

