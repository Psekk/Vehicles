package me.psek.vehicles.vehicle.builders;

import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class CarData {
    public static final HashMap<String, CarData> ALL_REGISTERED_CARS = new HashMap<>();

    @Getter
    private final String name;
    @Getter
    private final int id;
    @Getter
    private final double accelerationSpeed;
    @Getter
    private final double brakingSpeed;
    @Getter
    private final double backwardsAccelerationSpeed;
    @Getter
    private final int seatCount;
    @Getter
    private final List<Vector> seatPositions;
    @Getter
    private final List<Vector> boundingBoxVectors;
    @Getter
    private final int gearCount;
    @Getter
    private final int shiftTime;
    @Getter
    /*
     * 0: max RPM on meter
     * 1: orange zone of RPM
     * 2: red zone of RPM
     */
    private final List<Integer> RPMs;
    @Getter
    /*
     * note: should match the amount of gears - size of list = gears
     * each entry should be the amount the RPM increases per time they accelerate (in RPM)
     */
    private final List<Double> RPMIncreasePerGear;
    @Getter
    private final int steeringSeatIndex;
    @Getter
    private final boolean isAutomaticShifting;
    @Getter
    private final List<Double> accelerationMultipliers;
    @Getter
    private final double gripFactor;
    @Getter
    private final int maxRedRPMTicks;

    public static class Builder {
        private String name;
        private int id;
        private double accelerationSpeed;
        private double brakingSpeed;
        private double backwardsAccelerationSpeed;
        private int seatCount;
        private List<Vector> seatPositions;
        private List<Vector> boundingBoxVectors;
        private int gearCount;
        private int shiftTime;
        private List<Integer> RPMs;
        private int steeringSeatIndex;
        private boolean isAutomaticShifting;
        private List<Double> accelerationMultipliers;
        private List<Double> RPMIncreasePerGear;
        private double gripFactor;
        private int maxRedRPMTicks;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withAccelerationSpeed(double accelerationSpeed) {
            this.accelerationSpeed = accelerationSpeed;
            return this;
        }

        public Builder withBrakingSpeed(double brakingSpeed) {
            this.brakingSpeed = brakingSpeed;
            return this;
        }

        public Builder withBackwardsAccelerationSpeed(double backwardsAccelerationSpeed) {
            this.backwardsAccelerationSpeed = backwardsAccelerationSpeed;
            return this;
        }

        public Builder withSeatCount(int seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public Builder withSeatPositions(List<Vector> seatPositions) {
            this.seatPositions = seatPositions;
            return this;
        }
        
        /*
         * 1: top right corner
         * 2: bottom left corner
         */
        public Builder withBoundingBoxVectors(List<Vector> boundingBoxVectors) {
            this.boundingBoxVectors = boundingBoxVectors;
            return this;
        }

        public Builder withGearCount(int gearCount) {
            this.gearCount = gearCount;
            return this;
        }

        public Builder withShiftTime(int shiftTime) {
            this.shiftTime = shiftTime;
            return this;
        }

        public Builder withRPMs(List<Integer> RPMs) {
            this.RPMs = RPMs;
            return this;
        }

        public Builder withSteeringSeatIndex(int steeringSeatIndex) {
            this.steeringSeatIndex = steeringSeatIndex;
            return this;
        }

        public Builder isAutomaticShifting(boolean isAS) {
            isAutomaticShifting = isAS;
            return this;
        }

        public Builder withAccelerationMultipliers(List<Double> accelerationMultipliers) {
            this.accelerationMultipliers = accelerationMultipliers;
            return this;
        }

        public Builder withRPMIncreasePerGear(List<Double> RPMIncreasePerGear) {
            this.RPMIncreasePerGear = RPMIncreasePerGear;
            return this;
        }

        public Builder withGripFactor(double gripFactor) {
            this.gripFactor = gripFactor;
            return this;
        }

        public Builder withMaxRedRPMTicks(int maxRedRPMTicks) {
            this.maxRedRPMTicks = maxRedRPMTicks;
            return this;
        }

        public CarData build() {
            return new CarData(name,
                    id,
                    accelerationSpeed,
                    brakingSpeed,
                    backwardsAccelerationSpeed,
                    seatCount,
                    seatPositions,
                    boundingBoxVectors,
                    gearCount,
                    shiftTime,
                    RPMs,
                    steeringSeatIndex,
                    isAutomaticShifting,
                    accelerationMultipliers,
                    RPMIncreasePerGear,
                    gripFactor,
                    maxRedRPMTicks);
        }
    }

    private CarData(String name, int id, double accelerationSpeed,
                    double backwardsAccelerationSpeed, double brakingSpeed,
                    int seatCount, List<Vector> seatPositions,
                    List<Vector> boundingBoxVectors, int gearCount,
                    int shiftTime, List<Integer> RPMs, int steeringSeatIndex,
                    boolean isAS, List<Double> aMultipliers,
                    List<Double> RPMIncreasePerGear, double gripFactor,
                    int maxRedRPMTicks) {
        this.name = name;
        this.id = id;
        this.accelerationSpeed = accelerationSpeed;
        this.backwardsAccelerationSpeed = backwardsAccelerationSpeed;
        this.brakingSpeed = brakingSpeed;
        this.seatCount = seatCount;
        this.seatPositions = seatPositions;
        this.boundingBoxVectors = boundingBoxVectors;
        this.gearCount = gearCount;
        this.shiftTime = shiftTime;
        this.RPMs = RPMs;
        this.steeringSeatIndex = steeringSeatIndex;
        this.isAutomaticShifting = isAS;
        this.accelerationMultipliers = aMultipliers;
        this.RPMIncreasePerGear = RPMIncreasePerGear;
        this.gripFactor = gripFactor;
        this.maxRedRPMTicks = maxRedRPMTicks;
    }
}
