package me.psek.vehicles.api;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.utility.UUIDUtils;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.util.List;

@SuppressWarnings("unused")
public class Registering {
    private final Vehicles plugin;
    public void registerSpawnedVehicle(ISpawnedVehicle iSpawnedVehicle) {
        plugin.spawnedVehicles.put(UUIDUtils.bytesToUUID(iSpawnedVehicle.getCenterUUID()), iSpawnedVehicle);
    }

    public void unregisterSpawnedVehicle(byte[] UUID) {
        plugin.spawnedVehicles.remove(UUIDUtils.bytesToUUID(UUID));
    }

    public void registerVehicleTypes(IVehicle... vehicleTypes) {
        plugin.vehicleTypes.addAll(List.of(vehicleTypes));
    }

    public void registerSubVehicleType(String name, IVehicle type) {
        plugin.subVehicleTypes.put(name, type);
    }

    public Registering(Vehicles plugin) {
        this.plugin = plugin;
    }
}
