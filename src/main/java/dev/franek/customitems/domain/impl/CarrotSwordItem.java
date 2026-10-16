package dev.franek.customitems.domain.impl;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import dev.franek.customitems.shared.util.PDCUtils;

public class CarrotSwordItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public CarrotSwordItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "marchewkowy_miecz";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getCarrotSword().getItem().clone();
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
        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (!this.isThisItem(item)) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            return;
        }
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getCarrotSword().getCooldownInSeconds())) {
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, this.configuration.getCarrotSword().getFreezeDurationTicks(), 10, false, false));
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
        this.configuration.getCarrotSword().getOnUse().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getCarrotSword().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{ATTACKER}", attacker.getName()));
    }
}

