package dev.franek.customitems.shared.configuration.serdes.itemstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import dev.franek.customitems.shared.configuration.serdes.itemstack.ItemStackFormat;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ItemStackSpec {
    public ItemStackFormat format() default ItemStackFormat.NATURAL;
}

