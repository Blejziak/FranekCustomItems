package dev.franek.customitems.domain.impl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class GrinchShovelItem
extends CustomItem {
    private final Random random = new Random();
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Set<UUID> frozenPlayers = new HashSet<UUID>();

    public GrinchShovelItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "grinch_shovel";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getGrinchShovelItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        Player damager = (Player)entity;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (!this.isThisItem(damager.getInventory().getItemInMainHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(damager) || this.worldGuardHook.isInRestrictedRegion(target)) {
            return;
        }
        if (!this.cooldownController.canUse(damager, this.getItemKey(), this.configuration.getGrinchShovelItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)damager, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(damager, this.getItemKey())));
            return;
        }
        Location location = target.getLocation().clone();
        location.setYaw((float)this.random.nextInt(360));
        location.setPitch(this.random.nextFloat() * 60.0f - 30.0f);
        target.teleport(location);
        target.setFreezeTicks(this.configuration.getGrinchShovelItem().getFreezeInSeconds() * 20 + 20);
        this.frozenPlayers.add(target.getUniqueId());
        TaskDispatcher.scheduleTaskLater(this.plugin, false, this.configuration.getGrinchShovelItem().getFreezeInSeconds(), TimeUnit.SECONDS, () -> this.frozenPlayers.remove(target.getUniqueId()));
        this.configuration.getGrinchShovelItem().getOnUseDamager().send((CommandSender)damager);
        this.configuration.getGrinchShovelItem().getOnUseTarget().send((CommandSender)target);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.frozenPlayers.contains(player.getUniqueId())) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setTo(from.setDirection(to.getDirection()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.frozenPlayers.remove(event.getPlayer().getUniqueId());
    }
}

