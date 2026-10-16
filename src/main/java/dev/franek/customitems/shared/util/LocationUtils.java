package dev.franek.customitems.shared.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
    public static boolean compareLocations(Location locationOne, Location locationTwo) {
        return locationOne.getWorld() == locationTwo.getWorld() && locationOne.getBlockX() == locationTwo.getBlockX() && locationOne.getBlockY() == locationTwo.getBlockY() && locationOne.getBlockZ() == locationTwo.getBlockZ();
    }

    public static Location adjustLocationToBlock(Location location) {
        int x = location.getBlock().getX();
        int z = location.getBlock().getZ();
        location.setX(x > location.getBlock().getX() ? (double)x - 0.5 : (double)x + 0.5);
        location.setZ(z > location.getBlock().getZ() ? (double)z - 0.5 : (double)z + 0.5);
        return location;
    }

    public static String serialize(Location location) {
        return String.join((CharSequence)";", location.getWorld() == null ? "null" : location.getWorld().getName(), Double.toString(location.getX()), Double.toString(location.getY()), Double.toString(location.getZ()), Float.toString(location.getYaw()), Float.toString(location.getPitch()));
    }

    public static Location deserialize(String stringLocation) {
        String[] split = stringLocation.split(";");
        World world = Bukkit.getWorld((String)split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}

