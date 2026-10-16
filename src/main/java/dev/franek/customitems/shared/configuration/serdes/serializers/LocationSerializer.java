package dev.franek.customitems.shared.configuration.serdes.serializers;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;

public class LocationSerializer
implements ObjectSerializer<Location> {
    @Override
    public boolean supports(@NonNull Class<? super Location> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return Location.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Location location, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (location == null) {
            throw new NullPointerException("location is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        data.add("world", location.getWorld(), World.class);
        data.add("x", location.getX());
        data.add("y", location.getY());
        data.add("z", location.getZ());
        data.add("yaw", Float.valueOf(location.getYaw()));
        data.add("pitch", Float.valueOf(location.getPitch()));
    }

    @Override
    public Location deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        World world = data.get("world", World.class);
        double x = data.get("x", Double.class);
        double y = data.get("y", Double.class);
        double z = data.get("z", Double.class);
        float yaw = data.get("yaw", Float.class).floatValue();
        float pitch = data.get("pitch", Float.class).floatValue();
        return new Location(world, x, y, z, yaw, pitch);
    }
}

