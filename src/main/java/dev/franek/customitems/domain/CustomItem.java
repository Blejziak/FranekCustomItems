package dev.franek.customitems.domain;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.shared.util.PDCUtils;

public abstract class CustomItem
implements Listener {
    private final CustomItemsPlugin plugin;

    public abstract String getItemKey();

    public abstract ItemStack buildItem();

    public boolean isThisItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = item.getItemMeta();
        String itemKey = PDCUtils.getString(itemMeta, this.plugin.getCustomItemKey());
        if (itemKey == null || itemKey.isEmpty()) {
            return false;
        }
        return this.getItemKey().equalsIgnoreCase(itemKey);
    }
    public CustomItem(CustomItemsPlugin plugin) {
        this.plugin = plugin;
    }
}

