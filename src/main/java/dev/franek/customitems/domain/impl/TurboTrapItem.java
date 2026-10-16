package dev.franek.customitems.domain.impl;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.hook.WorldEditHook;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.shared.logger.FastLogger;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.PDCUtils;

public class TurboTrapItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;
    private final WorldGuardHook worldGuardHook;
    private final CooldownController cooldownController;
    private final WorldEditHook worldEditHook;
    private Clipboard cachedSchematic;

    public TurboTrapItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration, WorldGuardHook worldGuardHook, CooldownController cooldownController, WorldEditHook worldEditHook) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
        this.worldGuardHook = worldGuardHook;
        this.cooldownController = cooldownController;
        this.worldEditHook = worldEditHook;
        if (this.worldEditHook.isHooked()) {
            this.loadSchematic();
        }
    }

    @Override
    public String getItemKey() {
        return "turbo_trap";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getTurboTrapItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }

    public void loadSchematic() {
        try {
            File schematicFile = new File(this.plugin.getDataFolder(), "schematics/turbotrap.schem");
            if (!schematicFile.exists()) {
                FastLogger.warning("[TurboTrapItem] Brak pliku schematu: schematics/turbotrap.schem");
                return;
            }
            ClipboardFormat format = ClipboardFormats.findByFile((File)schematicFile);
            if (format == null) {
                FastLogger.warning("[TurboTrapItem] Nieznany format schematu!");
                return;
            }
            try (FileInputStream fis = new FileInputStream(schematicFile);
                 ClipboardReader reader = format.getReader((InputStream)fis);){
                this.cachedSchematic = reader.read();
                FastLogger.info("[TurboTrapItem] Schemat załadowany pomyślnie.");
            }
        }
        catch (Exception exception) {
            FastLogger.error("[TurboTrapItem] Błąd ładowania schematu: " + exception.getMessage());
        }
    }

    @EventHandler
    public void onEggThrow(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)projectile;
        ProjectileSource projectileSource = egg.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player player = (Player)projectileSource;
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!this.isThisItem(main) && !this.isThisItem(off)) {
            return;
        }
        if (!this.worldEditHook.isHooked() || this.cachedSchematic == null) {
            this.configuration.getTurboTrapItem().getSchematicNotLoaded().send((CommandSender)player);
            event.setCancelled(true);
            return;
        }
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            event.setCancelled(true);
            return;
        }
        if (!this.cooldownController.canUse(player, this.getItemKey(), this.configuration.getTurboTrapItem().getCooldownInSeconds())) {
            this.configuration.getMessagesSection().getCooldownIsActive().send((CommandSender)player, new PlaceholderSet().addPlaceholder("{TIME}", this.cooldownController.getFormattedRemainingCooldown(player, this.getItemKey())));
            event.setCancelled(true);
            return;
        }
        egg.getPersistentDataContainer().set(this.plugin.getCustomItemKey(), PersistentDataType.STRING, this.getItemKey());
        this.configuration.getTurboTrapItem().getOnUse().send((CommandSender)player);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)projectile;
        String key = (String)egg.getPersistentDataContainer().get(this.plugin.getCustomItemKey(), PersistentDataType.STRING);
        if (!this.getItemKey().equals(key)) {
            return;
        }
        ProjectileSource projectileSource = egg.getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }
        Player player = (Player)projectileSource;
        if (this.worldGuardHook.isInRestrictedRegion(player)) {
            return;
        }
        this.worldEditHook.paste(this.cachedSchematic, player.getWorld(), egg.getLocation().getBlockX(), egg.getLocation().getBlockY(), egg.getLocation().getBlockZ());
    }
}

