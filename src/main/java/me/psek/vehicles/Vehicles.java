package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.psek.vehicles.builders.CarData;
import me.psek.vehicles.packetlisteners.OnVehicleSteerPacket;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public final class Vehicles extends JavaPlugin {
    @Getter
    private static ProtocolManager protocolManager;
    @Getter
    private static Vehicles pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        registerPacketListeners();

        CarData carData = new CarData.Builder()
                .withName("Lada")
                .withId(0).withAccelerationSpeed(3.2)
                .withBrakingSpeed(0.3)
                .withBackwardsAccelerationSpeed(0.6)
                .withSeatCount(5)
                .withSeatPositions(new ArrayList(Arrays.asList(
                        new Vector(1,-0.5,1),
                        new Vector(-1,-0.5,1),
                        new Vector(1,-0.3,-1),
                        new Vector(0,-0.3,-1),
                        new Vector(-1,-0.3,-1) )))
                .withBumperPositions(new ArrayList(Arrays.asList(
                        new Vector(0, 0.2, 2),
                        new Vector(0, 0.15, -2) )))
                .build();
    }

    private void registerPacketListeners() {
        new OnVehicleSteerPacket();
    }
}
