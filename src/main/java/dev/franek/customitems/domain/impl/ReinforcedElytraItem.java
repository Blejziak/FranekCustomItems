package dev.franek.customitems.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.LocationUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class ReinforcedElytraItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final Map<UUID, Double> playerCharge = new HashMap<UUID, Double>();

    public ReinforcedElytraItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
    }

    @Override
    public String getItemKey() {
        return "reinforced_elytra";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getReinforcedElytra().getItem().clone();
        ItemMeta meta = item.getItemMeta();
        PDCUtils.setString(meta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isGliding()) {
            return;
        }
        if (LocationUtils.compareLocations(event.getFrom(), event.getTo())) {
            return;
        }
        ItemStack chest = player.getInventory().getChestplate();
        if (!this.isThisItem(chest)) {
            return;
        }
        UUID uuid = player.getUniqueId();
        double currentCharge = this.playerCharge.getOrDefault(uuid, 0.0);
        if (currentCharge < 100.0) {
            currentCharge = Math.min(100.0, currentCharge + this.configuration.getReinforcedElytra().getChargeRate());
            this.playerCharge.put(uuid, currentCharge);
        }
        this.configuration.getReinforcedElytra().getActionBar().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{PERCENTAGE}", String.format("%.1f", currentCharge)));
    }

    @EventHandler
    public void onGlideToggle(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!event.isGliding()) {
            double charge = this.playerCharge.getOrDefault(player.getUniqueId(), 0.0);
            if (charge >= 100.0 && player.getLocation().subtract(0.0, 0.1, 0.0).getBlock().getType().isSolid()) {
                this.handleLanding(player);
            }
            this.playerCharge.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            this.playerCharge.remove(event.getPlayer().getUniqueId());
        }
    }

    private void handleLanding(Player attacker) {
        Location location = attacker.getLocation();
        double radius = this.configuration.getReinforcedElytra().getLandingRadius();
        int hitCount = 0;
        location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 3);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        location.getWorld().spawnParticle(Particle.DUST, location, 100, 2.0, 0.0, 2.0, new Particle.DustOptions(Color.PURPLE, 2.0f));
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                Block block = location.clone().add((double)x, -1.0, (double)z).getBlock();
                if (block.getType() == Material.AIR || !block.getType().isSolid()) continue;
                FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0.1, 0.5), block.getBlockData());
                fallingBlock.setVelocity(new Vector((double)x * 0.1, 0.4, (double)z * 0.1));
                fallingBlock.setDropItem(false);
            }
        }
        for (Entity entity : attacker.getNearbyEntities(radius, radius, radius)) {
            Player target;
            if (!(entity instanceof Player) || (target = (Player)entity).equals(attacker) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) continue;
            target.damage(this.configuration.getReinforcedElytra().getDamage(), (Entity)attacker);
            ++hitCount;
            this.configuration.getReinforcedElytra().getTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{ATTACKER}", attacker.getName()));
        }
        if (hitCount > 0) {
            this.configuration.getReinforcedElytra().getAttacker().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{COUNT}", String.valueOf(hitCount)));
        }
    }
}

