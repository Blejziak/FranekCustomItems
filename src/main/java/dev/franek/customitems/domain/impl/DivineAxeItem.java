package dev.franek.customitems.domain.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class DivineAxeItem
extends CustomItem {
    private final Set<UUID> invulnerablePlayers = new HashSet<UUID>();
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public DivineAxeItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "divine_axe";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getDivineAxeItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getDivineAxeItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.activatePower(player);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.5f, 1.0f);
        this.configuration.getDivineAxeItem().getOnUse().send((CommandSender)player);
    }

    private void activatePower(Player player) {
        player.getWorld().createExplosion(player.getLocation(), 0.0f, false, false);
        for (Entity entity : player.getNearbyEntities(6.0, 6.0, 6.0)) {
            Player targetPlayer;
            if (!(entity instanceof Player) || (targetPlayer = (Player)entity).equals(player) || this.worldGuardHook.isInRestrictedRegion(targetPlayer)) continue;
            this.configuration.getDivineAxeItem().getOnHit().send((CommandSender)targetPlayer);
            Vector direction = targetPlayer.getLocation().toVector().subtract(player.getLocation().toVector());
            if (direction.lengthSquared() > 0.0) {
                direction.setY(0).normalize().multiply(1.5).setY(1.0);
            } else {
                direction = new Vector(0.0, 1.0, 0.0);
            }
            targetPlayer.setVelocity(direction);
        }
        this.invulnerablePlayers.add(player.getUniqueId());
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.0f);
        TaskDispatcher.scheduleTaskLater(this.plugin, false, this.configuration.getDivineAxeItem().getInvulnerabilityDurationInSeconds(), TimeUnit.SECONDS, () -> this.invulnerablePlayers.remove(player.getUniqueId()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!this.invulnerablePlayers.contains(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.invulnerablePlayers.remove(player.getUniqueId());
    }
}

