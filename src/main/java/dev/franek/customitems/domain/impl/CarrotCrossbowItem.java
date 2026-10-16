package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class CarrotCrossbowItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public CarrotCrossbowItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "carrot_crossbow";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getCarrotCrossbow().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)entity;
        ProjectileSource projectileSource = arrow.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player shooter = (Player)projectileSource;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (!this.isThisItem(shooter.getInventory().getItemInMainHand()) && !this.isThisItem(shooter.getInventory().getItemInOffHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(shooter.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            return;
        }
        if (!this.cooldownController.canUse(shooter, this.getItemKey(), this.configuration.getCarrotCrossbow().getCooldownInSeconds())) {
            return;
        }
        Location shooterLocation = shooter.getLocation();
        Location targetLocation = target.getLocation();
        Vector pullVector = shooterLocation.toVector().subtract(targetLocation.toVector()).normalize();
        target.setVelocity(pullVector.multiply(this.configuration.getCarrotCrossbow().getPullStrength()).setY(2.5));
        this.configuration.getCarrotCrossbow().getOnUse().send((CommandSender)shooter, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getCarrotCrossbow().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{SHOOTER}", shooter.getName()));
    }
}

