package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.handlers.data.serializabledata.SerializableSpawnedCarData;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.UUIDUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.*;

import static me.psek.vehicles.handlers.physics.CarPhysics.*;

public class Car implements IVehicle {
    //todo do this with MovementTicker
    public static final Map<Entity, List<UUID>> movingCars = new HashMap<>();

    private static final Map<String, Builder> carSubTypes = new HashMap<>();

    private final List<SpawnedCarData> allSpawnedCars = new ArrayList<>();
    private final INMS NMSInstance;
    private final NamespacedKey childUUIDsKey;
    private final NamespacedKey centerUUIDKey;
    private final NamespacedKey vehicleSortClassName;

    public Car(INMS NMSInstance, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassName, NamespacedKey childUUIDsKey) {
        this.NMSInstance = NMSInstance;
        this.centerUUIDKey = centerUUIDKey;
        this.vehicleSortClassName = vehicleSortClassName;
        this.childUUIDsKey = childUUIDsKey;
    }

    @Override
    public void spawn(Vehicles plugin, String name, Location centerLocation) {
        centerLocation.setYaw(0);
        Builder subCarData = carSubTypes.get(name);
        World world = Objects.requireNonNull(centerLocation.getWorld());
        ArmorStand center = world.spawn(centerLocation, ArmorStand.class);
        byte[] centerUUIDBytes = UUIDUtils.UUIDtoBytes(center.getUniqueId());
        applySpawnModifiers(center);
        List<Vector> seatPositions = subCarData.getSeatPositions();
        byte[][] childUUIDs = new byte[subCarData.getSeatCount()][2];
        byte[] steererUUID = null;
        for (int i = 0; i < subCarData.getSeatCount(); i++) {
            ArmorStand seat = world.spawn(centerLocation.clone().add(seatPositions.get(i)), ArmorStand.class);
            applySpawnModifiers(seat);
            seat.getPersistentDataContainer().set(centerUUIDKey, PersistentDataType.BYTE_ARRAY, centerUUIDBytes);
            seat.getPersistentDataContainer().set(vehicleSortClassName, PersistentDataType.STRING, carSubTypes.get(name).getName());
            childUUIDs[i] = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            if (i == subCarData.steeringSeatIndex) {
                steererUUID = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            }
        }
        String[] childStrings = new String[childUUIDs.length];
        for (int i = 0; i < childUUIDs.length; i++) {
            childStrings[i] = UUIDUtils.bytesToUUID(childUUIDs[i]).toString();
        }
        center.getPersistentDataContainer().set(childUUIDsKey, PersistentDataType.STRING, String.join(",", childStrings));
        Builder carSubType = carSubTypes.get(name);
        SpawnedCarData spawnedCarData =
                new SpawnedCarData(this, carSubType.getName(), UUIDUtils.UUIDtoBytes(center.getUniqueId()), childUUIDs, steererUUID, subCarData.isElectric(), carSubType.getRPMs().get(1));
        allSpawnedCars.add(spawnedCarData);
        plugin.getAPIHandler().getRegisteringAPI().registerSpawnedVehicle(spawnedCarData);
    }

    private void applySpawnModifiers(ArmorStand armorStand) {
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(true);
        armorStand.setBasePlate(false);
        NMSInstance.setNoClip(armorStand, true);
    }

    @Override
    public void movementHandler(Vehicles plugin, Entity vehicle, Player player, float forwards, float sideways, boolean flag1, boolean flag2) {
        Builder builder = carSubTypes.get(vehicle.getPersistentDataContainer().get(vehicleSortClassName, PersistentDataType.STRING));
        SpawnedCarData spawnedCarData =
                (SpawnedCarData) plugin.getAPIHandler().getSpawnedVehicles().get(UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY)));
        UUID centerUUID = UUIDUtils.bytesToUUID(vehicle.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
        //todo refine this like... bruh wtf is this
        if (forwards > 0) {
            if (spawnedCarData.getCurrentGear() == 0) {
                //todo add some sounds + particles (which are configurable)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You're in neutral!"));
            } else {
                if (spawnedCarData.getCurrentSpeed() < 0) {
                    brake(centerUUID, spawnedCarData, builder);
                } else {
                    moveForwards(centerUUID, spawnedCarData, builder);
                }
            }
        } else if (forwards != 0) {
            moveBackwards(centerUUID, spawnedCarData, builder);
        }

        if (sideways == 1) {

        }
    }

    private void moveForwards(UUID centerUUID, SpawnedCarData spawnedCarData, Builder builder) {
        double engineTorque = getEngineTorque(builder.getHorsepower(), spawnedCarData.getCurrentRPM());
        double frictionForce = getFrictionForce(spawnedCarData.getCurrentSpeed(),
                builder.getTirePressure(),
                builder.getWheelRadius(),
                9.81*builder.getVehicleMass())
                *builder.getDrivetrainWheelCount();
        List<Double> gearRatios = builder.getGearRatios();
        double wheelForce = getDriveWheelForce(builder.getDrivetrainWheelCount(),
                gearRatios.get(spawnedCarData.getCurrentGear())*gearRatios.get(0),
                builder.getWheelRadius(),
                builder.getVehicleMass(),
                engineTorque,
                frictionForce);
        /*System.out.println("hp: " + builder.getHorsepower() + ", RPMe: " + spawnedCarData.getCurrentRPM() + ", Te: " + engineTorque);
        System.out.println("Vc: " + spawnedCarData.getCurrentSpeed() + "Pt: " + builder.getTirePressure() + ", Rw: " + builder.getWheelRadius() + ", Mv: "
                + builder.getVehicleMass() + ", Fg: " + 9.81*builder.getVehicleMass() + ", WheelCount: " + builder.getDrivetrainWheelCount() + ", Fr: " + frictionForce);
        System.out.println("DriveTrainWheelAmount: " + builder.getWheelCount() + ", currentGear: " + spawnedCarData.getCurrentGear() + ", finalDriveRatio: " + gearRatios.get(0) +
                ", totalGearRatio: " + gearRatios.get(spawnedCarData.getCurrentGear())*gearRatios.get(0) + ", Rw: " + builder.getWheelRadius() + ", Mv: " + builder.getVehicleMass() +
                ", Te: " + engineTorque + ", Fr: " + frictionForce + ", Fw: " + wheelForce);*/
        double acceleration = getAcceleration(wheelForce, builder.getVehicleMass())/20D;
        byte[] centerUUIDBytes = UUIDUtils.UUIDtoBytes(centerUUID);
        byte[][] childUUIDs = new byte[builder.seatCount][2];
        for (SpawnedCarData sc : allSpawnedCars) {
            if (!Arrays.equals(sc.getCenterUUID(), centerUUIDBytes)) {
                continue;
            }
            childUUIDs = sc.getChildUUIDs();
        }
        Entity center = Bukkit.getEntity(centerUUID);
        if (center == null) {
            return;
        }
        double yaw = Math.toRadians(center.getLocation().getYaw());
        Vector accelerationVector = new Vector(acceleration, 0, 0).rotateAroundY(yaw);
        for (byte[] UUID : childUUIDs) {
            Entity entity = Bukkit.getEntity(UUIDUtils.bytesToUUID(UUID));
            if (entity == null) {
                return;
            }
            entity.setGravity(true);
            System.out.println(accelerationVector);
            entity.setVelocity(entity.getVelocity().clone().add(accelerationVector));
        }
    }

    private void moveBackwards(UUID centerUUID, SpawnedCarData spawnedCarData, Builder builder) {

    }

    private void brake(UUID centerUUID, SpawnedCarData spawnedCarData, Builder builder) {

    }

    @Override
    public UUID getCenterUUID(Entity entity) {
        return UUIDUtils.bytesToUUID(entity.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
    }

    public static void registerCarSubtype(String name, Builder builder) {
        carSubTypes.put(name, builder);
    }

    //todo move to me.psek.vehicles.api (also create dis lol)
    @SuppressWarnings("unused")
    public static void unregisterCarSubType(Builder builder) {
        carSubTypes.remove(builder.getName());
    }

    @Override
    public List<SerializableSpawnedCarData> getSerializableData() {
        List<SerializableSpawnedCarData> tempList = new ArrayList<>();
        for (SpawnedCarData scd : allSpawnedCars) {
            tempList.add(new SerializableSpawnedCarData(scd.getCurrentSpeed(), scd.getName(), scd.getCenterUUID(),
                    scd.getVehicleType().getClass().getSimpleName().toLowerCase(), scd.getSteererUUID(), scd.getChildUUIDs(),
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
            SpawnedCarData spawnedCarData = new SpawnedCarData(this, data.getName(), data.getCenterUUID(), data.getChildUUIDs(), data.getSteererUUID(), data.isElectric(), data.getCurrentRPM());
            plugin.getAPIHandler().getRegisteringAPI().registerSpawnedVehicle(spawnedCarData);
        }
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
        private final double wheelRadius;
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
