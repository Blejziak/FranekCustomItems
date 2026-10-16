package dev.franek.customitems.shared.configuration.serdes.transformers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Tag;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;

public class StringTagTransformer
extends BidirectionalTransformer<String, Tag> {
    private final Map<String, Tag> tagMap = new HashMap<String, Tag>();

    public StringTagTransformer() {
        try {
            for (Field field : Tag.class.getFields()) {
                Tag tag;
                if (!Tag.class.isAssignableFrom(field.getType()) || !Modifier.isStatic(field.getModifiers()) || (tag = (Tag) field.get(null)) == null) continue;
                this.tagMap.put(tag.getKey().toString(), tag);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GenericsPair<String, Tag> getPair() {
        return this.genericsPair(String.class, Tag.class);
    }

    @Override
    public Tag leftToRight(@NonNull String data, @NonNull SerdesContext context) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (context == null) {
            throw new NullPointerException("context is marked non-null but is null");
        }
        Tag tag = this.tagMap.get(data);
        if (tag == null) {
            throw new IllegalArgumentException("Unknown tag: " + data);
        }
        return tag;
    }

    @Override
    public String rightToLeft(Tag data, @NonNull SerdesContext context) {
        if (context == null) {
            throw new NullPointerException("context is marked non-null but is null");
        }
        return data.getKey().toString();
    }
}

