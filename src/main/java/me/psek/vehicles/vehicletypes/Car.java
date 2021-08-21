package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.api.RegisteringAPI;
import me.psek.vehicles.handlers.data.serializabledata.SerializableCarEntityData;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.vehicleentites.CarEntity;
import me.psek.vehicles.psekutils.MathUtils;
import me.psek.vehicles.psekutils.UUIDUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

import static me.psek.vehicles.handlers.physics.CarPhysics.*;

public class Car implements IVehicle {
    public static final Map<UUID, Entity[]> movingCars = new HashMap<>();

    @Getter
    private static final Map<String, Builder> carSubTypes = new HashMap<>();

    private final List<CarEntity> allSpawnedCars = new ArrayList<>();
    private final INMS NMSInstance;
    private final NamespacedKey childUUIDsKey;
    private final NamespacedKey centerUUIDKey;
    private final NamespacedKey vehicleSortClassNameKey;

    public Car(INMS NMSInstance, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassNameKey, NamespacedKey childUUIDsKey) {
        this.NMSInstance = NMSInstance;
        this.centerUUIDKey = centerUUIDKey;
        this.vehicleSortClassNameKey = vehicleSortClassNameKey;
        this.childUUIDsKey = childUUIDsKey;
    }

    @Override
    public void spawn(Vehicles plugin, String name, Location centerLocation) {
        Builder carData = carSubTypes.get(name);
        World world = Objects.requireNonNull(centerLocation.getWorld());
        ArmorStand center = world.spawn(centerLocation, ArmorStand.class);
        byte[] centerUUIDAsBytes = UUIDUtils.UUIDtoBytes(center.getUniqueId());
        Vector[] seatVectors = carData.getSeatVectors();
        Entity[] children = new Entity[seatVectors.length];
        @Nullable UUID steererUUID = null;
        applySpawnModifiers(center);
        center.setGravity(true);
        for (int i = 0; i < carData.getSeatCount(); i++) {
            ArmorStand seatEntity = world.spawn(centerLocation.clone().add(seatVectors[i]), ArmorStand.class);
            seatEntity.getPersistentDataContainer().set(centerUUIDKey, PersistentDataType.BYTE_ARRAY, centerUUIDAsBytes);
            seatEntity.getPersistentDataContainer().set(vehicleSortClassNameKey, PersistentDataType.STRING, carSubTypes.get(name).getName());
            children[i] = seatEntity;
            if (i == carData.steeringSeatIndex) {
                steererUUID = seatEntity.getUniqueId();
            }
            applySpawnModifiers(seatEntity);
        }
        String[] childStrings = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            childStrings[i] = children[i].getUniqueId().toString();
        }
        center.getPersistentDataContainer().set(childUUIDsKey, PersistentDataType.STRING, String.join(",", childStrings));
        center.getPersistentDataContainer().set(vehicleSortClassNameKey, PersistentDataType.STRING, carSubTypes.get(name).getName());
        Builder carSubType = carSubTypes.get(name);
        CarEntity spawnedCarData =
                new CarEntity(this, carSubType.getName(), center.getUniqueId(), center, children, steererUUID, carData.isElectric(), carSubType.getRPMs()[1]);
        allSpawnedCars.add(spawnedCarData);
        RegisteringAPI.registerSpawnedVehicle(spawnedCarData);
    }

    @Override
    public void movementHandler(Vehicles plugin, Entity vehicle, Player player, float forwards, float sideways, boolean flag1, boolean flag2) {
        Builder builder = carSubTypes.get(vehicle.getPersistentDataContainer().get(vehicleSortClassNameKey, PersistentDataType.STRING));
        CarEntity spawnedCarData =
                (CarEntity) DataAPI.getSpawnedVehicles().get(UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY)));

        if (sideways != 0) {
            moveSideways(sideways, spawnedCarData);
        }

        //todo refine this
        if (!spawnedCarData.isShifting()) {
            if (forwards > 0) {
                if (spawnedCarData.getCurrentGear() != 0) {
                    if (spawnedCarData.getCurrentSpeed() < 0) {
                        moveStraight(getBrakingDeceleration(builder.getBrakingForce(), builder.getVehicleMass()), NMSInstance, spawnedCarData, builder);
                    } else {
                        moveStraight(getForwardAcceleration(spawnedCarData, builder), NMSInstance, spawnedCarData, builder);
                    }
                } else {
                    //todo add some sounds + particles (which are configurable)
                    changeRPM(builder, spawnedCarData);
                }
            } else if (forwards < 0) {
                if (spawnedCarData.getCurrentSpeed() > 0) {
                    moveStraight(MathUtils.flipNumber(getBrakingDeceleration(builder.getBrakingForce(), builder.getVehicleMass())), NMSInstance, spawnedCarData, builder);
                } else {

                }
            }
        } else changeRPM(builder, spawnedCarData);
    }

    @Override
    public Serializable[] getSerializableData() {
        SerializableCarEntityData[] temporarilyData = new SerializableCarEntityData[allSpawnedCars.size()];
        int i = 0;
        for (CarEntity spawnedCarData : allSpawnedCars) {
            Entity[] children = spawnedCarData.getChildren();
            byte[][] bytes = new byte[children.length][2];
            for (int j = 0; j < bytes.length; j++) {
                bytes[j] = UUIDUtils.UUIDtoBytes(children[j].getUniqueId());
            }
            temporarilyData[i] = new SerializableCarEntityData(spawnedCarData.getCurrentSpeed(), spawnedCarData.getName(), UUIDUtils.UUIDtoBytes(spawnedCarData.getCenterUUID()),
                    spawnedCarData.getVehicleType().getClass().getSimpleName().toLowerCase(), UUIDUtils.UUIDtoBytes(spawnedCarData.getSteererUUID()), bytes,
                    spawnedCarData.getCurrentGear(), spawnedCarData.getGasAmount(), spawnedCarData.isElectric(), spawnedCarData.getCurrentRPM(), spawnedCarData.getAngle());
            i++;
        }
        return temporarilyData;
    }

    @Override
    public Serializable getSerializableClass() {
        return SerializableCarEntityData.class;
    }

    @Override
    public void loadFromData(Vehicles plugin, Object[] input) {
        for (Object object : input) {
            SerializableCarEntityData data = (SerializableCarEntityData) object;
            byte[][] childUUIDBytes = data.getChildUUIDBytes();
            Entity[] children = new Entity[childUUIDBytes.length];
            for (int i = 0; i < childUUIDBytes.length; i++) {
                children[i] = Bukkit.getEntity(UUIDUtils.bytesToUUID(childUUIDBytes[i]));
            }
            UUID centerUUID = UUIDUtils.bytesToUUID(data.getCenterUUID());
            CarEntity spawnedCarData = new CarEntity(this, data.getName(), centerUUID, Bukkit.getEntity(centerUUID), children,
                    UUIDUtils.bytesToUUID(data.getSteererUUID()), data.isElectric(), data.getCurrentRPM());
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

    public void moveStraight(double acceleration, INMS NMSInstance, CarEntity spawnedCarData, Builder builder) {
        Entity centerEntity = spawnedCarData.getCenterEntity();
        if (centerEntity == null) {
            RegisteringAPI.unregisterSpawnedVehicle(spawnedCarData.getCenterUUID());
            return;
        }
        double speed = spawnedCarData.getCurrentSpeed() / 3.6 + acceleration/50;
        if (MathUtils.checkSignBitChange(spawnedCarData.getCurrentSpeed() / 3.6, speed) && spawnedCarData.getCurrentSpeed() / 3.6 != 0) {
            spawnedCarData.setCurrentSpeed(0);
            spawnedCarData.setCurrentRPM(builder.getRPMs()[1]);
            centerEntity.setVelocity(new Vector(0, 0, 0));
            Entity[] entities = moveChildren(builder, centerEntity, spawnedCarData, NMSInstance);
            movingCars.remove(centerEntity.getUniqueId());
            if (entities == null) {
                RegisteringAPI.unregisterSpawnedVehicle(spawnedCarData.getCenterUUID());
                return;
            }
            for (Entity entity : entities) {
                entity.setGravity(true);
            }
            return;
        }
        double yaw = Math.toRadians(spawnedCarData.getAngle());
        Vector accelerationVector = new Vector(0, 0, speed).rotateAroundY(yaw);
        centerEntity.setGravity(true);
        centerEntity.setVelocity(accelerationVector);
        Entity[] entities = moveChildren(builder, centerEntity, spawnedCarData, NMSInstance);
        if (entities == null) {
            tryDeleteCar(spawnedCarData);
            return;
        }
        spawnedCarData.setCurrentSpeed(spawnedCarData.getCurrentSpeed()+acceleration/50*3.6);
        changeRPM(builder, spawnedCarData);
        TryAddMovingCar(centerEntity.getUniqueId(), entities);
    }

    public void moveSideways(double sidewaysValue, CarEntity spawnedCarData) {
        if (spawnedCarData.getCurrentSpeed() < 0.15 && spawnedCarData.getCurrentSpeed() > -0.15) {
            return;
        }
        spawnedCarData.setAngle(sidewaysValue > 0 ? spawnedCarData.getAngle() + 1.25 : spawnedCarData.getAngle() - 1.25);
    }

    private Entity[] moveChildren(Car.Builder builder, Entity centerEntity, CarEntity spawnedCarData, INMS NMSInstance) {
        double yaw = Math.toRadians(spawnedCarData.getAngle());
        Vector[] seatPositions = builder.getSeatVectors();
        Vector centerVector = centerEntity.getLocation().toVector();
        Entity[] children = spawnedCarData.getChildren();
        Entity[] entities = (Entity[]) ArrayUtils.addAll(new Entity[] { centerEntity }, children);
        int i = 0;
        for (Entity entity : children) {
            if (entity == null || entity.isDead()) {
                tryDeleteCar(spawnedCarData);
                return null;
            }
            Vector requiredVector = centerVector.clone().add(seatPositions[i].clone().rotateAroundY(yaw)).subtract(entity.getLocation().toVector());
            entity.setGravity(true);
            NMSInstance.setNoClip(entity, true);
            entity.setVelocity(requiredVector);
            i++;
        }
        return entities;
    }

    public void tryDeleteCar(CarEntity spawnedCarData) {
        Entity centerEntity = spawnedCarData.getCenterEntity();
        Entity[] children = spawnedCarData.getChildren();
        Entity[] entities = (Entity[]) ArrayUtils.addAll(new Entity[] { centerEntity }, children);
        for (Entity e : entities) {
            e.remove();
        }
        movingCars.remove(centerEntity.getUniqueId());
        RegisteringAPI.unregisterSpawnedVehicle(spawnedCarData.getCenterUUID());
    }

    //todo improve this a lot
    private void changeRPM(Car.Builder builder, CarEntity spawnedCarData) {
        if (spawnedCarData.getCurrentGear() == 0) {
            return;
        }
        double engineRPM = getEngineRPM(spawnedCarData.getCurrentSpeed(),
                builder.getTireRadius(), builder.getGearRatios()[0]*builder.getGearRatios()[spawnedCarData.getCurrentGear()], builder.getRPMs()[1]);
        spawnedCarData.setCurrentRPM(engineRPM);
    }

    private void applySpawnModifiers(ArmorStand armorStand) {
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(true);
        armorStand.setBasePlate(false);
        NMSInstance.setNoClip(armorStand, true);
    }

    private void TryAddMovingCar(UUID centerUUID, Entity[] entities) {
        if (movingCars.containsKey(centerUUID)) {
            return;
        }
        Entity[] entityArray = new Entity[entities.length];
        System.arraycopy(entities, 0, entityArray, 0, entityArray.length);
        movingCars.put(centerUUID, entityArray);
    }

    private double getForwardAcceleration(CarEntity spawnedCarData, Builder builder) {
        double engineTorque = getEngineTorque(builder.getHorsepower(), spawnedCarData.getCurrentRPM());
        double[] gearRatios = builder.getGearRatios();
        double wheelForce = getDrivetrainForce(gearRatios[spawnedCarData.getCurrentGear()]*gearRatios[0],
                builder.getTireRadius(),
                builder.getVehicleMass(),
                engineTorque,
                0);
       return getAcceleration(wheelForce, builder.getVehicleMass())/20.0;
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
        private final Vector[] seatVectors;
        //todo add bounding boxes for collision detection
        private final Vector[] boundingBoxVectors;
        //todo add tire armor stand placement and turning
        private final Vector[] tireVectors;
        private final int gearCount;
        private final int shiftTime;
        private final double tireRadius;
        private final double[] RPMs;
        private final int steeringSeatIndex;
        private final boolean isAutomatic;
        private final double[] gearRatios;
        private final double tirePressure;
        private final double vehicleMass;
        private final double gripFactor;
        private final int maxRedRPMTicks;
        private final boolean electric;
        private final boolean RPMLimiter;
    }
}
