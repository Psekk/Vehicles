package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.UUIDUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Car implements IVehicle {
    private static final List<Builder> carSubTypes = new ArrayList<>();

    private final List<SpawnedCarData> allSpawnedCars = new ArrayList<>();
    private final INMS NMSInstance;

    public Car(INMS NMSInstance, Vehicles plugin) {
        this.NMSInstance = NMSInstance;
        registerNamespacedKeys(plugin);
    }

    @Override
    public void spawn(Vehicles plugin, int id, Location centerLocation) {
        centerLocation.setYaw(0);
        Builder subCarData = carSubTypes.get(id);
        World world = Objects.requireNonNull(centerLocation.getWorld());
        ArmorStand center = world.spawn(centerLocation, ArmorStand.class);
        byte[] centerUUIDBytes = UUIDUtils.UUIDtoBytes(center.getUniqueId());
        applySpawnModifiers(center);
        List<Vector> seatPositions = subCarData.getSeatPositions();
        byte[][] childUUIDs = new byte[subCarData.getSeatCount()][2];
        byte[] steererUUID = null;
        for (int i = 0; i < subCarData.getSeatCount(); i++) {
            ArmorStand seat = world.spawn(centerLocation.add(seatPositions.get(i)), ArmorStand.class);
            seat.getPersistentDataContainer().set(centerUUIDKey, PersistentDataType.BYTE_ARRAY, centerUUIDBytes);
            childUUIDs[i] = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            applySpawnModifiers(seat);
            if (i == subCarData.steeringSeatIndex) {
                steererUUID = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            }
        }
        SpawnedCarData spawnedCarData =
                new SpawnedCarData(this, id, carSubTypes.get(id).getName(), UUIDUtils.UUIDtoBytes(center.getUniqueId()), childUUIDs, steererUUID, subCarData.isElectric());
        allSpawnedCars.add(spawnedCarData);
        plugin.registerSpawnedVehicle(spawnedCarData);
    }

    @Override
    public void move(int id, double length, Object direction) {

    }

    @Override
    public int getId(String name) {
        AtomicInteger id = new AtomicInteger(-1);
        carSubTypes.forEach(c -> {
            if (c.name.equals(name)) {
                id.set(carSubTypes.indexOf(c));
            }
        });
        return id.get();
    }

    NamespacedKey centerUUIDKey;

    private void registerNamespacedKeys(Vehicles plugin) {
        centerUUIDKey = new NamespacedKey(plugin, "centerUUID");
    }

    @Override
    public UUID getCenterUUID(Entity entity) {
        return UUIDUtils.bytesToUUID(entity.getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
    }

    private void applySpawnModifiers(ArmorStand armorStand) {
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(true);
        armorStand.setBasePlate(false);
        NMSInstance.setNoClip(armorStand, true);
    }

    /**
     * registerCarSubtype
     * @param builder the built car subtype (note: order on should be equal to their id)
     */
    public static void registerCarSubtype(Builder builder) {
        carSubTypes.add(builder);
    }

    @SuppressWarnings("unused")
    public static void unRegisterCarSubType(Builder builder) {
        carSubTypes.remove(builder);
    }

    @Override
    public List<SpawnedCarData> getSerializableData() {
        return allSpawnedCars;
    }

    @Override
    public Class<? extends Serializable> getSerializableClass() {
        return SpawnedCarData.class;
    }

    private void checkPositions() {

    }

    private void fixPositions() {

    }

    @Getter
    @lombok.Builder
    public static class Builder {
        private final String name;
        private final double accelerationSpeed;
        private final double brakingSpeed;
        private final double backwardsAccelerationSpeed;
        private final int seatCount;
        private final List<Vector> seatPositions;
        private final List<Vector> boundingBoxVectors;
        private final int gearCount;
        private final int shiftTime;
        /*
         * 0: max RPM on meter
         * 1: orange zone of RPM
         * 2: red zone of RPM
         */
        private final List<Integer> RPMs;
        /*
         * note: should match the amount of gears - size of list = gears
         * each entry should be the amount the RPM increases per time they accelerate (in RPM)
         */
        private final List<Double> RPMIncreasePerGear;
        private final int steeringSeatIndex;
        private final boolean isAutomaticShifting;
        private final List<Double> accelerationMultipliers;
        private final double gripFactor;
        private final int maxRedRPMTicks;
        private final boolean electric;
    }
}
