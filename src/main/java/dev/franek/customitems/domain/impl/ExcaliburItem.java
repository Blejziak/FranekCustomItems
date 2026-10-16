package dev.franek.customitems.domain.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.itemstack.ItemStackPreparer;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.ColorUtils;
import dev.franek.customitems.shared.util.PDCUtils;

public class ExcaliburItem
extends CustomItem {
    private final Cache<UUID, Map<UUID, Long>> cache = CacheBuilder.newBuilder().expireAfterAccess(1L, TimeUnit.HOURS).build();
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;

    public ExcaliburItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
    }

    @Override
    public String getItemKey() {
        return "excalibur_item";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getExcaliburItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return ItemStackPreparer.prepare(item, new PlaceholderSet().addPlaceholder("{KILLS}", 0).addPlaceholder("{PROGRESS}", ColorUtils.fixColors("&f&l-".repeat(14))).addPlaceholder("{PERCENT}", "0"));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victimPlayer = event.getEntity();
        Player killerPlayer = victimPlayer.getKiller();
        if (killerPlayer == null || killerPlayer.equals(victimPlayer)) {
            return;
        }
        ItemStack weapon = killerPlayer.getInventory().getItemInMainHand();
        if (!this.isThisItem(weapon) || this.worldGuardHook.isInRestrictedRegion(victimPlayer)) {
            return;
        }
        boolean keepInventory = Boolean.TRUE.equals(victimPlayer.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY));
        if (keepInventory) {
            return;
        }
        if (this.configuration.getExcaliburItem().isCheckIP()) {
            String killerPlayerIp;
            String victimPlayerIp = killerPlayer.getAddress() != null ? killerPlayer.getAddress().getAddress().getHostAddress() : "1";
            String string = killerPlayerIp = victimPlayer.getAddress() != null ? victimPlayer.getAddress().getAddress().getHostAddress() : "2";
            if (victimPlayerIp.equalsIgnoreCase(killerPlayerIp)) {
                this.configuration.getExcaliburItem().getBlockIP().send((CommandSender)killerPlayer);
                return;
            }
        }
        if (!this.hasKilledRecently(killerPlayer.getUniqueId(), victimPlayer.getUniqueId())) {
            this.increaseKills(weapon);
            this.recordKill(killerPlayer.getUniqueId(), victimPlayer.getUniqueId());
        } else {
            this.configuration.getExcaliburItem().getRecentlyKilled().send((CommandSender)killerPlayer);
        }
    }

    private boolean hasKilledRecently(UUID killerPlayerUniqueId, UUID victimPlayerUniqueId) {
        if (killerPlayerUniqueId == null) {
            return false;
        }
        Map victims = (Map)this.cache.getIfPresent(killerPlayerUniqueId);
        if (victims == null || !victims.containsKey(victimPlayerUniqueId)) {
            return false;
        }
        Long lastKillTime = (Long)victims.get(victimPlayerUniqueId);
        if (lastKillTime == null) {
            return false;
        }
        if (System.currentTimeMillis() - lastKillTime >= (long)this.configuration.getExcaliburItem().getCooldownInSeconds() * 1000L) {
            victims.remove(victimPlayerUniqueId);
            return false;
        }
        return true;
    }

    private void recordKill(UUID killerId, UUID killedId) {
        if (killerId == null || killedId == null) {
            return;
        }
        HashMap<UUID, Long> victims = (HashMap<UUID, Long>)this.cache.getIfPresent(killerId);
        if (victims == null) {
            victims = new HashMap<UUID, Long>();
            this.cache.put(killerId, victims);
        }
        victims.put(killedId, System.currentTimeMillis());
    }

    public void increaseKills(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey((Plugin)this.plugin, "excalibur_kills");
        int kills = (Integer)meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0) + 1;
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, kills);
        int maxKills = this.configuration.getExcaliburItem().getMaxKills();
        double progressPercent = Math.min((double)kills / (double)maxKills * 100.0, 100.0);
        int barLength = 14;
        int filled = (int)Math.round(progressPercent / 100.0 * (double)barLength);
        String bar = "&a&l-".repeat(Math.max(0, filled)) + "&f&l-".repeat(Math.max(0, barLength - filled));
        String progressLine = ColorUtils.fixColors("&8» " + bar + " &e" + Math.round(progressPercent) + "%");
        List lore = this.configuration.getExcaliburItem().getItem().getItemMeta().getLore().stream().map(line -> line.replace("{KILLS}", String.valueOf(kills)).replace("{PROGRESS}", progressLine).replace("{PERCENT}", String.valueOf(Math.round(progressPercent)))).map(ColorUtils::fixColors).collect(Collectors.toList());
        meta.setLore(lore);
        if (kills >= maxKills && meta.getEnchantLevel(Enchantment.SHARPNESS) < this.configuration.getExcaliburItem().getMaxSharpness()) {
            meta.addEnchant(Enchantment.SHARPNESS, this.configuration.getExcaliburItem().getMaxSharpness(), true);
        }
        item.setItemMeta(meta);
    }

    public void setKills(ItemStack item, int kills) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey((Plugin)this.plugin, "excalibur_kills");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, kills);
        int maxKills = this.configuration.getExcaliburItem().getMaxKills();
        double progressPercent = Math.min((double)kills / (double)maxKills * 100.0, 100.0);
        int barLength = 14;
        int filled = (int)Math.round(progressPercent / 100.0 * (double)barLength);
        String bar = "&a&l-".repeat(Math.max(0, filled)) + "&f&l-".repeat(Math.max(0, barLength - filled));
        String progressLine = ColorUtils.fixColors(bar);
        List lore = this.configuration.getExcaliburItem().getItem().getItemMeta().getLore().stream().map(line -> line.replace("{KILLS}", String.valueOf(kills)).replace("{PROGRESS}", progressLine).replace("{PERCENT}", String.valueOf(Math.round(progressPercent)))).map(ColorUtils::fixColors).collect(Collectors.toList());
        meta.setLore(lore);
        if (kills >= maxKills && meta.getEnchantLevel(Enchantment.SHARPNESS) < this.configuration.getExcaliburItem().getMaxSharpness()) {
            meta.addEnchant(Enchantment.SHARPNESS, this.configuration.getExcaliburItem().getMaxSharpness(), true);
        } else if (kills < maxKills) {
            meta.addEnchant(Enchantment.SHARPNESS, this.configuration.getExcaliburItem().getItem().getItemMeta().getEnchantLevel(Enchantment.SHARPNESS), true);
        }
        item.setItemMeta(meta);
    }
}

