package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
import dev.franek.customitems.shared.util.PDCUtils;

public class ScreenItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public ScreenItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "screen";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getScreenItem().getItem().clone();
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
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getScreenItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        Location playerLocation = player.getLocation();
        double knockbackRadius = this.configuration.getScreenItem().getKnockbackRadius();
        for (Entity entity : player.getWorld().getNearbyEntities(playerLocation, knockbackRadius, knockbackRadius, knockbackRadius)) {
            Player target;
            if (!(entity instanceof Player) || (target = (Player)entity).equals(player) || this.worldGuardHook.isInRestrictedRegion(target)) continue;
            Vector knockback = target.getLocation().toVector().subtract(playerLocation.toVector()).normalize().multiply(this.configuration.getScreenItem().getKnockbackStrength());
            knockback.setY(0.5);
            target.setVelocity(knockback);
            this.configuration.getScreenItem().getOnHit().send((CommandSender)target);
        }
        player.getWorld().playSound(playerLocation, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
        player.swingMainHand();
        this.configuration.getScreenItem().getOnUse().send((CommandSender)player);
        clickedItem.setAmount(clickedItem.getAmount() - 1);
    }
}

