package dev.franek.customitems.shared.util;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtils {
    public static void giveItem(Player player, ItemStack itemStack) {
        PlayerInventory playerInventory = player.getInventory();
        if (playerInventory.firstEmpty() != -1) {
            player.getInventory().addItem(new ItemStack[]{itemStack});
            player.updateInventory();
            return;
        }
        for (ItemStack itemInInventory : playerInventory.getContents()) {
            if (itemInInventory == null || !itemInInventory.isSimilar(itemStack) || itemInInventory.getMaxStackSize() <= itemInInventory.getAmount()) continue;
            player.getInventory().addItem(new ItemStack[]{itemStack});
            player.updateInventory();
            return;
        }
        player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
    }

    public static void giveItems(Player player, List<ItemStack> itemStacks) {
        for (ItemStack itItemStack : itemStacks) {
            PlayerUtils.giveItem(player, itItemStack);
        }
    }

    public static void removeItem(Player player, Material material, int amount, String customDisplayName) {
        if (amount <= 0) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        int inventorySize = inventory.getSize();
        for (int slot = 0; slot < inventorySize; ++slot) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || material != item.getType() || customDisplayName != null && item.hasItemMeta() && !item.getItemMeta().getDisplayName().equalsIgnoreCase(customDisplayName)) continue;
            int newAmount = item.getAmount() - amount;
            if (newAmount > 0) {
                item.setAmount(newAmount);
                break;
            }
            inventory.clear(slot);
            amount = -newAmount;
            if (amount == 0) break;
        }
        player.updateInventory();
    }
}

