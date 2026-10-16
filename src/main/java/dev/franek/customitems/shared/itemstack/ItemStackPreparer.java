package dev.franek.customitems.shared.itemstack;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.ColorUtils;

public class ItemStackPreparer {
    public static ItemStack prepare(ItemStack itemToPrepare, PlaceholderSet placeholderSet) {
        if (itemToPrepare == null || !itemToPrepare.hasItemMeta()) {
            return itemToPrepare;
        }
        ItemMeta itemMeta = itemToPrepare.getItemMeta();
        if (itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(ItemStackPreparer.getPreparedDisplayName(itemToPrepare, placeholderSet));
        }
        if (itemMeta.hasLore()) {
            itemMeta.setLore(ItemStackPreparer.getPreparedLore(itemToPrepare, placeholderSet));
        }
        itemToPrepare.setItemMeta(itemMeta);
        return itemToPrepare;
    }

    private static String getPreparedDisplayName(ItemStack itemToPrepare, PlaceholderSet placeholderSet) {
        ItemMeta itemMeta = itemToPrepare.getItemMeta();
        String originalDisplayName = itemMeta.getDisplayName();
        return ColorUtils.fixColors(placeholderSet.prepareAndGetResult(originalDisplayName));
    }

    private static List<String> getPreparedLore(ItemStack itemToPrepare, PlaceholderSet placeholderSet) {
        ItemMeta itemMeta = itemToPrepare.getItemMeta();
        ArrayList<String> output = new ArrayList<String>();
        for (String loreLine : itemMeta.getLore()) {
            output.add(ColorUtils.fixColors(placeholderSet.prepareAndGetResult(loreLine)));
        }
        return output;
    }
}

