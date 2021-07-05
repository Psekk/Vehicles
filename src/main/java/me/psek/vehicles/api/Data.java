package me.psek.vehicles.api;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Data {
    private final Vehicles plugin;

    public Data(Vehicles plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, ISpawnedVehicle> getSpawnedVehicles() {
        return plugin.spawnedVehicles;
    }

    public List<IVehicle> getVehicleTypes() {
        return plugin.vehicleTypes;
    }

    public Map<String, IVehicle> getSubVehicleTypes() {
        return plugin.subVehicleTypes;
    }
}
