package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class QuitListener implements Listener {
    private final NamespacedKey centerUUIDKey;

    @EventHandler(priority= EventPriority.HIGHEST)
    public void event(PlayerQuitEvent event) {
        if (!event.getPlayer().isInsideVehicle()) {
            return;
        }
        Entity vehicleEntity = event.getPlayer().getVehicle();
        if (!Objects.requireNonNull(vehicleEntity).getPersistentDataContainer().has(centerUUIDKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        vehicleEntity.removePassenger(event.getPlayer());
    }

    public QuitListener(NamespacedKey uuidOfCenterSeatKey) {
        this.centerUUIDKey = uuidOfCenterSeatKey;
    }
}
