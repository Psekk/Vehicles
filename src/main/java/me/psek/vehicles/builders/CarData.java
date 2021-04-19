package me.psek.vehicles.builders;

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
    private final List<Vector> bumperPositions;

    public static class Builder {
        private String name;
        private int id;
        private double accelerationSpeed;
        private double brakingSpeed;
        private double backwardsAccelerationSpeed;
        private int seatCount;
        private List<Vector> seatPositions;
        private List<Vector> bumperPositions;

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
        
        public Builder withBumperPositions(List<Vector> bumperPositions) {
            this.bumperPositions = bumperPositions;
            return this;
        }

        public CarData build() {
            return new CarData(name, id, accelerationSpeed, brakingSpeed, backwardsAccelerationSpeed, seatCount, seatPositions, bumperPositions);
        }
    }

    private CarData(String name, int id, double accelerationSpeed,
                    double backwardsAccelerationSpeed, double brakingSpeed,
                    int seatCount, List<Vector> seatPositions,
                    List<Vector> bumperLocations) {
        this.name = name;
        this.id = id;
        this.accelerationSpeed = accelerationSpeed;
        this.backwardsAccelerationSpeed = backwardsAccelerationSpeed;
        this.brakingSpeed = brakingSpeed;
        this.seatCount = seatCount;
        this.seatPositions = seatPositions;
        this.bumperPositions = bumperLocations;
    }
}
