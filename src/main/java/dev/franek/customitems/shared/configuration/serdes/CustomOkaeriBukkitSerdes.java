package dev.franek.customitems.shared.configuration.serdes;

import lombok.NonNull;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import dev.franek.customitems.shared.configuration.serdes.serializers.ItemMetaSerializer;
import dev.franek.customitems.shared.configuration.serdes.serializers.ItemStackSerializer;
import dev.franek.customitems.shared.configuration.serdes.serializers.LocationSerializer;
import dev.franek.customitems.shared.configuration.serdes.serializers.PotionEffectSerializer;
import dev.franek.customitems.shared.configuration.serdes.transformers.StringEnchantmentTransformer;
import dev.franek.customitems.shared.configuration.serdes.transformers.StringPotionEffectTypeTransformer;
import dev.franek.customitems.shared.configuration.serdes.transformers.StringTagTransformer;
import dev.franek.customitems.shared.configuration.serdes.transformers.StringWorldTransformer;

public class CustomOkaeriBukkitSerdes
implements OkaeriSerdesPack {
    @Override
    public void register(@NonNull SerdesRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("registry is marked non-null but is null");
        }
        registry.register(new LocationSerializer());
        registry.register(new PotionEffectSerializer());
        registry.register(new ItemStackSerializer());
        registry.register(new ItemMetaSerializer());
        registry.register(new StringEnchantmentTransformer());
        registry.register(new StringPotionEffectTypeTransformer());
        CustomOkaeriBukkitSerdes.whenClass("org.bukkit.Tag", () -> registry.register(new StringTagTransformer()));
        registry.register(new StringWorldTransformer());
    }

    private static void whenClass(@NonNull String name, @NonNull Runnable runnable) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (runnable == null) {
            throw new NullPointerException("runnable is marked non-null but is null");
        }
        try {
            Class.forName(name);
            runnable.run();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }
}

