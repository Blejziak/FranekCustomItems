package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class ArcusMagnusItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final Map<UUID, PlayerCombo> activeCombos = new HashMap<UUID, PlayerCombo>();

    public ArcusMagnusItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
    }

    @Override
    public String getItemKey() {
        return "arcus_magnus";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getArcusMagnusItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (!(entity instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)entity;
        ProjectileSource projectileSource = arrow.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player shooter = (Player)projectileSource;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (!this.isThisItem(shooter.getInventory().getItemInMainHand()) && !this.isThisItem(shooter.getInventory().getItemInOffHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(shooter.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            event.setCancelled(true);
            return;
        }
        UUID shooterUUID = shooter.getUniqueId();
        long now = System.currentTimeMillis();
        PlayerCombo combo = this.activeCombos.get(shooterUUID);
        int currentStep = 0;
        currentStep = combo != null && combo.getTargetUUID().equals(target.getUniqueId()) && now - combo.getLastHitTime() <= this.configuration.getArcusMagnusItem().getComboDurationMs() ? Math.min(combo.getStep() + 1, this.configuration.getArcusMagnusItem().getComboDamage().size() - 1) : 0;
        this.activeCombos.put(shooterUUID, new PlayerCombo(target.getUniqueId(), currentStep, now));
        double extraDamage = this.configuration.getArcusMagnusItem().getComboDamage().get(currentStep);
        event.setDamage(event.getDamage() + extraDamage);
        this.sendComboNotifications(shooter, target, extraDamage);
    }

    private void sendComboNotifications(Player shooter, Player target, double damage) {
        String damageStr = String.valueOf(damage);
        this.configuration.getArcusMagnusItem().getOnShooter().send((CommandSender)shooter, new PlaceholderSet().addPlaceholder("{DAMAGE}", damageStr).addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getArcusMagnusItem().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{DAMAGE}", damageStr).addPlaceholder("{SHOOTER}", shooter.getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.activeCombos.remove(event.getPlayer().getUniqueId());
    }

    private static class PlayerCombo {
        private final UUID targetUUID;
        private int step;
        private long lastHitTime;
        public UUID getTargetUUID() {
            return this.targetUUID;
        }
        public int getStep() {
            return this.step;
        }
        public long getLastHitTime() {
            return this.lastHitTime;
        }
        public void setStep(int step) {
            this.step = step;
        }
        public void setLastHitTime(long lastHitTime) {
            this.lastHitTime = lastHitTime;
        }
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PlayerCombo)) {
                return false;
            }
            PlayerCombo other = (PlayerCombo)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getStep() != other.getStep()) {
                return false;
            }
            if (this.getLastHitTime() != other.getLastHitTime()) {
                return false;
            }
            UUID this$targetUUID = this.getTargetUUID();
            UUID other$targetUUID = other.getTargetUUID();
            return !(this$targetUUID == null ? other$targetUUID != null : !(this$targetUUID).equals(other$targetUUID));
        }
        protected boolean canEqual(Object other) {
            return other instanceof PlayerCombo;
        }
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getStep();
            long $lastHitTime = this.getLastHitTime();
            result = result * 59 + (int)($lastHitTime >>> 32 ^ $lastHitTime);
            UUID $targetUUID = this.getTargetUUID();
            result = result * 59 + ($targetUUID == null ? 43 : ($targetUUID).hashCode());
            return result;
        }
        public String toString() {
            return "ArcusMagnusItem.PlayerCombo(targetUUID=" + String.valueOf(this.getTargetUUID()) + ", step=" + this.getStep() + ", lastHitTime=" + this.getLastHitTime() + ")";
        }
        public PlayerCombo(UUID targetUUID, int step, long lastHitTime) {
            this.targetUUID = targetUUID;
            this.step = step;
            this.lastHitTime = lastHitTime;
        }
    }
}

