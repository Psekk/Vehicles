package me.psek.vehicles.vehicle.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.data.SpawnedCarData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

import static me.psek.vehicles.vehicle.Actions.*;

public class MoveTicker {
    private static final HashMap<UUID, SpawnedCarData> NOT_GASSING = new HashMap<>();

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Vehicles.getPluginInstance(), MoveTicker::check, 1, 1);
    }

    //todo fix lag dropping of y level and lag causing weird seat shifting (literal shifting after lag aswell)
    private static void check() {
        if (NOT_GASSING.size() > 0) {
            for (SpawnedCarData spawnedCarData : NOT_GASSING.values()) {
                if (!spawnedCarData.isMoving()) {
                    remove(spawnedCarData);
                    continue;
                }
                if (spawnedCarData.isControlling()) {
                    continue;
                }
                double currentSpeed = spawnedCarData.getCurrentSpeed();
                if (currentSpeed <= 0) {
                    for (Entity entity : spawnedCarData.getEntities()) {
                        entity.setGravity(false);
                    }
                    remove(spawnedCarData);
                    continue;
                }
                double newSpeed = Math.max(0, currentSpeed - 0.0075);
                spawnedCarData.setCurrentSpeed(newSpeed);
                moveVehicle(spawnedCarData, newSpeed);
                Bukkit.broadcastMessage(newSpeed + "");
            }
        }
    }

    public static void add(SpawnedCarData spawnedCarData) {
        spawnedCarData.setMoving(true);
        NOT_GASSING.put(spawnedCarData.getEntities().get(0).getUniqueId(), spawnedCarData);
    }

    public static void remove(SpawnedCarData spawnedCarData) {
        spawnedCarData.setMoving(false);
        NOT_GASSING.remove(spawnedCarData.getEntities().get(0).getUniqueId());
    }
}
