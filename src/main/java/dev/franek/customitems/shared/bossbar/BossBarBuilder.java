package dev.franek.customitems.shared.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import dev.franek.customitems.shared.util.ColorUtils;

public class BossBarBuilder {
    private final String title;
    private double progress;
    private BarColor color;
    private BarStyle style;

    public static BossBarBuilder of(String name) {
        return new BossBarBuilder(name, 1.0, BarColor.WHITE, BarStyle.SOLID);
    }

    public static BossBarBuilder of(String name, double progress) {
        return new BossBarBuilder(name, progress, BarColor.WHITE, BarStyle.SOLID);
    }

    public static BossBarBuilder of(String name, double progress, BarColor color) {
        return new BossBarBuilder(name, progress, color, BarStyle.SOLID);
    }

    public static BossBarBuilder of(String name, double progress, BarColor color, BarStyle style) {
        return new BossBarBuilder(name, progress, color, style);
    }

    public BossBarBuilder setProgress(double progress) {
        this.progress = progress;
        return this;
    }

    public BossBarBuilder setColor(BarColor color) {
        this.color = color;
        return this;
    }

    public BossBarBuilder setStyle(BarStyle style) {
        this.style = style;
        return this;
    }

    public BossBar build() {
        BossBar bossBar = Bukkit.createBossBar((String)ColorUtils.fixColors(this.title), (BarColor)this.color, (BarStyle)this.style, (BarFlag[])new BarFlag[0]);
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, this.progress)));
        return bossBar;
    }
    public BossBarBuilder(String title, double progress, BarColor color, BarStyle style) {
        this.title = title;
        this.progress = progress;
        this.color = color;
        this.style = style;
    }
}

