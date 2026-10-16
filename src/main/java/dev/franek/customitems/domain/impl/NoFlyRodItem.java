package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class NoFlyRodItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Set<UUID> blockedGlide = new HashSet<UUID>();
    private final Map<UUID, FishHook> activeHooks = new HashMap<UUID, FishHook>();
    private final Set<UUID> teleportingPlayers = new HashSet<UUID>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<UUID, BukkitTask>();

    public NoFlyRodItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "no_fly_rod";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getNoFlyRodItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (this.isThisItem(player.getInventory().getItemInMainHand()) || this.isThisItem(player.getInventory().getItemInOffHand())) {
                this.activeHooks.put(player.getUniqueId(), event.getHook());
            }
        } else if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            this.activeHooks.remove(player.getUniqueId());
        }
        if (this.worldGuardHook.isInRestrictedRegion(player) && event.getState() == PlayerFishEvent.State.FISHING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFishHookHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof FishHook)) {
            return;
        }
        FishHook hook = (FishHook)projectile;
        Entity entity = event.getHitEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player target = (Player)entity;
        ProjectileSource projectileSource = hook.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player attacker = (Player)projectileSource;
        ItemStack itemInMainHand = attacker.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = attacker.getInventory().getItemInOffHand();
        if (!this.isThisItem(itemInMainHand) && !this.isThisItem(itemInOffHand)) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker) || this.worldGuardHook.isInRestrictedRegion(target)) {
            hook.remove();
            return;
        }
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getNoFlyRodItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(attacker, this.getItemKey())));
            hook.remove();
            return;
        }
        this.handleDisruption(attacker, target, hook);
    }

    private void handleDisruption(Player attacker, Player target, final FishHook hook) {
        if (attacker.equals(target)) {
            return;
        }
        final UUID targetId = target.getUniqueId();
        final UUID attackerId = attacker.getUniqueId();
        if (this.blockedGlide.contains(targetId)) {
            return;
        }
        this.blockedGlide.add(targetId);
        this.configuration.getNoFlyRodItem().getOnUseAttacker().send((CommandSender)attacker);
        this.configuration.getNoFlyRodItem().getOnUseTarget().send((CommandSender)target);
        BukkitTask task = new BukkitRunnable(){
            int timeout = 0;

            public void run() {
                Player currentTarget = Bukkit.getPlayer((UUID)targetId);
                Player currentAttacker = Bukkit.getPlayer((UUID)attackerId);
                ++this.timeout;
                if (currentTarget == null || currentAttacker == null || !currentTarget.getWorld().equals(currentAttacker.getWorld()) || this.timeout > 120) {
                    this.stopTask(targetId, attackerId, null, null);
                    return;
                }
                if (hook.isDead() || !hook.isValid()) {
                    NoFlyRodItem.this.activeHooks.remove(attackerId);
                    NoFlyRodItem.this.activeTasks.remove(targetId);
                    NoFlyRodItem.this.configuration.getNoFlyRodItem().getOnHookBreakAttacker().send((CommandSender)currentAttacker);
                    NoFlyRodItem.this.configuration.getNoFlyRodItem().getOnHookBreakTarget().send((CommandSender)currentTarget);
                    new BukkitRunnable(){

                        public void run() {
                            NoFlyRodItem.this.blockedGlide.remove(targetId);
                            Player endTarget = Bukkit.getPlayer((UUID)targetId);
                            if (endTarget != null) {
                                NoFlyRodItem.this.configuration.getNoFlyRodItem().getOnExpire().send((CommandSender)endTarget);
                            }
                        }
                    }.runTaskLater((Plugin)NoFlyRodItem.this.plugin, 200L);
                    this.cancel();
                    return;
                }
                if (currentTarget.getLocation().distanceSquared(currentAttacker.getLocation()) > 400.0) {
                    currentAttacker.playSound(currentAttacker.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                    NoFlyRodItem.this.configuration.getNoFlyRodItem().getOnTooFarAttacker().send((CommandSender)currentAttacker);
                    NoFlyRodItem.this.configuration.getNoFlyRodItem().getOnTooFarTarget().send((CommandSender)currentTarget);
                    this.stopTask(targetId, attackerId, null, null);
                    return;
                }
                if (currentTarget.isGliding() && !NoFlyRodItem.this.teleportingPlayers.contains(targetId)) {
                    currentTarget.setGliding(false);
                }
            }

            private void stopTask(UUID tId, UUID aId, Player t, Player a) {
                NoFlyRodItem.this.blockedGlide.remove(tId);
                NoFlyRodItem.this.activeHooks.remove(aId);
                NoFlyRodItem.this.activeTasks.remove(tId);
                this.cancel();
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 5L);
        this.activeTasks.put(targetId, task);
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!this.blockedGlide.contains(player.getUniqueId()) || !event.isGliding()) {
            return;
        }
        event.setCancelled(true);
        player.setGliding(false);
        this.configuration.getNoFlyRodItem().getOnGlideBlocked().send((CommandSender)player);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.teleportingPlayers.add(uuid);
        new BukkitRunnable(){

            public void run() {
                NoFlyRodItem.this.teleportingPlayers.remove(uuid);
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.blockedGlide.remove(uuid);
        this.activeHooks.remove(uuid);
        this.teleportingPlayers.remove(uuid);
        BukkitTask task = this.activeTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
}

