package me.psek.vehicles.handlers.config;

import me.psek.vehicles.Vehicles;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;

public class ConfigHandler {
    private @Nullable File configurationFile;

    public void reloadConfig(Vehicles plugin) {
        if (configurationFile == null) {
            configurationFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configurationFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configurationFile);

    }

    public ConfigHandler(Vehicles plugin) {
        reloadConfig(plugin);
    }
}
