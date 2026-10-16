package dev.franek.customitems.shared.configuration.serdes.transformers;

import lombok.NonNull;
import org.bukkit.potion.PotionEffectType;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;

public class StringPotionEffectTypeTransformer
extends BidirectionalTransformer<String, PotionEffectType> {
    @Override
    public GenericsPair<String, PotionEffectType> getPair() {
        return this.genericsPair(String.class, PotionEffectType.class);
    }

    @Override
    public PotionEffectType leftToRight(@NonNull String data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return PotionEffectType.getByName((String)data);
    }

    @Override
    public String rightToLeft(@NonNull PotionEffectType data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return data.getName();
    }
}

