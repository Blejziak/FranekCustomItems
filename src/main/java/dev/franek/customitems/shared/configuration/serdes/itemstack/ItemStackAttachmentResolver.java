package dev.franek.customitems.shared.configuration.serdes.itemstack;

import java.lang.reflect.Field;
import java.util.Optional;
import lombok.NonNull;
import eu.okaeri.configs.serdes.SerdesAnnotationResolver;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackSpec;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackSpecData;

public class ItemStackAttachmentResolver
implements SerdesAnnotationResolver<ItemStackSpec, ItemStackSpecData> {
    @Override
    public Class<ItemStackSpec> getAnnotationType() {
        return ItemStackSpec.class;
    }

    @Override
    public Optional<ItemStackSpecData> resolveAttachment(@NonNull Field field, @NonNull ItemStackSpec annotation) {
        if (field == null) {
            throw new NullPointerException("field is marked non-null but is null");
        }
        if (annotation == null) {
            throw new NullPointerException("annotation is marked non-null but is null");
        }
        return Optional.of(ItemStackSpecData.of(annotation.format()));
    }
}

