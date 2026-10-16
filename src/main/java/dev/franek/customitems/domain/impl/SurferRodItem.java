package dev.franek.customitems.domain.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class SurferRodItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public SurferRodItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "surfer_rod";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getSurferRodItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Vector pull;
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!this.isThisItem(itemInMainHand) && !this.isThisItem(itemInOffHand)) {
            return;
        }
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (this.worldGuardHook.isInRestrictedRegion(player)) {
                event.setCancelled(true);
            }
            return;
        }
        if (event.getState() != PlayerFishEvent.State.REEL_IN && event.getState() != PlayerFishEvent.State.IN_GROUND && event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getSurferRodItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            if (event.getHook() != null) {
                event.getHook().remove();
            }
            return;
        }
        Entity caught = event.getCaught();
        if (caught != null) {
            pull = caught.getLocation().toVector().subtract(player.getLocation().toVector());
        } else if (event.getHook() != null) {
            pull = event.getHook().getLocation().toVector().subtract(player.getLocation().toVector());
        } else {
            return;
        }
        if (pull.lengthSquared() < 0.1) {
            return;
        }
        pull.normalize().multiply(this.configuration.getSurferRodItem().getLaunchPullVector()).add(new Vector(0.0, 0.2, 0.0));
        player.setVelocity(pull);
    }
}

