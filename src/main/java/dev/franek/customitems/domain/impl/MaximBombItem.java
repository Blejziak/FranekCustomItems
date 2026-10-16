package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
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

public class MaximBombItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public MaximBombItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "maxim_bomb";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getMaximBombItem().getItem().clone();
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
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getMaximBombItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        Fireball fireball = (Fireball)player.launchProjectile(Fireball.class);
        fireball.setYield(0.0f);
        fireball.setIsIncendiary(false);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2.2));
        fireball.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        this.configuration.getMaximBombItem().getOnUse().send((CommandSender)player);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Fireball)) {
            return;
        }
        Fireball fireball = (Fireball)projectile;
        if (!fireball.getPersistentDataContainer().has(this.plugin.getCustomItemKey(), PersistentDataType.STRING)) {
            return;
        }
        Location fireballLocation = fireball.getLocation();
        World world = fireballLocation.getWorld();
        if (world == null) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(fireballLocation)) {
            event.setCancelled(true);
            fireball.remove();
            return;
        }
        world.createExplosion(fireballLocation, 6.0f, false, true);
        world.playSound(fireballLocation, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
        world.spawnParticle(Particle.EXPLOSION, fireballLocation, 3);
        int radius = 3;
        int baseX = fireballLocation.getBlockX();
        int baseY = fireballLocation.getBlockY();
        int baseZ = fireballLocation.getBlockZ();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    Material blockType = block.getType();
                    if (blockType != Material.OBSIDIAN && blockType != Material.CRYING_OBSIDIAN && blockType != Material.ANCIENT_DEBRIS || this.worldGuardHook.isInRestrictedRegion(block.getLocation())) continue;
                    block.setType(Material.AIR);
                    world.spawnParticle(Particle.LAVA, block.getLocation().add(0.5, 0.5, 0.5), 5);
                }
            }
        }
        for (Entity entity : world.getNearbyEntities(fireballLocation, 6.0, 6.0, 6.0)) {
            if (!(entity instanceof Player)) continue;
            Player targetPlayer = (Player)entity;
            this.configuration.getMaximBombItem().getOnDamage().send((CommandSender)targetPlayer);
        }
    }
}

