package me.psek.vehicles.vehicle.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.nms.INms;
import me.psek.vehicles.nms.Mediator;
import me.psek.vehicles.vehicle.builders.SpawnedCarData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MoveTicker {
    private static final INms NMS_INSTANCE = Mediator.getNMS();
    private static final HashMap<UUID, SpawnedCarData> NOT_GASSING = new HashMap<>();

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Vehicles.getPluginInstance(), MoveTicker::check, 1, 1);
    }

    //todo fix lag dropping of y level and lag causing weird seat shifting (literal shifting after lag aswell)
    private static void check() {
        if (NOT_GASSING.size() > 0) {
            for (SpawnedCarData spawnedCarData : NOT_GASSING.values()) {
                if (spawnedCarData.isControlling()) {
                    continue;
                }
                double currentSpeed = spawnedCarData.getCurrentSpeed();
                if (currentSpeed > 0) {
                    currentSpeed = Math.max(0, currentSpeed - 0.0065);
                    spawnedCarData.setCurrentSpeed(currentSpeed);
                    List<Entity> entityList = spawnedCarData.getEntities();
                    double yaw = entityList.get(0).getLocation().getYaw();
                    double vectorX = Math.sin(yaw) * currentSpeed;
                    double vectorZ = Math.cos(yaw) * currentSpeed;

                    for (Entity entity : entityList) {
                        entity.setGravity(true);
                        NMS_INSTANCE.setNoClip(entity, true);
                        entity.setVelocity(new Vector(vectorX, 0, vectorZ));
                    }
                } else {
                    for (Entity entity : spawnedCarData.getEntities()) {
                        entity.setGravity(false);
                    }
                }
            }
        }
    }

    public static void add(SpawnedCarData spawnedCarData) {
        NOT_GASSING.put(spawnedCarData.getEntities().get(0).getUniqueId(), spawnedCarData);
    }

    public static void remove(UUID uuid) {
        NOT_GASSING.remove(uuid);
    }
}
