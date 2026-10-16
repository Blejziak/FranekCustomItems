package dev.franek.customitems.shared.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import dev.franek.customitems.shared.logger.FastLogger;

public class PDCUtils {
    public static boolean getBoolean(Object object, NamespacedKey key) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return false;
        }
        Integer data = (Integer)container.get(key, PersistentDataType.INTEGER);
        if (data == null) {
            return false;
        }
        return data == 1;
    }

    public static void setBoolean(Object object, NamespacedKey key, boolean value) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return;
        }
        container.set(key, PersistentDataType.INTEGER, (value ? 1 : 0));
    }

    public static int getInteger(Object object, NamespacedKey key) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return -1;
        }
        Integer data = (Integer)container.get(key, PersistentDataType.INTEGER);
        if (data == null) {
            return -1;
        }
        return data;
    }

    public static void setInteger(Object object, NamespacedKey key, int value) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return;
        }
        container.set(key, PersistentDataType.INTEGER, value);
    }

    public static String getString(Object object, NamespacedKey key) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return null;
        }
        return (String)container.get(key, PersistentDataType.STRING);
    }

    public static void setString(Object object, NamespacedKey key, String value) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return;
        }
        container.set(key, PersistentDataType.STRING, value);
    }

    public static long getLong(Object object, NamespacedKey key) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return 0L;
        }
        return container.get(key, PersistentDataType.LONG) == null ? 0L : (Long)container.get(key, PersistentDataType.LONG);
    }

    public static void setLong(Object object, NamespacedKey key, Long value) {
        PersistentDataContainer container = PDCUtils.getContainer(object);
        if (container == null) {
            return;
        }
        container.set(key, PersistentDataType.LONG, value);
    }

    private static PersistentDataContainer getContainer(Object object) {
        PersistentDataContainer container = null;
        if (object instanceof Entity) {
            container = ((Entity)object).getPersistentDataContainer();
        }
        if (object instanceof ItemMeta) {
            container = ((ItemMeta)object).getPersistentDataContainer();
        }
        if (container == null) {
            FastLogger.warning("Could not get PersistentDataContainer for object of type " + object.getClass().getName() + ".");
        }
        return container;
    }
}

