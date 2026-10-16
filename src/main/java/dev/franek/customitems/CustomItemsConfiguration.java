package dev.franek.customitems;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.franek.customitems.database.DatabaseType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Comments;
import dev.franek.customitems.shared.itemstack.ItemStackBuilder;
import dev.franek.customitems.shared.notification.Notification;
import dev.franek.customitems.shared.notification.NotificationType;

public class CustomItemsConfiguration
extends OkaeriConfig {
    @Comment(value={"Dostępne typy baz danych: SQLITE, MYSQL"})
    private DatabaseType databaseType = DatabaseType.SQLITE;
    @Comments(value={@Comment(value={""}), @Comment(value={"Poniższe ustawienia są ignorowane, jeśli typ bazy danych to SQLITE"})})
    private String databaseHost = "localhost";
    private int databasePort = 3306;
    private String databaseUser = "";
    private String databasePassword = "";
    private String databaseBase = "main";
    private List<String> regionsWhereItemsAreBlocked = List.of("spawn");
    @Comment(value={""})
    private GhostBlockItemSection ghostBlockItem = new GhostBlockItemSection(this);
    @Comment(value={""})
    private MutatedCreeperItemSection mutatedCreeperItem = new MutatedCreeperItemSection(this);
    @Comment(value={""})
    private ArcusMagnusItemSection arcusMagnusItem = new ArcusMagnusItemSection(this);
    @Comment(value={""})
    private ReinforcedElytraSection reinforcedElytra = new ReinforcedElytraSection(this);
    @Comment(value={""})
    private TurboDomekItemSection turboDomekItem = new TurboDomekItemSection(this);
    @Comment(value={""})
    private BalloonItemSection balloonItem = new BalloonItemSection(this);
    @Comment(value={""})
    private DraculaBackpackSection draculaBackpack = new DraculaBackpackSection(this);
    @Comment(value={""})
    private MiracleLanternSection miracleLantern = new MiracleLanternSection(this);
    @Comment(value={""})
    private BrokenHeartItemSection brokenHeartItem = new BrokenHeartItemSection(this);
    @Comment(value={""})
    private BunnySwordItemSection bunnySwordItem = new BunnySwordItemSection(this);
    @Comment(value={""})
    private MaximBombItemSection maximBombItem = new MaximBombItemSection(this);
    @Comment(value={""})
    private DivineAxeItemSection divineAxeItem = new DivineAxeItemSection(this);
    @Comment(value={""})
    private WarmMilkItemSection warmMilkItem = new WarmMilkItemSection(this);
    @Comment(value={""})
    private DynamiteItemSection dynamiteItem = new DynamiteItemSection(this);
    @Comment(value={""})
    private ExcaliburItemSection excaliburItem = new ExcaliburItemSection(this);
    @Comment(value={""})
    private AnarchyCrownItemSection anarchyCrownItem = new AnarchyCrownItemSection(this);
    @Comment(value={""})
    private CottonCandyItemSection cottonCandyItem = new CottonCandyItemSection(this);
    @Comment(value={""})
    private GingerbreadItemSection gingerbreadItem = new GingerbreadItemSection(this);
    @Comment(value={""})
    private GrinchShovelItemSection grinchShovelItem = new GrinchShovelItemSection(this);
    @Comment(value={""})
    private LeftEggItemSection leftEggItem = new LeftEggItemSection(this);
    @Comment(value={""})
    private LollipopItemSection lollipopItem = new LollipopItemSection(this);
    @Comment(value={""})
    private MoldySandwichItemSection moldySandwichItem = new MoldySandwichItemSection(this);
    @Comment(value={""})
    private PoisonedPencilItemSection poisonedPencilItem = new PoisonedPencilItemSection(this);
    @Comment(value={""})
    private ScreenItemSection screenItem = new ScreenItemSection(this);
    @Comment(value={""})
    private SnowballSwapItemSection snowballSwapItem = new SnowballSwapItemSection(this);
    @Comment(value={""})
    private SnowflakeItemSection snowflakeItem = new SnowflakeItemSection(this);
    @Comment(value={""})
    private VampireAppleItemSection vampireAppleItem = new VampireAppleItemSection(this);
    @Comment(value={""})
    private VampireBloodItemSection vampireBloodItem = new VampireBloodItemSection(this);
    @Comment(value={""})
    private PurseItemSection purseItem = new PurseItemSection(this);
    @Comment(value={""})
    private AppeasementTotemItemSection appeasementTotemItem = new AppeasementTotemItemSection(this);
    @Comment(value={""})
    private HydroCageItemSection hydroCageItem = new HydroCageItemSection(this);
    @Comment(value={""})
    private HydroTridentItemSection hydroTridentItem = new HydroTridentItemSection(this);
    @Comment(value={""})
    private NoFlyRodItemSection noFlyRodItem = new NoFlyRodItemSection(this);
    @Comment(value={""})
    private SurferRodItemSection surferRodItem = new SurferRodItemSection(this);
    @Comment(value={""})
    private TurboTrapItemSection turboTrapItem = new TurboTrapItemSection(this);
    @Comment(value={""})
    private SuperCarrotItemSection superCarrotItem = new SuperCarrotItemSection(this);
    @Comment(value={""})
    private DragonSwordItemSection dragonSwordItem = new DragonSwordItemSection(this);
    @Comment(value={""})
    private GrinchAxeItemSection grinchAxeItem = new GrinchAxeItemSection(this);
    @Comment(value={""})
    private OvercookedCornItemSection overcookedCornItem = new OvercookedCornItemSection(this);
    @Comment(value={""})
    private RodItemSection rodItem = new RodItemSection(this);
    @Comment(value={""})
    private IllusionistWandItemSection illusionistWandItem = new IllusionistWandItemSection(this);
    @Comment(value={""})
    private CupidsRoseItemSection cupidsRoseItem = new CupidsRoseItemSection(this);
    @Comment(value={""})
    private UnicornHornItemSection unicornHornItem = new UnicornHornItemSection(this);
    @Comment(value={""})
    private InfernalShieldItemSection infernalShieldItem = new InfernalShieldItemSection(this);
    @Comment(value={""})
    private InfernalSwordItemSection infernalSwordItem = new InfernalSwordItemSection(this);
    @Comment(value={""})
    private ExpiredPotionItemSection expiredPotionItem = new ExpiredPotionItemSection(this);
    @Comment(value={""})
    private CarrotCrossbowSection carrotCrossbow = new CarrotCrossbowSection(this);
    @Comment(value={""})
    private CupidBowItemSection cupidBowItem = new CupidBowItemSection(this);
    @Comment(value={""})
    private KosaItemSection kosaItem = new KosaItemSection(this);
    @Comment(value={""})
    private KupaAnarchiItemSection kupaAnarchiItem = new KupaAnarchiItemSection(this);
    @Comment(value={""})
    private OlafItemSection olafItem = new OlafItemSection(this);
    @Comment(value={""})
    private RubikCubeItemSection rubikCubeItem = new RubikCubeItemSection(this);
    @Comment(value={""})
    private CarrotSwordSection carrotSword = new CarrotSwordSection(this);
    @Comment(value={""})
    private BackpackItemSection backpackItem = new BackpackItemSection(this);
    @Comment(value={""})
    private MessagesSection messagesSection = new MessagesSection(this);
    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }
    public String getDatabaseHost() {
        return this.databaseHost;
    }
    public int getDatabasePort() {
        return this.databasePort;
    }
    public String getDatabaseUser() {
        return this.databaseUser;
    }
    public String getDatabasePassword() {
        return this.databasePassword;
    }
    public String getDatabaseBase() {
        return this.databaseBase;
    }
    public List<String> getRegionsWhereItemsAreBlocked() {
        return this.regionsWhereItemsAreBlocked;
    }
    public GhostBlockItemSection getGhostBlockItem() {
        return this.ghostBlockItem;
    }
    public MutatedCreeperItemSection getMutatedCreeperItem() {
        return this.mutatedCreeperItem;
    }
    public ArcusMagnusItemSection getArcusMagnusItem() {
        return this.arcusMagnusItem;
    }
    public ReinforcedElytraSection getReinforcedElytra() {
        return this.reinforcedElytra;
    }
    public TurboDomekItemSection getTurboDomekItem() {
        return this.turboDomekItem;
    }
    public BalloonItemSection getBalloonItem() {
        return this.balloonItem;
    }
    public DraculaBackpackSection getDraculaBackpack() {
        return this.draculaBackpack;
    }
    public MiracleLanternSection getMiracleLantern() {
        return this.miracleLantern;
    }
    public BrokenHeartItemSection getBrokenHeartItem() {
        return this.brokenHeartItem;
    }
    public BunnySwordItemSection getBunnySwordItem() {
        return this.bunnySwordItem;
    }
    public MaximBombItemSection getMaximBombItem() {
        return this.maximBombItem;
    }
    public DivineAxeItemSection getDivineAxeItem() {
        return this.divineAxeItem;
    }
    public WarmMilkItemSection getWarmMilkItem() {
        return this.warmMilkItem;
    }
    public DynamiteItemSection getDynamiteItem() {
        return this.dynamiteItem;
    }
    public ExcaliburItemSection getExcaliburItem() {
        return this.excaliburItem;
    }
    public AnarchyCrownItemSection getAnarchyCrownItem() {
        return this.anarchyCrownItem;
    }
    public CottonCandyItemSection getCottonCandyItem() {
        return this.cottonCandyItem;
    }
    public GingerbreadItemSection getGingerbreadItem() {
        return this.gingerbreadItem;
    }
    public GrinchShovelItemSection getGrinchShovelItem() {
        return this.grinchShovelItem;
    }
    public LeftEggItemSection getLeftEggItem() {
        return this.leftEggItem;
    }
    public LollipopItemSection getLollipopItem() {
        return this.lollipopItem;
    }
    public MoldySandwichItemSection getMoldySandwichItem() {
        return this.moldySandwichItem;
    }
    public PoisonedPencilItemSection getPoisonedPencilItem() {
        return this.poisonedPencilItem;
    }
    public ScreenItemSection getScreenItem() {
        return this.screenItem;
    }
    public SnowballSwapItemSection getSnowballSwapItem() {
        return this.snowballSwapItem;
    }
    public SnowflakeItemSection getSnowflakeItem() {
        return this.snowflakeItem;
    }
    public VampireAppleItemSection getVampireAppleItem() {
        return this.vampireAppleItem;
    }
    public VampireBloodItemSection getVampireBloodItem() {
        return this.vampireBloodItem;
    }
    public PurseItemSection getPurseItem() {
        return this.purseItem;
    }
    public AppeasementTotemItemSection getAppeasementTotemItem() {
        return this.appeasementTotemItem;
    }
    public HydroCageItemSection getHydroCageItem() {
        return this.hydroCageItem;
    }
    public HydroTridentItemSection getHydroTridentItem() {
        return this.hydroTridentItem;
    }
    public NoFlyRodItemSection getNoFlyRodItem() {
        return this.noFlyRodItem;
    }
    public SurferRodItemSection getSurferRodItem() {
        return this.surferRodItem;
    }
    public TurboTrapItemSection getTurboTrapItem() {
        return this.turboTrapItem;
    }
    public SuperCarrotItemSection getSuperCarrotItem() {
        return this.superCarrotItem;
    }
    public DragonSwordItemSection getDragonSwordItem() {
        return this.dragonSwordItem;
    }
    public GrinchAxeItemSection getGrinchAxeItem() {
        return this.grinchAxeItem;
    }
    public OvercookedCornItemSection getOvercookedCornItem() {
        return this.overcookedCornItem;
    }
    public RodItemSection getRodItem() {
        return this.rodItem;
    }
    public IllusionistWandItemSection getIllusionistWandItem() {
        return this.illusionistWandItem;
    }
    public CupidsRoseItemSection getCupidsRoseItem() {
        return this.cupidsRoseItem;
    }
    public UnicornHornItemSection getUnicornHornItem() {
        return this.unicornHornItem;
    }
    public InfernalShieldItemSection getInfernalShieldItem() {
        return this.infernalShieldItem;
    }
    public InfernalSwordItemSection getInfernalSwordItem() {
        return this.infernalSwordItem;
    }
    public ExpiredPotionItemSection getExpiredPotionItem() {
        return this.expiredPotionItem;
    }
    public CarrotCrossbowSection getCarrotCrossbow() {
        return this.carrotCrossbow;
    }
    public CupidBowItemSection getCupidBowItem() {
        return this.cupidBowItem;
    }
    public KosaItemSection getKosaItem() {
        return this.kosaItem;
    }
    public KupaAnarchiItemSection getKupaAnarchiItem() {
        return this.kupaAnarchiItem;
    }
    public OlafItemSection getOlafItem() {
        return this.olafItem;
    }
    public RubikCubeItemSection getRubikCubeItem() {
        return this.rubikCubeItem;
    }
    public CarrotSwordSection getCarrotSword() {
        return this.carrotSword;
    }
    public BackpackItemSection getBackpackItem() {
        return this.backpackItem;
    }
    public MessagesSection getMessagesSection() {
        return this.messagesSection;
    }

    public class GhostBlockItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.STRUCTURE_BLOCK).setDisplayName("&d&lBlok Widmo").setLore(List.of("&8» &7Jest to przedmiot zdobyty podczas", "&8» &fDnia Dziecka 2025&7!", "", "&8» &7Po postawieniu na ziemi,", "&8» &7uwalnia &eenergię&7, która obniża", "&8» &fmaksymalny limit &7serc pobliskim", "&8» &7Po postawieniu na ziemi,", "&8» &7graczom na określony czas.")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int effectDurationInSeconds = 30;
        private double healthToRemove = 20.0;
        private double minHealth = 2.0;
        private double radius = 30.0;
        @Comment(value={""})
        private Notification locationOfLastUseIsTooClose = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz użyć tego przedmiotu tak blisko poprzedniego użycia!");
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aBloku Widmo&f.");
        private Notification onAffected = new Notification(NotificationType.CHAT, "&8[&e@&8] &fEfekt &eBloku Widmo &fobniżył Twój limit serc! &7Bądź ostrożny, efekt wkrótce minie.");
        private Notification onExpire = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fEfekt &aBloku Widmo &fminął! &7Twoje serca wróciły do normy.");

        public GhostBlockItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getEffectDurationInSeconds() {
            return this.effectDurationInSeconds;
        }
        public double getHealthToRemove() {
            return this.healthToRemove;
        }
        public double getMinHealth() {
            return this.minHealth;
        }
        public double getRadius() {
            return this.radius;
        }
        public Notification getLocationOfLastUseIsTooClose() {
            return this.locationOfLastUseIsTooClose;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnAffected() {
            return this.onAffected;
        }
        public Notification getOnExpire() {
            return this.onExpire;
        }
    }

    public class MutatedCreeperItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.CREEPER_SPAWN_EGG).setDisplayName("&a&lJajko zmutowanego creepera").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fspecjalnego wydarzenia marcowego (2025)", "", "&8 » &7Prawym kliknięciem w jajko przywołuje ono", "&8 » &fpotężnego&7 creepera, który po &cwybuchu", "&8 » &7zadaje &eogromne &7obrażenia.", "")).setCustomModelData(12345).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        private String creeperName = "&2Zainfekowany Creeper";
        private double explosionRadius = 5.0;
        private double healthDamagePercent = 0.5;
        @Comment(value={""})
        private Notification onSpawn = new Notification(NotificationType.CHAT, "");
        private Notification onDespawnRegion = new Notification(NotificationType.CHAT, "&8» &7Twój &2Zmutowany Creeper &7zniknął po wejściu do regionu bezpiecznego!");
        private Notification target = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&a&lZMUTOWANY CREEPER<N>&7Straciłeś &f50% &7swojego zdrowia!");

        public MutatedCreeperItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public String getCreeperName() {
            return this.creeperName;
        }
        public double getExplosionRadius() {
            return this.explosionRadius;
        }
        public double getHealthDamagePercent() {
            return this.healthDamagePercent;
        }
        public Notification getOnSpawn() {
            return this.onSpawn;
        }
        public Notification getOnDespawnRegion() {
            return this.onDespawnRegion;
        }
        public Notification getTarget() {
            return this.target;
        }
    }

    public class ArcusMagnusItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BOW).setDisplayName("&a&lArcus Magnus").setLore(List.of("", "&8 » &7Jest to przedmiot z:", "&8 » &feventu halloween (2024)", "", "&8 » &7Łuk, który pozwala na", "&8 » &eniszczycielskie &7combo!", "&8 » &7Po trafieniu przeciwnika", "&8 » &7zadajesz mu &cogromne obrażenia&7, które", "&8 » &7zwiększają się z każdym trafieniem!", "")).setCustomModelData(5324).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        private List<Double> comboDamage = List.of(Double.valueOf(0.5), Double.valueOf(1.0), Double.valueOf(2.0), Double.valueOf(4.0), Double.valueOf(8.0));
        private long comboDurationMs = 4000L;
        @Comment(value={""})
        private Notification onShooter = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fTrafiłeś gracza &a{TARGET}&f. &8(&7Zadałeś &4&4{DAMAGE}❤&8)");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony przez &e{SHOOTER}&f. &8(&7Straciłeś &4{DAMAGE}❤&8)");

        public ArcusMagnusItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public List<Double> getComboDamage() {
            return this.comboDamage;
        }
        public long getComboDurationMs() {
            return this.comboDurationMs;
        }
        public Notification getOnShooter() {
            return this.onShooter;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class ReinforcedElytraSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.ELYTRA).setDisplayName("&5&lWzmocniona elytra").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fWielkanocnego Wydarzenia 2025&7!", "", "&8 » &7Po naładowaniu eletry do &f100%&7, zyskujesz", "&8 » &cpotężną moc&7, która przy uderzeniu w ziemie", "&8 » &7zadaje stałe &c8 HP &7obrażeń wszystkim graczom", "&8 » &7w promieniu &f5 bloków&7! Bloki wylatują w powietrze!", "")).setCustomModelData(10000).addEnchantment(Enchantment.UNBREAKING, 3).addEnchantment(Enchantment.FEATHER_FALLING, 4).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        private double chargeRate = 0.15;
        private double landingRadius = 5.0;
        private double damage = 20.0;
        @Comment(value={""})
        private Notification actionBar = new Notification(NotificationType.ACTIONBAR, "&bWzmocniona elytra &fzaładowana w &3{PERCENTAGE}%");
        private Notification attacker = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&5&lWZMOCNIONA ELYTR<N>&7Trafiłeś &f{COUNT}&7 graczy elytrą!");
        private Notification target = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&5&lWZMOCNIONA ELYTRA<N> &f{ATTACKER} &7uderzył Cię wzmocnioną elytrą!");
        private String targetSubtitle = "&f{ATTACKER} &7uderzył Cię wzmocnioną elytrą!";

        public ReinforcedElytraSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public double getChargeRate() {
            return this.chargeRate;
        }
        public double getLandingRadius() {
            return this.landingRadius;
        }
        public double getDamage() {
            return this.damage;
        }
        public Notification getActionBar() {
            return this.actionBar;
        }
        public Notification getAttacker() {
            return this.attacker;
        }
        public Notification getTarget() {
            return this.target;
        }
        public String getTargetSubtitle() {
            return this.targetSubtitle;
        }
    }

    public class TurboDomekItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.EGG).setDisplayName("&a&lTurbo-Domek").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fWielkanocnego Wydarzenia 2025&7!", "", "&8 » &7Podczas wystrzelenia turbo-domek", "&8 » &fbudujesz prostą pułapkę&7 w miejscu ekspozji!", "")).setCustomModelData(51).addEnchantment(Enchantment.UNBREAKING, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        @Comment(value={""})
        private int cooldownInSeconds = 120;
        @Comment(value={"Ustawienia animacji budowania"})
        private boolean animationEnabled = true;
        private int minDelay = 100;
        private int maxDelay = 250;
        private boolean particlesEnabled = true;
        @Comment(value={"Ustawienia dźwięku"})
        private boolean soundEnabled = true;
        private String soundType = "BLOCK_WOOD_PLACE";
        private float soundVolume = 1.0f;
        private float soundPitch = 0.8f;
        @Comment(value={""})
        private Notification shooter = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&a&lTURBO-DOMEK<N>&7Stworzyłeś &fdomek&7!");

        public TurboDomekItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public boolean isAnimationEnabled() {
            return this.animationEnabled;
        }
        public int getMinDelay() {
            return this.minDelay;
        }
        public int getMaxDelay() {
            return this.maxDelay;
        }
        public boolean isParticlesEnabled() {
            return this.particlesEnabled;
        }
        public boolean isSoundEnabled() {
            return this.soundEnabled;
        }
        public String getSoundType() {
            return this.soundType;
        }
        public float getSoundVolume() {
            return this.soundVolume;
        }
        public float getSoundPitch() {
            return this.soundPitch;
        }
        public Notification getShooter() {
            return this.shooter;
        }
    }

    public class BalloonItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.PLAYER_HEAD).setDisplayName("&3&lBalonik z helem").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fDnia Dziecka 2025&7!", "", "&8 » &7Po aktywacji unosi się, niszcząc", "&8 » &7wszystkie bloki znajdujące się nad nim.", "")).setCustomModelData(811).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).build();
        private String headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4NTUyMmVlODE1ZDExMDU4N2ZmZmM3NDExM2Y0MTlkOTI5NTk4ZTI0NjNiOGNlOWQzOWNhYTlmYjZmZjVhYiJ9fX0=";
        private int cooldownInSeconds = 60;
        private int maxHeight = 256;
        private int updateTickRate = 2;
        private double speedPerUpdate = 0.3;
        @Comment(value={""})
        private Notification spawn = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&3&lBALONIK Z HELEM<N>&7Twój balonik zaczął unosić się w &fpowietrze&7!");

        public BalloonItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public String getHeadTexture() {
            return this.headTexture;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getMaxHeight() {
            return this.maxHeight;
        }
        public int getUpdateTickRate() {
            return this.updateTickRate;
        }
        public double getSpeedPerUpdate() {
            return this.speedPerUpdate;
        }
        public Notification getSpawn() {
            return this.spawn;
        }
    }

    public class DraculaBackpackSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BUNDLE).setDisplayName("&c&lPlecak Drakuli").setLore(List.of("", "&8 » &7Otwiera przenośny &dplecak &7na &e5 slotów&7.", "&8 » &7Zawartość zostaje w &dtym egzemplarzu&7.", "&8 » &7Przedmiot &anie traci się po śmierci&7.", "")).setCustomModelData(1).build();

        public DraculaBackpackSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
    }

    public class MiracleLanternSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BEACON).setDisplayName("&d&lCudowna Latarnia").setLore(List.of("", "&8 » &7Przedmiot z &fświątecznego wydarzenia 2025&7!", "", "&8 » &7Po postawieniu otrzymujesz:", "&8 » &dRegeneracje V &8(20s)", "&8 » &eAbsorbcje VI &8(10s)", "&8 » &cSiłę II &8(10s)", "", "&8 » &7Fontanna pozostanie przez &f30 sekund&7.", "&8 » &7Efekty działają w zasięgu &f30 kratek&7.", "&8 » &7Zniszczenie fontanny usuwa efekty.", "")).setCustomModelData(0).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES).build();
        private int cooldownInSeconds = 120;
        private int durationSeconds = 30;
        private double radius = 30.0;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.REGENERATION, 400, 4), new PotionEffect(PotionEffectType.ABSORPTION, 200, 5), new PotionEffect(PotionEffectType.STRENGTH, 200, 1));
        private Notification placed = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&d&lCUDOWNA LATARNIA<N>&7Efekty działają w zasięgu &f30 kratek&7!");

        public MiracleLanternSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getDurationSeconds() {
            return this.durationSeconds;
        }
        public double getRadius() {
            return this.radius;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getPlaced() {
            return this.placed;
        }
    }

    public class BrokenHeartItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.PURPLE_DYE).setDisplayName("&5&lZłamane serce").setLore(List.of("", "&8 » &7Jest to przedmiot z:", "&8 » &feventu walentynkowego (2025)", "", "&8 » &7Uderz nim w przeciwnika, aby", "&8 » &7złamać jego &dserduszko&7!", "")).setCustomModelData(56463534).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).build();
        @Comment(value={""})
        private int cooldownInSeconds = 40;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1), new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
        @Comment(value={""})
        private Notification onShooter = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZłamałeś serce graczowi &a{TARGET}&f.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fTwoje serce zostało złamane przez &e{SHOOTER}&f.");

        public BrokenHeartItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnShooter() {
            return this.onShooter;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class BunnySwordItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_SWORD).setDisplayName("&3&lZajęczy miecz").setLore(List.of("", "&8 » &7Jest to przedmiot z &fwydarzenia", "&8 » &fwielkanocnego &7z &f2024 roku&7.", "", "&8 » &7Po uderzeniu przeciwnika blokuje", "&8 » &7możliwość skakania na &f4 sekundy&7!", "")).setCustomModelData(2).addEnchantment(Enchantment.SHARPNESS, 6).addEnchantment(Enchantment.FIRE_ASPECT, 2).build();
        @Comment(value={""})
        private int cooldownInSeconds = 40;
        private int jumpDisableDurationInSeconds = 4;
        @Comment(value={""})
        private Notification shooter = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&3&lZAJĘCZY MIECZ<N>&7Zablokowałeś skakanie &f{TARGET}&7!");
        private Notification target = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&3&lZAJĘCZY MIECZ<N>&f{SHOOTER} &7zablokował Ci skakanie!");

        public BunnySwordItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getJumpDisableDurationInSeconds() {
            return this.jumpDisableDurationInSeconds;
        }
        public Notification getShooter() {
            return this.shooter;
        }
        public Notification getTarget() {
            return this.target;
        }
    }

    public class MaximBombItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.FIRE_CHARGE).setDisplayName("&5&lBombarda Maxima").setLore(List.of("&8» &7Po wystrzeleniu wybucha w okolicy", "&8» &7i niszczy każdy, możliwy blok oprócz", "&8» &fskały macierzystej&7!")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aBombardę Maxima&f.");
        private Notification onDamage = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony przez &eBombardę Maxima&f!");

        public MaximBombItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnDamage() {
            return this.onDamage;
        }
    }

    public class DivineAxeItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_AXE).setDisplayName("&b&lBoski topór").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &fspecjalnego wydarzenia (2025)&7!", "", "&8» &7Po &fkliknięciu &7prawym przyciskiem", "&8» &7aktywujesz &epotężną falę uderzeniową", "&8» &7która zapewna Tobie &cnieśmiertelność&7!")).setCustomModelData(3462232).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int invulnerabilityDurationInSeconds = 3;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aBoskiego Topora&f.");
        private Notification onHit = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony przez falę uderzeniową &eBoskiego Topora&f!");

        public DivineAxeItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getInvulnerabilityDurationInSeconds() {
            return this.invulnerabilityDurationInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnHit() {
            return this.onHit;
        }
    }

    public class WarmMilkItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BOWL).setDisplayName("&9&lCiepłe Mleko").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &feventu świątecznego (2024)&7!", "", "&8» &7Po wypiciu usuwa wszystkie", "&8» &cnegatywne efekty&7! Również posiada", "&8» &7mechanikę stakowania!")).setCustomModelData(43634712).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification noEffectsToRemove = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie masz żadnych negatywnych efektów do usunięcia!");
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aCiepłe Mleko&f.");

        public WarmMilkItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getNoEffectsToRemove() {
            return this.noEffectsToRemove;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class DynamiteItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.RED_CANDLE).setDisplayName("&4&lDynamit").setLore(List.of("&8» &7Ten przedmiot umożliwi tobie zniszczenie", "&8» &7skały macierzystej! Wystarczy kliknąć&7!", "&8» &7tylko &cprawy &7przycisk!")).setCustomModelData(523).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aDynamitu&f.");
        private Notification targetBlockIsNotBedrock = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fMusisz kliknąć &4skałę macierzystą&f, aby użyć tego przedmiotu!");
        private Notification targetBlockIsNaturalBedrock = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz użyć tego przedmiotu na naturalnej skale macierzystej!");

        public DynamiteItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getTargetBlockIsNotBedrock() {
            return this.targetBlockIsNotBedrock;
        }
        public Notification getTargetBlockIsNaturalBedrock() {
            return this.targetBlockIsNaturalBedrock;
        }
    }

    public class ExcaliburItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_SWORD).setDisplayName("&e&lExcalibur").setLore(List.of("&8» &7Jest to przedmiot zdobyty z", "&8» &fwydarzenia wakacyjnego 2024&7!", "", "&8» &7Aktualnie zabójstw: &f{KILLS}", "&8» &f&l{PROGRESS} &e{PERCENT}%", "", "&8» &7Zapełnienie paska zapenia", "&8» &7Ci &f12 punktów obrażeń&7, co", "&8» &7jest równoznaczne z &eostrością 7&7!")).setCustomModelData(5).addEnchantment(Enchantment.SHARPNESS, 6).addEnchantment(Enchantment.FIRE_ASPECT, 2).addItemFlags(ItemFlag.HIDE_ENCHANTS).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int maxKills = 200;
        @Comment(value={""})
        private int maxSharpness = 7;
        private boolean checkIP = true;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aExcalibura&f.");
        private Notification blockIP = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz użyć tego przedmiotu na gracza z tym samym IP!");
        private Notification recentlyKilled = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fJuż niedawno zabiłeś tego gracza za pomocą &cExcalibura&f!");

        public ExcaliburItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getMaxKills() {
            return this.maxKills;
        }
        public int getMaxSharpness() {
            return this.maxSharpness;
        }
        public boolean isCheckIP() {
            return this.checkIP;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getBlockIP() {
            return this.blockIP;
        }
        public Notification getRecentlyKilled() {
            return this.recentlyKilled;
        }
    }

    public class AnarchyCrownItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOLDEN_HELMET).setDisplayName("&6&lKorona ANARCHII").setLore(List.of("&8» &eTen przedmiot jest wyjątkowy!", "&8» &7Na serwerze znajduję się tylko jedna", "&8» &7taka korona! Która nigdy nie zostanie", "&8» &7zniszczona.", "", "&8» &eZalety przedmiotu:", "   &fStałe efekty:", "    &8- &7Szybkość II", "    &8- &7Odporność na ogień I", "    &8- &7Siła II", "    &8- &7Odporność III", "    &8- &7Szczęście I")).setCustomModelData(1).setUnbreakable(true).build();
        @Comment(value={""})
        private int refreshThreshold = 80;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.SPEED, 160, 1), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 160, 0), new PotionEffect(PotionEffectType.STRENGTH, 160, 1), new PotionEffect(PotionEffectType.RESISTANCE, 160, 2), new PotionEffect(PotionEffectType.LUCK, 160, 0));

        public AnarchyCrownItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getRefreshThreshold() {
            return this.refreshThreshold;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }

    public class CottonCandyItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.PINK_DYE).setDisplayName("&b&lWata cukrowa").setLore(List.of("&8» &7Jest to przedmiot zdobyty podczas", "&8» &fwydarzenia cyrkowego 2025&7!", "", "&8» &7Po użyciu rozpoczyna &dnaprawę &7jednego", "&8» &7z elementów twojej zbroi do &apełnej trwałości&7.")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fNaprawiłeś element zbroi &aWatą Cukrową&f.");
        private Notification nothingToRepair = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie masz żadnego uszkodzonego elementu zbroi!");

        public CottonCandyItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getNothingToRepair() {
            return this.nothingToRepair;
        }
    }

    public class GingerbreadItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.COOKIE).setDisplayName("&6&lPiernik").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &feventu świątecznego (2024)&7!", "", "&8» &7Po zjedzeniu &epiernika &7otrzymujesz", "&8» &7efekt &aszybkości kopania 10&7!")).setCustomModelData(346521).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.HASTE, 200, 9));
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZjadłeś &aPiernika&f!");

        public GingerbreadItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class GrinchShovelItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.DIAMOND_SHOVEL).setDisplayName("&a&lŁopata Grincha").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &fwydarzenia światecznego (2024)&7!", "", "&8» &7Po &auderzeniu &7przeciwnika", "&8» &7zostaje on &cogłuszony&7 przez co", "&8» &7rotacja zostaje wylosowana!")).setCustomModelData(4572321).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int freezeInSeconds = 3;
        @Comment(value={""})
        private Notification onUseDamager = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aŁopaty Grincha&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony &eŁopatą Grincha&f!");

        public GrinchShovelItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getFreezeInSeconds() {
            return this.freezeInSeconds;
        }
        public Notification getOnUseDamager() {
            return this.onUseDamager;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class LeftEggItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.EGG).setDisplayName("&eLewe jajko").setLore(List.of("&8» &7Jest to przedmiot z &fwydarzenia", "&8» &fwielkanocnego z 2024 roku&7!", "", "&8» &7Dzięki niemu możesz wyrzucić przeciwnika", "&8» &7w powietrze!")).setCustomModelData(0).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private double launchVelocity = 5.0;
        @Comment(value={""})
        private Notification onUseThrower = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fWyrzuciłeś gracza w górę &aLewym Jajkiem&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś wyrzucony w górę &eLewym Jajkiem&f!");

        public LeftEggItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getLaunchVelocity() {
            return this.launchVelocity;
        }
        public Notification getOnUseThrower() {
            return this.onUseThrower;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class LollipopItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.ALLIUM).setDisplayName("&d&lLizak").setLore(List.of("&8» &7Przedmiot zdobyty w &fdzień dziecka&7!", "&8» &7Trzymaj go w ręce aby otrzymywać:", "&8- &fEfekt SIŁY 1")).setCustomModelData(1).build();
        @Comment(value={""})
        private int refreshThreshold = 80;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.STRENGTH, 160, 0));

        public LollipopItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getRefreshThreshold() {
            return this.refreshThreshold;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }

    public class MoldySandwichItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.POISONOUS_POTATO).setDisplayName("&2&lSpleśniała Kanapka").setLore(List.of("&8» &7Jest to przedmiot zdobyty z:", "&8» &fwydarzenia szkolnego (2024)&7!", "", "&8» &7Po uderzeniu przeciwnika", "&8» &7zarazisz go &achorobą&7!")).setCustomModelData(53).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.POISON, 100, 0));
        @Comment(value={""})
        private Notification onUseAttacker = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aSpleśniałej Kanapki&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony &eSpleśniałą Kanapką&f!");

        public MoldySandwichItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUseAttacker() {
            return this.onUseAttacker;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class PoisonedPencilItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.LIME_CANDLE).setDisplayName("&aZatruty Ołówek").setLore(List.of("&8» &7Jest to przedmiot zdobyty z", "&8» &fwydarzenia szkolnego (2024)&7!", "", "&8» &7Po uderzeniu przeciwnika", "&8» &7otrzymuje on &ctrujący efekt&7!")).setCustomModelData(14).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.POISON, 100, 0));
        @Comment(value={""})
        private Notification onUseAttacker = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aZatrutego Ołówka&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony &eZatrutym Ołówkiem&f!");

        public PoisonedPencilItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUseAttacker() {
            return this.onUseAttacker;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class ScreenItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.FEATHER).setDisplayName("&e&lParawan").setLore(List.of("&8» &7Jest to przedmiot zdobyty z", "&8» &fwydarzenia wakacyjnego 2024&7!", "", "&8» &7Po użyciu tego przedmiotu odrzuca", "&8» &7wszystkich przeciwników w okolicy!")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private double knockbackRadius = 5.0;
        private double knockbackStrength = 1.5;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aParawanu&f!");
        private Notification onHit = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś odrzucony przez &eParawan&f!");

        public ScreenItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getKnockbackRadius() {
            return this.knockbackRadius;
        }
        public double getKnockbackStrength() {
            return this.knockbackStrength;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnHit() {
            return this.onHit;
        }
    }

    public class SnowballSwapItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.SNOWBALL).setDisplayName("&f&lŚnieżka").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &fwydarzenia światecznego (2023)&7!", "", "&8» &7Po trafieniu &ezamieniasz się", "&8» &7z przeciwnikiem miejscem!")).setCustomModelData(0).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUseThrower = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZamieniłeś się miejscami z graczem &aŚnieżką&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fKtoś zamienił się z Tobą miejscami &eŚnieżką&f!");

        public SnowballSwapItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUseThrower() {
            return this.onUseThrower;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class SnowflakeItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.SNOWBALL).setDisplayName("&b&lŚnieżynka").setLore(List.of("&8» &7Przedmiot z &fwydarzenia mikołajkowego 2025", "", "&8» &7Rzuć w gracza, aby go &bzamrozić&7 na krótką chwilę.", "&8» &7Możesz ją zdobyć poprzez &ezabjanie bałwanków", "&8» &7albo &ekopiąc bloki&7.")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int freezeInSeconds = 3;
        @Comment(value={""})
        private Notification onUseThrower = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZamroziłeś gracza &aŚnieżynką&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś zamrożony &eŚnieżynką&f!");

        public SnowflakeItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getFreezeInSeconds() {
            return this.freezeInSeconds;
        }
        public Notification getOnUseThrower() {
            return this.onUseThrower;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class VampireAppleItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.ENCHANTED_GOLDEN_APPLE).setDisplayName("&c&lWampirze Jabłko").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &feventu halloween (2024)&7!", "", "&8» &7Po zjedzeniu otrzymujesz", "&8» &7na krótki czas &csiły II&7!")).setCustomModelData(5324).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZjadłeś &aWampirze Jabłko&f!");

        public VampireAppleItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class VampireBloodItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BEETROOT_SOUP).setDisplayName("&c&lKrew wampira").setLore(List.of("&8» &7Jest to przedmiot z:", "&8» &feventu halloween (2024)&7!", "", "&8» &7Po kliknięciu uleczy Cię", "&8» &7do &cpełnego HP&7!")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aKrwi Wampira&f. Twoje serca zostały przywrócone!");

        public VampireBloodItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class PurseItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.RABBIT_FOOT).setDisplayName("&2&lSakiewka dropu").setLore(List.of("&8» &7Posiadając ten przedmiot w", "&8» &7ekwipunku po &czabiciu &7dowolnego", "&8» &fgracza &7jego przedmioty trafiają", "&8» &7do twojej &asakiewki&7!", "", "&8» &eKliknij PRAWYM, aby otworzyć sakiewkę!")).setCustomModelData(1).build();
        @Comment(value={""})
        private String viewTitle = "&2Sakiewka";
        @Comment(value={""})
        private ItemStack withdrawItem = ItemStackBuilder.of(Material.LIME_DYE).setDisplayName("&aWypłać wszystko").setLore(List.of("&8» &7Kliknij aby wypłacić wszystkie", "&8» &7przedmioty z sakiewki", "")).build();
        @Comment(value={""})
        private Notification cannotOpenUnlinked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz otworzyć tej sakiewki! &7Nie jest ona jeszcze powiązana z żadnym &cUUID&7.");
        private Notification cannotOpenStacked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz przypisać &cUUID &fdo sakiewek w stosie! &7Proszę rozdzielić sakiewki.");

        public PurseItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public String getViewTitle() {
            return this.viewTitle;
        }
        public ItemStack getWithdrawItem() {
            return this.withdrawItem;
        }
        public Notification getCannotOpenUnlinked() {
            return this.cannotOpenUnlinked;
        }
        public Notification getCannotOpenStacked() {
            return this.cannotOpenStacked;
        }
    }

    public class AppeasementTotemItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.TOTEM_OF_UNDYING).setDisplayName("&5&lTotem ułaskawienia").setLore(List.of("&8» &7Jest to przedmiot zdobyty z:", "&8» &feventu halloween (2023)&7!", "", "&8» &7Trzymając go po śmierci &dnie", "&8» &dstracisz &7swoich przedmiotów!")).setCustomModelData(1).build();
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fTotem ułaskawienia ochronił Cię przed utratą przedmiotów!");

        public AppeasementTotemItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class HydroCageItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BLAZE_ROD).setDisplayName("&3&lWyrzutnia Hydro Klatki").setLore(List.of("&8» &7Jest to przedmiot zdobyty podczas", "&8» &fwydarzenia wakacyjnego 2025&7!", "", "&8» &7Po użyciu wystrzeliwuje pocisk, który", "&8» &7tworzy &3wodną klatkę&7 uwięziającą", "&8» &7przeciwników w miejscu!")).setCustomModelData(2).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private int effectDurationInSeconds = 90;
        @Comment(value={""})
        private int radius = 8;
        private long buildSpeedTicks = 3L;
        @Comment(value={""})
        private String bossBarTitle = "&b&lHYDRO KLATKA";
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fWystrzeliłeś &aHydro Klatkę&f!");
        private Notification tooClose = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz użyć klatki tak blisko innej!");
        private Notification cannotBreakBlock = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz niszczyć bloków wewnątrz Hydro Klatki!");
        private Notification cannotPlaceBlock = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz stawiać bloków wewnątrz Hydro Klatki!");
        private Notification teleportBlocked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz teleportować się z Hydro Klatki!");
        private Notification itemUseBlocked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz użyć tego przedmiotu w Hydro Klatce!");

        public HydroCageItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getEffectDurationInSeconds() {
            return this.effectDurationInSeconds;
        }
        public int getRadius() {
            return this.radius;
        }
        public long getBuildSpeedTicks() {
            return this.buildSpeedTicks;
        }
        public String getBossBarTitle() {
            return this.bossBarTitle;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getTooClose() {
            return this.tooClose;
        }
        public Notification getCannotBreakBlock() {
            return this.cannotBreakBlock;
        }
        public Notification getCannotPlaceBlock() {
            return this.cannotPlaceBlock;
        }
        public Notification getTeleportBlocked() {
            return this.teleportBlocked;
        }
        public Notification getItemUseBlocked() {
            return this.itemUseBlocked;
        }
    }

    public class HydroTridentItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BOW).setDisplayName("&3&lHydro Trójząb").setLore(List.of("&8» &7Jest to przedmiot zdobyty podczas", "&8» &fwydarzenia wakacyjnego 2025&7!", "", "&8» &7Po uderzeniu trójzębem w miejsce", "&8» &7uderzenia &buderza piorun&7, który", "&8» &7zadaje obrażenia i odpycha", "&8» &7pobliskich przeciwników!", "", "&8» &7Klikając &fSHIFT &7możesz się", "&8» &7wystrzelić w dowolnym miejscu!")).setCustomModelData(6000).build();
        @Comment(value={""})
        private int dashCooldownInSeconds = 15;
        private int lightningCooldownInSeconds = 30;
        private double dashVelocityMultiplier = 5.5;
        private double lightningDamage = 10.0;
        private double lightningRadius = 3.0;

        public HydroTridentItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getDashCooldownInSeconds() {
            return this.dashCooldownInSeconds;
        }
        public int getLightningCooldownInSeconds() {
            return this.lightningCooldownInSeconds;
        }
        public double getDashVelocityMultiplier() {
            return this.dashVelocityMultiplier;
        }
        public double getLightningDamage() {
            return this.lightningDamage;
        }
        public double getLightningRadius() {
            return this.lightningRadius;
        }
    }

    public class NoFlyRodItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.FISHING_ROD).setDisplayName("&5&lWędka nielota").setLore(List.of("&8» &7Wędka nielota była do zdobycia", "&8» &fw &f2024 roku &7podczas &adnia dziecka", "", "&8» &7Po złapaniu gracza na haczyk", "&8» &7nie może on odlecieć &delytrą&7!")).setCustomModelData(2).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUseAttacker = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZłapałeś gracza &aWędką Nielota&f!");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś złapany &eWędką Nielota&f! Nie możesz lecieć!");
        private Notification onHookBreakAttacker = new Notification(NotificationType.CHAT, "&8[&e@&8] &fHaczyk się urwał! Gracz może lecieć za &e10 sekund&f.");
        private Notification onHookBreakTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fHaczyk się urwał! Możesz lecieć za &e10 sekund&f.");
        private Notification onExpire = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fMożesz znowu lecieć!");
        private Notification onTooFarAttacker = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fGracz uciekł za daleko — wędka się urwała!");
        private Notification onTooFarTarget = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUciekłeś wystarczająco daleko — wędka się urwała!");
        private Notification onGlideBlocked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz teraz latać!");

        public NoFlyRodItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUseAttacker() {
            return this.onUseAttacker;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
        public Notification getOnHookBreakAttacker() {
            return this.onHookBreakAttacker;
        }
        public Notification getOnHookBreakTarget() {
            return this.onHookBreakTarget;
        }
        public Notification getOnExpire() {
            return this.onExpire;
        }
        public Notification getOnTooFarAttacker() {
            return this.onTooFarAttacker;
        }
        public Notification getOnTooFarTarget() {
            return this.onTooFarTarget;
        }
        public Notification getOnGlideBlocked() {
            return this.onGlideBlocked;
        }
    }

    public class SurferRodItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.FISHING_ROD).setDisplayName("&b&lWędka surferka").setLore(List.of("&8» &7Jest to przedmiot z", "&8» &feventu wakacyjnego (2023)", "", "&8» &7Ta &aniezwykła &7wędka zapewnia ci", "&8» &7zdumiewająca zdolność przyciągania się", "&8» &7do &bbloków &7i &dgraczy&7, tworząc zupełnie", "&8» &7nowe możliwości eksploracji i walki!")).setCustomModelData(1).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        private double launchPullVector = 4.0;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aWędki Surferki&f!");

        public SurferRodItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getLaunchPullVector() {
            return this.launchPullVector;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class TurboTrapItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.EGG).setDisplayName("&a&lTurbo-Trap").setLore(List.of("&8» &7Jest to przedmiot zdobyty podczas", "&8» &fWielkanocnego Wydarzenia 2025&7!", "", "&8» &7Podczas wystrzelenia Turbotrapu", "&8» &fbudujesz prostą pułapkę&7 w miejscu eksplozji!")).setCustomModelData(50).build();
        @Comment(value={""})
        private int cooldownInSeconds = 300;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fWystrzelono &aTurbo-Trap&f!");
        private Notification schematicNotLoaded = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fBłąd: schemat pułapki nie jest załadowany!");

        public TurboTrapItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getSchematicNotLoaded() {
            return this.schematicNotLoaded;
        }
    }

    public class SuperCarrotItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOLDEN_CARROT).setDisplayName("&6&lSuper Nieskończona Marchewka").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwielkanocnego wydarzenia 2026&7!", "", "&8 » &7Po użyciu &fpowiększa cię do &6ogromnej wielkości&7,", "&8 » &7nakłada &cSpowolnienie II &7i &aOdporność III", "&8 » &7oraz &czwiększa &dobrażenia krytyczne&7!")).setCustomModelData(2026).addEnchantment(Enchantment.UNBREAKING, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        private int durationInSeconds = 30;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1), new PotionEffect(PotionEffectType.RESISTANCE, 600, 2));
        @Comment(value={""})
        private double scale = 2.0;
        private double criticalDamageMultiplier = 1.5;
        @Comment(value={""})
        private boolean cancelOnTeleport = true;
        @Comment(value={""})
        private boolean cancelOnEnterSpawn = true;
        @Comment(value={""})
        private Notification onActivation = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aSuper Nieskończonej Marchewki&f! &7Stałeś się ogromny.");
        private Notification onDeactivation = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fEfekt &aSuper Nieskończonej Marchewki &fsię skończył! &7Wróciłeś do normalnego rozmiaru.");
        private Notification onCancellation = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fEfekt &aSuper Nieskończonej Marchewki &fzostał anulowany!");

        public SuperCarrotItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getDurationInSeconds() {
            return this.durationInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public double getScale() {
            return this.scale;
        }
        public double getCriticalDamageMultiplier() {
            return this.criticalDamageMultiplier;
        }
        public boolean isCancelOnTeleport() {
            return this.cancelOnTeleport;
        }
        public boolean isCancelOnEnterSpawn() {
            return this.cancelOnEnterSpawn;
        }
        public Notification getOnActivation() {
            return this.onActivation;
        }
        public Notification getOnDeactivation() {
            return this.onDeactivation;
        }
        public Notification getOnCancellation() {
            return this.onCancellation;
        }
    }

    public class DragonSwordItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_SWORD).setDisplayName("&d&lSmoczy miecz").setLore(List.of("", "&8 » &7Posiada on możliwość strzelania", "&8 » &dperłami kresu&7!")).setCustomModelData(1).addEnchantment(Enchantment.FIRE_ASPECT, 2).addEnchantment(Enchantment.LOOTING, 3).addEnchantment(Enchantment.MENDING, 1).addEnchantment(Enchantment.SHARPNESS, 6).addEnchantment(Enchantment.UNBREAKING, 3).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 80;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aSmoczego Miecza&f!");

        public DragonSwordItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class GrinchAxeItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOLDEN_AXE).setDisplayName("&2Siekiera Grincha").setLore(List.of("", "&8 » &7Jest to przedmiot z:", "&8 » &feventu świątecznego (2023)", "", "&8 » &7Po &auderzeniu &7strzela &fpiorun", "&8 » &7w przeciwnika zadając mu &c30%&7 jego", "&8 » &7aktualnego zdrowia jako obrażenia,", "&8 » &7oraz wywołując &fefekt wizualny&7!", "&8 » &7Umiejętność &7działa raz na 1 minutę.")).setCustomModelData(1).addEnchantment(Enchantment.UNBREAKING, 3).addEnchantment(Enchantment.EFFICIENCY, 5).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        private double damagePercentage = 30.0;
        @Comment(value={""})
        private Notification onUseAttacker = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUderzyłeś gracza &7{PLAYER} &fsiekierą &agrincha&f! &8(&c-{DAMAGE} HP&8)");
        private Notification onUseTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony &7siekierą &eGrincha&f! &8(&c-{DAMAGE} HP&8)");

        public GrinchAxeItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getDamagePercentage() {
            return this.damagePercentage;
        }
        public Notification getOnUseAttacker() {
            return this.onUseAttacker;
        }
        public Notification getOnUseTarget() {
            return this.onUseTarget;
        }
    }

    public class OvercookedCornItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BLAZE_ROD).setDisplayName("&a&lRozgotowana kukurydza").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty z", "&8 » &fwydarzenia wakacyjnego 2024&7!", "", "&8 » &7Wystrzeliwuje magiczny pocisk,", "&8 » &7który niszczy zbroję oraz zadaje", "&8 » &7obrażenia w miejscu wybuchu.")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        private float explosionPower = 4.0f;
        private boolean createFire = false;
        private boolean preventRegionDestruction = true;
        @Comment(value={""})
        private List<Material> protectedBlocks = List.of(Material.BEDROCK);
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fWystrzeliłeś &aRozgotowaną Kukurydzę&f!");

        public OvercookedCornItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public float getExplosionPower() {
            return this.explosionPower;
        }
        public boolean isCreateFire() {
            return this.createFire;
        }
        public boolean isPreventRegionDestruction() {
            return this.preventRegionDestruction;
        }
        public List<Material> getProtectedBlocks() {
            return this.protectedBlocks;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class RodItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.STICK).setDisplayName("&6Rózga").setLore(List.of("", "&8 » &7Jest to przedmiot z:", "&8 » &feventu świątecznego (2023)", "", "&8 » &cOdrzuca &7z potężną siłą", "&8 » &7niegrzecznych graczy!")).setCustomModelData(1).addEnchantment(Enchantment.KNOCKBACK, 4).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();

        public RodItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
    }

    public class IllusionistWandItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOLDEN_HOE).setDisplayName("&5&lRóżdżka iluzjonisty").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia cyrkowego 2025&7!", "", "&8 » &7LPM: przywołuje &dSzczęki Evokera", "&8 » &7atakujące przeciwników na wprost.", "", "&8 » &7PPM: &dznikasz na &e4s &7i tworzysz", "&8 » &dprawdziwego bota &7który biega przed siebie!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build();
        @Comment(value={""})
        private int leftClickCooldownInSeconds = 60;
        @Comment(value={""})
        private int rightClickCooldownInSeconds = 120;
        @Comment(value={""})
        private int evokerFangsWaves = 5;
        @Comment(value={""})
        private int evokerFangsPerWave = 3;
        private double evokerFangsDistanceBetweenWaves = 2.0;
        private double evokerFangsDamage = 8.0;
        private long evokerFangsDelayBetweenWaves = 5L;
        @Comment(value={""})
        private int invisibilityDurationInSeconds = 4;
        private int cloneRunDurationInSeconds = 4;
        private double cloneSpeed = 0.3;
        @Comment(value={""})
        private Notification onEvokerFangs = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &aRóżdżki Iluzjonisty&f! &7Szczęki Evokera zostały przywołane.");
        private Notification onInvisibility = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fStałeś się &aniewidzialny&f.");

        public IllusionistWandItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getLeftClickCooldownInSeconds() {
            return this.leftClickCooldownInSeconds;
        }
        public int getRightClickCooldownInSeconds() {
            return this.rightClickCooldownInSeconds;
        }
        public int getEvokerFangsWaves() {
            return this.evokerFangsWaves;
        }
        public int getEvokerFangsPerWave() {
            return this.evokerFangsPerWave;
        }
        public double getEvokerFangsDistanceBetweenWaves() {
            return this.evokerFangsDistanceBetweenWaves;
        }
        public double getEvokerFangsDamage() {
            return this.evokerFangsDamage;
        }
        public long getEvokerFangsDelayBetweenWaves() {
            return this.evokerFangsDelayBetweenWaves;
        }
        public int getInvisibilityDurationInSeconds() {
            return this.invisibilityDurationInSeconds;
        }
        public int getCloneRunDurationInSeconds() {
            return this.cloneRunDurationInSeconds;
        }
        public double getCloneSpeed() {
            return this.cloneSpeed;
        }
        public Notification getOnEvokerFangs() {
            return this.onEvokerFangs;
        }
        public Notification getOnInvisibility() {
            return this.onInvisibility;
        }
    }

    public class CupidsRoseItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.POPPY).setDisplayName("&c&lRóża kupidyna").setLore(List.of("&8 » &7Przedmiot zdobyty w &fwalentynki w 2024 roku&7!", "&8 » &7Trzymaj go w ręce aby otrzymać:", "&8  - &fEfekt odporności 1", "&8  - &fEfekt regeneracji 1")).setCustomModelData(1).addEnchantment(Enchantment.UNBREAKING, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int refreshThreshold = 40;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false, false), new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false, false));

        public CupidsRoseItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getRefreshThreshold() {
            return this.refreshThreshold;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }

    public class UnicornHornItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOAT_HORN).setDisplayName("&d&lRóg jednorożca").setLore(List.of("", "&8 » &7Jest to przedmiot z:", "&8 » &feventu walentynkowego (2026)", "", "&8 » &7Po użyciu przywołujesz &djednorożca&7,", "&8 » &7którym możesz jeździć! Niszczy bloki", "&8 » &7na swojej drodze i &cogłusza graczy&7,", "&8 » &7w których wjedziesz!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        @Comment(value={""})
        private int durationInSeconds = 20;
        @Comment(value={""})
        private int stunDurationInSeconds = 3;
        private double horseSpeed = 0.45;
        private int horseHearts = 4;
        @Comment(value={""})
        private Notification onSuccess = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fPrzywołałeś &djednorożca&f.");
        private Notification onAlreadyActive = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fMasz już aktywnego &cjednorożca&f!");
        private Notification onExpired = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fCzas jednorożca minał!");
        private Notification onDismounted = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fZsiadłeś z &djednorożca&f.");
        private Notification onKilled = new Notification(NotificationType.CHAT, "&8[&e@&8] &fTwój &djednorożec &fzostał &czabity&f.");
        private Notification onStunned = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś stratowany przez &fjednorożca&f.");
        private Notification onEnteredSpawn = new Notification(NotificationType.CHAT, "&8[&e@&8] &fJednorożec zniknął w &cchronionym regionie&f.");

        public UnicornHornItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getDurationInSeconds() {
            return this.durationInSeconds;
        }
        public int getStunDurationInSeconds() {
            return this.stunDurationInSeconds;
        }
        public double getHorseSpeed() {
            return this.horseSpeed;
        }
        public int getHorseHearts() {
            return this.horseHearts;
        }
        public Notification getOnSuccess() {
            return this.onSuccess;
        }
        public Notification getOnAlreadyActive() {
            return this.onAlreadyActive;
        }
        public Notification getOnExpired() {
            return this.onExpired;
        }
        public Notification getOnDismounted() {
            return this.onDismounted;
        }
        public Notification getOnKilled() {
            return this.onKilled;
        }
        public Notification getOnStunned() {
            return this.onStunned;
        }
        public Notification getOnEnteredSpawn() {
            return this.onEnteredSpawn;
        }
    }

    public class InfernalShieldItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.SHIELD).setDisplayName("&6Piekielna tarcza").setLore(List.of("", "&8 » &7Posiada &f25% szansy &7na odbicie", "&8 » &cataku &7wroga!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        @Comment(value={""})
        private int cooldownInSeconds = 5;
        @Comment(value={""})
        private int reflectChance = 25;
        @Comment(value={""})
        private Notification onDefender = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fOdbiłeś atak od &7{ATTACKER}&f.");
        private Notification onAttacker = new Notification(NotificationType.CHAT, "&8[&e@&8] &fTwój atak został odbity przez &7{TARGET}&f!");

        public InfernalShieldItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getReflectChance() {
            return this.reflectChance;
        }
        public Notification getOnDefender() {
            return this.onDefender;
        }
        public Notification getOnAttacker() {
            return this.onAttacker;
        }
    }

    public class InfernalSwordItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_SWORD).setDisplayName("&c&lPiekielny miecz").setLore(List.of("", "&8 » &7Po uderzeniu przeciwnika podpalasz", "&8 » &7go &cpiekielnym ogniem&7, który", "&8 » &7ignoruje &fodporność na ogień&7!")).setCustomModelData(666).addEnchantment(Enchantment.UNBREAKING, 5).addEnchantment(Enchantment.SHARPNESS, 6).addEnchantment(Enchantment.FIRE_ASPECT, 2).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        @Comment(value={""})
        private int fireDurationInTicks = 100;
        private double fireDamagePerSecond = 1.0;

        public InfernalSwordItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getFireDurationInTicks() {
            return this.fireDurationInTicks;
        }
        public double getFireDamagePerSecond() {
            return this.fireDamagePerSecond;
        }
    }

    public class ExpiredPotionItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.HONEY_BOTTLE).setDisplayName("&2&lPrzeterminowany trunek").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia pirackiego 2025&7!", "", "&8 » &7Po rzuceniu na ziemię, uwalnia &emgiełkę&7, która", "&8 » &7nakłada &atruciznę &7i &fspowolnienie", "&8 » &7na pobliskich graczy przez &ckilkadziesiąt &7sekund.")).setCustomModelData(4325325).addEnchantment(Enchantment.UNBREAKING, 1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        @Comment(value={""})
        private int cooldownInSeconds = 120;
        private double throwVelocity = 1.2;
        private double areaRadius = 4.5;
        private int areaDurationInSeconds = 30;
        private int particleAmount = 30;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.POISON, 20, 4), new PotionEffect(PotionEffectType.SLOWNESS, 20, 4));
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.TITLE_AND_SUBTITLE, "&8[&a✔&8] &fRzuciłeś &aprzeterminowany trunek&f.");

        public ExpiredPotionItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getThrowVelocity() {
            return this.throwVelocity;
        }
        public double getAreaRadius() {
            return this.areaRadius;
        }
        public int getAreaDurationInSeconds() {
            return this.areaDurationInSeconds;
        }
        public int getParticleAmount() {
            return this.particleAmount;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
    }

    public class CarrotCrossbowSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.CROSSBOW).setDisplayName("&6&lMarchewkowa kusza").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwielkanocnego wydarzenia 2026&7!", "", "&8 » &7Strzała z kuszy &fprzywołuje&7 trafionego", "&8 » &7gracza do ciebie!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 15;
        private double pullStrength = 2.5;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUżyłeś &6Marchewkową Kuszę&f!\n&8[&a✔&8] &fPrzyciągnąłeś gracza &7{TARGET} &fdo siebie.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś przyciągnięty do &7{SHOOTER}&f przez &6Marchewkową Kuszę&f!");

        public CarrotCrossbowSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getPullStrength() {
            return this.pullStrength;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class CupidBowItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BOW).setDisplayName("&c&lŁuk kupidyna").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia walentynkowego 2024&7!", "", "&8 » &7Strzała z łuku ma &c50% szansy&7 na", "&8 » &7nałożenie efektów miłości!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 10;
        private double chance = 50.0;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1), new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
        @Comment(value={""})
        private Notification onShoot = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fTrafiłeś gracza &7{TARGET} &fstrzałą miłości.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś trafiony strzałą miłości od &7{SHOOTER}&f.");

        public CupidBowItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public double getChance() {
            return this.chance;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnShoot() {
            return this.onShoot;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class KosaItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.NETHERITE_HOE).setDisplayName("&7&lKosa").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia halloween 2025&7!", "", "&8 » &7Po uderzeniu nakłada &cefekty&7 na", "&8 » &7trafionego gracza!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 10;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.WITHER, 60, 0, false, true), new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, true));
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUderzyłeś &7{TARGET} &fkosą.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś uderzony kosą przez gracza &7{PLAYER}&f.");

        public KosaItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class KupaAnarchiItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BROWN_DYE).setDisplayName("&6&lKupa anarchii").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia prima aprilis 2025&7!", "", "&8 » &7Trzymaj go w lewej ręce", "&8 » &7aby otrzymywać efekty!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int refreshThreshold = 40;
        @Comment(value={""})
        private List<PotionEffect> effects = List.of(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false), new PotionEffect(PotionEffectType.STRENGTH, 100, 0, false, false));

        public KupaAnarchiItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getRefreshThreshold() {
            return this.refreshThreshold;
        }
        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }

    public class OlafItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.EGG).setDisplayName("&f&lOlaf").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia bożonarodzeniowego 2024&7!", "", "&8 » &7Rzuć jajkiem w gracza aby", "&8 » &7&fuwięzić go w bałwanie&7!")).setCustomModelData(889).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 60;
        private int hitsToBreak = 5;
        @Comment(value={""})
        private String snowmanName = "&f&lOLAF &7[{HITS_LEFT}/{HITS_TOTAL}]";
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUwięziłeś &7{TARGET}&f w bałwanie.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś uwięziony w bałwanie&f.");
        private Notification onFreed = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fUwolniłeś się z bałwana.");

        public OlafItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getHitsToBreak() {
            return this.hitsToBreak;
        }
        public String getSnowmanName() {
            return this.snowmanName;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
        public Notification getOnFreed() {
            return this.onFreed;
        }
    }

    public class RubikCubeItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.PLAYER_HEAD).setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGYxYTI3N2JlYjllNGZhYTZlN2UzNTZjNzQ3ODZlOTY2MTU1NzM2YTY4NThiZjViYjVhZDI5ZGY1YmFiNjFhMSJ9fX0=").setDisplayName("&e&lKostka Rubika").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwydarzenia prima aprilis 2025&7!", "", "&8 » &7Po uderzeniu gracza &fmiesza&7", "&8 » &7jego &ahotbar&7!", "")).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).build();
        @Comment(value={""})
        private int cooldownInSeconds = 30;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fPomieszałeś hotbar gracza &7{TARGET}&f.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fGracz &7{SHOOTER} &fpomieszał twój hotbar&f!");

        public RubikCubeItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class CarrotSwordSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.GOLDEN_SWORD).setDisplayName("&6&lMarchewkowy miecz").setLore(List.of("", "&8 » &7Jest to przedmiot zdobyty podczas", "&8 » &fwielkanocnego wydarzenia 2026&7!", "", "&8 » &7Po uderzeniu nakłada &cspowalnianie &7na", "&8 » &7trafionego gracza!")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build();
        @Comment(value={""})
        private int cooldownInSeconds = 10;
        @Comment(value={""})
        private int freezeDurationTicks = 60;
        @Comment(value={""})
        private Notification onUse = new Notification(NotificationType.CHAT, "&8[&a✔&8] &fSpowolniłeś gracza &7{TARGET}&f.");
        private Notification onTarget = new Notification(NotificationType.CHAT, "&8[&e@&8] &fZostałeś spowolniony przez gracza &7{ATTACKER}&f.");

        public CarrotSwordSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public int getCooldownInSeconds() {
            return this.cooldownInSeconds;
        }
        public int getFreezeDurationTicks() {
            return this.freezeDurationTicks;
        }
        public Notification getOnUse() {
            return this.onUse;
        }
        public Notification getOnTarget() {
            return this.onTarget;
        }
    }

    public class BackpackItemSection
    extends OkaeriConfig {
        private ItemStack item = ItemStackBuilder.of(Material.BUNDLE).setDisplayName("&c&lPlecak Drakuli").setLore(List.of("", "&8 » &7Przedmiot z &fwydarzenia halloween 2025&7!", "", "&8 » &7Otwiera przenośny &dplecak &7na &e5 slotów&7.", "&8 » &7Zawartość zapisywana jest w &dtym egzemplarzu&7.", "&8 » &7Edytowanie zawartości podczas &cwalki &7jest zablokowane.", "&8 » &7Przedmiot &anie traci się po śmierci&7.")).setCustomModelData(1).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP).build();
        @Comment(value={""})
        private String viewTitle = "&c&lPlecak Drakuli";
        @Comment(value={""})
        private ItemStack fillerItem = ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
        @Comment(value={""})
        private int combatCooldownSeconds = 30;
        private boolean canOpenInCombat = false;
        @Comment(value={""})
        private Notification cannotOpenStacked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz otworzyć kilku plecaków naraz!");
        private Notification cannotOpenUnlinked = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fTen plecak nie jest powiązany z żadnymi danymi!");
        private Notification cannotOpenInCombat = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz otworzyć plecaka podczas walki!");

        public BackpackItemSection(CustomItemsConfiguration this$0) {
        }
        public ItemStack getItem() {
            return this.item;
        }
        public String getViewTitle() {
            return this.viewTitle;
        }
        public ItemStack getFillerItem() {
            return this.fillerItem;
        }
        public int getCombatCooldownSeconds() {
            return this.combatCooldownSeconds;
        }
        public boolean isCanOpenInCombat() {
            return this.canOpenInCombat;
        }
        public Notification getCannotOpenStacked() {
            return this.cannotOpenStacked;
        }
        public Notification getCannotOpenUnlinked() {
            return this.cannotOpenUnlinked;
        }
        public Notification getCannotOpenInCombat() {
            return this.cannotOpenInCombat;
        }
    }

    public class MessagesSection
    extends OkaeriConfig {
        private String consoleIsBlocked = "&8[&4✖&8] &fWpisanej komendy nie można użyć z poziomu konsoli!";
        private String noPermission = "&8[&4✖&8] &fNie posiadasz wymaganego uprawnienia, aby to zrobić!";
        private String playerIsOffline = "&8[&4✖&8] &fPodany gracz jest Offline!";
        private String usage = "&8[&e@&8] &fPoprawne użycie: &e{USAGE}";
        private String playerIsNotExists = "&8[&4✖&8] &fPodany gracz nigdy nie był na serwerze!";
        @Comment(value={""})
        private Notification cannotUseInRegion = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fNie możesz tutaj użyć tego przedmiotu!");
        private Notification cooldownIsActive = new Notification(NotificationType.CHAT, "&8[&4✖&8] &fOdczekaj &7{TIME}&f, aby ponownie użyć tego przedmiotu!");
        @Comment(value={"# Wiadomość po przeładowaniu konfiguracji komendą /customitems reload"})
        private String configurationsReloaded = "&8[&a✔&8] &fWszystkie pliki konfiguracyjne zostały &aprzeładowane&f.";

        public MessagesSection(CustomItemsConfiguration this$0) {
        }
        public String getConsoleIsBlocked() {
            return this.consoleIsBlocked;
        }
        public String getNoPermission() {
            return this.noPermission;
        }
        public String getPlayerIsOffline() {
            return this.playerIsOffline;
        }
        public String getUsage() {
            return this.usage;
        }
        public String getPlayerIsNotExists() {
            return this.playerIsNotExists;
        }
        public Notification getCannotUseInRegion() {
            return this.cannotUseInRegion;
        }
        public Notification getCooldownIsActive() {
            return this.cooldownIsActive;
        }
        public String getConfigurationsReloaded() {
            return this.configurationsReloaded;
        }
    }
}

