package me.psek.vehicles.handlers.data.serializabledata;

import lombok.Getter;

import java.io.Serializable;

public class SerializableSpawnedCarData implements Serializable {
    @Getter
    private final double currentSpeed;
    @Getter
    private final String name;
    @Getter
    private final byte[] centerUUID;
    @Getter
    private final String vehicleTypeName;
    @Getter
    private final byte[] steererUUID;
    @Getter
    private final byte[][] childUUIDs;
    @Getter
    private final int currentGear;
    @Getter
    private final double gasAmount;
    @Getter
    private final boolean electric;

    public SerializableSpawnedCarData(double currentSpeed, String name, byte[] centerUUID,
                                      String vehicleTypeName, byte[] steererUUID, byte[][] childUUIDs,
                                      int currentGear, double gasAmount, boolean electric) {
        this.currentSpeed = currentSpeed;
        this.name = name;
        this.centerUUID = centerUUID;
        this.vehicleTypeName = vehicleTypeName;
        this.steererUUID = steererUUID;
        this.childUUIDs = childUUIDs;
        this.currentGear = currentGear;
        this.gasAmount = gasAmount;
        this.electric = electric;
    }
}
