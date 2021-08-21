package me.psek.vehicles.vehicleentites;

import java.util.UUID;

public interface IVehicleEntity {
    double getCurrentSpeed();
    String getName();
    UUID getCenterUUID();
}
