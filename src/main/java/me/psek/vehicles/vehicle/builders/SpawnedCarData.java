package me.psek.vehicles.vehicle.builders;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpawnedCarData {
    public static final HashMap<UUID, SpawnedCarData> ALL_SPAWNED_CAR_DATA = new HashMap<>();

    @Getter
    private final CarData carData;
    @Getter
    @Setter
    private Vector currentVector;
    @Getter
    @Setter
    private double currentSpeed;
    @Getter
    @Setter
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
    private final List<UUID> entityUUIDs;
    @Getter
    @Setter
    private int ticksInRedZone = 0;

    public SpawnedCarData(CarData carData, List<Location> entityLocations, Vector cVector, double cSpeed, double cRPM, List<UUID> entityUUIDs) {
        this.carData = carData;
        this.entityLocations = entityLocations;
        this.currentVector = cVector;
        this.currentSpeed = cSpeed;
        this.currentRPM = cRPM;
        this.entityUUIDs = entityUUIDs;
    }
}
