package dev.franek.customitems.shared.itemstack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import dev.franek.customitems.shared.util.ColorUtils;
import dev.franek.customitems.shared.util.SkullUtil;

public class ItemStackBuilder {
    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON};
    private final ItemStack itemStack;

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material));
    }

    public static ItemStackBuilder of(Material material, int amount) {
        return new ItemStackBuilder(new ItemStack(material, amount));
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack.clone());
    }

    public static ItemStackBuilder of(ItemStack itemStack, boolean clone) {
        return new ItemStackBuilder(clone ? itemStack.clone() : itemStack);
    }

    public ItemStackBuilder transform(Consumer<ItemStack> consumer) {
        consumer.accept(this.itemStack);
        return this;
    }

    public ItemStackBuilder transformMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        consumer.accept(itemMeta);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        return this.transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        return this.transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder clearEnchantments() {
        return this.transform(itemStack -> itemStack.getEnchantments().keySet().forEach(arg_0 -> ((ItemStack)itemStack).removeEnchantment(arg_0)));
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        return this.transformMeta(itemMeta -> itemMeta.setDisplayName(ColorUtils.fixColors(displayName)));
    }

    public ItemStackBuilder setLore(List<String> lore) {
        return this.transformMeta(itemMeta -> itemMeta.setLore(ColorUtils.fixColors(lore)));
    }

    public ItemStackBuilder setSkullTexture(String base64) {
        return this.transformMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta)itemMeta;
                SkullUtil.getInstance().fromB64(skullMeta, base64);
            }
        });
    }

    public ItemStackBuilder setOwnerProfile(Player player) {
        return this.transformMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta)itemMeta;
                skullMeta.setOwnerProfile(player.getPlayerProfile());
            }
        });
    }

    public ItemStackBuilder appendLore(List<String> lore) {
        return this.transformMeta(itemMeta -> {
            ArrayList<String> loreFromItem = itemMeta.getLore() == null ? new ArrayList<String>() : new ArrayList(itemMeta.getLore());
            for (String line : lore) {
                loreFromItem.add(ColorUtils.fixColors(line));
            }
            itemMeta.setLore(loreFromItem);
        });
    }

    public ItemStackBuilder clearLore() {
        return this.transformMeta(itemMeta -> itemMeta.setLore(new ArrayList()));
    }

    public ItemStackBuilder addItemFlags(ItemFlag ... flags) {
        return this.transformMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemStackBuilder removeItemFlags(ItemFlag ... flags) {
        return this.transformMeta(itemMeta -> itemMeta.removeItemFlags(flags));
    }

    public ItemStackBuilder hideAttributes() {
        this.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemStackBuilder showAttributes() {
        return this.removeItemFlags(ItemFlag.values());
    }

    public ItemStackBuilder setLeatherColor(Color color) {
        return this.transform(itemStack -> {
            Material itemStackType = this.itemStack.getType();
            switch (itemStackType) {
                case LEATHER_BOOTS: 
                case LEATHER_CHESTPLATE: 
                case LEATHER_HELMET: 
                case LEATHER_LEGGINGS: {
                    LeatherArmorMeta armorMeta = (LeatherArmorMeta)itemStack.getItemMeta();
                    armorMeta.setColor(color);
                    itemStack.setItemMeta((ItemMeta)armorMeta);
                }
            }
        });
    }

    public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        return this.transformMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public ItemStackBuilder setCustomModelData(int customModelData) {
        return this.transformMeta(itemMeta -> itemMeta.setCustomModelData(Integer.valueOf(customModelData)));
    }

    public ItemStack build() {
        return this.itemStack;
    }
    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}

