package dev.franek.customitems.domain;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.domain.CustomItem;
import dev.franek.customitems.domain.CustomItemStorage;

public class CustomItemService {
    private final CustomItemStorage customItemStorage;

    public void registerItems(CustomItemsPlugin plugin, CustomItem ... items) {
        Arrays.asList(items).forEach(item -> this.registerItem(plugin, (CustomItem)item));
    }

    private void registerItem(CustomItemsPlugin plugin, CustomItem item) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents((Listener)item, (Plugin)plugin);
        this.customItemStorage.add(item);
    }
    public CustomItemService(CustomItemStorage customItemStorage) {
        this.customItemStorage = customItemStorage;
    }
}

