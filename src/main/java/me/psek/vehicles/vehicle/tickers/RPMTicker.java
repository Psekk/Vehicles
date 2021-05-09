package me.psek.vehicles.vehicle.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.data.SpawnedCarData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class RPMTicker {
    private static final HashMap<UUID, SpawnedCarData> IN_RED_RPM_ZONE = new HashMap<>();
    private static final HashMap<UUID, SpawnedCarData> IN_RED_RPM_ZONE_NOT_GASSING = new HashMap<>();

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Vehicles.getPluginInstance(), RPMTicker::check, 0, 1);
    }

    private static void check() {
        if (IN_RED_RPM_ZONE.size() > 0) {
            for (SpawnedCarData inRedZoneCar : IN_RED_RPM_ZONE.values()) {
                CarData carData = inRedZoneCar.getCarData();
                if (inRedZoneCar.getCurrentRPM() >= carData.getRPMs().get(2)) {
                    if (inRedZoneCar.getCurrentRPM() < carData.getRPMs().get(0)) {
                        inRedZoneCar.setTicksInRedZone(inRedZoneCar.getTicksInRedZone() + 1);
                        if (inRedZoneCar.getTicksInRedZone() > carData.getMaxRedRPMTicks()) {
                            System.out.println("kaboom car kaduk now brrr better get new car kekw");
                        }
                    }
                }
            }
        }
        if (IN_RED_RPM_ZONE_NOT_GASSING.size() > 0) {
            for (SpawnedCarData inRedZoneCar : IN_RED_RPM_ZONE_NOT_GASSING.values()) {
                int ticksInRedZone = Math.max(inRedZoneCar.getTicksInRedZone() - 1, 0);
                if (ticksInRedZone > 0) {
                    inRedZoneCar.setTicksInRedZone(ticksInRedZone);
                } else {
                    IN_RED_RPM_ZONE_NOT_GASSING.remove(inRedZoneCar.getEntities().get(0).getUniqueId());
                }
            }
        }
    }

    public static void add(SpawnedCarData spawnedCarData) {
        UUID uuid = spawnedCarData.getEntities().get(0).getUniqueId();
        if (!IN_RED_RPM_ZONE.containsKey(uuid)) {
            IN_RED_RPM_ZONE.put(uuid, spawnedCarData);
        }
    }

    @SuppressWarnings("unused")
    public static void remove(SpawnedCarData spawnedCarData) {
        UUID uuid = spawnedCarData.getEntities().get(0).getUniqueId();
        IN_RED_RPM_ZONE.remove(uuid);
        IN_RED_RPM_ZONE_NOT_GASSING.put(uuid, spawnedCarData);
    }
}
