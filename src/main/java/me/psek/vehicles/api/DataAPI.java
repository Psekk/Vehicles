package me.psek.vehicles.api;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicleentites.IVehicleEntity;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataAPI {
    public static Map<UUID, IVehicleEntity> getSpawnedVehicles() {
        return Vehicles.spawnedVehicles;
    }

    public static List<IVehicle> getVehicleTypes() {
        return Vehicles.getInstance().vehicleTypes;
    }

    public static Map<String, IVehicle> getSubVehicleTypes() {
        return Vehicles.getInstance().subVehicleTypes;
    }
}
