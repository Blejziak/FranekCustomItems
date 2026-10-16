package dev.franek.customitems.hook;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import dev.franek.customitems.shared.logger.FastLogger;

public class WorldEditHook {
    private final boolean hooked = Bukkit.getPluginManager().getPlugin("WorldEdit") != null || Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;

    public void paste(Clipboard clipboard, World world, int x, int y, int z) {
        if (!this.hooked) {
            FastLogger.error("[WorldEditHook] Nie można wkleić schematu, ponieważ WorldEdit nie jest zainstalowany.");
            return;
        }
        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt((World)world)).build();){
            Operation operation = new ClipboardHolder(clipboard).createPaste((Extent)editSession).to(BlockVector3.at((int)x, (int)y, (int)z)).ignoreAirBlocks(true).build();
            Operations.complete((Operation)operation);
        }
        catch (WorldEditException exception) {
            FastLogger.error("[WorldEditHook] Błąd wklejania schematu: " + exception.getMessage());
        }
    }
    public boolean isHooked() {
        return this.hooked;
    }
}

