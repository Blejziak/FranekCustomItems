package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.util.PDCUtils;

public class InfernalSwordItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final Map<UUID, BukkitRunnable> burningTasks = new HashMap<UUID, BukkitRunnable>();

    public InfernalSwordItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
    }

    @Override
    public String getItemKey() {
        return "infernal_sword";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getInfernalSwordItem().getItem().clone();
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
        Player attacker = (Player)entity;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (!this.isThisItem(attacker.getInventory().getItemInMainHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker) || this.worldGuardHook.isInRestrictedRegion(target)) {
            return;
        }
        this.applyInfernalFire(attacker, target);
    }

    private void applyInfernalFire(final Player attacker, final Player target) {
        CustomItemsConfiguration.InfernalSwordItemSection section = this.configuration.getInfernalSwordItem();
        final int fireDuration = section.getFireDurationInTicks();
        final double fireDamage = section.getFireDamagePerSecond();
        target.setFireTicks(fireDuration);
        BukkitRunnable existing = this.burningTasks.remove(target.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
        BukkitRunnable task = new BukkitRunnable(){
            int secondsLeft;
            {
                this.secondsLeft = fireDuration / 20;
            }

            public void run() {
                if (!target.isOnline() || this.secondsLeft <= 0) {
                    InfernalSwordItem.this.burningTasks.remove(target.getUniqueId());
                    this.cancel();
                    return;
                }
                target.damage(fireDamage, (Entity)attacker);
                --this.secondsLeft;
            }
        };
        task.runTaskTimer((Plugin)this.plugin, 0L, 20L);
        this.burningTasks.put(target.getUniqueId(), task);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitRunnable task = this.burningTasks.remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
}

