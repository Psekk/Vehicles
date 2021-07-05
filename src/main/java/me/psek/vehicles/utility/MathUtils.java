package me.psek.vehicles.utility;

@SuppressWarnings("unused")
public class MathUtils {
    public static double flipNumber(double input) {
        return input*-1;
    }

    /**
     * precisionRoundNumber
     * @param precision the amount of decimals the value will be rounded to (for example 10000 = 4 decimals)
     * @param value input number
     * @return returns the rounded number
     */
    public static double precisionRoundNumber(double precision, double value) {
        return (double) Math.round(value * precision) / precision;
    }

    public static boolean checkSignBitChange(double oldNumber, double newNumber) {
        return (Math.signum(oldNumber*-1) == Math.signum(newNumber));
    }
}
