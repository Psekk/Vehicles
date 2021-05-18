package me.psek.vehicles.spawnedvehiclesdata;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.io.Serializable;

public class SpawnedCarData implements Serializable {
    @Getter
    private final IVehicle vehicleType;

    @Getter
    private final int id;

    @Getter
    private final byte[] centerUUID;

    @Getter
    private final byte[][] childUUIDs;

    @Getter
    @Setter
    private double currentSpeed = 0D;

    @Getter
    @Setter
    private int currentGear = 0;

    @Getter
    @Setter
    private double gasAmount = 0D;

    @Getter
    private final boolean electric;


    public SpawnedCarData(IVehicle vehicleType, int id, byte[] centerUUID, byte[][] childUUIDs, boolean electric) {
        this.vehicleType = vehicleType;
        this.id = id;
        this.centerUUID = centerUUID;
        this.childUUIDs = childUUIDs;
        this.electric = electric;
    }
}