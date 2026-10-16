package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class ExpiredPotionItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, BukkitTask> activeClouds = new HashMap<UUID, BukkitTask>();

    public ExpiredPotionItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "expired_potion";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getExpiredPotionItem().getItem().clone();
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
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getExpiredPotionItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        CustomItemsConfiguration.ExpiredPotionItemSection section = this.configuration.getExpiredPotionItem();
        final Item thrownItem = player.getWorld().dropItem(player.getEyeLocation(), clickedItem.clone());
        thrownItem.setVelocity(player.getLocation().getDirection().multiply(section.getThrowVelocity()));
        thrownItem.setPickupDelay(Integer.MAX_VALUE);
        thrownItem.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
        clickedItem.setAmount(clickedItem.getAmount() - 1);
        player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
        section.getOnUse().send((CommandSender)player);
        final UUID throwerId = player.getUniqueId();
        BukkitTask task = new BukkitRunnable(){
            int ticksLeft = 40;

            public void run() {
                if (!thrownItem.isValid() || this.ticksLeft <= 0 || thrownItem.isOnGround()) {
                    Location landLoc = thrownItem.getLocation().clone();
                    thrownItem.remove();
                    ExpiredPotionItem.this.activeClouds.remove(thrownItem.getUniqueId());
                    this.cancel();
                    if (ExpiredPotionItem.this.worldGuardHook.isInRestrictedRegion(landLoc)) {
                        return;
                    }
                    ExpiredPotionItem.this.spawnCloud(landLoc, throwerId);
                    return;
                }
                this.ticksLeft -= 2;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
        this.activeClouds.put(thrownItem.getUniqueId(), task);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        String key = (String)event.getEntity().getPersistentDataContainer().get(this.plugin.getCustomItemKey(), PersistentDataType.STRING);
        if (!this.getItemKey().equals(key)) {
            return;
        }
        event.setCancelled(true);
    }

    private void spawnCloud(final Location center, UUID throwerId) {
        final CustomItemsConfiguration.ExpiredPotionItemSection section = this.configuration.getExpiredPotionItem();
        final double radius = section.getAreaRadius();
        final int durationSeconds = section.getAreaDurationInSeconds();
        center.getWorld().playSound(center, Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 1.0f);
        new BukkitRunnable(){
            int secondsLeft;
            int particleTick;
            {
                this.secondsLeft = durationSeconds;
                this.particleTick = 0;
            }

            public void run() {
                if (this.secondsLeft <= 0) {
                    this.cancel();
                    return;
                }
                if (this.particleTick % 2 == 0) {
                    center.getWorld().spawnParticle(Particle.WITCH, center, section.getParticleAmount(), radius / 2.0, 0.5, radius / 2.0, 0.01);
                }
                if (this.particleTick % 10 == 0) {
                    for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                        Player nearby;
                        if (!(entity instanceof Player) || ExpiredPotionItem.this.worldGuardHook.isInRestrictedRegion(nearby = (Player)entity)) continue;
                        for (PotionEffect effect : section.getEffects()) {
                            nearby.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration() * 20, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
                        }
                    }
                    --this.secondsLeft;
                }
                ++this.particleTick;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
    }
}

