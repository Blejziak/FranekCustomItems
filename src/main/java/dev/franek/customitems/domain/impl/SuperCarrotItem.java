package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class SuperCarrotItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Set<UUID> activePlayers = new HashSet<UUID>();
    private final Map<UUID, AttributeModifier> scaleModifiers = new HashMap<UUID, AttributeModifier>();
    private static final UUID SCALE_MODIFIER_UUID = UUID.fromString("a8c6d5e4-f3b2-4a1c-9e8d-7f6c5b4a3d2e");

    public SuperCarrotItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "super_carrot";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getSuperCarrotItem().getItem().clone();
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
        if (this.activePlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getSuperCarrotItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.activate(player);
    }

    private void activate(Player player) {
        CustomItemsConfiguration.SuperCarrotItemSection section = this.configuration.getSuperCarrotItem();
        this.activePlayers.add(player.getUniqueId());
        for (PotionEffect effect : section.getEffects()) {
            player.addPotionEffect(effect);
        }
        this.applyScale(player, section.getScale());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        section.getOnActivation().send((CommandSender)player);
        TaskDispatcher.scheduleTaskLater(this.plugin, false, section.getDurationInSeconds(), TimeUnit.SECONDS, () -> {
            if (!this.activePlayers.contains(player.getUniqueId())) {
                return;
            }
            this.deactivate(player, false);
        });
    }

    private void deactivate(Player player, boolean cancelled) {
        if (!this.activePlayers.remove(player.getUniqueId())) {
            return;
        }
        CustomItemsConfiguration.SuperCarrotItemSection section = this.configuration.getSuperCarrotItem();
        this.removeScale(player);
        for (PotionEffect effect : section.getEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (cancelled) {
            section.getOnCancellation().send((CommandSender)player);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            section.getOnDeactivation().send((CommandSender)player);
        }
    }

    private void applyScale(Player player, double scale) {
        try {
            Attribute scaleAttr = Attribute.valueOf("GENERIC_SCALE");
            AttributeInstance attribute = player.getAttribute(scaleAttr);
            if (attribute == null) {
                return;
            }
            attribute.getModifiers().stream().filter(m -> SCALE_MODIFIER_UUID.equals(m.getUniqueId())).findFirst().ifPresent(attribute::removeModifier);
            double addedValue = attribute.getBaseValue() * scale - attribute.getBaseValue();
            AttributeModifier modifier = new AttributeModifier(SCALE_MODIFIER_UUID, "super_carrot_scale", addedValue, AttributeModifier.Operation.ADD_NUMBER);
            attribute.addModifier(modifier);
        } catch (IllegalArgumentException | NoSuchFieldError | NoSuchMethodError ignored) {
        }
    }

    private void removeScale(Player player) {
        try {
            Attribute scaleAttr = Attribute.valueOf("GENERIC_SCALE");
            AttributeInstance attribute = player.getAttribute(scaleAttr);
            if (attribute == null) {
                return;
            }
            attribute.getModifiers().stream().filter(m -> SCALE_MODIFIER_UUID.equals(m.getUniqueId())).findFirst().ifPresent(attribute::removeModifier);
        } catch (IllegalArgumentException | NoSuchFieldError | NoSuchMethodError ignored) {
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!this.activePlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!this.isCriticalHit(player)) {
            return;
        }
        event.setDamage(event.getDamage() * this.configuration.getSuperCarrotItem().getCriticalDamageMultiplier());
    }

    private boolean isCriticalHit(Player player) {
        return player.getFallDistance() > 0.0f && !player.isOnGround() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && !player.isSprinting() && !player.isInsideVehicle();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.activePlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!this.configuration.getSuperCarrotItem().isCancelOnEnterSpawn()) {
            return;
        }
        if (!this.worldGuardHook.isInRestrictedRegion(player)) {
            return;
        }
        this.deactivate(player, true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!this.activePlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!this.configuration.getSuperCarrotItem().isCancelOnTeleport()) {
            return;
        }
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return;
        }
        this.deactivate(player, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.removeScale(event.getPlayer());
        this.activePlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.activePlayers.contains(player.getUniqueId())) {
            return;
        }
        this.deactivate(player, true);
    }
}

