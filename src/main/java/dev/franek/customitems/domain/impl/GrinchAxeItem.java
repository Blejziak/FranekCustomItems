package dev.franek.customitems.domain.impl;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class GrinchAxeItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public GrinchAxeItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "grinch_axe";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getGrinchAxeItem().getItem().clone();
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
        Player damager = (Player)entity;
        Entity entity2 = event.getEntity();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player target = (Player)entity2;
        if (event.getDamager() instanceof LightningStrike) {
            return;
        }
        if (!this.isThisItem(damager.getInventory().getItemInMainHand())) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(damager) || this.worldGuardHook.isInRestrictedRegion(target)) {
            return;
        }
        if (!this.cooldownController.canUse(damager, this.getItemKey(), this.configuration.getGrinchAxeItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)damager, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(damager, this.getItemKey())));
            return;
        }
        double damage = target.getHealth() * (this.configuration.getGrinchAxeItem().getDamagePercentage() / 100.0);
        double roundedDamage = (double)Math.round(damage * 10.0) / 10.0;
        target.damage(damage, (Entity)damager);
        target.getWorld().strikeLightningEffect(target.getLocation());
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        this.configuration.getGrinchAxeItem().getOnUseAttacker().send((CommandSender)damager, new PlaceholderSet().addPlaceholder("{PLAYER}", target.getName()).addPlaceholder("{DAMAGE}", String.valueOf(roundedDamage)));
        this.configuration.getGrinchAxeItem().getOnUseTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{PLAYER}", damager.getName()).addPlaceholder("{DAMAGE}", String.valueOf(roundedDamage)));
    }
}

