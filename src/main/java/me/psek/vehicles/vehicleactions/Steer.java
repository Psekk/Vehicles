package me.psek.vehicles.vehicleactions;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.builders.CarData;
import me.psek.vehicles.builders.SpawnedCarData;
import me.psek.vehicles.enums.VehicleSteerDirection;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Steer {
    private static final Vehicles PLUGIN_INSTANCE = Vehicles.getPluginInstance();
    private static final NamespacedKey VEHICLE_NAME_KEY = new NamespacedKey(PLUGIN_INSTANCE, "vehicleName");

    private CarData getCarData(Entity vehicleEntity) {
        PersistentDataContainer persistentDataContainer = vehicleEntity.getPersistentDataContainer();
        if (persistentDataContainer.has(VEHICLE_NAME_KEY, PersistentDataType.STRING)) {
            return CarData.ALL_REGISTERED_CARS.get(persistentDataContainer.get(VEHICLE_NAME_KEY, PersistentDataType.STRING));
        }
        return null;
    }

    public void steerVehicle(SpawnedCarData spawnedCarData, VehicleSteerDirection direction) {

    }
}
