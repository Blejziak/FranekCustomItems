package dev.franek.customitems.domain.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.MathUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class CupidBowItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public CupidBowItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "cupid_bow";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getCupidBowItem().getItem().clone();
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
        ItemStack itemInMainHand = shooter.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = shooter.getInventory().getItemInOffHand();
        if (!this.isThisItem(itemInMainHand) && !this.isThisItem(itemInOffHand)) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(shooter.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            return;
        }
        if (!this.cooldownController.canUse(shooter, this.getItemKey(), this.configuration.getCupidBowItem().getCooldownInSeconds())) {
            return;
        }
        if (!MathUtils.chance(this.configuration.getCupidBowItem().getChance())) {
            return;
        }
        target.addPotionEffects(this.configuration.getCupidBowItem().getEffects());
        this.configuration.getCupidBowItem().getOnShoot().send((CommandSender)shooter, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getCupidBowItem().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{SHOOTER}", shooter.getName()));
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)projectile;
        ProjectileSource projectileSource = arrow.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player shooter = (Player)projectileSource;
        ItemStack main = shooter.getInventory().getItemInMainHand();
        ItemStack off = shooter.getInventory().getItemInOffHand();
        if (!this.isThisItem(main) && !this.isThisItem(off)) {
            return;
        }
        arrow.setBasePotionType(PotionType.WATER);
    }
}

