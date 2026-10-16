package dev.franek.customitems.shared.bossbar;

import lombok.NonNull;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import dev.franek.customitems.shared.bossbar.BossBarBuilder;

public class BossBarSerializer
implements ObjectSerializer<BossBar> {
    @Override
    public boolean supports(@NonNull Class<? super BossBar> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return BossBar.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull BossBar bossBar, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (bossBar == null) {
            throw new NullPointerException("bossBar is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        data.add("title", bossBar.getTitle(), String.class);
        data.add("progress", bossBar.getProgress(), Double.class);
        data.add("color", bossBar.getColor(), BarColor.class);
        data.add("style", bossBar.getStyle(), BarStyle.class);
    }

    @Override
    public BossBar deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        String bossBarTitle = data.get("title", String.class);
        double bossBarProgress = data.get("progress", Double.class);
        BarColor bossBarColor = data.get("color", BarColor.class);
        BarStyle bossBarStyle = data.get("style", BarStyle.class);
        return new BossBarBuilder(bossBarTitle, bossBarProgress, bossBarColor, bossBarStyle).build();
    }
}

