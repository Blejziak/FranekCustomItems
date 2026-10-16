package dev.franek.customitems.shared.configuration.serdes.transformers;

import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;

public class StringEnchantmentTransformer
extends BidirectionalTransformer<String, Enchantment> {
    @Override
    public GenericsPair<String, Enchantment> getPair() {
        return this.genericsPair(String.class, Enchantment.class);
    }

    @Override
    public Enchantment leftToRight(@NonNull String data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return Enchantment.getByName((String)data);
    }

    @Override
    public String rightToLeft(@NonNull Enchantment data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return data.getName();
    }
}

