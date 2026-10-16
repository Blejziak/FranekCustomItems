package dev.franek.customitems.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook {
    private final List<String> restrictedRegions;
    private final RegionContainer regionContainer;
    private final boolean hooked;

    public WorldGuardHook(List<String> restrictedRegions) {
        this.restrictedRegions = new ArrayList<String>(restrictedRegions);
        this.regionContainer = Bukkit.getPluginManager().getPlugin("WorldGuard") != null ? WorldGuard.getInstance().getPlatform().getRegionContainer() : null;
        this.hooked = this.regionContainer != null;
    }

    public boolean isInRestrictedRegion(Player player) {
        return this.isInRestrictedRegion(player.getLocation());
    }

    public boolean isInRestrictedRegion(Location location) {
        if (!this.hooked) {
            return false;
        }
        if (location == null || location.getWorld() == null) {
            return false;
        }
        World adaptedWorld = BukkitAdapter.adapt((org.bukkit.World)location.getWorld());
        RegionManager regionManager = this.regionContainer.get(adaptedWorld);
        if (regionManager == null) {
            return false;
        }
        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector((Location)location));
        return applicableRegionSet.getRegions().stream().anyMatch(region -> this.restrictedRegions.contains(region.getId()));
    }
    public List<String> getRestrictedRegions() {
        return this.restrictedRegions;
    }
    public RegionContainer getRegionContainer() {
        return this.regionContainer;
    }
    public boolean isHooked() {
        return this.hooked;
    }
}

