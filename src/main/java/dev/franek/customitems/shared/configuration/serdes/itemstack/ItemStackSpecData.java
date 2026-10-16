package dev.franek.customitems.shared.configuration.serdes.itemstack;

import eu.okaeri.configs.serdes.SerdesContextAttachment;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackFormat;

public final class ItemStackSpecData
implements SerdesContextAttachment {
    private final ItemStackFormat format;
    private ItemStackSpecData(ItemStackFormat format) {
        this.format = format;
    }
    public static ItemStackSpecData of(ItemStackFormat format) {
        return new ItemStackSpecData(format);
    }
    public ItemStackFormat getFormat() {
        return this.format;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemStackSpecData)) {
            return false;
        }
        ItemStackSpecData other = (ItemStackSpecData)o;
        ItemStackFormat this$format = this.getFormat();
        ItemStackFormat other$format = other.getFormat();
        return !(this$format == null ? other$format != null : !((this$format)).equals(other$format));
    }
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ItemStackFormat $format = this.getFormat();
        result = result * 59 + ($format == null ? 43 : (($format)).hashCode());
        return result;
    }
    public String toString() {
        return "ItemStackSpecData(format=" + String.valueOf(this.getFormat()) + ")";
    }
}

