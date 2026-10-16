package dev.franek.customitems.domain.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class DynamiteItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public DynamiteItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "dynamite_item";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getDynamiteItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack playerItemInMainHand = event.getItemInHand();
        if (playerItemInMainHand.hasItemMeta() && PDCUtils.getString(playerItemInMainHand.getItemMeta(), this.plugin.getCustomItemKey()) == null) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.BEDROCK) {
            return;
        }
        block.getChunk().getPersistentDataContainer().set(new NamespacedKey((Plugin)this.plugin, this.getLocationKey(block)), PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.BEDROCK) {
            return;
        }
        NamespacedKey blockLocationKey = new NamespacedKey((Plugin)this.plugin, this.getLocationKey(block));
        if (!block.getChunk().getPersistentDataContainer().has(blockLocationKey, PersistentDataType.BYTE)) {
            return;
        }
        block.getChunk().getPersistentDataContainer().remove(blockLocationKey);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack clickedItem = event.getItem();
        if (!this.isThisItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() != Material.BEDROCK) {
            this.configuration.getDynamiteItem().getTargetBlockIsNotBedrock().send((CommandSender)player);
            return;
        }
        NamespacedKey blockLocationKey = new NamespacedKey((Plugin)this.plugin, this.getLocationKey(targetBlock));
        if (!targetBlock.getChunk().getPersistentDataContainer().has(blockLocationKey, PersistentDataType.BYTE)) {
            this.configuration.getDynamiteItem().getTargetBlockIsNaturalBedrock().send((CommandSender)player);
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(player) || event.getClickedBlock() == null || this.worldGuardHook.isInRestrictedRegion(event.getClickedBlock().getLocation())) {
            this.configuration.getMessagesSection().getCannotUseInRegion().send((CommandSender)player);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getDynamiteItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            return;
        }
        Location targetBlockLocation = targetBlock.getLocation();
        targetBlock.getChunk().getPersistentDataContainer().remove(blockLocationKey);
        targetBlock.setType(Material.AIR);
        player.getWorld().playSound(targetBlockLocation, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.0f);
        this.configuration.getDynamiteItem().getOnUse().send((CommandSender)player);
        clickedItem.setAmount(clickedItem.getAmount() - 1);
    }

    private String getLocationKey(Block block) {
        return "b_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
    }
}

