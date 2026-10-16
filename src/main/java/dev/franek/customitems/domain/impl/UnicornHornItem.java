package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.task.TaskDispatcher;
import dev.franek.customitems.shared.util.PDCUtils;

public class UnicornHornItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, Horse> activeHorses = new HashMap<UUID, Horse>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<UUID, BukkitTask>();
    private final Set<UUID> stunnedPlayers = new HashSet<UUID>();

    public UnicornHornItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "unicorn_horn";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getUnicornHornItem().getItem().clone();
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
        if (this.activeHorses.containsKey(player.getUniqueId())) {
            this.configuration.getUnicornHornItem().getOnAlreadyActive().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getUnicornHornItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.spawnUnicorn(player);
    }

    private void spawnUnicorn(final Player player) {
        AttributeInstance healthAttr;
        final CustomItemsConfiguration.UnicornHornItemSection section = this.configuration.getUnicornHornItem();
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
        Horse horse = (Horse)player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.setOwner((AnimalTamer)player);
        horse.setAdult();
        horse.setColor(Horse.Color.WHITE);
        horse.setStyle(Horse.Style.NONE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        AttributeInstance speedAttr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(section.getHorseSpeed());
        }
        if ((healthAttr = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)) != null) {
            healthAttr.setBaseValue((double)section.getHorseHearts() * 2.0);
            horse.setHealth((double)section.getHorseHearts() * 2.0);
        }
        horse.addPassenger((Entity)player);
        this.activeHorses.put(player.getUniqueId(), horse);
        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_BREATHE, 1.0f, 1.0f);
        section.getOnSuccess().send((CommandSender)player);
        final int totalTicks = section.getDurationInSeconds() * 20;
        BukkitTask task = new BukkitRunnable(){
            int ticksLeft;
            {
                this.ticksLeft = totalTicks;
            }

            public void run() {
                Horse horse = UnicornHornItem.this.activeHorses.get(player.getUniqueId());
                if (horse == null || !horse.isValid()) {
                    section.getOnKilled().send((CommandSender)player);
                    UnicornHornItem.this.cleanup(player);
                    this.cancel();
                    return;
                }
                if (!horse.getPassengers().contains(player)) {
                    section.getOnDismounted().send((CommandSender)player);
                    UnicornHornItem.this.cleanup(player);
                    this.cancel();
                    return;
                }
                if (UnicornHornItem.this.worldGuardHook.isInRestrictedRegion(horse.getLocation())) {
                    section.getOnEnteredSpawn().send((CommandSender)player);
                    UnicornHornItem.this.cleanup(player);
                    this.cancel();
                    return;
                }
                UnicornHornItem.this.destroyBlocksAhead(horse, player);
                UnicornHornItem.this.stompNearbyPlayers(horse, player);
                if (this.ticksLeft <= 0) {
                    section.getOnExpired().send((CommandSender)player);
                    UnicornHornItem.this.cleanup(player);
                    this.cancel();
                    return;
                }
                this.ticksLeft -= 2;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
        this.activeTasks.put(player.getUniqueId(), task);
    }

    private void destroyBlocksAhead(Horse horse, Player player) {
        Location loc = horse.getLocation();
        double speed = horse.getVelocity().length();
        if (speed < 0.1) {
            return;
        }
        for (int y = 0; y <= 2; ++y) {
            Block block = loc.clone().add(horse.getVelocity().normalize().multiply(1.2)).add(0.0, (double)y, 0.0).getBlock();
            if (block.getType().isAir() || block.getType() == Material.BEDROCK || !block.getType().isSolid() || this.worldGuardHook.isInRestrictedRegion(block.getLocation())) continue;
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
            block.setType(Material.AIR);
        }
    }

    private void stompNearbyPlayers(Horse horse, Player rider) {
        CustomItemsConfiguration.UnicornHornItemSection section = this.configuration.getUnicornHornItem();
        if (horse.getVelocity().length() < 0.1) {
            return;
        }
        for (Entity entity : horse.getNearbyEntities(1.2, 1.0, 1.2)) {
            Player target;
            if (!(entity instanceof Player) || (target = (Player)entity).equals(rider) || this.stunnedPlayers.contains(target.getUniqueId()) || this.worldGuardHook.isInRestrictedRegion(target)) continue;
            this.stunnedPlayers.add(target.getUniqueId());
            target.setFreezeTicks(section.getStunDurationInSeconds() * 20 + 20);
            section.getOnStunned().send((CommandSender)target);
            TaskDispatcher.scheduleTaskLater(this.plugin, false, section.getStunDurationInSeconds(), TimeUnit.SECONDS, () -> this.stunnedPlayers.remove(target.getUniqueId()));
        }
    }

    private void cleanup(Player player) {
        BukkitTask task;
        Horse horse = this.activeHorses.remove(player.getUniqueId());
        if (horse != null && horse.isValid()) {
            horse.eject();
            horse.remove();
        }
        if ((task = this.activeTasks.remove(player.getUniqueId())) != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.stunnedPlayers.contains(player.getUniqueId())) {
            return;
        }
        if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) {
            return;
        }
        if (!this.activeHorses.containsKey(player.getUniqueId())) {
            return;
        }
        this.configuration.getUnicornHornItem().getOnDismounted().send((CommandSender)player);
        this.cleanup(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Horse)) {
            return;
        }
        Horse horse = (Horse)entity;
        if (!this.activeHorses.containsValue(horse)) {
            return;
        }
        if (horse.getHealth() - event.getFinalDamage() <= 0.0) {
            UUID ownerUuid = this.activeHorses.entrySet().stream().filter(e -> ((Horse)e.getValue()).equals(horse)).map(Map.Entry::getKey).findFirst().orElse(null);
            if (ownerUuid == null) {
                return;
            }
            Player owner = Bukkit.getPlayer((UUID)ownerUuid);
            if (owner != null) {
                this.configuration.getUnicornHornItem().getOnKilled().send((CommandSender)owner);
            }
            this.cleanup(owner != null ? owner : Bukkit.getOfflinePlayer((UUID)ownerUuid).getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.cleanup(event.getPlayer());
        this.stunnedPlayers.remove(event.getPlayer().getUniqueId());
    }
}

