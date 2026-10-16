package dev.franek.customitems.shared.configuration.serdes.transformers;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;

public class StringWorldTransformer
extends BidirectionalTransformer<String, World> {
    @Override
    public GenericsPair<String, World> getPair() {
        return this.genericsPair(String.class, World.class);
    }

    @Override
    public World leftToRight(@NonNull String data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return Bukkit.getWorld((String)data);
    }

    @Override
    public String rightToLeft(@NonNull World data, @NonNull SerdesContext serdesContext) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (serdesContext == null) {
            throw new NullPointerException("serdesContext is marked non-null but is null");
        }
        return data.getName();
    }
}

