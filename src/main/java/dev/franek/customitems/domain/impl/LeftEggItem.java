package dev.franek.customitems.domain.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class LeftEggItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public LeftEggItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "left_egg";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getLeftEggItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)projectile;
        ProjectileSource projectileSource = egg.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player thrower = (Player)projectileSource;
        ItemStack itemInMainHand = thrower.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = thrower.getInventory().getItemInOffHand();
        if (!this.isThisItem(itemInMainHand) && !this.isThisItem(itemInOffHand)) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(thrower)) {
            event.setCancelled(true);
            return;
        }
        if (!this.cooldownController.canUse(thrower, this.getItemKey(), this.configuration.getLeftEggItem().getCooldownInSeconds())) {
            event.setCancelled(true);
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)thrower, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(thrower, this.getItemKey())));
            return;
        }
        egg.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)entity;
        String key = (String)egg.getPersistentDataContainer().get(this.plugin.getCustomItemKey(), PersistentDataType.STRING);
        if (!this.getItemKey().equals(key)) {
            return;
        }
        ProjectileSource projectileSource = egg.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player thrower = (Player)projectileSource;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (this.worldGuardHook.isInRestrictedRegion(thrower) || this.worldGuardHook.isInRestrictedRegion(target)) {
            return;
        }
        event.setCancelled(true);
        target.setVelocity(new Vector(0.0, this.configuration.getLeftEggItem().getLaunchVelocity(), 0.0));
        this.configuration.getLeftEggItem().getOnUseThrower().send((CommandSender)thrower);
        this.configuration.getLeftEggItem().getOnUseTarget().send((CommandSender)target);
    }
}

