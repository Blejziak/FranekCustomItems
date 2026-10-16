package dev.franek.customitems.domain.impl.purse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import dev.franek.customitems.shared.logger.FastLogger;

public class PurseItemSerializer {
    /*
     * Enabled aggressive exception aggregation
     */
    public static byte[] serialize(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return new byte[0];
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            byte[] byArray;
            try (BukkitObjectOutputStream oos = new BukkitObjectOutputStream((OutputStream)baos);){
                oos.writeObject(items.toArray(new ItemStack[0]));
                oos.flush();
                byArray = baos.toByteArray();
            }
            return byArray;
        }
        catch (IOException exception) {
            FastLogger.error("[PurseItemSerializer] Błąd serializacji: " + exception.getMessage());
            return new byte[0];
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static List<ItemStack> deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return new ArrayList<ItemStack>();
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);){
            List list;
            try (BukkitObjectInputStream ois = new BukkitObjectInputStream((InputStream)bais);){
                ItemStack[] items = (ItemStack[])ois.readObject();
                list = Arrays.stream(items).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
            }
            return list;
        }
        catch (IOException | ClassNotFoundException exception) {
            FastLogger.error("[PurseItemSerializer] Błąd deserializacji: " + exception.getMessage());
            return new ArrayList<ItemStack>();
        }
    }
}

