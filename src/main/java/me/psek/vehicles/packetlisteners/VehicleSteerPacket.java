package me.psek.vehicles.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import me.psek.vehicles.Vehicles;

public class VehicleSteerPacket {
    private final Vehicles plugin;

    private void onVehicleSteerPacket() {
        plugin.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {

        });
    }

    public VehicleSteerPacket(Vehicles plugin) {
        this.plugin = plugin;
        onVehicleSteerPacket();
    }
}
