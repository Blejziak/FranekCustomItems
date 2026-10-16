package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.notification.Notification;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class GhostBlockItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, Double> playerHealthMap = new HashMap<UUID, Double>();
    private Location locationOfLastUse;

    public GhostBlockItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "ghost_block";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getGhostBlockItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (this.worldGuardHook.isInRestrictedRegion(player) || event.getClickedBlock() == null || this.worldGuardHook.isInRestrictedRegion(event.getClickedBlock().getLocation())) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (this.locationOfLastUse != null && player.getWorld().equals(this.locationOfLastUse.getWorld()) && player.getLocation().distance(this.locationOfLastUse) <= this.configuration.getGhostBlockItem().getRadius()) {
            this.configuration.getGhostBlockItem().getLocationOfLastUseIsTooClose().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getGhostBlockItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.locationOfLastUse = player.getLocation();
        TaskDispatcher.scheduleTaskLater(this.plugin, false, this.configuration.getGhostBlockItem().getEffectDurationInSeconds(), TimeUnit.SECONDS, () -> {
            this.locationOfLastUse = null;
        });
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
        this.configuration.getGhostBlockItem().getOnUse().send((CommandSender)player);
        double radius = this.configuration.getGhostBlockItem().getRadius();
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            AttributeInstance healthAttribute;
            Player onlinePlayer;
            if (!(entity instanceof Player) || (onlinePlayer = (Player)entity).equals(player) || this.worldGuardHook.isInRestrictedRegion(onlinePlayer) || this.playerHealthMap.containsKey(onlinePlayer.getUniqueId()) || (healthAttribute = onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)) == null) continue;
            double originalMaxHealth = healthAttribute.getBaseValue();
            double newMaxHealth = Math.max(this.configuration.getGhostBlockItem().getMinHealth(), originalMaxHealth - this.configuration.getGhostBlockItem().getHealthToRemove());
            if (newMaxHealth >= originalMaxHealth) continue;
            healthAttribute.setBaseValue(newMaxHealth);
            this.playerHealthMap.put(onlinePlayer.getUniqueId(), originalMaxHealth);
            if (onlinePlayer.getHealth() > newMaxHealth) {
                onlinePlayer.setHealth(newMaxHealth);
            }
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
            this.configuration.getGhostBlockItem().getOnAffected().send((CommandSender)onlinePlayer);
        }
        TaskDispatcher.scheduleTaskLater(this.plugin, false, this.configuration.getGhostBlockItem().getEffectDurationInSeconds(), TimeUnit.SECONDS, () -> this.restoreHealthsForAllAffectedPlayers(this.configuration.getGhostBlockItem().getOnExpire()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.playerHealthMap.containsKey(player.getUniqueId())) {
            return;
        }
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null) {
            return;
        }
        double originalMaxHealth = this.playerHealthMap.get(player.getUniqueId());
        this.playerHealthMap.remove(player.getUniqueId());
        healthAttribute.setBaseValue(originalMaxHealth);
    }

    public void restoreHealthsForAllAffectedPlayers(Notification notification) {
        for (UUID itPlayerUniqueId : this.playerHealthMap.keySet()) {
            AttributeInstance healthAttribute;
            Player onlinePlayer = Bukkit.getPlayer((UUID)itPlayerUniqueId);
            if (onlinePlayer == null || !onlinePlayer.isOnline() || (healthAttribute = onlinePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH)) == null) continue;
            healthAttribute.setBaseValue(this.playerHealthMap.get(itPlayerUniqueId).doubleValue());
            if (notification == null) continue;
            notification.send((CommandSender)onlinePlayer);
        }
        this.playerHealthMap.clear();
    }
}

