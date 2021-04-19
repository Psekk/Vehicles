package me.psek.vehicles.utils;

import me.psek.vehicles.Vehicles;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class Utils {
    private static final Vehicles PLUGIN_INSTANCE = Vehicles.getPluginInstance();

    private static final NamespacedKey IS_DRIVEABLE_KEY = new NamespacedKey(PLUGIN_INSTANCE, "isSteeringSeat");

    public static boolean canDriveable(Player player) {
        if (!player.isInsideVehicle()) {
            return false;
        }
        Entity vehicleEntity = player.getVehicle();
        if (!vehicleEntity.getType().equals(EntityType.ARMOR_STAND)) {
            return false;
        }
        if (!vehicleEntity.getPersistentDataContainer().has(IS_DRIVEABLE_KEY, PersistentDataType.STRING)) {
            return false;
        }
        return true;
    }
}
