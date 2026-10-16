package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class IllusionistWandItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, ArmorStand> activeClones = new HashMap<UUID, ArmorStand>();

    public IllusionistWandItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "illusionist_wand";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getIllusionistWandItem().getItem().clone();
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
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            this.handleLeftClick(player);
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        this.handleRightClick(player);
    }

    private void handleLeftClick(Player player) {
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey() + "_left", this.configuration.getIllusionistWandItem().getLeftClickCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey() + "_left")));
            return;
        }
        this.spawnEvokerFangs(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
        this.configuration.getIllusionistWandItem().getOnEvokerFangs().send((CommandSender)player);
    }

    private void handleRightClick(Player player) {
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey() + "_right", this.configuration.getIllusionistWandItem().getRightClickCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey() + "_right")));
            return;
        }
        this.spawnCloneAndVanish(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.0f, 1.0f);
        this.configuration.getIllusionistWandItem().getOnInvisibility().send((CommandSender)player);
    }

    private void spawnEvokerFangs(Player player) {
        CustomItemsConfiguration.IllusionistWandItemSection section = this.configuration.getIllusionistWandItem();
        int waves = section.getEvokerFangsWaves();
        int fangsPerWave = section.getEvokerFangsPerWave();
        double distanceBetweenWaves = section.getEvokerFangsDistanceBetweenWaves();
        long delayBetweenWaves = section.getEvokerFangsDelayBetweenWaves();
        double damage = section.getEvokerFangsDamage();
        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        int wave = 0;
        while (wave < waves) {
            int currentWave = wave++;
            TaskDispatcher.scheduleTaskLater(this.plugin, false, (long)currentWave * delayBetweenWaves, () -> {
                Location waveCenter = player.getLocation().clone().add(direction.clone().multiply((double)(currentWave + 1) * distanceBetweenWaves));
                for (int i = 0; i < fangsPerWave; ++i) {
                    double offset = ((double)i - (double)(fangsPerWave - 1) / 2.0) * 0.8;
                    Vector perpendicular = new Vector(-direction.getZ(), 0.0, direction.getX()).normalize().multiply(offset);
                    Location fangLocation = waveCenter.clone().add(perpendicular);
                    if (this.worldGuardHook.isInRestrictedRegion(fangLocation)) continue;
                    EvokerFangs fangs = (EvokerFangs)player.getWorld().spawnEntity(fangLocation, EntityType.EVOKER_FANGS);
                    fangs.setOwner((LivingEntity)player);
                    if (damage == 6.0) continue;
                    TaskDispatcher.scheduleTaskLater(this.plugin, false, 1L, () -> {
                        for (Entity nearby : fangLocation.getWorld().getNearbyEntities(fangLocation, 0.5, 1.0, 0.5)) {
                            Player target;
                            LivingEntity living;
                            if (!(nearby instanceof LivingEntity) || (living = (LivingEntity)nearby).equals(player) || living instanceof Player && this.worldGuardHook.isInRestrictedRegion(target = (Player)living)) continue;
                            living.damage(damage, (Entity)player);
                        }
                    });
                }
            });
        }
    }

    private void spawnCloneAndVanish(final Player player) {
        CustomItemsConfiguration.IllusionistWandItemSection section = this.configuration.getIllusionistWandItem();
        int invisDuration = section.getInvisibilityDurationInSeconds();
        int cloneRunDuration = section.getCloneRunDurationInSeconds();
        final double cloneSpeed = section.getCloneSpeed();
        final Vector direction = player.getLocation().getDirection().setY(0).normalize();
        ArmorStand existing = this.activeClones.remove(player.getUniqueId());
        if (existing != null && existing.isValid()) {
            existing.remove();
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, invisDuration * 20, 0, false, false));
        final ArmorStand clone = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        clone.setVisible(false);
        clone.setGravity(true);
        clone.setBasePlate(false);
        clone.setArms(true);
        clone.setMarker(false);
        clone.setCustomNameVisible(true);
        clone.setCustomName(player.getName());
        for (int slot = 0; slot < 6; ++slot) {
            ItemStack piece = this.getArmorPiece(player, slot);
            if (piece == null) continue;
            if (slot == 0) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta)head.getItemMeta();
                meta.setOwningPlayer((OfflinePlayer)player);
                head.setItemMeta((ItemMeta)meta);
                piece = head;
            }
            this.setArmorPiece(clone, slot, piece.clone());
        }
        this.activeClones.put(player.getUniqueId(), clone);
        final int totalTicks = cloneRunDuration * 20;
        new BukkitRunnable(){
            int ticks = 0;

            public void run() {
                ++this.ticks;
                if (this.ticks >= totalTicks || clone.isDead() || !clone.isValid()) {
                    clone.remove();
                    IllusionistWandItem.this.activeClones.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                clone.setVelocity(direction.clone().multiply(cloneSpeed).setY(-0.1));
                if (this.ticks % 2 == 0) {
                    Location location = clone.getLocation();
                    location.setDirection(direction);
                    clone.teleport(location);
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
    }

    private ItemStack getArmorPiece(Player player, int slot) {
        return switch (slot) {
            case 0 -> player.getInventory().getHelmet();
            case 1 -> player.getInventory().getChestplate();
            case 2 -> player.getInventory().getLeggings();
            case 3 -> player.getInventory().getBoots();
            case 4 -> player.getInventory().getItemInMainHand();
            case 5 -> player.getInventory().getItemInOffHand();
            default -> null;
        };
    }

    private void setArmorPiece(ArmorStand stand, int slot, ItemStack item) {
        switch (slot) {
            case 0: {
                stand.getEquipment().setHelmet(item);
                break;
            }
            case 1: {
                stand.getEquipment().setChestplate(item);
                break;
            }
            case 2: {
                stand.getEquipment().setLeggings(item);
                break;
            }
            case 3: {
                stand.getEquipment().setBoots(item);
                break;
            }
            case 4: {
                stand.getEquipment().setItemInMainHand(item);
                break;
            }
            case 5: {
                stand.getEquipment().setItemInOffHand(item);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ArmorStand clone = this.activeClones.remove(event.getPlayer().getUniqueId());
        if (clone != null && clone.isValid()) {
            clone.remove();
        }
    }
}

