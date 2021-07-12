package me.psek.vehicles.spawnedvehicledata;

import java.util.UUID;

public interface ISpawnedVehicle {
    double getCurrentSpeed();
    String getName();
    UUID getCenterUUID();
}
