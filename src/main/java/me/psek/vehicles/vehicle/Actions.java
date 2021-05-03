package me.psek.vehicles.vehicle;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.nms.INms;
import me.psek.vehicles.nms.Mediator;
import me.psek.vehicles.utils.Utils;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.data.SpawnedCarData;
import me.psek.vehicles.vehicle.enums.VehicleSteerDirection;
import me.psek.vehicles.vehicle.events.VehicleSteerEvent;
import me.psek.vehicles.vehicle.tickers.MoveTicker;
import me.psek.vehicles.vehicle.tickers.RPMTicker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Actions {
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private static final INms NMS_INSTANCE = Mediator.getNMS();

    public static void spawn(Location location, String vehicleName) {
        CarData carData = CarData.ALL_REGISTERED_CARS.get(vehicleName);

        location.setYaw(0);
        location.setPitch(0);

        ArmorStand centerArmorStand = Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class);
        centerArmorStand.setGravity(false);
        NMS_INSTANCE.setNoClip(centerArmorStand, true);
        centerArmorStand.setInvulnerable(true);
        centerArmorStand.setVisible(true);
        centerArmorStand.setBasePlate(false);
        centerArmorStand.setCustomName("center seat");

        List<Entity> entities = new ArrayList<>();
        entities.add(centerArmorStand);
        byte[] centerArmorStandUUID = Utils.UUIDAsBytes(centerArmorStand.getUniqueId());
        Vector[] seatVectors = carData.getSeatPositions().toArray(new Vector[0]);
        int steeringSeatIndex = carData.getSteeringSeatIndex();
        String[] childUUIDS = new String[carData.getSeatCount() + 2];
        for (int i = 0; i < carData.getSeatCount(); i++) {
            ArmorStand armorStand = location.getWorld().spawn(location.clone().add(seatVectors[i]), ArmorStand.class);
            armorStand.setGravity(false);
            NMS_INSTANCE.setNoClip(armorStand, true);
            armorStand.getPersistentDataContainer().set(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY, centerArmorStandUUID);
            armorStand.setInvulnerable(true);
            armorStand.setVisible(true);
            armorStand.setBasePlate(false);
            childUUIDS[i] = armorStand.getUniqueId().toString();
            entities.add(armorStand);
            if (i == steeringSeatIndex) {
                armorStand.setCustomName("steerer");
                armorStand.setCustomNameVisible(true);
                armorStand.getPersistentDataContainer().set(Vehicles.isSteeringSeatKey, PersistentDataType.INTEGER, 1);
            }
        }

        String stringChildUUIDS = String.join("/", childUUIDS);
        centerArmorStand.getPersistentDataContainer().set(Vehicles.uuidOfChildrenAsKey, PersistentDataType.STRING, stringChildUUIDS);

        SpawnedCarData spawnedCarData = new SpawnedCarData(carData,
                0D,
                75D,
                entities,
                1);
        SpawnedCarData.ALL_SPAWNED_CAR_DATA.put(centerArmorStand.getUniqueId(), spawnedCarData);
    }

    public static boolean tryShift(SpawnedCarData spawnedCarData, int gear) {
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear++ <= spawnedCarData.getCarData().getGearCount()) {
            spawnedCarData.setCurrentGear(currentGear);
            return true;
        }
        return false;
    }

    public static void toggleHandBrake(SpawnedCarData spawnedCarData) {
        spawnedCarData.setHandBrake(!spawnedCarData.isHandBrake());
    }

    public static void steerVehicle(SpawnedCarData spawnedCarData, VehicleSteerDirection direction) {
        int currentGear = spawnedCarData.getCurrentGear();
        CarData carData = spawnedCarData.getCarData();

        VehicleSteerEvent vehicleSteerEvent = new VehicleSteerEvent(carData, direction);
        PLUGIN_MANAGER.callEvent(vehicleSteerEvent);
        if (vehicleSteerEvent.isCancelled()) {
            return;
        }

        spawnedCarData.setControlling(true);
        switch (direction.directionValue) {
            //forwards
            case 0:
                if (currentGear == 0) {
                    //todo maybe add sounds ;)
                    return;
                }
                if (spawnedCarData.getCurrentSpeed() < 0) {
                    System.out.println("braking");
                    return;
                }
                if (!spawnedCarData.isMoving()) {
                    MoveTicker.add(spawnedCarData);
                }
                double RPMIncrease = carData.getRPMIncreasePerGear().get(spawnedCarData.getCurrentGear()) - 1;
                spawnedCarData.setCurrentRPM(spawnedCarData.getCurrentRPM() + RPMIncrease);
                if (spawnedCarData.getCurrentRPM() > carData.getRPMs().get(2)) {
                    RPMTicker.add(spawnedCarData);
                }
                double newSpeed = spawnedCarData.getCurrentSpeed() + carData.getAccelerationSpeed() * carData.getAccelerationMultipliers().get(currentGear - 1) / 10;
                moveVehicle(spawnedCarData, newSpeed);
                spawnedCarData.setCurrentSpeed(newSpeed);
            //backwards
            case 1:

            //right
            case 2:

            //left
            case 3:
        }
        spawnedCarData.setControlling(false);
    }

    public static void moveVehicle(SpawnedCarData spawnedCarData, double distance) {
        List<Entity> entities = spawnedCarData.getEntities();
        Vector speedVector = new Vector (Math.sin(entities.get(0).getLocation().getYaw()) * distance, 0, Math.cos(entities.get(0).getLocation().getYaw()) * distance);
        for (Entity entity : entities) {
            entity.setGravity(true);
            entity.setVelocity(speedVector);
        }
        fixVehicleIfNecessary(spawnedCarData);
    }

    private static void fixVehicleIfNecessary(SpawnedCarData spawnedCarData) {
        List<Vector> seatPositions = spawnedCarData.getCarData().getSeatPositions();
        List<Entity> entities = spawnedCarData.getEntities();
        Entity center = entities.get(0);
        for (int i = 0; i < seatPositions.size(); i++) {
            Entity entity = entities.get(i + 1);
            Vector requiredVector = center.getLocation().toVector().add(seatPositions.get(i)).subtract(entity.getLocation().toVector());
            if (requiredVector.getX() > 0.075 || requiredVector.getY() > 0.075 || requiredVector.getZ() > 0.075) {
                entity.setVelocity(requiredVector);
                System.out.println("adjusting -> " + requiredVector);
            }
        }
    }
}
