package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.vehicleentites.CarEntity;
import me.psek.vehicles.psekutils.UUIDUtils;
import me.psek.vehicles.vehicletypes.Car;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemHeldListener implements Listener {
    private final NamespacedKey vehicleSortClassNameKey;
    private final NamespacedKey centerUUIDKey;
    private final Vehicles plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PlayerItemHeldEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle == null) {
            return;
        }
        if (vehicle.getPersistentDataContainer().has(vehicleSortClassNameKey, PersistentDataType.STRING)) {
            listener(event, vehicle);
        }
    }

    private void listener(PlayerItemHeldEvent event, Entity vehicle) {
        UUID centerUUID = UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
        CarEntity spawnedCarData = (CarEntity) DataAPI.getSpawnedVehicles().get(centerUUID);
        Car.Builder builder = Car.getCarSubTypes().get(spawnedCarData.getName());
        if (builder.getGearCount() < event.getNewSlot()) {
            return;
        }
        spawnedCarData.setShifting(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            spawnedCarData.setCurrentGear(event.getNewSlot());
            spawnedCarData.setShifting(false);
        }, builder.getShiftTime());
    }

    public ItemHeldListener(Vehicles plugin, NamespacedKey vehicleSortClassNameKey, NamespacedKey centerUUIDKey) {
        this.vehicleSortClassNameKey = vehicleSortClassNameKey;
        this.centerUUIDKey = centerUUIDKey;
        this.plugin = plugin;
    }
}
