package dev.franek.customitems.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.domain.CustomItemStorage;
import dev.franek.customitems.domain.impl.ExcaliburItem;
import dev.franek.customitems.shared.itemstack.ItemStackBuilder;
import dev.franek.customitems.shared.notification.NotificationDispatcher;
import dev.franek.customitems.shared.notification.NotificationType;
import dev.franek.customitems.shared.util.ColorUtils;
import dev.franek.customitems.shared.util.ValidationsUtils;

public class CustomItemsCommand
implements CommandExecutor,
TabCompleter {
    private final CustomItemsConfiguration configuration;
    private final CustomItemStorage customItemStorage;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("franek.customitems.command")) {
            NotificationDispatcher.sendNotification(sender, NotificationType.CHAT, this.configuration.getMessagesSection().getNoPermission());
            return true;
        }
        if (!(sender instanceof Player)) {
            NotificationDispatcher.sendNotification(sender, NotificationType.CHAT, this.configuration.getMessagesSection().getConsoleIsBlocked());
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.openGUI(player, 1);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                this.configuration.load(true);
                this.customItemStorage.prepareMapWithItemDisplayNames();
                NotificationDispatcher.sendNotification(sender, NotificationType.CHAT, this.configuration.getMessagesSection().getConfigurationsReloaded());
                break;
            }
            case "kills": {
                if (args.length < 2 || !ValidationsUtils.isInteger(args[1])) {
                    NotificationDispatcher.sendNotification(sender, NotificationType.CHAT, this.configuration.getMessagesSection().getUsage().replace("{USAGE}", "/customitems kills <ilosc>"));
                    return true;
                }
                int amount = Integer.parseInt(args[1]);
                ItemStack item = player.getInventory().getItemInMainHand();
                ExcaliburItem excalibur = (ExcaliburItem)this.customItemStorage.getCustomItemMap().get("excalibur_item");
                if (excalibur == null || !excalibur.isThisItem(item)) {
                    player.sendMessage(ColorUtils.fixColors("&8[&4✖&8] &fMusisz trzymać &eExcalibur &fw ręce!"));
                    return true;
                }
                excalibur.setKills(item, amount);
                player.sendMessage(ColorUtils.fixColors("&8[&a✔&8] &fUstawiono liczbę zabójstw Excalibura na: &a" + amount));
                break;
            }
            default: {
                if (!ValidationsUtils.isInteger(args[0])) {
                    NotificationDispatcher.sendNotification(sender, NotificationType.CHAT, this.configuration.getMessagesSection().getUsage().replace("{USAGE}", "/customitems <strona>"));
                    return true;
                }
                this.openGUI(player, Integer.parseInt(args[0]));
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("franek.customitems.command")) {
            return List.of();
        }
        if (args.length == 1) {
            return List.of("reload", "kills");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("kills")) {
            return List.of("1", "2", "3", "4", "5");
        }
        return List.of();
    }

    public void openGUI(Player player, int page) {
        if (page < 1) {
            page = 1;
        }
        ArrayList<CustomItem> allItems = new ArrayList<CustomItem>(this.customItemStorage.getCustomItemMap().values());
        int itemsPerPage = 45;
        int maxPages = (int)Math.ceil((double)allItems.size() / 45.0);
        if (page > maxPages && maxPages > 0) {
            page = maxPages;
        }
        Inventory inventory = Bukkit.createInventory(null, (int)54, (String)ColorUtils.fixColors("&8CustomItems &7(Strona " + page + ") &8[" + dev.franek.customitems.CustomItemsPlugin.AUTHOR + "]"));
        int start = (page - 1) * 45;
        int end = Math.min(start + 45, allItems.size());
        for (int i = start; i < end; ++i) {
            CustomItem customItem = (CustomItem)allItems.get(i);
            int slot = i - start;
            inventory.setItem(slot, customItem.buildItem());
        }
        if (page > 1) {
            inventory.setItem(45, ItemStackBuilder.of(Material.ARROW).setDisplayName("&c« Poprzednia strona").setLore(List.of("&7Kliknij, aby przejść do strony &f" + (page - 1))).build());
        }
        inventory.setItem(49, ItemStackBuilder.of(Material.PAPER).setDisplayName("&7Strona: &6" + page + " / " + maxPages).setLore(List.of("&7Przeglądasz listę przedmiotów")).build());
        if (page < maxPages) {
            inventory.setItem(53, ItemStackBuilder.of(Material.ARROW).setDisplayName("&aNastępna strona »").setLore(List.of("&7Kliknij, aby przejść do strony &f" + (page + 1))).build());
        }
        player.openInventory(inventory);
    }
    public CustomItemsCommand(CustomItemsConfiguration configuration, CustomItemStorage customItemStorage) {
        this.configuration = configuration;
        this.customItemStorage = customItemStorage;
    }
}

