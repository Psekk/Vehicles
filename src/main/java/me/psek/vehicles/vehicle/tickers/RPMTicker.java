package me.psek.vehicles.vehicle.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.builders.SpawnedCarData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class RPMTicker {
    private static final HashMap<UUID, SpawnedCarData> IN_RED_RPM_ZONE = new HashMap<>();

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Vehicles.getPluginInstance(), RPMTicker::checkRPMs, 0, 1);
    }

    private static void checkRPMs() {
        if (IN_RED_RPM_ZONE.size() > 0) {
            for (SpawnedCarData inRedZoneCar : IN_RED_RPM_ZONE.values()) {
                CarData carData = inRedZoneCar.getCarData();
                if (inRedZoneCar.getCurrentRPM() >= carData.getRPMs().get(2)) {
                    inRedZoneCar.setTicksInRedZone(inRedZoneCar.getTicksInRedZone() + 1);
                    if (inRedZoneCar.getTicksInRedZone() > carData.getMaxRedRPMTicks()) {
                        System.out.println("kaboom car kaduk now brrr better get new car kekw");
                    }
                } else {
                    int ticksInRedZone = Math.max(inRedZoneCar.getTicksInRedZone() - 1, 0);
                    if (ticksInRedZone > 0) {
                        inRedZoneCar.setTicksInRedZone(ticksInRedZone);
                    } else {
                        IN_RED_RPM_ZONE.remove(inRedZoneCar.getEntityUUIDs().get(0));
                    }
                }
            }
        }
    }

    public static void addRedRPM(UUID uuid, SpawnedCarData spawnedCarData) {
        if (!IN_RED_RPM_ZONE.containsKey(uuid)) {
            IN_RED_RPM_ZONE.put(uuid, spawnedCarData);
        }
    }
}
