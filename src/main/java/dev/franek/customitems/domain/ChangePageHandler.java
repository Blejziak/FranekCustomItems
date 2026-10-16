package dev.franek.customitems.domain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import dev.franek.customitems.command.CustomItemsCommand;
import dev.franek.customitems.shared.util.ColorUtils;

public class ChangePageHandler
implements Listener {
    private final CustomItemsCommand customItemsCommand;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.contains(ColorUtils.fixColors("&8CustomItems &7(Strona "))) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0) {
            return;
        }
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        if (rawSlot == 45 || rawSlot == 49 || rawSlot == 53) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }
            Player player = (Player)event.getWhoClicked();
            int currentPage = this.extractPageNumber(ChatColor.stripColor((String)title));
            String cleanDisplayName = ChatColor.stripColor((String)clickedItem.getItemMeta().getDisplayName());
            if (cleanDisplayName.contains("Następna strona")) {
                this.customItemsCommand.openGUI(player, currentPage + 1);
            } else if (cleanDisplayName.contains("Poprzednia strona")) {
                this.customItemsCommand.openGUI(player, currentPage - 1);
            }
        }
    }

    private int extractPageNumber(String title) {
        try {
            String clean = title.replaceAll("[^0-9]", "");
            return Integer.parseInt(clean);
        }
        catch (NumberFormatException e) {
            return 1;
        }
    }
    public ChangePageHandler(CustomItemsCommand customItemsCommand) {
        this.customItemsCommand = customItemsCommand;
    }
}

