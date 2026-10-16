package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.itemstack.ItemStackBuilder;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class BalloonItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public BalloonItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "balonikzhelem";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getBalloonItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!this.isThisItem(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (this.worldGuardHook.isInRestrictedRegion(player.getLocation())) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getBalloonItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        this.spawnBalloon(player);
        event.getItem().setAmount(event.getItem().getAmount() - 1);
    }

    private void spawnBalloon(Player owner) {
        Location loc = owner.getLocation();
        ArmorStand balloon = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        balloon.setVisible(false);
        balloon.setGravity(false);
        balloon.setSmall(true);
        balloon.setMarker(true);
        this.configuration.getBalloonItem().getSpawn().send((CommandSender)owner);
        if (!balloon.isValid()) {
            return;
        }
        balloon.setHelmet(ItemStackBuilder.of(Material.PLAYER_HEAD).setOwnerProfile(owner).build());
        this.startFloating(balloon);
    }

    private void startFloating(final ArmorStand balloon) {
        new BukkitRunnable(){

            public void run() {
                if (!balloon.isValid()) {
                    this.cancel();
                    return;
                }
                Location currentLoc = balloon.getLocation();
                if (currentLoc.getY() >= (double)BalloonItem.this.configuration.getBalloonItem().getMaxHeight()) {
                    balloon.remove();
                    this.cancel();
                    return;
                }
                Location nextLoc = currentLoc.add(0.0, BalloonItem.this.configuration.getBalloonItem().getSpeedPerUpdate(), 0.0);
                balloon.teleport(nextLoc);
                Block blockAbove = nextLoc.clone().add(0.0, 1.5, 0.0).getBlock();
                if (blockAbove.getType() == Material.BEDROCK) {
                    balloon.remove();
                    this.cancel();
                    return;
                }
                if (blockAbove.getType() != Material.AIR && !BalloonItem.this.worldGuardHook.isInRestrictedRegion(blockAbove.getLocation())) {
                    blockAbove.breakNaturally();
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, (long)this.configuration.getBalloonItem().getUpdateTickRate());
    }
}

