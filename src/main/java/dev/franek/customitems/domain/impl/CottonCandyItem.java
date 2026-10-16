package dev.franek.customitems.domain.impl;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class CottonCandyItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public CottonCandyItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "cotton_candy";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getCottonCandyItem().getItem().clone();
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
        ItemStack[] armor = player.getInventory().getArmorContents();
        int repairIndex = -1;
        for (int i = 0; i < armor.length; ++i) {
            ItemStack armorPiece = armor[i];
            if (armorPiece == null || armorPiece.getType() == Material.AIR || armorPiece.getDurability() <= 0) continue;
            repairIndex = i;
            break;
        }
        if (repairIndex == -1) {
            this.configuration.getCottonCandyItem().getNothingToRepair().send((CommandSender)player);
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getCottonCandyItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        armor[repairIndex].setDurability((short)0);
        player.getInventory().setArmorContents(armor);
        clickedItem.setAmount(clickedItem.getAmount() - 1);
        this.configuration.getCottonCandyItem().getOnUse().send((CommandSender)player);
    }
}

