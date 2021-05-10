package me.psek.vehicles.vehicletypes;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class Car implements IVehicle {
    //todo find a way to store all car sorts like Lamborghini, Mercedes etc in a list probs with ID based shit
    @Override
    public void spawn(int id, Location location) {

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
    }
}
