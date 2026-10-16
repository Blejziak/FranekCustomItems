package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class SnowballSwapItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public SnowballSwapItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "snowball_swap";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getSnowballSwapItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball)projectile;
        ProjectileSource projectileSource = snowball.getShooter();
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
        if (!this.cooldownController.canUse(thrower, this.getItemKey(), this.configuration.getSnowballSwapItem().getCooldownInSeconds())) {
            event.setCancelled(true);
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)thrower, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(thrower, this.getItemKey())));
            return;
        }
        snowball.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball)projectile;
        String key = (String)snowball.getPersistentDataContainer().get(this.plugin.getCustomItemKey(), PersistentDataType.STRING);
        if (!this.getItemKey().equals(key)) {
            return;
        }
        ProjectileSource projectileSource = snowball.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player thrower = (Player)projectileSource;
        Entity entity = event.getHitEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player target = (Player)entity;
        if (this.worldGuardHook.isInRestrictedRegion(target)) {
            return;
        }
        Location throwerLocation = thrower.getLocation().clone();
        Location targetLocation = target.getLocation().clone();
        thrower.teleport(targetLocation);
        target.teleport(throwerLocation);
        this.configuration.getSnowballSwapItem().getOnUseThrower().send((CommandSender)thrower);
        this.configuration.getSnowballSwapItem().getOnUseTarget().send((CommandSender)target);
    }
}

