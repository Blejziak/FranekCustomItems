package dev.franek.customitems.shared.configuration.serdes.serializers;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackFormat;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackSpecData;
import dev.franek.customitems.shared.configuration.serdes.serializers.ItemMetaSerializer;
import dev.franek.customitems.shared.configuration.serdes.transformers.experimental.StringBase64ItemStackTransformer;

public class ItemStackSerializer
implements ObjectSerializer<ItemStack> {
    private static final ItemMetaSerializer ITEM_META_SERIALIZER = new ItemMetaSerializer();
    private static final StringBase64ItemStackTransformer ITEM_STACK_TRANSFORMER = new StringBase64ItemStackTransformer();
    private boolean failsafe = false;

    @Override
    public boolean supports(@NonNull Class<? super ItemStack> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return ItemStack.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull ItemStack itemStack, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemStack == null) {
            throw new NullPointerException("itemStack is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        data.add("material", itemStack.getType());
        if (itemStack.getAmount() != 1) {
            data.add("amount", itemStack.getAmount());
        }
        if (itemStack.getDurability() != 0) {
            data.add("durability", itemStack.getDurability());
        }
        ItemStackFormat format = data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
        if (!itemStack.hasItemMeta()) {
            return;
        }
        switch (format) {
            case NATURAL: {
                data.add("meta", itemStack.getItemMeta(), ItemMeta.class);
                break;
            }
            case COMPACT: {
                ITEM_META_SERIALIZER.serialize(itemStack.getItemMeta(), data, generics);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown format: " + String.valueOf(format));
            }
        }
        if (!this.failsafe) {
            return;
        }
        DeserializationData deserializationData = new DeserializationData(data.asMap(), data.getConfigurer(), data.getContext());
        ItemStack deserializedStack = this.deserialize(deserializationData, generics);
        if (deserializedStack.equals(itemStack)) {
            return;
        }
        data.clear();
        String base64Stack = ITEM_STACK_TRANSFORMER.leftToRight(itemStack, data.getContext());
        data.add("base64", base64Stack);
    }

    @Override
    public ItemStack deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        if (data.containsKey("base64")) {
            String base64Stack = data.get("base64", String.class);
            return ITEM_STACK_TRANSFORMER.rightToLeft(base64Stack, data.getContext());
        }
        String materialName = data.get("material", String.class);
        Material material = Material.valueOf((String)materialName);
        int amount = data.containsKey("amount") ? data.get("amount", Integer.class) : 1;
        short durability = data.containsKey("durability") ? data.get("durability", Short.class) : (short)0;
        ItemStackFormat format = data.getContext().getAttachment(ItemStackSpecData.class).map(ItemStackSpecData::getFormat).orElse(ItemStackFormat.NATURAL);
        ItemMeta itemMeta = switch (format) {
            case ItemStackFormat.NATURAL -> {
                if (data.containsKey("display") || data.containsKey("display-name")) {
                    yield ITEM_META_SERIALIZER.deserialize(data, generics);
                }
                yield data.containsKey("meta") ? data.get("meta", ItemMeta.class) : data.get("item-meta", ItemMeta.class);
            }
            case ItemStackFormat.COMPACT -> {
                if (data.containsKey("meta")) {
                    yield data.get("meta", ItemMeta.class);
                }
                if (data.containsKey("item-meta")) {
                    yield data.get("item-meta", ItemMeta.class);
                }
                yield ITEM_META_SERIALIZER.deserialize(data, generics);
            }
            default -> throw new IllegalArgumentException("Unknown format: " + String.valueOf(format));
        };
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.setItemMeta(itemMeta);
        if (durability != 0) {
            itemStack.setDurability(durability);
        }
        return itemStack;
    }
    public ItemStackSerializer() {
    }
    public ItemStackSerializer(boolean failsafe) {
        this.failsafe = failsafe;
    }
}

