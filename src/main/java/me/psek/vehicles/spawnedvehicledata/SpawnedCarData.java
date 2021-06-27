package me.psek.vehicles.spawnedvehicledata;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.util.Vector;

public class SpawnedCarData implements ISpawnedVehicle {
    @Setter
    private double currentSpeed = 0D;

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }

    private final byte[] centerUUID;

    @Override
    public byte[] getCenterUUID() {
        return centerUUID;
    }

    @Getter
    private final IVehicle vehicleType;

    @Getter
    private final byte[] steererUUID;

    @Getter
    private final byte[][] childUUIDs;

    @Getter
    private Vector currentVector = new Vector(0, 0, 0);

    @Getter
    @Setter
    //todo change to 0 later on
    private int currentGear = 1;

    @Getter
    @Setter
    private double gasAmount = 0D;

    @Getter
    @Setter
    private double currentRPM;

    @Getter
    private final boolean electric;


    public SpawnedCarData(IVehicle vehicleType, String name, byte[] centerUUID, byte[][] childUUIDs, byte[] steererUUID, boolean electric, double currentRPM) {
        this.vehicleType = vehicleType;
        this.name = name;
        this.currentRPM = currentRPM;
        this.centerUUID = centerUUID;
        this.childUUIDs = childUUIDs;
        this.steererUUID = steererUUID;
        this.electric = electric;
    }
}
