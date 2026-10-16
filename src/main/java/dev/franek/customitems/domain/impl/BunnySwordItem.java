package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class BunnySwordItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, Long> jumpBlockedPlayers = new HashMap<UUID, Long>();

    public BunnySwordItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "bunny_sword";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getBunnySwordItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        if (!(event.getEntity() instanceof Player target)) {
            return;
        }
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
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getBunnySwordItem().getCooldownInSeconds())) {
            return;
        }
        long expireTime = System.currentTimeMillis() + (long)this.configuration.getBunnySwordItem().getJumpDisableDurationInSeconds() * 1000L;
        this.jumpBlockedPlayers.put(target.getUniqueId(), expireTime);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_RABBIT_HURT, 1.0f, 1.0f);
        this.configuration.getBunnySwordItem().getShooter().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getBunnySwordItem().getTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{ATTACKER}", attacker.getName()));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!this.jumpBlockedPlayers.containsKey(uuid)) {
            return;
        }
        if (System.currentTimeMillis() > this.jumpBlockedPlayers.get(uuid)) {
            this.jumpBlockedPlayers.remove(uuid);
            return;
        }
        if (event.getTo().getY() > event.getFrom().getY() && !player.getLocation().getBlock().getType().name().contains("STAIRS") && !player.isFlying() && player.getVelocity().getY() > 0.0) {
            event.setTo(event.getFrom());
        }
    }
}

