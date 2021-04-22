package me.psek.vehicles.vehicle;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.builders.SpawnedCarData;
import me.psek.vehicles.vehicle.enums.VehicleSteerDirection;
import me.psek.vehicles.vehicle.events.VehicleSteerEvent;
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

public class Actions {
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

    public void spawn(Location location, String vehicleName) {
        CarData carData = CarData.ALL_REGISTERED_CARS.get(vehicleName);

        location.setYaw(0);
        location.setPitch(0);

        ArmorStand centerArmorStand = location.getWorld().spawn(location, ArmorStand.class);
        centerArmorStand.setGravity(false);
        centerArmorStand.setInvulnerable(true);
        centerArmorStand.setInvisible(true);
        centerArmorStand.setBasePlate(false);
        centerArmorStand.setCustomName("center seat");

        List<Entity> entities = new ArrayList<>();
        entities.add(centerArmorStand);
        String centerArmorStandUUID = centerArmorStand.getUniqueId().toString();
        Vector[] seatVectors = carData.getSeatPositions().toArray(new Vector[0]);
        int steeringSeatIndex = carData.getSteeringSeatIndex();
        String[] childUUIDS = new String[carData.getSeatCount() + 2];
        List<Location> armorStandLocations = new ArrayList<>();
        armorStandLocations.add(centerArmorStand.getLocation());
        for (int i = 0; i < carData.getSeatCount(); i++) {
            ArmorStand armorStand = location.getWorld().spawn(location.clone().add(seatVectors[i]), ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.getPersistentDataContainer().set(Vehicles.uuidOfCenterAsKey, PersistentDataType.STRING, centerArmorStandUUID);
            armorStand.setInvulnerable(true);
            armorStand.setInvisible(false);
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

        SpawnedCarData.ALL_SPAWNED_CAR_DATA.put(centerArmorStand.getUniqueId().toString(),
                new SpawnedCarData(carData,
                        armorStandLocations,
                        0D,
                        0D,
                        entities,
                        1));
    }

    public boolean tryShiftUp(SpawnedCarData spawnedCarData) {
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear++ <= spawnedCarData.getCarData().getGearCount()) {
            spawnedCarData.setCurrentGear(currentGear);
            return true;
        }
        return false;
    }

    public boolean tryShiftDown(SpawnedCarData spawnedCarData) {
        int currentGear = spawnedCarData.getCurrentGear();
        if (currentGear-- >= 0) {
            spawnedCarData.setCurrentGear(currentGear);
            return true;
        }
        return false;
    }

    public boolean tryPutNeutral(SpawnedCarData spawnedCarData) {
        spawnedCarData.setCurrentGear(0);
        return true;
    }

    private CarData getCarData(Entity vehicleEntity) {
        PersistentDataContainer persistentDataContainer = vehicleEntity.getPersistentDataContainer();
        if (persistentDataContainer.has(Vehicles.vehicleNameKey, PersistentDataType.STRING)) {
            return CarData.ALL_REGISTERED_CARS.get(persistentDataContainer.get(Vehicles.vehicleNameKey, PersistentDataType.STRING));
        }
        return null;
    }

    public void steerVehicle(SpawnedCarData spawnedCarData, VehicleSteerDirection direction) {
        System.out.println(spawnedCarData == null);
        int currentGear = spawnedCarData.getCurrentGear();
        CarData carData = spawnedCarData.getCarData();

        /*VehicleSteerEvent vehicleSteerEvent = new VehicleSteerEvent(carData, direction);
        PLUGIN_MANAGER.callEvent(vehicleSteerEvent);
        if (vehicleSteerEvent.isCancelled()) {
            return;
        }*/

        switch (direction.directionValue) {
            //forwards
            case 0:
                if (currentGear == 0) {
                    //todo maybe add sounds ;)
                    return;
                }
                Vector currentVelocityVector = spawnedCarData.getEntities().get(0).getVelocity();
                if (currentVelocityVector.getZ() >= 0) {
                    System.out.println("gassing");
                    double RPMIncrease = carData.getRPMIncreasePerGear().get(currentGear - 1);
                    spawnedCarData.setCurrentRPM(spawnedCarData.getCurrentRPM() + RPMIncrease);
                    if (spawnedCarData.getCurrentRPM() > carData.getRPMs().get(2)) {
                        RPMTicker.addRedRPM(spawnedCarData.getEntities().get(0).getUniqueId(), spawnedCarData);
                    }

                    double accelerationAmount = carData.getAccelerationSpeed() * carData.getAccelerationMultipliers().get(spawnedCarData.getCurrentGear() - 1);
                    Vector modifiedCurrentVector = currentVelocityVector.clone().setZ(currentVelocityVector.getZ() + accelerationAmount);
                    Entity centerArmorStand = spawnedCarData.getEntities().get(0);
                    centerArmorStand.setVelocity(modifiedCurrentVector);

                    List<Entity> seatEntities = spawnedCarData.getEntities().subList(1, spawnedCarData.getEntities().size());
                    for (Entity seatEntity : seatEntities) {
                       seatEntity.teleport(centerArmorStand.getLocation().add(carData.getSeatPositions().get(seatEntities.indexOf(seatEntity))));
                    }
                    //remove after testing and addition of moving ticker
                    centerArmorStand.setVelocity(new Vector(0, 0, 0));
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
    }
}
