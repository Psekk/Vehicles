package me.psek.vehicles.spawnedvehicledata;

import java.util.UUID;

public interface ISpawnedVehicle {
    ISpawnedVehicle getSpawnedVehicle(UUID centerUUID);
    double getCurrentSpeed();
    String getName();
    byte[] getCenterUUID();
}
