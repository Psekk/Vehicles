package me.psek.vehicles.enums;

public enum VehicleSteerDirection {
    FORWARD(0),
    BACKWARDS(1),
    RIGHT(2),
    LEFT(3);

    public final int directionValue;
    VehicleSteerDirection(int directionValue) {
        this.directionValue = directionValue;
    }
}
