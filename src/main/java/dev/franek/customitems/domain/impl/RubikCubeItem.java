package dev.franek.customitems.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.itemstack.ItemStackBuilder;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;
import dev.franek.customitems.shared.util.SkullUtil;

public class RubikCubeItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public RubikCubeItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "rubik_cube";
    }

    @Override
    public ItemStack buildItem() {
        ItemMeta baseMeta = this.configuration.getRubikCubeItem().getItem().getItemMeta();
        ItemStack skull = ItemStackBuilder.of(Material.PLAYER_HEAD).build();
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setDisplayName(baseMeta.getDisplayName());
        skullMeta.setLore(baseMeta.getLore());
        if (baseMeta.hasCustomModelData()) {
            skullMeta.setCustomModelData(Integer.valueOf(baseMeta.getCustomModelData()));
        }
        baseMeta.getEnchants().forEach((enchant, level) -> skullMeta.addEnchant(enchant, level.intValue(), true));
        skullMeta.addItemFlags(baseMeta.getItemFlags().toArray(new ItemFlag[0]));
        skullMeta.setUnbreakable(baseMeta.isUnbreakable());
        baseMeta.getPersistentDataContainer().getKeys().forEach(key -> skullMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, ((String)baseMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING))));
        PDCUtils.setString(skullMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        SkullUtil.getInstance().fromB64(skullMeta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FkYjk4YzFlNWI3ZjdjNTc0MzlhYTY2YzMyN2M0NzRkMWM3N2E2NzdiY2RjMDllMTQ4M2E3ODk5ZmQyZjk0ZCJ9fX0=");
        skull.setItemMeta((ItemMeta)skullMeta);
        return skull;
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
        ItemStack itemInMainHand = attacker.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = attacker.getInventory().getItemInOffHand();
        ItemStack item = this.isThisItem(itemInMainHand) ? itemInMainHand : (this.isThisItem(itemInOffHand) ? itemInOffHand : null);
        if (item == null) {
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(attacker.getLocation()) || this.worldGuardHook.isInRestrictedRegion(target.getLocation())) {
            event.setCancelled(true);
            return;
        }
        if (!this.cooldownController.canUse(attacker, this.getItemKey(), this.configuration.getRubikCubeItem().getCooldownInSeconds())) {
            event.setCancelled(true);
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(attacker, this.getItemKey())));
            return;
        }
        this.shuffleHotbar(target);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.5f);
        this.configuration.getRubikCubeItem().getOnUse().send((CommandSender)attacker, new PlaceholderSet().addPlaceholder("{TARGET}", target.getName()));
        this.configuration.getRubikCubeItem().getOnTarget().send((CommandSender)target, new PlaceholderSet().addPlaceholder("{SHOOTER}", attacker.getName()));
        item.setAmount(item.getAmount() - 1);
    }

    private void shuffleHotbar(Player player) {
        int i;
        ArrayList<ItemStack> hotbarItems = new ArrayList<ItemStack>();
        for (i = 0; i < 9; ++i) {
            hotbarItems.add(player.getInventory().getItem(i));
        }
        Collections.shuffle(hotbarItems);
        for (i = 0; i < 9; ++i) {
            player.getInventory().setItem(i, (ItemStack)hotbarItems.get(i));
        }
    }
}

