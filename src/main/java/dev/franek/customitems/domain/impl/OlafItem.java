package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.ColorUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class OlafItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Map<UUID, UUID> snowmanToVictim = new HashMap<UUID, UUID>();
    private final Map<UUID, Integer> victimHits = new HashMap<UUID, Integer>();
    private final Map<UUID, Snowman> activeSnowmen = new HashMap<UUID, Snowman>();
    private BukkitTask teleportTask = null;

    public OlafItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "olaf_item";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getOlafItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    private void startTeleportTask() {
        if (this.teleportTask != null) {
            return;
        }
        this.teleportTask = new BukkitRunnable(){

            public void run() {
                if (OlafItem.this.activeSnowmen.isEmpty()) {
                    OlafItem.this.teleportTask = null;
                    this.cancel();
                    return;
                }
                OlafItem.this.activeSnowmen.entrySet().removeIf(entry -> {
                    Player victim = OlafItem.this.plugin.getServer().getPlayer((UUID)entry.getKey());
                    if (victim == null || !victim.isOnline() || ((Snowman)entry.getValue()).isDead()) {
                        Snowman snowman = (Snowman)entry.getValue();
                        OlafItem.this.snowmanToVictim.remove(snowman.getUniqueId());
                        OlafItem.this.victimHits.remove(entry.getKey());
                        snowman.remove();
                        return true;
                    }
                    Location loc = victim.getEyeLocation().add(victim.getLocation().getDirection().multiply(0.2));
                    loc.setY(loc.getY() - 0.5);
                    ((Snowman)entry.getValue()).teleport(loc);
                    return false;
                });
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)projectile;
        ProjectileSource projectileSource = egg.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player shooter = (Player)projectileSource;
        Entity entity = event.getHitEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player target = (Player)entity;
        if (!this.isThisItem(shooter.getInventory().getItemInMainHand()) && !this.isThisItem(shooter.getInventory().getItemInOffHand())) {
            return;
        }
        if (this.cooldownController.canUse(shooter, this.getItemKey(), this.configuration.getOlafItem().getCooldownInSeconds())) {
            this.spawnOlaf(target, shooter);
        }
    }

    private void spawnOlaf(Player target, Player shooter) {
        if (this.activeSnowmen.containsKey(target.getUniqueId())) {
            return;
        }
        Snowman olaf = (Snowman)target.getWorld().spawnEntity(target.getLocation(), EntityType.SNOW_GOLEM);
        olaf.setAI(false);
        olaf.setInvulnerable(false);
        int totalHits = this.configuration.getOlafItem().getHitsToBreak();
        olaf.setCustomName(ColorUtils.fixColors(this.configuration.getOlafItem().getSnowmanName().replace("{HITS_LEFT}", String.valueOf(totalHits)).replace("{HITS_TOTAL}", String.valueOf(totalHits))));
        olaf.setCustomNameVisible(true);
        this.activeSnowmen.put(target.getUniqueId(), olaf);
        this.snowmanToVictim.put(olaf.getUniqueId(), target.getUniqueId());
        this.victimHits.put(target.getUniqueId(), totalHits);
        this.startTeleportTask();
        this.configuration.getOlafItem().getOnUse().send((CommandSender)shooter, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getOlafItem().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{SHOOTER}", shooter.getName()));
    }

    @EventHandler
    public void onOlafDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        UUID olafUUID = event.getEntity().getUniqueId();
        if (!this.snowmanToVictim.containsKey(olafUUID)) {
            return;
        }
        UUID victimUUID = this.snowmanToVictim.get(olafUUID);
        event.setDamage(0.0);
        int left = this.victimHits.get(victimUUID) - 1;
        if (left <= 0) {
            this.removeOlaf(victimUUID);
            this.configuration.getOlafItem().getOnFreed().send((CommandSender)player);
        } else {
            this.victimHits.put(victimUUID, left);
            Snowman olaf = this.activeSnowmen.get(victimUUID);
            int total = this.configuration.getOlafItem().getHitsToBreak();
            olaf.setCustomName(ColorUtils.fixColors(this.configuration.getOlafItem().getSnowmanName().replace("{HITS_LEFT}", String.valueOf(left)).replace("{HITS_TOTAL}", String.valueOf(total))));
        }
    }

    private void removeOlaf(UUID victimUUID) {
        Snowman olaf = this.activeSnowmen.remove(victimUUID);
        if (olaf != null) {
            this.snowmanToVictim.remove(olaf.getUniqueId());
            olaf.remove();
        }
        this.victimHits.remove(victimUUID);
    }
}

