package me.psek.vehicles.api;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.utility.UUIDUtils;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.util.List;

@SuppressWarnings("unused")
public class RegisteringAPI {
    public static void registerSpawnedVehicle(ISpawnedVehicle iSpawnedVehicle) {
        Vehicles.spawnedVehicles.put(iSpawnedVehicle.getCenterUUID(), iSpawnedVehicle);
    }

    public static void unregisterSpawnedVehicle(byte[] UUID) {
        Vehicles.spawnedVehicles.remove(UUIDUtils.bytesToUUID(UUID));
    }

    public static void registerVehicleTypes(IVehicle... vehicleTypes) {
        Vehicles.getInstance().vehicleTypes.addAll(List.of(vehicleTypes));
    }

    public static void registerSubVehicleType(String name, IVehicle type) {
        Vehicles.getInstance().subVehicleTypes.put(name, type);
    }
}
