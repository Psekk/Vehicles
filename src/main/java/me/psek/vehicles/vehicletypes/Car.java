package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.spawnedvehiclesdata.SpawnedCarData;
import me.psek.vehicles.utility.UUIDUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Car implements IVehicle {
    public static final List<SpawnedCarData> ALL_SPAWNED_CARS = new ArrayList<>();

    private static final List<Builder> carSubTypes = new ArrayList<>();
    private final INMS NMSInstance;

    public Car(INMS NMSInstance) {
        this.NMSInstance = NMSInstance;
    }

    @Override
    public void spawn(int id, Location centerLocation) {
        centerLocation.setYaw(0);
        Builder subCarData = carSubTypes.get(id);
        World world = Objects.requireNonNull(centerLocation.getWorld());
        ArmorStand center = world.spawn(centerLocation, ArmorStand.class);
        applySpawnModifiers(center);
        List<Vector> seatPositions = subCarData.getSeatPositions();
        byte[][] childUUIDs = new byte[subCarData.getSeatCount()][2];
        for (int i = 0; i < subCarData.getSeatCount(); i++) {
            ArmorStand seat = world.spawn(centerLocation.add(seatPositions.get(i)), ArmorStand.class);
            childUUIDs[i] = UUIDUtils.UUIDtoBytes(seat.getUniqueId());
            applySpawnModifiers(seat);
        }
        SpawnedCarData spawnedCarData = new SpawnedCarData(this, id, UUIDUtils.UUIDtoBytes(center.getUniqueId()), childUUIDs, subCarData.isElectric());
        ALL_SPAWNED_CARS.add(spawnedCarData);
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

    public static void unRegisterCarSubType(Builder builder) {
        carSubTypes.remove(builder);
    }

    @Override
    public List<SpawnedCarData> getSerializableData() {
        return ALL_SPAWNED_CARS;
    }

    @Override
    public Class<? extends Serializable> getSerializableClass() {
        return SpawnedCarData.class;
    }

    @Override
    public void move(int id, double length, Object direction) {

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
