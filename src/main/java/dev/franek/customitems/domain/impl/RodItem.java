package dev.franek.customitems.domain.impl;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.shared.util.PDCUtils;

public class RodItem
extends CustomItem {
    private final CustomItemsPlugin plugin;
    private final CustomItemsConfiguration configuration;

    public RodItem(CustomItemsPlugin plugin, CustomItemsConfiguration configuration) {
        super(plugin);
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public String getItemKey() {
        return "rod";
    }

    @Override
    public ItemStack buildItem() {
        ItemStack item = this.configuration.getRodItem().getItem().clone();
        ItemMeta itemMeta = item.getItemMeta();
        PDCUtils.setString(itemMeta, this.plugin.getCustomItemKey(), this.getItemKey());
        item.setItemMeta(itemMeta);
        return item;
    }
}

