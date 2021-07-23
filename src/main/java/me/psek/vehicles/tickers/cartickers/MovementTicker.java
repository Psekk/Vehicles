package me.psek.vehicles.tickers.cartickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.handlers.physics.CarPhysics;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.MathUtils;
import me.psek.vehicles.vehicletypes.Car;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.UUID;

public class MovementTicker {
    public MovementTicker(Vehicles plugin, INMS NMSInstance, Car carInstance) {
        run(plugin, NMSInstance, carInstance);
    }

    private void run(Vehicles plugin, INMS NMSInstance, Car carInstance) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Entity[] entities : Car.movingCars.values()) {
                UUID centerEntityUUID = entities[0].getUniqueId();
                SpawnedCarData spawnedCarData = (SpawnedCarData) DataAPI.getSpawnedVehicles().get(centerEntityUUID);
                double currentSpeed = spawnedCarData.getCurrentSpeed();
                Car.Builder builder = Car.getCarSubTypes().get(spawnedCarData.getName());
                double frictionForce = CarPhysics.getFrictionForce(currentSpeed, builder.getTirePressure(), builder.getTireRadius(), builder.getVehicleMass() * 9.81);
                double deceleration = MathUtils.flipNumber(CarPhysics.getAcceleration(frictionForce, builder.getVehicleMass()) / 20.0)*3.6;
                if (MathUtils.checkSignBitChange(currentSpeed, currentSpeed - deceleration)) {
                    entities[0].setVelocity(new Vector(0, 0, 0));
                    Car.movingCars.remove(centerEntityUUID);
                    spawnedCarData.setCurrentSpeed(0);
                    for (Entity entity : entities) {
                        entity.setGravity(false);
                        NMSInstance.setNoClip(entity, false);
                    }
                    continue;
                }
                carInstance.moveStraight(deceleration, NMSInstance, spawnedCarData, builder);
            }
        }, 1L, 1L);
    }
}
