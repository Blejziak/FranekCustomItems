package dev.franek.customitems.shared.bossbar;

import org.bukkit.boss.BossBar;
import dev.franek.customitems.shared.placeholder.PlaceholderSet;
import dev.franek.customitems.shared.util.ColorUtils;

public class BossBarPreparer {
    public static BossBar prepare(BossBar bossBar, PlaceholderSet placeholderSet) {
        String preparedTitle = placeholderSet.prepareAndGetResult(bossBar.getTitle());
        bossBar.setTitle(ColorUtils.fixColors(preparedTitle));
        return bossBar;
    }
}

