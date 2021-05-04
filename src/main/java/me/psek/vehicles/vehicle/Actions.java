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
import org.bukkit.World;
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
        centerArmorStand.setInvulnerable(true);
        centerArmorStand.setVisible(true);
        centerArmorStand.setBasePlate(false);
        NMS_INSTANCE.setNoClip(centerArmorStand, true);

        List<Entity> entities = new ArrayList<>();
        entities.add(centerArmorStand);
        byte[] centerArmorStandUUID = Utils.UUIDAsBytes(centerArmorStand.getUniqueId());
        Vector[] seatVectors = carData.getSeatPositions().toArray(new Vector[0]);
        int steeringSeatIndex = carData.getSteeringSeatIndex();
        String[] childUUIDS = new String[carData.getSeatCount() + 2];
        for (int i = 0; i < carData.getSeatCount(); i++) {
            ArmorStand armorStand = location.getWorld().spawn(location.clone().add(seatVectors[i]), ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.getPersistentDataContainer().set(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY, centerArmorStandUUID);
            armorStand.setInvulnerable(true);
            armorStand.setVisible(true);
            armorStand.setBasePlate(false);
            childUUIDS[i] = armorStand.getUniqueId().toString();
            //todo noclip not remaining/working
            NMS_INSTANCE.setNoClip(armorStand, true);
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

    private static final World WORLD = Bukkit.getWorlds().get(0);
    public static boolean tryShift(SpawnedCarData spawnedCarData, int gear) {
        CarData carData = spawnedCarData.getCarData();
        if (gear > carData.getGearCount()) {
            return false;
        }
        if (spawnedCarData.getLastTimeShifted() > WORLD.getFullTime() + carData.getShiftTime()) {
            System.out.println("cant shift yet cus shiftime -> " + WORLD.getFullTime());
            return false;
        }
        //todo maybe add stalling
        spawnedCarData.setCurrentGear(gear);
        return true;
    }

    public static void toggleHandBrake(SpawnedCarData spawnedCarData) {
        spawnedCarData.setHandBrake(!spawnedCarData.isHandBrake());
    }

    public static void steerVehicle(SpawnedCarData spawnedCarData, VehicleSteerDirection direction) {
        if (spawnedCarData.isHandBrake()) {
            return;
        }
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear == 0) {
            updateRPM(spawnedCarData);
            return;
        }
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
                if (spawnedCarData.getCurrentSpeed() < 0) {
                    System.out.println("braking");
                    return;
                }
                if (!spawnedCarData.isMoving()) {
                    MoveTicker.add(spawnedCarData);
                }
                updateRPM(spawnedCarData);
                double newSpeed = spawnedCarData.getCurrentSpeed() + carData.getAccelerationSpeed() * carData.getAccelerationMultipliers().get(currentGear - 1) / 10;
                moveVehicle(spawnedCarData, newSpeed);
                spawnedCarData.setCurrentSpeed(newSpeed);
                break;
            //backwards
            case 1:

            case 2:
                //todo use grip factor n shit (also include drifting cus cool)
                Entity center = spawnedCarData.getEntities().get(0);
                setAngle(center, Math.toDegrees(getAngle(center)) - 1.15);
                break;
            //left
            case 3:
                center = spawnedCarData.getEntities().get(0);
                setAngle(center, Math.toDegrees(getAngle(center)) - 1.15);
                break;
        }
        spawnedCarData.setControlling(false);
    }

    public static void moveVehicle(SpawnedCarData spawnedCarData, double distance) {
        List<Entity> entities = spawnedCarData.getEntities();
        //Vector speedVector = Utils.newRotatedVector(Math.toRadians(entities.get(0).getLocation().getYaw()), distance);
        Vector speedVector = new Vector(0, 0, distance).rotateAroundY(getAngle(entities.get(0)));
        System.out.println("moving ->" + speedVector);
        for (Entity entity : entities) {
            entity.setGravity(true);
            entity.setVelocity(speedVector);
        }
        fixVehicleIfNecessary(spawnedCarData);
    }

    public static void fixVehicleIfNecessary(SpawnedCarData spawnedCarData) {
        List<Vector> seatPositions = spawnedCarData.getCarData().getSeatPositions();
        List<Entity> entities = spawnedCarData.getEntities();
        Entity center = entities.get(0);
        Location centerLocation = center.getLocation();

        Vector centerVector = centerLocation.toVector();
        for (int i = 0; i < seatPositions.size(); i++) {
            Entity entity = entities.get(i + 1);
            Vector requiredVector = centerVector.clone().add(seatPositions.get(i)).subtract(entity.getLocation().toVector()).rotateAroundY(getAngle(center));
            if (requiredVector.getX() > 0.075 || requiredVector.getY() > 0.075 || requiredVector.getZ() > 0.075) {
                entity.setVelocity(requiredVector);
                System.out.println("adjusting -> " + requiredVector);
            }
        }
    }

    private static void updateRPM(SpawnedCarData spawnedCarData) {
        CarData carData = spawnedCarData.getCarData();
        double RPMIncrease = carData.getRPMIncreasePerGear().get(spawnedCarData.getCurrentGear() - 1);
        spawnedCarData.setCurrentRPM(spawnedCarData.getCurrentRPM() + RPMIncrease);
        if (spawnedCarData.getCurrentRPM() > carData.getRPMs().get(2)) {
            RPMTicker.add(spawnedCarData);
        }
    }

    /**
     * @param centerArmorStand center entity of the vehicle
     * @param angle angle must be in degrees
     */
    private static void setAngle(Entity centerArmorStand, double angle) {
        centerArmorStand.getPersistentDataContainer().set(Vehicles.vehicleSteerAngleKey, PersistentDataType.DOUBLE, Math.toRadians(angle));
    }

    /**
     * @param centerArmorStand center entity of the vehicle
     * @return returns the angle in radians
     */
    @SuppressWarnings("ConstantConditions")
    private static double getAngle(Entity centerArmorStand) {
        PersistentDataContainer persistentDataContainer = centerArmorStand.getPersistentDataContainer();
        return persistentDataContainer.has(Vehicles.vehicleSteerAngleKey, PersistentDataType.DOUBLE)
                ? persistentDataContainer.get(Vehicles.vehicleSteerAngleKey, PersistentDataType.DOUBLE)
                : Math.toRadians(0D);
    }
}
