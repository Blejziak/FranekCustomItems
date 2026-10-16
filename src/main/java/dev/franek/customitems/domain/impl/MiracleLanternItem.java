package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class MiracleLanternItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<Location, BukkitTask> activeLanterns = new HashMap<Location, BukkitTask>();

    public MiracleLanternItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "cudownalatarnia";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getMiracleLantern().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!this.isThisItem(event.getItemInHand())) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getMiracleLantern().getCooldownInSeconds())) {
            event.setCancelled(true);
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.startLanternTask(block.getLocation(), player);
        this.configuration.getMiracleLantern().getPlaced().send((CommandSender)player);
        player.getWorld().playSound(block.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (this.activeLanterns.containsKey(loc)) {
            this.stopLantern(loc, true);
            event.getPlayer().sendMessage("§cZniszczono Cudowną Latarnię!");
        }
    }

    private void startLanternTask(final Location loc, Player owner) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, new Runnable(){
            int ticks;
            {
                this.ticks = MiracleLanternItem.this.configuration.getMiracleLantern().getDurationSeconds();
            }

            @Override
            public void run() {
                if (this.ticks <= 0) {
                    MiracleLanternItem.this.stopLantern(loc, false);
                    return;
                }
                double radius = MiracleLanternItem.this.configuration.getMiracleLantern().getRadius();
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                    if (!(entity instanceof Player)) continue;
                    Player player = (Player)entity;
                    player.addPotionEffects(MiracleLanternItem.this.configuration.getMiracleLantern().getEffects());
                }
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0.5, 1.0, 0.5), 10, 0.5, 0.5, 0.5);
                --this.ticks;
            }
        }, 0L, 20L);
        this.activeLanterns.put(loc, task);
    }

    private void stopLantern(Location loc, boolean destroyed) {
        BukkitTask task = this.activeLanterns.remove(loc);
        if (task != null) {
            task.cancel();
        }
        loc.getBlock().setType(Material.AIR);
        if (destroyed) {
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
        } else {
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
        }
    }
}

