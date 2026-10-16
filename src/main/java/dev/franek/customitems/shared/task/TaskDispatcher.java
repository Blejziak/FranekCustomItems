package dev.franek.customitems.shared.task;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TaskDispatcher {
    public static void scheduleTask(JavaPlugin plugin, boolean async, Runnable runnable) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        if (async) {
            bukkitScheduler.runTaskAsynchronously((Plugin)plugin, runnable);
        } else {
            bukkitScheduler.runTask((Plugin)plugin, runnable);
        }
    }

    public static void scheduleTaskLater(JavaPlugin plugin, boolean async, long delay, TimeUnit unit, Runnable runnable) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        long delayTicks = TaskDispatcher.toTicks(delay, unit);
        if (async) {
            bukkitScheduler.runTaskLaterAsynchronously((Plugin)plugin, runnable, delayTicks);
        } else {
            bukkitScheduler.runTaskLater((Plugin)plugin, runnable, delayTicks);
        }
    }

    public static void scheduleTaskLater(JavaPlugin plugin, boolean async, long delaInTicks, Runnable runnable) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        if (async) {
            bukkitScheduler.runTaskLaterAsynchronously((Plugin)plugin, runnable, delaInTicks);
        } else {
            bukkitScheduler.runTaskLater((Plugin)plugin, runnable, delaInTicks);
        }
    }

    public static void scheduleTaskTimer(JavaPlugin plugin, boolean async, long delay, long period, TimeUnit unit, Runnable runnable) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        long delayTicks = TaskDispatcher.toTicks(delay, unit);
        long periodTicks = TaskDispatcher.toTicks(period, unit);
        if (async) {
            bukkitScheduler.runTaskTimerAsynchronously((Plugin)plugin, runnable, delayTicks, periodTicks);
        } else {
            bukkitScheduler.runTaskTimer((Plugin)plugin, runnable, delayTicks, periodTicks);
        }
    }

    public static void scheduleTaskTimer(JavaPlugin plugin, boolean async, long delayInTicks, long periodInTicks, Runnable runnable) {
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        if (async) {
            bukkitScheduler.runTaskTimerAsynchronously((Plugin)plugin, runnable, delayInTicks, periodInTicks);
        } else {
            bukkitScheduler.runTaskTimer((Plugin)plugin, runnable, delayInTicks, periodInTicks);
        }
    }

    private static long toTicks(long time, TimeUnit unit) {
        return switch (unit) {
            case TimeUnit.SECONDS -> time * 20L;
            case TimeUnit.MINUTES -> time * 1200L;
            case TimeUnit.HOURS -> time * 72000L;
            case TimeUnit.DAYS -> time * 1728000L;
            default -> throw new IllegalArgumentException("Unsupported TimeUnit: " + String.valueOf(unit));
        };
    }
}

