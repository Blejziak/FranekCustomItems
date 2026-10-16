package dev.franek.customitems.domain.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class SnowflakeItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Set<UUID> frozenPlayers = new HashSet<UUID>();

    public SnowflakeItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "snowflake";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getSnowflakeItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onSnowballLaunch(ProjectileLaunchEvent event) {
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
        if (!this.cooldownController.canUse(thrower, this.getItemKey(), this.configuration.getSnowflakeItem().getCooldownInSeconds())) {
            event.setCancelled(true);
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)thrower, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(thrower, this.getItemKey())));
            return;
        }
        snowball.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
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
        this.frozenPlayers.add(target.getUniqueId());
        target.setFreezeTicks(this.configuration.getSnowflakeItem().getFreezeInSeconds() * 20 + 20);
        TaskDispatcher.scheduleTaskLater(this.plugin, false, this.configuration.getSnowflakeItem().getFreezeInSeconds(), TimeUnit.SECONDS, () -> this.frozenPlayers.remove(target.getUniqueId()));
        this.configuration.getSnowflakeItem().getOnUseThrower().send((CommandSender)thrower);
        this.configuration.getSnowflakeItem().getOnUseTarget().send((CommandSender)target);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.frozenPlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.frozenPlayers.remove(event.getPlayer().getUniqueId());
    }
}

