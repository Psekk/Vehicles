package me.psek.vehicles.utils;

import me.psek.vehicles.Vehicles;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class Utils {
    public static boolean canDrive(Player player) {
        if (!player.isInsideVehicle()) {
            return false;
        }
        Entity vehicleEntity = player.getVehicle();
        if (!(vehicleEntity != null && vehicleEntity.getType().equals(EntityType.ARMOR_STAND))) {
            return false;
        }
        return vehicleEntity.getPersistentDataContainer().has(Vehicles.isSteeringSeatKey, PersistentDataType.INTEGER);
    }
}
