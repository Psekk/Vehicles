package me.psek.vehicles.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class VehicleSteerPacket {
    private void onVehicleSteerPacket(Vehicles plugin, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassName) {
        plugin.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.STEER_VEHICLE) {
                    return;
                }
                Player player = event.getPlayer();
                if (!Objects.requireNonNull(player.getVehicle()).getPersistentDataContainer().has(centerUUIDKey, PersistentDataType.BYTE_ARRAY)) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                float sidewaysValue = packet.getFloat().read(0);
                float forwardsValue = packet.getFloat().read(1);
                boolean isJump = packet.getBooleans().read(0);
                boolean isExit = packet.getBooleans().read(1);
                String name = player.getVehicle().getPersistentDataContainer().get(vehicleSortClassName, PersistentDataType.STRING);
                IVehicle iVehicle = getVehicleInstance(name);
                iVehicle.movementHandler(forwardsValue, sidewaysValue, isJump, isExit);
            }
        });
    }

    private final Vehicles plugin;

    //todo add error checking etc for if the vehicle went bye bye
    private IVehicle getVehicleInstance(String name) {
        return this.plugin.getSubVehicleTypes().get(name);
    }

    public VehicleSteerPacket(Vehicles plugin, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassName) {
        onVehicleSteerPacket(plugin, centerUUIDKey, vehicleSortClassName);
        this.plugin = plugin;
    }
}
