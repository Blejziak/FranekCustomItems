package dev.franek.customitems.domain.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.bossbar.BossBarService;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.ColorUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class HydroCageItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final BossBarService bossBarService;
    private final Map<UUID, Map<Block, BlockData>> cages = new HashMap<UUID, Map<Block, BlockData>>();
    private final Map<UUID, Location> buildingCages = new HashMap<UUID, Location>();
    private final Map<UUID, Location> cageCenters = new HashMap<UUID, Location>();
    private final Map<UUID, List<BukkitTask>> playerTasks = new HashMap<UUID, List<BukkitTask>>();
    private final Map<UUID, BossBar> activeBossBars = new HashMap<UUID, BossBar>();

    public HydroCageItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController, BossBarService bossBarService) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
        this.bossBarService = bossBarService;
    }

    @Override
    public String getItemKey() {
        return "hydro_cage";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getHydroCageItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Location center;
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
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getHydroCageItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 15.0, FluidCollisionMode.NEVER, true);
        Location location = center = result != null ? result.getHitPosition().toLocation(player.getWorld()) : player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(15));
        if (this.worldGuardHook.isInRestrictedRegion(center)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (this.isTooClose(center)) {
            this.configuration.getHydroCageItem().getTooClose().send((CommandSender)player);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.2f, 1.0f);
            return;
        }
        this.spawnProjectileEffect(player, center);
        this.buildCage(center, player);
        this.configuration.getHydroCageItem().getOnUse().send((CommandSender)player);
    }

    private void spawnProjectileEffect(Player player, Location target) {
        final Vector direction = target.toVector().subtract(player.getEyeLocation().toVector()).normalize();
        final double distance = player.getEyeLocation().distance(target);
        final Location current = player.getEyeLocation().clone();
        UUID uuid = player.getUniqueId();
        BukkitTask task = new BukkitRunnable(){
            double traveled = 0.0;

            public void run() {
                if (this.traveled > distance) {
                    this.cancel();
                    return;
                }
                current.add(direction.clone().multiply(1.0));
                World world = current.getWorld();
                if (world == null) {
                    this.cancel();
                    return;
                }
                world.spawnParticle(Particle.SPLASH, current, 3, 0.1, 0.1, 0.1, 0.05);
                if ((int)(this.traveled * 2.0) % 2 == 0) {
                    world.playSound(current, Sound.ENTITY_DOLPHIN_SPLASH, 0.5f, 1.2f);
                }
                this.traveled += 1.0;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
        this.playerTasks.computeIfAbsent(uuid, ignored -> new ArrayList()).add(task);
    }

    private void buildCage(final Location center, final Player player) {
        final World world = center.getWorld();
        final UUID uuid = player.getUniqueId();
        final int radius = this.configuration.getHydroCageItem().getRadius();
        long buildSpeedTicks = this.configuration.getHydroCageItem().getBuildSpeedTicks();
        final int effectDuration = this.configuration.getHydroCageItem().getEffectDurationInSeconds();
        this.buildingCages.put(uuid, center.clone());
        final HashMap originalBlocks = new HashMap();
        world.playSound(center, Sound.BLOCK_WATER_AMBIENT, 2.0f, 1.2f);
        world.playSound(center, Sound.ENTITY_PLAYER_SPLASH, 2.0f, 0.8f);
        world.spawnParticle(Particle.SPLASH, center, 60, (double)radius, (double)radius / 2.0, (double)radius, 0.1);
        BukkitTask task = new BukkitRunnable(){
            int currentY;
            {
                this.currentY = radius;
            }

            public void run() {
                if (!HydroCageItem.this.buildingCages.containsKey(uuid)) {
                    this.cancel();
                    return;
                }
                if (this.currentY < -radius) {
                    HydroCageItem.this.cages.put(uuid, originalBlocks);
                    HydroCageItem.this.cageCenters.put(uuid, center.clone());
                    HydroCageItem.this.buildingCages.remove(uuid);
                    HydroCageItem.this.startCageTimer(uuid, player, center, originalBlocks, effectDuration);
                    this.cancel();
                    return;
                }
                for (int x = -radius; x <= radius; ++x) {
                    for (int z = -radius; z <= radius; ++z) {
                        Block block;
                        double dist = Math.sqrt(x * x + this.currentY * this.currentY + z * z);
                        if (dist > (double)radius || (block = center.clone().add((double)x, (double)this.currentY, (double)z).getBlock()).getType() == Material.BEDROCK || block.getState() instanceof Container) continue;
                        originalBlocks.putIfAbsent(block, block.getBlockData());
                        if (dist >= (double)radius - 1.2) {
                            block.setType(Material.BLUE_GLAZED_TERRACOTTA, false);
                            continue;
                        }
                        if (block.getType().isAir()) continue;
                        HydroCageItem.this.transformInsideBlock(block);
                    }
                }
                world.playSound(center.clone().add(0.0, (double)this.currentY, 0.0), Sound.BLOCK_WATER_AMBIENT, 0.5f, 1.5f);
                --this.currentY;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, buildSpeedTicks);
        this.playerTasks.computeIfAbsent(uuid, ignored -> new ArrayList()).add(task);
    }

    private void transformInsideBlock(Block block) {
        Material type = block.getType();
        if (type.name().endsWith("_LOG")) {
            block.setType(Material.WARPED_STEM, false);
        } else if (type.name().endsWith("_LEAVES")) {
            block.setType(Material.WARPED_WART_BLOCK, false);
        } else if (type == Material.SAND) {
            block.setType(Material.LIGHT_BLUE_CONCRETE_POWDER, false);
        } else {
            block.setType(Material.LIGHT_BLUE_CONCRETE, false);
        }
    }

    private void startCageTimer(final UUID uuid, final Player owner, final Location center, final Map<Block, BlockData> originalBlocks, final int durationSeconds) {
        final BossBar bossBar = Bukkit.createBossBar((String)ColorUtils.fixColors(this.configuration.getHydroCageItem().getBossBarTitle()), (BarColor)BarColor.BLUE, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);
        bossBar.setProgress(1.0);
        this.bossBarService.sendBossBar(owner, "hydro_cage", bossBar);
        this.activeBossBars.put(uuid, bossBar);
        final int bufferRadius = this.configuration.getHydroCageItem().getRadius();
        BukkitTask task = new BukkitRunnable(){
            int timeLeft;
            {
                this.timeLeft = durationSeconds;
            }

            public void run() {
                if (this.timeLeft <= 0 || !HydroCageItem.this.cages.containsKey(uuid)) {
                    bossBar.removeAll();
                    HydroCageItem.this.bossBarService.removeBossBar(owner, "hydro_cage");
                    HydroCageItem.this.activeBossBars.remove(uuid);
                    originalBlocks.forEach((block, data) -> {
                        if (block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                            block.setBlockData(data, false);
                        }
                    });
                    HydroCageItem.this.cages.remove(uuid);
                    HydroCageItem.this.cageCenters.remove(uuid);
                    this.cancel();
                    return;
                }
                HydroCageItem.this.bossBarService.updateBossBar(owner, "hydro_cage", ColorUtils.fixColors(HydroCageItem.this.configuration.getHydroCageItem().getBossBarTitle()), (double)this.timeLeft / (double)durationSeconds);
                for (Player p : center.getWorld().getPlayers()) {
                    if (p.getLocation().distance(center) <= (double)(bufferRadius + 2)) {
                        bossBar.addPlayer(p);
                        continue;
                    }
                    if (!(p.getLocation().distance(center) > (double)(bufferRadius + 5))) continue;
                    bossBar.removePlayer(p);
                }
                --this.timeLeft;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L);
        this.playerTasks.computeIfAbsent(uuid, ignored -> new ArrayList()).add(task);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        Player player = event.getPlayer();
        for (Location location : this.buildingCages.values()) {
            if (location == null || !location.getWorld().equals(block.getWorld()) || !(block.getLocation().distance(location) <= (double)this.configuration.getHydroCageItem().getRadius() + 0.2)) continue;
            event.setCancelled(true);
            this.configuration.getHydroCageItem().getCannotBreakBlock().send((CommandSender)player);
            player.playSound(block.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.2f, 0.7f);
            return;
        }
        for (final Map map : this.cages.values()) {
            if (block.getType() == Material.BLUE_GLAZED_TERRACOTTA) {
                event.setCancelled(true);
                this.configuration.getHydroCageItem().getCannotBreakBlock().send((CommandSender)player);
                return;
            }
            if (!map.containsKey(block)) continue;
            new BukkitRunnable(){

                public void run() {
                    if (block.getType().isAir()) {
                        map.remove(block);
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 1L);
            event.setDropItems(false);
            player.playSound(block.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.0f, 1.6f);
            player.spawnParticle(Particle.BUBBLE_POP, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.05);
            return;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.isInsideAnyCage(event.getBlockPlaced().getLocation())) {
            return;
        }
        event.setCancelled(true);
        this.configuration.getHydroCageItem().getCannotPlaceBlock().send((CommandSender)event.getPlayer());
        event.getPlayer().playSound(event.getBlockPlaced().getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.2f, 0.7f);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL && event.getCause() != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            return;
        }
        for (Location center : this.cageCenters.values()) {
            if (!event.getFrom().getWorld().equals(center.getWorld()) || !(event.getFrom().distance(center) <= (double)this.configuration.getHydroCageItem().getRadius() + 0.5)) continue;
            event.setCancelled(true);
            this.configuration.getHydroCageItem().getTeleportBlocked().send((CommandSender)event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteractItem(PlayerInteractEvent event) {
        if (event.getHand() == null) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL && item.getType() != Material.CHORUS_FRUIT) {
            return;
        }
        if (!this.isInsideAnyCage(event.getPlayer().getLocation())) {
            return;
        }
        event.setCancelled(true);
        this.configuration.getHydroCageItem().getItemUseBlocked().send((CommandSender)event.getPlayer());
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.2f, 0.6f);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        this.filterExplosion(event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        this.filterExplosion(event.blockList());
    }

    private void filterExplosion(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return;
        }
        double protectRadiusSq = Math.pow((double)this.configuration.getHydroCageItem().getRadius() + 1.5, 2.0);
        blocks.removeIf(block -> {
            Location blockLoc = block.getLocation();
            World world = blockLoc.getWorld();
            for (Location center : this.cageCenters.values()) {
                if (!center.getWorld().equals(world) || !(center.distanceSquared(blockLoc) <= protectRadiusSq)) continue;
                return true;
            }
            for (Location center : this.buildingCages.values()) {
                if (!center.getWorld().equals(world) || !(center.distanceSquared(blockLoc) <= protectRadiusSq)) continue;
                return true;
            }
            return false;
        });
    }

    private boolean isInsideAnyCage(Location loc) {
        double radiusSq = Math.pow((double)this.configuration.getHydroCageItem().getRadius() + 0.5, 2.0);
        World world = loc.getWorld();
        for (Location center : this.cageCenters.values()) {
            if (!center.getWorld().equals(world) || !(center.distanceSquared(loc) <= radiusSq)) continue;
            return true;
        }
        for (Location center : this.buildingCages.values()) {
            if (!center.getWorld().equals(world) || !(center.distanceSquared(loc) <= radiusSq)) continue;
            return true;
        }
        return false;
    }

    private boolean isTooClose(Location center) {
        int radius = this.configuration.getHydroCageItem().getRadius();
        for (Location location : this.buildingCages.values()) {
            if (!location.getWorld().equals(center.getWorld()) || !(location.distance(center) <= (double)(radius * 2))) continue;
            return true;
        }
        for (Location location : this.cageCenters.values()) {
            if (!location.getWorld().equals(center.getWorld()) || !(location.distance(center) <= (double)(radius * 2))) continue;
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossBar bossBar;
        UUID uuid = event.getPlayer().getUniqueId();
        List<BukkitTask> tasks = this.playerTasks.remove(uuid);
        if (tasks != null) {
            tasks.forEach(BukkitTask::cancel);
        }
        if ((bossBar = this.activeBossBars.remove(uuid)) != null) {
            bossBar.removeAll();
        }
        this.bossBarService.removeBossBar(event.getPlayer(), "hydro_cage");
        Map<Block, BlockData> cage = this.cages.remove(uuid);
        if (cage != null) {
            cage.forEach((block, data) -> block.setBlockData(data, false));
        }
        this.cageCenters.remove(uuid);
        this.buildingCages.remove(uuid);
    }

    public void removeAllCages() {
        this.activeBossBars.forEach((uuid, bossBar) -> {
            bossBar.removeAll();
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player != null) {
                this.bossBarService.removeBossBar(player, "hydro_cage");
            }
        });
        this.activeBossBars.clear();
        this.cages.values().forEach(map -> map.forEach((block, data) -> block.setBlockData(data, false)));
        this.cages.clear();
        this.cageCenters.clear();
        this.buildingCages.clear();
        this.playerTasks.values().forEach(tasks -> tasks.forEach(BukkitTask::cancel));
        this.playerTasks.clear();
    }
}

