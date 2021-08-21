package me.psek.vehicles.handlers.config;

import me.psek.vehicles.Vehicles;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;

public class ConfigHandler {
    private @Nullable File configurationFile;
    private @Nullable YamlConfiguration yamlConfiguration;

    public ConfigHandler(Vehicles plugin) {
        reloadConfig(plugin);
    }

    public void reloadConfig(Vehicles plugin) {
        if (configurationFile == null) {
            configurationFile = new File(plugin.getDataFolder(), "config.yml");
        }

        if (!configurationFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        yamlConfiguration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Nullable
    public <T> T get(String path) {
        Object object = yamlConfiguration.get(path);
        if (object == null) {
            return null;
        }
        return (T) object;
    }
}
