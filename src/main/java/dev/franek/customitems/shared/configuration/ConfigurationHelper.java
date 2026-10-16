package dev.franek.customitems.shared.configuration;

import java.io.File;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import dev.franek.customitems.shared.configuration.serdes.CustomOkaeriBukkitSerdes;
import dev.franek.customitems.shared.notification.NotificationSerializer;

public class ConfigurationHelper {
    public static <T extends OkaeriConfig> T prepareConfiguration(Class<T> clazz, File dataFolder, String pathAndConfigurationName) {
        return ConfigManager.create(clazz, it -> {
            it.withBindFile(String.valueOf(dataFolder) + "/configurations/" + pathAndConfigurationName + ".yml");
            it.withConfigurer(new YamlBukkitConfigurer(), new CustomOkaeriBukkitSerdes());
            it.withSerdesPack(registry -> registry.register(new NotificationSerializer()));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }
}

