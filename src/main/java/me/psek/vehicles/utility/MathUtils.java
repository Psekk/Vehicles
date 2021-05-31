package me.psek.vehicles.utility;

@SuppressWarnings("unused")
public class MathUtils {
    /**
     * @param deltaV velocity in the given timespan
     * @param deltaT the time measured in seconds
     * @return the acceleration in m/s squared
     */
    public static double getAcceleration(double deltaV, double deltaT) {
        return deltaV/deltaT;
    }

    public static double getDeltaV(double oldSpeed, double newSpeed) {
        return newSpeed-oldSpeed;
    }
}
