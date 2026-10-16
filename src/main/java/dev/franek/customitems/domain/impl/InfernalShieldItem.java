package dev.franek.customitems.domain.impl;

import java.util.Random;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
import dev.franek.customitems.shared.util.MathUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class InfernalShieldItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final Random random = new Random();

    public InfernalShieldItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "infernal_shield";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getInfernalShieldItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player defender = (Player)entity;
        Entity entity2 = event.getDamager();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player attacker = (Player)entity2;
        if (!this.isThisItem(defender.getInventory().getItemInOffHand())) {
            return;
        }
        if (!defender.isBlocking()) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(defender) || this.worldGuardHook.isInRestrictedRegion(attacker)) {
            return;
        }
        if (!this.cooldownController.canUse(defender, this.getItemKey(), this.configuration.getInfernalShieldItem().getCooldownInSeconds())) {
            return;
        }
        int chance = this.random.nextInt(100);
        if (!MathUtils.chance(this.configuration.getInfernalShieldItem().getReflectChance())) {
            return;
        }
        double damage = event.getDamage();
        event.setCancelled(true);
        attacker.damage(damage, (Entity)defender);
        this.configuration.getInfernalShieldItem().getOnDefender().send((CommandSender)defender, new PlaceholderSet().addPlaceholder("{ATTACKER}", attacker.getName()));
        this.configuration.getInfernalShieldItem().getOnAttacker().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TARGET}", defender.getName()));
    }
}

