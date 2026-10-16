package dev.franek.customitems.shared.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public class SkullUtil {
    public static SkullUtil getInstance() {
        return Holder.INSTANCE;
    }

    public void fromB64(SkullMeta meta, String b64) {
        if (b64 == null || b64.isEmpty()) {
            return;
        }
        String urlString = this.getUrlFromB64(b64);
        if (urlString == null) {
            return;
        }
        try {
            UUID id = UUID.nameUUIDFromBytes(b64.getBytes());
            PlayerProfile profile = Bukkit.createPlayerProfile((UUID)id, (String)"customItem");
            PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(new URL(urlString));
            }
            catch (MalformedURLException e) {
                return;
            }
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUrlFromB64(String b64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(b64));
            int start = decoded.indexOf("\"url\":\"") + 7;
            int end = decoded.indexOf("\"", start);
            if (start < 7 || end == -1) {
                return null;
            }
            return decoded.substring(start, end);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static final class Holder {
        private static final SkullUtil INSTANCE = new SkullUtil();

        private Holder() {
        }
    }
}

