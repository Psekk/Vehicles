package me.psek.vehicles.handlers.data;

import lombok.Getter;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class SpawnedVehicleData implements Serializable {
    public static final transient HashMap<UUID, SpawnedVehicleData> ALL_SPAWNED_VEHICLES = new HashMap<>();

    @Getter
    private final IVehicle vehicleType;

    public SpawnedVehicleData(IVehicle vehicleType) {
        this.vehicleType = vehicleType;
    }
}
