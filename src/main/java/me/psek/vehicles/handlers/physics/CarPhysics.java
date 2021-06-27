package me.psek.vehicles.handlers.physics;

@SuppressWarnings("unused")
public class CarPhysics {
    /**
     * getEngineTorque
     * @param horsepower engine max horsepower
     * @param engineRPM current engine RPM (min)
     * @return engine torque (Nm)
     */
    public static double getEngineTorque(double horsepower, double engineRPM) {
        return 5252*horsepower/engineRPM*1.36;
    }

    /**
     * getDriveWheelForce
     * @param drivetrainWheelCount the amount of wheels driven by the drivetrain
     * @param totalGearRatio all the gear ratios that affect the drivetrain multiplied
     * @param wheelRadius distance from the outer edge to the center of the wheel (m)
     * @param vehicleMass weight of the vehicle (kg)
     * @param engineTorque the torque (Nm) the engine provides at the given RPM (min)
     * @return force (N) applied to 1 wheel driven by the drivetrain
     */
    public static double getDriveWheelForce(int drivetrainWheelCount, double totalGearRatio, double wheelRadius, double vehicleMass, double engineTorque, double frictionForce) {
        return engineTorque*totalGearRatio/drivetrainWheelCount/wheelRadius-frictionForce;
    }

    /**
     * getMaxGrip (per tire)
     * @param wheelCount amount of wheels that touch the ground
     * @param gravityForce acceleration of gravity acting on the car (m/s²)
     * @param tireGripFactor factor of tire grip
     * @return max force (N) each tire can handle before losing grip
     */
    public static double getMaxGrip(int wheelCount, double gravityForce, double tireGripFactor) {
        return tireGripFactor*(tireGripFactor/wheelCount);
    }

    /**
     * getFrictionForce (per tire)
     * @param velocity speed of the vehicle (km/h)
     * @param tirePressure pressure in the tire (bar)
     * @param wheelRadius distance from the edge to the center (m)
     * @param gravityForce force of gravity acting on the car (N)
     * @return friction force working against the car (N)
     */
    public static double getFrictionForce(double velocity, double tirePressure, double wheelRadius, double gravityForce) {
        double Crr = 0.005+1/tirePressure*(0.001+0.0095*Math.pow(velocity/100.0, 2));
        return Crr*gravityForce/wheelRadius;
    }

    /**
     * getCarAcceleration
     * @param accelerationForce all the forces combined acting on the vehicle (N)
     * @param mass weight of the vehicle (kg)
     * @return acceleration of the vehicle (m/s²)
     */
    public static double getAcceleration(double accelerationForce, double mass) {
        System.out.println(accelerationForce + " // " + mass);
        return accelerationForce/mass;
    }
}
