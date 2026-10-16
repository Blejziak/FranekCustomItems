package dev.franek.customitems.shared.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import dev.franek.customitems.shared.logger.FastLogger;

public class ItemUtils {
    private static final Gson GSON = new Gson();

    public static ItemStack getCustomSkull(String value, String signature) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        if (value == null || value.isEmpty()) {
            return skull;
        }
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        UUID profileId = UUID.nameUUIDFromBytes(value.getBytes());
        PlayerProfile profile = Bukkit.createPlayerProfile((UUID)profileId, (String)"CustomSkull");
        PlayerTextures textures = profile.getTextures();
        try {
            String json = new String(Base64.getDecoder().decode(value));
            JsonObject obj = (JsonObject)GSON.fromJson(json, JsonObject.class);
            String url = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            textures.setSkin(new URL(url));
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        }
        catch (Exception exception) {
            FastLogger.error("[ItemUtils] Błąd ustawiania tekstury: " + exception.getMessage());
        }
        skull.setItemMeta((ItemMeta)meta);
        return skull;
    }
}

