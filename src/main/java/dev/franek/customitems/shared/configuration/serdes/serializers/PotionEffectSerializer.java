package dev.franek.customitems.shared.configuration.serdes.serializers;

import lombok.NonNull;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;

public class PotionEffectSerializer
implements ObjectSerializer<PotionEffect> {
    @Override
    public boolean supports(@NonNull Class<? super PotionEffect> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return PotionEffect.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull PotionEffect potionEffect, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (potionEffect == null) {
            throw new NullPointerException("potionEffect is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        data.add("amplifier", potionEffect.getAmplifier());
        data.add("duration", potionEffect.getDuration());
        data.add("type", potionEffect.getType(), PotionEffectType.class);
    }

    @Override
    public PotionEffect deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        int amplifier = data.get("amplifier", Integer.class);
        int duration = data.get("duration", Integer.class);
        PotionEffectType potionEffectType = data.get("type", PotionEffectType.class);
        return new PotionEffect(potionEffectType, duration, amplifier);
    }
}

