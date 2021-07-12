package me.psek.vehicles.spawnedvehicledata;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class SpawnedCarData implements ISpawnedVehicle {
    public SpawnedCarData(IVehicle vehicleType, String name, UUID centerUUID, Entity[] children, UUID steererUUID, boolean electric, double currentRPM) {
        this.vehicleType = vehicleType;
        this.name = name;
        this.currentRPM = currentRPM;
        this.centerUUID = centerUUID;
        this.children = children;
        this.steererUUID = steererUUID;
        this.electric = electric;
    }

    @Setter
    private double currentSpeed = 0D;

    @Getter
    @Setter
    private boolean isShifting = false;

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }

    private final UUID centerUUID;

    @Override
    public UUID getCenterUUID() {
        return centerUUID;
    }

    @Getter
    private final IVehicle vehicleType;

    @Getter
    private final UUID steererUUID;

    @Getter
    private final Entity[] children;

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
}
