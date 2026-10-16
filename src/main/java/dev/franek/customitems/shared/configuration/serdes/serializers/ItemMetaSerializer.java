package dev.franek.customitems.shared.configuration.serdes.serializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import dev.franek.customitems.shared.util.ColorUtils;

public class ItemMetaSerializer
implements ObjectSerializer<ItemMeta> {
    private static final char COLOR_CHAR = '§';
    private static final char ALT_COLOR_CHAR = '&';

    @Override
    public boolean supports(@NonNull Class<? super ItemMeta> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull ItemMeta itemMeta, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemMeta == null) {
            throw new NullPointerException("itemMeta is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        if (itemMeta.hasDisplayName()) {
            data.add("display", itemMeta.getDisplayName(), String.class);
        }
        if (itemMeta.hasLore()) {
            data.addCollection("lore", itemMeta.getLore(), String.class);
        }
        if (!itemMeta.getEnchants().isEmpty()) {
            data.addAsMap("enchantments", itemMeta.getEnchants(), Enchantment.class, Integer.class);
        }
        if (!itemMeta.getItemFlags().isEmpty()) {
            data.addCollection("flags", itemMeta.getItemFlags(), ItemFlag.class);
        }
        if (itemMeta.hasCustomModelData()) {
            data.add("customModelData", itemMeta.getCustomModelData(), Integer.class);
        }
    }

    @Override
    public ItemMeta deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        Integer customModelData;
        ItemMeta itemMeta;
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        String displayName = data.get("display", String.class);
        if (displayName == null) {
            displayName = data.get("display-name", String.class);
        }
        List<String> lore = data.containsKey("lore") ? data.getAsList("lore", String.class) : Collections.emptyList();
        Map<Enchantment, Integer> enchantments = data.containsKey("enchantments") ? data.getAsMap("enchantments", Enchantment.class, Integer.class) : Collections.emptyMap();
        ArrayList<ItemFlag> itemFlags = new ArrayList<ItemFlag>(data.containsKey("flags") ? data.getAsList("flags", ItemFlag.class) : Collections.emptyList());
        if (data.containsKey("item-flags")) {
            itemFlags.addAll(data.getAsList("item-flags", ItemFlag.class));
        }
        if ((itemMeta = new ItemStack(Material.COBBLESTONE).getItemMeta()) == null) {
            throw new IllegalStateException("Cannot extract empty ItemMeta from COBBLESTONE");
        }
        if (displayName != null) {
            itemMeta.setDisplayName(ColorUtils.fixColors(displayName));
        }
        if ((customModelData = data.get("customModelData", Integer.class)) != null) {
            itemMeta.setCustomModelData(customModelData);
        }
        itemMeta.setLore(ColorUtils.fixColors(lore));
        enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level.intValue(), true));
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return itemMeta;
    }

    private List<String> color(List<String> text) {
        return text.stream().map(this::color).collect(Collectors.toList());
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)text);
    }

    private List<String> decolor(List<String> text) {
        return text.stream().map(this::decolor).collect(Collectors.toList());
    }

    private String decolor(String text) {
        return text.replace("§", "&");
    }
}

