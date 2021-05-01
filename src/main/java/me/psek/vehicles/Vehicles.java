package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.psek.vehicles.listeners.OnPlayerQuit;
import me.psek.vehicles.nms.Mediator;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.commands.VehiclesCommand;
import me.psek.vehicles.listeners.OnPlayerEntityInteract;
import me.psek.vehicles.vehicle.packetlisteners.OnVehicleSteerPacket;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.logging.Level;

public final class Vehicles extends JavaPlugin {
    @Getter
    private static ProtocolManager protocolManager;
    @Getter
    private static Vehicles pluginInstance;

    public static NamespacedKey uuidOfCenterAsKey;
    public static NamespacedKey uuidOfChildrenAsKey;
    public static NamespacedKey isSteeringSeatKey;
    public static NamespacedKey isBackBoundingBoxKey;
    public static NamespacedKey vehicleNameKey;

    @Override
    public void onEnable() {
        pluginInstance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        //todo create the custom bounding box stuff that checks for hits etc just do 2 points in top right and bottom left and check if smth intersects it and kaboom

        CarData.ALL_REGISTERED_CARS.put("lada", new CarData.Builder()
                .withName("lada")
                .withId(0)
                .withAccelerationSpeed(0.1)
                .withBrakingSpeed(0.3)
                .withBackwardsAccelerationSpeed(0.05)
                .withSeatCount(5)
                .withSeatPositions(Arrays.asList(
                        new Vector(1,-0.5,1),
                        new Vector(-1,-0.5,1),
                        new Vector(1,-0.3,-1),
                        new Vector(0,-0.3,-1),
                        new Vector(-1,-0.3,-1) ))
                .withBoundingBoxVectors(Arrays.asList(
                        new Vector(2.5, 1, 2.3),
                        new Vector(-2.5, 0.15, -2.35) ))
                .withGearCount(5)
                .withRPMs(Arrays.asList(
                        9000,
                        6000,
                        7500
                ))
                .withShiftTime(15)
                .withSteeringSeatIndex(0)
                .withRPMIncreasePerGear(Arrays.asList(
                        25D,
                        22.5,
                        17D,
                        14D,
                        12D
                ))
                .withAccelerationMultipliers(Arrays.asList(
                        1D,
                        0.85,
                        0.7,
                        0.55,
                        0.35
                ))
                .isAutomaticShifting(false)
                .withGripFactor(0.78921)
                .withMaxRedRPMTicks(45)
                .build());

        registerKeys();
        registerPacketListeners();
        registerCommands();
        registerListeners(new OnPlayerEntityInteract(), new OnPlayerQuit());
    }

    private void registerPacketListeners() {
        new OnVehicleSteerPacket();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        this.getCommand("vehicles").setExecutor(new VehiclesCommand());
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerKeys() {
        uuidOfCenterAsKey = new NamespacedKey(this, "uuidOfCenterAS");
        uuidOfChildrenAsKey = new NamespacedKey(this, "uuidOfChildrenAS");
        isSteeringSeatKey = new NamespacedKey(this, "isSteeringSeat");
        isBackBoundingBoxKey = new NamespacedKey(this, "isBackBoundingBox");
        vehicleNameKey = new NamespacedKey(this, "vehicleName");
    }
}
