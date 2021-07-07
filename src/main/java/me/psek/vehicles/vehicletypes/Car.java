package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.api.RegisteringAPI;
import me.psek.vehicles.handlers.data.serializabledata.SerializableSpawnedCarData;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.MathUtils;
import me.psek.vehicles.utility.UUIDUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.*;

import static me.psek.vehicles.handlers.physics.CarPhysics.*;

public class Car implements IVehicle {

    public static final Map<UUID, Entity[]> movingCars = new HashMap<>();

    @Getter
    private static final Map<String, Builder> carSubTypes = new HashMap<>();

    private final List<SpawnedCarData> allSpawnedCars = new ArrayList<>();
    private final INMS NMSInstance;
    private final NamespacedKey childUUIDsKey;
    private final NamespacedKey centerUUIDKey;
    private final NamespacedKey vehicleSortClassNameKey;

    public Car(Vehicles plugin, INMS NMSInstance, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassNameKey, NamespacedKey childUUIDsKey) {
        this.NMSInstance = NMSInstance;
        this.centerUUIDKey = centerUUIDKey;
        this.vehicleSortClassNameKey = vehicleSortClassNameKey;
        this.childUUIDsKey = childUUIDsKey;
    }

    @Override
    public void spawn(Vehicles plugin, String name, Location centerLocation) {
        centerLocation.setYaw(0);
        Builder subCarData = carSubTypes.get(name);
        World world = Objects.requireNonNull(centerLocation.getWorld());
        ArmorStand center = world.spawn(centerLocation, ArmorStand.class);
        byte[] centerUUIDBytes = UUIDUtils.UUIDtoBytes(center.getUniqueId());
        List<Vector> seatPositions = subCarData.getSeatPositions();
        Entity[] children = new Entity[seatPositions.size()];
        byte[] steererUUID = null;
        applySpawnModifiers(center);
        center.setGravity(true);
        for (int i = 0; i < subCarData.getSeatCount(); i++) {
            ArmorStand seat = world.spawn(centerLocation.clone().add(seatPositions.get(i)), ArmorStand.class);
            seat.getPersistentDataContainer().set(centerUUIDKey, PersistentDataType.BYTE_ARRAY, centerUUIDBytes);
            seat.getPersistentDataContainer().set(vehicleSortClassNameKey, PersistentDataType.STRING, carSubTypes.get(name).getName());
            children[i] = seat;
            if (i == subCarData.steeringSeatIndex) {
                steererUUID = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            }
            applySpawnModifiers(seat);
        }
        String[] childStrings = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            childStrings[i] = children[i].getUniqueId().toString();
        }
        center.getPersistentDataContainer().set(childUUIDsKey, PersistentDataType.STRING, String.join(",", childStrings));
        center.getPersistentDataContainer().set(vehicleSortClassNameKey, PersistentDataType.STRING, carSubTypes.get(name).getName());
        Builder carSubType = carSubTypes.get(name);
        SpawnedCarData spawnedCarData =
                new SpawnedCarData(this, carSubType.getName(), UUIDUtils.UUIDtoBytes(center.getUniqueId()), children, steererUUID, subCarData.isElectric(), carSubType.getRPMs().get(1));
        allSpawnedCars.add(spawnedCarData);
        RegisteringAPI.registerSpawnedVehicle(spawnedCarData);
    }

    @Override
    public void movementHandler(Vehicles plugin, Entity vehicle, Player player, float forwards, float sideways, boolean flag1, boolean flag2) {
        Builder builder = carSubTypes.get(vehicle.getPersistentDataContainer().get(vehicleSortClassNameKey, PersistentDataType.STRING));
        SpawnedCarData spawnedCarData =
                (SpawnedCarData) DataAPI.getSpawnedVehicles().get(UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY)));
        //UUID centerUUID = UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));

        //todo refine this like... bruh wtf is this
        if (!spawnedCarData.isShifting()) {
            if (forwards > 0) {
                if (spawnedCarData.getCurrentGear() != 0) {
                    if (spawnedCarData.getCurrentSpeed() < 0) {
                        move(getBrakingDeceleration(builder.getBrakingForce(), builder.getVehicleMass()), NMSInstance, spawnedCarData, builder);
                    } else {
                        move(getForwardAcceleration(spawnedCarData, builder), NMSInstance, spawnedCarData, builder);
                    }
                } else {
                    //todo add some sounds + particles (which are configurable)
                    changeRPM(builder, spawnedCarData);
                }
            } else if (forwards < 0) {
                if (spawnedCarData.getCurrentSpeed() > 0) {
                    move(MathUtils.flipNumber(getBrakingDeceleration(builder.getBrakingForce(), builder.getVehicleMass())), NMSInstance, spawnedCarData, builder);
                } else {

                }
            }
        } else changeRPM(builder, spawnedCarData);

        if (sideways == 1) {

        }
    }

    @Override
    public List<SerializableSpawnedCarData> getSerializableData() {
        List<SerializableSpawnedCarData> tempList = new ArrayList<>();
        for (SpawnedCarData scd : allSpawnedCars) {
            Entity[] children = scd.getChildren();
            byte[][] bytes = new byte[children.length][2];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = UUIDUtils.UUIDtoBytes(children[i].getUniqueId());
            }
            tempList.add(new SerializableSpawnedCarData(scd.getCurrentSpeed(), scd.getName(), scd.getCenterUUID(),
                    scd.getVehicleType().getClass().getSimpleName().toLowerCase(), scd.getSteererUUID(), bytes,
                    scd.getCurrentGear(), scd.getGasAmount(), scd.isElectric(), scd.getCurrentRPM(), scd.getCurrentVector()));
        }
        return tempList;
    }

    @Override
    public Class<? extends Serializable> getSerializableClass() {
        return SerializableSpawnedCarData.class;
    }

    @Override
    public void loadFromData(Vehicles plugin, List<Object> input) {
        for (Object object : input) {
            SerializableSpawnedCarData data = (SerializableSpawnedCarData) object;
            byte[][] bytes = data.getChildUUIDs();
            Entity[] children = new Entity[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                children[i] = Bukkit.getEntity(UUIDUtils.bytesToUUID(bytes[i]));
            }
            SpawnedCarData spawnedCarData = new SpawnedCarData(this, data.getName(), data.getCenterUUID(), children, data.getSteererUUID(), data.isElectric(), data.getCurrentRPM());
            RegisteringAPI.registerSpawnedVehicle(spawnedCarData);
        }
    }

    public static void registerCarSubtype(String name, Builder builder) {
        carSubTypes.put(name, builder);
    }

    @SuppressWarnings("unused")
    public static void unregisterCarSubType(Builder builder) {
        carSubTypes.remove(builder.getName());
    }

    public static void move(double acceleration, INMS NMSInstance, SpawnedCarData spawnedCarData, Builder builder) {
        Entity centerEntity = Bukkit.getEntity(UUIDUtils.bytesToUUID(spawnedCarData.getCenterUUID()));
        if (centerEntity == null) {
            return;
        }
        double speed = spawnedCarData.getCurrentSpeed() / 3.6 + acceleration / 50;
        if (MathUtils.checkSignBitChange(spawnedCarData.getCurrentSpeed() / 3.6, speed) && spawnedCarData.getCurrentSpeed() / 3.6 != 0) {
            spawnedCarData.setCurrentSpeed(0);
            spawnedCarData.setCurrentRPM(builder.getRPMs().get(1));
            centerEntity.setVelocity(new Vector(0, 0, 0));
            List<Entity> entities = moveChildren(builder, centerEntity, spawnedCarData, NMSInstance);
            movingCars.remove(centerEntity.getUniqueId());
            if (entities == null) {
                return;
            }
            entities.forEach(entity -> entity.setGravity(true));
            return;
        }
        double yaw = Math.toRadians(centerEntity.getLocation().getYaw());
        Vector accelerationVector = new Vector(0, 0, speed).rotateAroundY(yaw);
        centerEntity.setGravity(true);
        centerEntity.setVelocity(accelerationVector);
        List<Entity> entities = moveChildren(builder, centerEntity, spawnedCarData, NMSInstance);
        if (entities == null) {
            return;
        }
        spawnedCarData.setCurrentSpeed(spawnedCarData.getCurrentSpeed()+acceleration/50*3.6);
        changeRPM(builder, spawnedCarData);
        TryAddMovingCar(centerEntity.getUniqueId(), entities);
    }

    //todo make dis better too lmfao
    private static void changeRPM(Car.Builder builder, SpawnedCarData spawnedCarData) {
        if (spawnedCarData.getCurrentGear() == 0) {
            return;
        }
        double engineRPM = getEngineRPM(spawnedCarData.getCurrentSpeed(),
                builder.getTireRadius(), builder.getGearRatios().get(0)*builder.getGearRatios().get(spawnedCarData.getCurrentGear()), builder.getRPMs().get(1));
        spawnedCarData.setCurrentRPM(engineRPM);
    }

    private static List<Entity> moveChildren(Car.Builder builder, Entity centerEntity, SpawnedCarData spawnedCarData, INMS NMSInstance) {
        double yaw = Math.toRadians(centerEntity.getLocation().getYaw());
        List<Vector> seatPositions = builder.getSeatPositions();
        Vector centerVector = centerEntity.getLocation().toVector();
        Entity[] children = spawnedCarData.getChildren();
        List<Entity> entities = new ArrayList<>();
        entities.add(centerEntity);
        entities.addAll(Arrays.asList(children));
        int i = 0;
        for (Entity entity : children) {
            if (entity == null || entity.isDead()) {
                entities.forEach(Entity::remove);
                movingCars.remove(centerEntity.getUniqueId());
                return null;
            }
            Vector requiredVector = centerVector.clone().add(seatPositions.get(i)).subtract(entity.getLocation().toVector()).rotateAroundY(yaw);
            entity.setGravity(true);
            NMSInstance.setNoClip(entity, true);
            entity.setVelocity(requiredVector);
            i++;
        }
        return entities;
    }

    private void applySpawnModifiers(ArmorStand armorStand) {
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(true);
        armorStand.setBasePlate(false);
        NMSInstance.setNoClip(armorStand, true);
    }

    private static void TryAddMovingCar(UUID centerUUID, List<Entity> entities) {
        if (movingCars.containsKey(centerUUID)) {
            return;
        }
        Entity[] entityArray = new Entity[entities.size()];
        for (int i = 0; i < entityArray.length; i++) {
            entityArray[i] = entities.get(i);
        }
        movingCars.put(centerUUID, entityArray);
    }

    private double getForwardAcceleration(SpawnedCarData spawnedCarData, Builder builder) {
        double engineTorque = getEngineTorque(builder.getHorsepower(), spawnedCarData.getCurrentRPM());
        List<Double> gearRatios = builder.getGearRatios();
        double wheelForce = getDrivetrainForce(gearRatios.get(spawnedCarData.getCurrentGear())*gearRatios.get(0),
                builder.getTireRadius(),
                builder.getVehicleMass(),
                engineTorque,
                0);
       return getAcceleration(wheelForce, builder.getVehicleMass())/20.0;
    }

    private void moveBackwards(UUID centerUUID, SpawnedCarData spawnedCarData, Builder builder) {

    }

    private void checkPositions() {

    }

    private void fixPositions() {

    }

    @Getter
    @lombok.Builder
    public static class Builder {
        private final String name;
        private final int drivetrainWheelCount;
        private final double horsepower;
        private final double brakingForce;
        private final int seatCount;
        private final int wheelCount;
        private final List<Vector> seatPositions;
        private final List<Vector> boundingBoxVectors;
        private final int gearCount;
        private final int shiftTime;
        private final double tireRadius;
        /*
         * 0: max RPM on meter
         * 1: min RPM
         * 2: red zone of RPM
         */
        private final List<Integer> RPMs;
        /*
         * note: should match the amount of gears - size of list = gears
         * each entry should be the amount the RPM increases per time they accelerate (in RPM)
         */
        private final int steeringSeatIndex;
        private final boolean isAutomatic;
        private final List<Double> gearRatios;
        private final double tirePressure;
        private final double vehicleMass;
        private final double gripFactor;
        private final int maxRedRPMTicks;
        private final boolean electric;
    }
}
