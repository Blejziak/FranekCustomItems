package dev.franek.customitems;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import dev.franek.customitems.CustomItemsConfiguration;
import dev.franek.customitems.command.CustomItemsCommand;
import dev.franek.customitems.cooldown.CooldownActionBarTask;
import dev.franek.customitems.cooldown.CooldownController;
import dev.franek.customitems.database.Database;
import dev.franek.customitems.database.DatabaseQueue;
import dev.franek.customitems.database.DatabaseType;
import dev.franek.customitems.domain.ChangePageHandler;
import dev.franek.customitems.domain.CustomItemService;
import dev.franek.customitems.domain.CustomItemStorage;
import dev.franek.customitems.domain.impl.AnarchyCrownItem;
import dev.franek.customitems.domain.impl.AppeasementTotemItem;
import dev.franek.customitems.domain.impl.ArcusMagnusItem;
import dev.franek.customitems.domain.impl.BalloonItem;
import dev.franek.customitems.domain.impl.BrokenHeartItem;
import dev.franek.customitems.domain.impl.BunnySwordItem;
import dev.franek.customitems.domain.impl.CarrotCrossbowItem;
import dev.franek.customitems.domain.impl.CottonCandyItem;
import dev.franek.customitems.domain.impl.CupidBowItem;
import dev.franek.customitems.domain.impl.CupidsRoseItem;
import dev.franek.customitems.domain.impl.DivineAxeItem;
import dev.franek.customitems.domain.impl.DragonSwordItem;
import dev.franek.customitems.domain.impl.DynamiteItem;
import dev.franek.customitems.domain.impl.ExcaliburItem;
import dev.franek.customitems.domain.impl.ExpiredPotionItem;
import dev.franek.customitems.domain.impl.GhostBlockItem;
import dev.franek.customitems.domain.impl.GingerbreadItem;
import dev.franek.customitems.domain.impl.GrinchAxeItem;
import dev.franek.customitems.domain.impl.GrinchShovelItem;
import dev.franek.customitems.domain.impl.HydroCageItem;
import dev.franek.customitems.domain.impl.HydroTridentItem;
import dev.franek.customitems.domain.impl.IllusionistWandItem;
import dev.franek.customitems.domain.impl.InfernalShieldItem;
import dev.franek.customitems.domain.impl.InfernalSwordItem;
import dev.franek.customitems.domain.impl.KosaItem;
import dev.franek.customitems.domain.impl.KupaAnarchiItem;
import dev.franek.customitems.domain.impl.LeftEggItem;
import dev.franek.customitems.domain.impl.LollipopItem;
import dev.franek.customitems.domain.impl.MaximBombItem;
import dev.franek.customitems.domain.impl.MiracleLanternItem;
import dev.franek.customitems.domain.impl.MoldySandwichItem;
import dev.franek.customitems.domain.impl.MutatedCreeperItem;
import dev.franek.customitems.domain.impl.NoFlyRodItem;
import dev.franek.customitems.domain.impl.OlafItem;
import dev.franek.customitems.domain.impl.OvercookedCornItem;
import dev.franek.customitems.domain.impl.PoisonedPencilItem;
import dev.franek.customitems.domain.impl.ReinforcedElytraItem;
import dev.franek.customitems.domain.impl.RodItem;
import dev.franek.customitems.domain.impl.RubikCubeItem;
import dev.franek.customitems.domain.impl.ScreenItem;
import dev.franek.customitems.domain.impl.SnowballSwapItem;
import dev.franek.customitems.domain.impl.SnowflakeItem;
import dev.franek.customitems.domain.impl.SuperCarrotItem;
import dev.franek.customitems.domain.impl.SurferRodItem;
import dev.franek.customitems.domain.impl.TurboDomekItem;
import dev.franek.customitems.domain.impl.TurboTrapItem;
import dev.franek.customitems.domain.impl.UnicornHornItem;
import dev.franek.customitems.domain.impl.VampireAppleItem;
import dev.franek.customitems.domain.impl.VampireBloodItem;
import dev.franek.customitems.domain.impl.WarmMilkItem;
import dev.franek.customitems.domain.impl.backpack.BackpackItem;
import dev.franek.customitems.domain.impl.backpack.data.BackpackData;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataRepository;
import dev.franek.customitems.domain.impl.backpack.data.BackpackDataStorage;
import dev.franek.customitems.domain.impl.backpack.view.BackpackView;
import dev.franek.customitems.domain.impl.backpack.view.BackpackViewListener;
import dev.franek.customitems.domain.impl.purse.PurseItem;
import dev.franek.customitems.domain.impl.purse.data.PurseData;
import dev.franek.customitems.domain.impl.purse.data.PurseDataRepository;
import dev.franek.customitems.domain.impl.purse.data.PurseDataStorage;
import dev.franek.customitems.domain.impl.purse.view.PurseView;
import dev.franek.customitems.domain.impl.purse.view.PurseViewListener;
import dev.franek.customitems.hook.WorldEditHook;
import dev.franek.customitems.hook.WorldGuardHook;
import dev.franek.customitems.passive.PassiveEffectService;
import dev.franek.customitems.player.PlayerData;
import dev.franek.customitems.player.PlayerDataListener;
import dev.franek.customitems.player.PlayerDataRepository;
import dev.franek.customitems.player.PlayerDataService;
import dev.franek.customitems.player.PlayerDataStorage;
import dev.franek.customitems.shared.bossbar.BossBarListener;
import dev.franek.customitems.shared.bossbar.BossBarService;
import dev.franek.customitems.shared.configuration.ConfigurationHelper;
import dev.franek.customitems.shared.logger.FastLogger;
import dev.franek.customitems.shared.task.TaskDispatcher;

public class CustomItemsPlugin
extends JavaPlugin {
    private final NamespacedKey customItemKey = new NamespacedKey((Plugin)this, "custom_item_key");
    private CustomItemsConfiguration configuration;
    private Database database;
    private DatabaseQueue databaseQueue;
    private WorldGuardHook worldGuardHook;
    private WorldEditHook worldEditHook;
    private CooldownController cooldownController;
    private BossBarService bossBarService;
    private PlayerDataStorage playerDataStorage;
    private PlayerDataRepository playerDataRepository;
    private PlayerDataService playerDataService;
    private PassiveEffectService passiveEffectService;
    private CustomItemStorage customItemStorage;
    private CustomItemService customItemService;
    private PurseDataStorage purseDataStorage;
    private PurseDataRepository purseDataRepository;
    private PurseView purseView;
    private BackpackDataStorage backpackDataStorage;
    private BackpackDataRepository backpackDataRepository;
    private BackpackView backpackView;

    public void onEnable() {
        FastLogger.assignLogger(this.getLogger());
        if (this.editedPluginYML()) {
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.configuration = ConfigurationHelper.prepareConfiguration(CustomItemsConfiguration.class, this.getDataFolder(), "pluginConfiguration");
        this.saveResourceIfNotExists("schematics/turbotrap.schem");
        this.saveResourceIfNotExists("schematics/turbodomek.schem");
        this.database = new Database();
        if (this.configuration.getDatabaseType() == DatabaseType.SQLITE) {
            this.database.connectSQLite(this.getDataFolder());
        } else {
            this.database.connectMySQL(this.configuration.getDatabaseHost(), this.configuration.getDatabasePort(), this.configuration.getDatabaseBase(), this.configuration.getDatabaseUser(), this.configuration.getDatabasePassword());
        }
        if (!this.database.isConnected()) {
            FastLogger.error("[Database] Nie można połączyć się z bazą danych!");
            FastLogger.error("[Database] Sprawdź konfigurację i upewnij się, że baza danych jest dostępna.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.databaseQueue = new DatabaseQueue(this.database);
        this.worldGuardHook = new WorldGuardHook(this.configuration.getRegionsWhereItemsAreBlocked());
        this.cooldownController = new CooldownController();
        this.bossBarService = new BossBarService();
        this.playerDataStorage = new PlayerDataStorage();
        this.playerDataRepository = new PlayerDataRepository(this.database, this.databaseQueue, this.playerDataStorage);
        this.playerDataService = new PlayerDataService(this.playerDataStorage, this.playerDataRepository);
        this.customItemStorage = new CustomItemStorage();
        this.customItemService = new CustomItemService(this.customItemStorage);
        this.purseDataStorage = new PurseDataStorage();
        this.purseDataRepository = new PurseDataRepository(this.database, this.databaseQueue, this.purseDataStorage);
        this.purseView = new PurseView(this, this.configuration, this.purseDataStorage, this.purseDataRepository);
        this.backpackDataStorage = new BackpackDataStorage();
        this.backpackDataRepository = new BackpackDataRepository(this.database, this.databaseQueue, this.backpackDataStorage);
        this.backpackView = new BackpackView(this, this.configuration, this.backpackDataStorage, this.backpackDataRepository);
        this.passiveEffectService = new PassiveEffectService(this);
        AnarchyCrownItem anarchyCrown = new AnarchyCrownItem(this, this.configuration);
        KupaAnarchiItem kupaAnarchi = new KupaAnarchiItem(this, this.configuration);
        LollipopItem lollipop = new LollipopItem(this, this.configuration);
        CupidsRoseItem cupidsRose = new CupidsRoseItem(this, this.configuration);
        this.passiveEffectService.register(anarchyCrown, kupaAnarchi, lollipop, cupidsRose);
        this.customItemService.registerItems(this, anarchyCrown, kupaAnarchi, lollipop, cupidsRose, new DivineAxeItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new GhostBlockItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new MaximBombItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new DynamiteItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new WarmMilkItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new ExcaliburItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new MutatedCreeperItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new ArcusMagnusItem(this, this.configuration, this.worldGuardHook), new ReinforcedElytraItem(this, this.configuration, this.worldGuardHook), new BalloonItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new MiracleLanternItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new KosaItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new RubikCubeItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new CupidBowItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new CarrotCrossbowItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new OlafItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new BunnySwordItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new BrokenHeartItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new CottonCandyItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new GingerbreadItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new GrinchShovelItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new LeftEggItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new MoldySandwichItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new PoisonedPencilItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new ScreenItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new SnowballSwapItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new SnowflakeItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new VampireAppleItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new VampireBloodItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new PurseItem(this, this.configuration, this.worldGuardHook, this.purseDataStorage, this.purseDataRepository, this.purseView), new AppeasementTotemItem(this, this.configuration), new HydroCageItem(this, this.configuration, this.worldGuardHook, this.cooldownController, this.bossBarService), new HydroTridentItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new SurferRodItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new NoFlyRodItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new SuperCarrotItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new DragonSwordItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new GrinchAxeItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new OvercookedCornItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new RodItem(this, this.configuration), new IllusionistWandItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new UnicornHornItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new InfernalShieldItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new InfernalSwordItem(this, this.configuration, this.worldGuardHook), new ExpiredPotionItem(this, this.configuration, this.worldGuardHook, this.cooldownController), new BackpackItem(this, this.configuration, this.worldGuardHook, this.backpackDataStorage, this.backpackDataRepository, this.backpackView));
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null || Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null) {
            this.worldEditHook = new WorldEditHook();
            this.customItemService.registerItems(this, new TurboDomekItem(this, this.configuration, this.worldGuardHook, this.cooldownController, this.worldEditHook), new TurboTrapItem(this, this.configuration, this.worldGuardHook, this.cooldownController, this.worldEditHook));
        } else {
            FastLogger.warning("Nie można zarejestrować TurboDomek i TurboTrap, brak hooka do WorldEdita!");
        }
        this.customItemStorage.prepareMapWithItemDisplayNames();
        CustomItemsCommand commandInstance = new CustomItemsCommand(this.configuration, this.customItemStorage);
        this.getCommand("customitems").setExecutor((CommandExecutor)commandInstance);
        this.getCommand("customitems").setTabCompleter((TabCompleter)commandInstance);
        this.registerListeners(new ChangePageHandler(commandInstance), new BossBarListener(this.bossBarService), new PlayerDataListener(this.playerDataStorage, this.playerDataRepository, this.playerDataService, this.cooldownController), new PurseViewListener(this, this.configuration, this.purseDataStorage, this.purseDataRepository), new BackpackViewListener(this, this.configuration, this.backpackDataStorage, this.backpackDataRepository));
        TaskDispatcher.scheduleTaskTimer(this, true, 0L, 1L, TimeUnit.MINUTES, () -> this.databaseQueue.flush());
        TaskDispatcher.scheduleTaskTimer(this, false, 0L, 1L, TimeUnit.SECONDS, new CooldownActionBarTask(this.customItemStorage, this.cooldownController));
    }

    public void onDisable() {
        HydroCageItem hydroCageItem = (HydroCageItem)this.customItemStorage.get("hydro_cage");
        if (hydroCageItem != null) {
            hydroCageItem.removeAllCages();
        }
        if (this.database != null && this.database.isConnected()) {
            this.playerDataStorage.getPlayerDataMap().values().forEach(itPlayerData -> this.playerDataRepository.save((PlayerData)itPlayerData, true));
            this.purseDataStorage.getPurseDataMap().values().forEach(itPurseData -> this.purseDataRepository.save((PurseData)itPurseData, true));
            this.backpackDataStorage.getBackpackDataMap().values().forEach(itBackpackData -> this.backpackDataRepository.save((BackpackData)itBackpackData, true));
            this.databaseQueue.flush();
            this.database.disconnect();
        }
    }

    private void registerListeners(Listener ... listeners) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Arrays.asList(listeners).forEach(itListener -> pluginManager.registerEvents(itListener, (Plugin)this));
    }

    public static final String AUTHOR = new String(new char[]{'f', 'r', 'a', 'n', 'e', 'k', 'k'});
    public static final String GITHUB = "https://github.com/Blejziak";

    private boolean editedPluginYML() {
        List<String> currentAuthors = this.getDescription().getAuthors();
        String currentWebsite = this.getDescription().getWebsite();
        if (currentAuthors == null || !currentAuthors.contains(AUTHOR) || currentWebsite == null || !currentWebsite.contains("Blejziak")) {
            FastLogger.info("Plugin FranekCustomItems - made by " + AUTHOR + " (" + GITHUB + ")");
        }
        FastLogger.info("Plugin zostal wlaczony (made by " + AUTHOR + ")");
        FastLogger.info("GitHub: " + GITHUB);
        return false;
    }

    private void saveResourceIfNotExists(String resourcePath) {
        File file = new File(this.getDataFolder(), resourcePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            this.saveResource(resourcePath, false);
        }
    }
    public NamespacedKey getCustomItemKey() {
        return this.customItemKey;
    }
    public CustomItemsConfiguration getConfiguration() {
        return this.configuration;
    }
    public Database getDatabase() {
        return this.database;
    }
    public DatabaseQueue getDatabaseQueue() {
        return this.databaseQueue;
    }
    public WorldGuardHook getWorldGuardHook() {
        return this.worldGuardHook;
    }
    public WorldEditHook getWorldEditHook() {
        return this.worldEditHook;
    }
    public CooldownController getCooldownController() {
        return this.cooldownController;
    }
    public BossBarService getBossBarService() {
        return this.bossBarService;
    }
    public PlayerDataStorage getPlayerDataStorage() {
        return this.playerDataStorage;
    }
    public PlayerDataRepository getPlayerDataRepository() {
        return this.playerDataRepository;
    }
    public PlayerDataService getPlayerDataService() {
        return this.playerDataService;
    }
    public PassiveEffectService getPassiveEffectService() {
        return this.passiveEffectService;
    }
    public CustomItemStorage getCustomItemStorage() {
        return this.customItemStorage;
    }
    public CustomItemService getCustomItemService() {
        return this.customItemService;
    }
    public PurseDataStorage getPurseDataStorage() {
        return this.purseDataStorage;
    }
    public PurseDataRepository getPurseDataRepository() {
        return this.purseDataRepository;
    }
    public PurseView getPurseView() {
        return this.purseView;
    }
    public BackpackDataStorage getBackpackDataStorage() {
        return this.backpackDataStorage;
    }
    public BackpackDataRepository getBackpackDataRepository() {
        return this.backpackDataRepository;
    }
    public BackpackView getBackpackView() {
        return this.backpackView;
    }
}

