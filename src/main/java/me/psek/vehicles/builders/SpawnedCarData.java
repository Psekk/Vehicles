package me.psek.vehicles.builders;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

public class SpawnedCarData {
    private final CarData carData;
    @Getter
    @Setter
    private Vector currentVector;
    @Getter
    @Setter
    private double currentSpeed;
    @Getter
    private int currentGear;

    public SpawnedCarData(CarData carData, Vector cVector, double cSpeed) {
        currentVector = cVector;
        currentSpeed = cSpeed;
        this.carData = carData;
    }

    public void tryShiftUp() {
        if (currentGear++ > carData.getGearCount()) {
            currentGear--;
        }
    }

    public void tryShiftDown() {
        if (currentGear-- < 0) {
            currentGear++;
        }
    }

    public void tryPutNeutral() {
        currentGear = 0;
    }
}
