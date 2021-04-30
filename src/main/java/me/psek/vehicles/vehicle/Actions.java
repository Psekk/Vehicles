package me.psek.vehicles.vehicle;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.nms.INms;
import me.psek.vehicles.nms.Mediator;
import me.psek.vehicles.utils.Utils;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.builders.SpawnedCarData;
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
        centerArmorStand.setInvulnerable(true);
        centerArmorStand.setVisible(false);
        centerArmorStand.setBasePlate(false);
        centerArmorStand.setCustomName("center seat");

        List<Entity> entities = new ArrayList<>();
        entities.add(centerArmorStand);
        byte[] centerArmorStandUUID = Utils.UUIDAsBytes(centerArmorStand.getUniqueId());
        Vector[] seatVectors = carData.getSeatPositions().toArray(new Vector[0]);
        int steeringSeatIndex = carData.getSteeringSeatIndex();
        String[] childUUIDS = new String[carData.getSeatCount() + 2];
        List<Location> armorStandLocations = new ArrayList<>();
        armorStandLocations.add(centerArmorStand.getLocation());
        for (int i = 0; i < carData.getSeatCount(); i++) {
            ArmorStand armorStand = location.getWorld().spawn(location.clone().add(seatVectors[i]), ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.getPersistentDataContainer().set(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY, centerArmorStandUUID);
            armorStand.setInvulnerable(true);
            armorStand.setVisible(true);
            armorStand.setBasePlate(false);
            childUUIDS[i] = armorStand.getUniqueId().toString();
            armorStandLocations.add(armorStand.getLocation());
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
                armorStandLocations,
                0D,
                75D,
                entities,
                1);
        SpawnedCarData.ALL_SPAWNED_CAR_DATA.put(centerArmorStand.getUniqueId(), spawnedCarData);
        MoveTicker.add(spawnedCarData);
    }

    public static boolean tryShiftUp(SpawnedCarData spawnedCarData) {
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear++ <= spawnedCarData.getCarData().getGearCount()) {
            spawnedCarData.setCurrentGear(currentGear);
            return true;
        }
        return false;
    }

    public static boolean tryShiftDown(SpawnedCarData spawnedCarData) {
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear-- >= 0) {
            spawnedCarData.setCurrentGear(currentGear);
            return true;
        }
        return false;
    }

    public static boolean tryPutNeutral(SpawnedCarData spawnedCarData) {
        spawnedCarData.setCurrentGear(0);
        return true;
    }

    private static CarData getCarData(Entity vehicleEntity) {
        PersistentDataContainer persistentDataContainer = vehicleEntity.getPersistentDataContainer();
        if (persistentDataContainer.has(Vehicles.vehicleNameKey, PersistentDataType.STRING)) {
            return CarData.ALL_REGISTERED_CARS.get(persistentDataContainer.get(Vehicles.vehicleNameKey, PersistentDataType.STRING));
        }
        return null;
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
                Vector currentVelocityVector = spawnedCarData.getEntities().get(0).getVelocity();
                if (spawnedCarData.getCurrentSpeed() >= 0) {
                    double RPMIncrease = carData.getRPMIncreasePerGear().get(currentGear - 1);
                    spawnedCarData.setCurrentRPM(spawnedCarData.getCurrentRPM() + RPMIncrease);
                    if (spawnedCarData.getCurrentRPM() > carData.getRPMs().get(2)) {
                        RPMTicker.add(spawnedCarData);
                    }

                    double accelerationAmount = carData.getAccelerationSpeed() * carData.getAccelerationMultipliers().get(spawnedCarData.getCurrentGear() - 1) / 10;
                    Vector modifiedCurrentVector = currentVelocityVector.setZ(currentVelocityVector.getZ() + accelerationAmount);
                    spawnedCarData.setCurrentSpeed(spawnedCarData.getCurrentSpeed() + accelerationAmount);
                    for (Entity seatEntity : spawnedCarData.getEntities()) {
                        seatEntity.setGravity(true);
                        NMS_INSTANCE.setNoClip(seatEntity, true);
                        seatEntity.setVelocity(modifiedCurrentVector);
                    }
                } else {
                    System.out.println("braking");
                }
            //backwards
            case 1:

            //right
            case 2:

            //left
            case 3:
        }
        spawnedCarData.setControlling(false);
    }
}
