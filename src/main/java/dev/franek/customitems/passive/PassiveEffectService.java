package dev.franek.customitems.passive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.franek.customitems.CustomItemsPlugin;
import dev.franek.customitems.passive.PassiveEffectItem;
import dev.franek.customitems.shared.task.TaskDispatcher;

public class PassiveEffectService {
    private final List<PassiveEffectItem> items = new ArrayList<PassiveEffectItem>();

    public PassiveEffectService(CustomItemsPlugin plugin) {
        TaskDispatcher.scheduleTaskTimer(plugin, false, 0L, 2L, TimeUnit.SECONDS, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (PassiveEffectItem itItem : this.items) {
                    itItem.updateEffects(player);
                }
            }
        });
    }

    public void register(PassiveEffectItem ... items) {
        Arrays.stream(items).forEach(this::register);
    }

    public void register(PassiveEffectItem item) {
        this.items.add(item);
    }
}

