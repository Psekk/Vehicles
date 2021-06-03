package me.psek.vehicles.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class KickListener implements Listener {
    private final NamespacedKey centerUUIDKey;

    @EventHandler
    public void OnKick(PlayerKickEvent event) {
        if (!event.getPlayer().isInsideVehicle()) {
            return;
        }
        Entity vehicleEntity = event.getPlayer().getVehicle();
        if (!Objects.requireNonNull(vehicleEntity).getPersistentDataContainer().has(centerUUIDKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        vehicleEntity.removePassenger(event.getPlayer());
    }

    public KickListener(NamespacedKey uuidOfCenterSeatKey) {
        this.centerUUIDKey = uuidOfCenterSeatKey;
    }
}
