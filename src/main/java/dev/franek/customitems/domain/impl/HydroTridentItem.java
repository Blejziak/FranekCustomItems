package dev.franek.customitems.domain.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class HydroTridentItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Set<UUID> inDash = new HashSet<UUID>();

    public HydroTridentItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "hydro_trident";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getHydroTridentItem().getItem().clone();
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
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        event.setCancelled(true);
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey() + "_dash", this.configuration.getHydroTridentItem().getDashCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey() + "_dash")));
            return;
        }
        player.setVelocity(player.getLocation().getDirection().normalize().multiply(this.configuration.getHydroTridentItem().getDashVelocityMultiplier()));
        this.inDash.add(player.getUniqueId());
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_3, 1.0f, 1.0f);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)projectile;
        ProjectileSource projectileSource = arrow.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player player = (Player)projectileSource;
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!this.isThisItem(main) && !this.isThisItem(off)) {
            return;
        }
        Location arrowLocation = arrow.getLocation();
        if (this.worldGuardHook.isInRestrictedRegion(arrowLocation)) {
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey() + "_lightning", this.configuration.getHydroTridentItem().getLightningCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey() + "_lightning")));
            return;
        }
        World world = arrowLocation.getWorld();
        if (world == null) {
            return;
        }
        world.strikeLightningEffect(arrowLocation);
        world.playSound(arrowLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        double radius = this.configuration.getHydroTridentItem().getLightningRadius();
        for (Entity entity : world.getNearbyEntities(arrowLocation, radius, radius, radius)) {
            Player targetPlayer;
            LivingEntity livingEntity;
            if (!(entity instanceof LivingEntity) || (livingEntity = (LivingEntity)entity).equals(player) || livingEntity instanceof Player && this.worldGuardHook.isInRestrictedRegion(targetPlayer = (Player)livingEntity)) continue;
            livingEntity.damage(this.configuration.getHydroTridentItem().getLightningDamage(), (Entity)player);
            Vector diff = livingEntity.getLocation().toVector().subtract(arrowLocation.toVector());
            if (!(diff.lengthSquared() > 0.0)) continue;
            Vector knock = diff.normalize().multiply(1.2).setY(0.5);
            livingEntity.setVelocity(knock);
        }
        arrow.remove();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.inDash.remove(event.getPlayer().getUniqueId());
    }
}

