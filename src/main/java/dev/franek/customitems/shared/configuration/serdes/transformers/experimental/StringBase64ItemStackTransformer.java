package dev.franek.customitems.shared.configuration.serdes.transformers.experimental;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class StringBase64ItemStackTransformer extends BidirectionalTransformer<ItemStack, String> {

    @Override
    public GenericsPair<ItemStack, String> getPair() {
        return this.genericsPair(ItemStack.class, String.class);
    }

    @Override
    public String leftToRight(@NonNull ItemStack data, @NonNull SerdesContext serdesContext) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(data);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray()).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemStack rightToLeft(@NonNull String data, @NonNull SerdesContext serdesContext) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
