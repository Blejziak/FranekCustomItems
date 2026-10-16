package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class OvercookedCornItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public OvercookedCornItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "overcooked_corn";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getOvercookedCornItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
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
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getOvercookedCornItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        Fireball fireball = (Fireball)player.launchProjectile(Fireball.class);
        fireball.setDirection(player.getLocation().getDirection().multiply(2.0));
        fireball.setIsIncendiary(this.configuration.getOvercookedCornItem().isCreateFire());
        fireball.setYield(this.configuration.getOvercookedCornItem().getExplosionPower());
        fireball.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        this.configuration.getOvercookedCornItem().getOnUse().send((CommandSender)player);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Fireball)) {
            return;
        }
        Fireball fireball = (Fireball)entity;
        String key = (String)fireball.getPersistentDataContainer().get(this.plugin.getCustomItemKey(), PersistentDataType.STRING);
        if (!this.getItemKey().equals(key)) {
            return;
        }
        Location explodeLococation = event.getLocation();
        if (this.worldGuardHook.isInRestrictedRegion(explodeLococation)) {
            event.blockList().clear();
            event.setCancelled(true);
            return;
        }
        explodeLococation.getWorld().playSound(explodeLococation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        for (Entity entity2 : explodeLococation.getWorld().getNearbyEntities(explodeLococation, (double)this.configuration.getOvercookedCornItem().getExplosionPower(), (double)this.configuration.getOvercookedCornItem().getExplosionPower(), (double)this.configuration.getOvercookedCornItem().getExplosionPower())) {
            Player target;
            LivingEntity living;
            if (!(entity2 instanceof LivingEntity) || (living = (LivingEntity)entity2) instanceof Player && this.worldGuardHook.isInRestrictedRegion(target = (Player)living)) continue;
            living.damage((double)this.configuration.getOvercookedCornItem().getExplosionPower() * 2.0);
        }
        if (this.configuration.getOvercookedCornItem().isPreventRegionDestruction()) {
            event.blockList().removeIf(block -> {
                if (this.worldGuardHook.isInRestrictedRegion(block.getLocation())) {
                    return true;
                }
                return this.configuration.getOvercookedCornItem().getProtectedBlocks().contains(block.getType());
            });
        } else {
            event.blockList().removeIf(block -> this.configuration.getOvercookedCornItem().getProtectedBlocks().contains(block.getType()));
        }
    }
}

