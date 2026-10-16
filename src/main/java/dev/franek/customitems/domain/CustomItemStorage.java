package dev.franek.customitems.domain;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.franek.customitems.domain.CustomItem;

public class CustomItemStorage {
    private final Map<String, CustomItem> customItemByKey = new HashMap<String, CustomItem>();
    private final Map<String, String> customItemDisplayNameByKey = new HashMap<String, String>();

    public void add(CustomItem customItem) {
        this.customItemByKey.put(customItem.getItemKey(), customItem);
    }

    public boolean contains(String itemKey) {
        return this.customItemByKey.containsKey(itemKey);
    }

    public CustomItem get(String itemKey) {
        return this.customItemByKey.get(itemKey);
    }

    public String getDisplayNameByKey(String itemKey) {
        return this.customItemDisplayNameByKey.getOrDefault(itemKey, itemKey);
    }

    public void prepareMapWithItemDisplayNames() {
        this.customItemDisplayNameByKey.clear();
        for (CustomItem itCustomItem : this.customItemByKey.values()) {
            ItemStack itemStack = itCustomItem.buildItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            this.customItemDisplayNameByKey.put(itCustomItem.getItemKey(), itemMeta != null && itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itCustomItem.getItemKey());
        }
    }

    public Map<String, CustomItem> getCustomItemMap() {
        return this.customItemByKey;
    }

    public Map<String, String> getCustomItemDisplayNameMap() {
        return this.customItemDisplayNameByKey;
    }
}

