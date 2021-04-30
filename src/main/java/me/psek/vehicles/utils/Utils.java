package me.psek.vehicles.utils;

import me.psek.vehicles.Vehicles;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;
import java.util.UUID;

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

    public static UUID bytesAsUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] UUIDAsBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
