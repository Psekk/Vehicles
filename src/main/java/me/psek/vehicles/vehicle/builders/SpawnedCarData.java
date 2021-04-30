package me.psek.vehicles.vehicle.builders;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpawnedCarData {
    public static final HashMap<UUID, SpawnedCarData> ALL_SPAWNED_CAR_DATA = new HashMap<>();

    @Getter
    private final CarData carData;
    @Getter
    @Setter
    private double currentSpeed;
    @Getter
    @Setter
    //change to 0 after testing cus ehh no shifting implemented yet
    private int currentGear;
    @Getter
    @Setter
    private double currentRPM;
    /*
     * 1: centerEntity
     * 2-size: rest of seats
     */
    @Getter
    @Setter
    private List<Location> entityLocations;
    @Getter
    private final List<Entity> entities;
    @Getter
    @Setter
    private int ticksInRedZone = 0;
    @Getter
    @Setter
    private boolean isControlling;
    @Getter
    @Setter
    private boolean handBrake;

    public SpawnedCarData(CarData carData, List<Location> entityLocations, double cSpeed, double cRPM, List<Entity> entities, int currentGear) {
        this.carData = carData;
        this.entityLocations = entityLocations;
        this.currentSpeed = cSpeed;
        this.currentRPM = cRPM;
        this.entities = entities;
        this.currentGear = currentGear;
    }
}
