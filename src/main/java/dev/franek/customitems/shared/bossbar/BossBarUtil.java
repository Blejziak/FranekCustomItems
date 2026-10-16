package dev.franek.customitems.shared.bossbar;

import org.bukkit.boss.BossBar;
import dev.franek.customitems.shared.bossbar.BossBarBuilder;

public class BossBarUtil {
    public static BossBar clone(BossBar input) {
        return BossBarBuilder.of(input.getTitle(), input.getProgress(), input.getColor(), input.getStyle()).build();
    }
}

