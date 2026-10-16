package dev.franek.customitems.domain.impl;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class WarmMilkItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public WarmMilkItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "wam_milk";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getWarmMilkItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (player.getActivePotionEffects().isEmpty()) {
            this.configuration.getWarmMilkItem().getNoEffectsToRemove().send((CommandSender)player);
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getWarmMilkItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        player.getActivePotionEffects().forEach(effect -> {
            if (this.isNegativeEffect(effect.getType())) {
                player.removePotionEffect(effect.getType());
            }
        });
        player.playSound(player.getLocation(), Sound.ENTITY_COW_MILK, 0.5f, 1.0f);
        this.configuration.getWarmMilkItem().getOnUse().send((CommandSender)player);
        if (player.getGameMode() != GameMode.CREATIVE) {
            clickedItem.setAmount(clickedItem.getAmount() - 1);
        }
    }

    private boolean isNegativeEffect(PotionEffectType type) {
        String name = type.getName().toUpperCase();
        return name.contains("SLOW") || name.contains("CONFUSION") || name.contains("BLIND") || name.contains("HUNGER") || name.contains("WEAK") || name.contains("POISON") || name.contains("WITHER") || name.contains("BAD") || name.contains("DARKNESS") || name.contains("MINING_FATIGUE") || name.contains("UNLUCK") || name.contains("LEVITATION");
    }
}

