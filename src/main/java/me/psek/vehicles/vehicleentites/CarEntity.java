package me.psek.vehicles.vehicleentites;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class CarEntity implements IVehicleEntity {
    public CarEntity(IVehicle vehicleType, String name, UUID centerUUID, Entity centerEntity, Entity[] children, UUID steererUUID, boolean electric, double currentRPM) {
        this.vehicleType = vehicleType;
        this.name = name;
        this.currentRPM = currentRPM;
        this.centerUUID = centerUUID;
        this.children = children;
        this.steererUUID = steererUUID;
        this.electric = electric; //this will require more physics YAY, ill rewrite the entire physics "engine" soon anyways
        this.centerEntity = centerEntity;
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

    @Getter
    private final Entity centerEntity;

    @Getter
    @Setter
    private double angle = 0.0;

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
    private int currentGear = 0;

    @Getter
    @Setter
    private double gasAmount = 0D;

    @Getter
    @Setter
    private double currentRPM;

    @Getter
    private final boolean electric;
}
