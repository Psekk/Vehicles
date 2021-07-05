package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.Data;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.UUIDUtils;
import me.psek.vehicles.vehicletypes.Car;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemHeldListener implements Listener {
    private final NamespacedKey vehicleSortClassNameKey;
    private final NamespacedKey centerUUIDKey;
    private final Data dataAPI;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PlayerItemHeldEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle == null) {
            return;
        }
        if (vehicle.getPersistentDataContainer().has(vehicleSortClassNameKey, PersistentDataType.STRING)) {
            carShiftEvent(event, vehicle);
        }
    }

    private void carShiftEvent(PlayerItemHeldEvent event, Entity vehicle) {
        UUID centerUUID = UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
        SpawnedCarData spawnedCarData = (SpawnedCarData) dataAPI.getSpawnedVehicles().get(centerUUID);
        Car.Builder builder = Car.getCarSubTypes().get(spawnedCarData.getName());
        if (builder.getGearCount() < event.getNewSlot()) {
            return;
        }
        spawnedCarData.setCurrentGear(event.getNewSlot());
        System.out.println("shifted to " + event.getNewSlot());
    }

    public ItemHeldListener(Vehicles plugin, NamespacedKey vehicleSortClassNameKey, NamespacedKey centerUUIDKey) {
        this.vehicleSortClassNameKey = vehicleSortClassNameKey;
        this.centerUUIDKey = centerUUIDKey;
        dataAPI = plugin.getAPIHandler().getDataAPI();
    }
}
