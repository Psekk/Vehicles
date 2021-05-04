package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.packetlisteners.OnVehicleSteerPacket;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class OnPlayerQuit implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            Entity vehicleEntity = event.getPlayer().getVehicle();
            if (Objects.requireNonNull(vehicleEntity).getPersistentDataContainer().has(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)) {
                vehicleEntity.removePassenger(event.getPlayer());
            }
        }

        //some GC
        OnVehicleSteerPacket.remove(event.getPlayer());
    }
}
