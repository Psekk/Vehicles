package me.psek.vehicles.spawnedvehicledata;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.beans.Transient;
import java.io.Serializable;
import java.util.UUID;

public class SpawnedCarData implements Serializable, ISpawnedVehicle {
    @Setter
    private double currentSpeed = 0D;

    @Override
    @Transient public double getCurrentSpeed() {
        return currentSpeed;
    }

    private final String name;

    @Override
    @Transient public String getName() {
        return name;
    }

    @Override
    @Transient public ISpawnedVehicle getSpawnedVehicle(UUID centerUUID) {
        return this;
    }

    private final byte[] centerUUID;

    @Override
    @Transient public byte[] getCenterUUID() {
        return centerUUID;
    }

    @Getter
    private final IVehicle vehicleType;

    @Getter
    private final int id;

    @Getter
    private final byte[] steererUUID;

    @Getter
    private final byte[][] childUUIDs;

    @Getter
    @Setter
    private int currentGear = 0;

    @Getter
    @Setter
    private double gasAmount = 0D;

    @Getter
    private final boolean electric;


    public SpawnedCarData(IVehicle vehicleType, int id, String name, byte[] centerUUID, byte[][] childUUIDs, byte[] steererUUID, boolean electric) {
        this.vehicleType = vehicleType;
        this.id = id;
        this.name = name;
        this.centerUUID = centerUUID;
        this.childUUIDs = childUUIDs;
        this.steererUUID = steererUUID;
        this.electric = electric;
    }
}
