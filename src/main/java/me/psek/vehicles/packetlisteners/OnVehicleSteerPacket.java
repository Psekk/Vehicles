package me.psek.vehicles.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.builders.CarData;
import me.psek.vehicles.enums.VehicleSteerDirection;
import me.psek.vehicles.events.VehicleSteerEvent;
import me.psek.vehicles.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

public class OnVehicleSteerPacket {
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private static final Vehicles PLUGIN_INSTANCE = Vehicles.getPluginInstance();
    private static final NamespacedKey VEHICLE_NAME_KEY = new NamespacedKey(PLUGIN_INSTANCE, "isSteeringSeat");

    static {
        Vehicles.getProtocolManager().addPacketListener(
                new PacketAdapter(Vehicles.getPluginInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            float sidewaysValue = event.getPacket().getFloat().read(0);
                            float forwardsValue = event.getPacket().getFloat().read(1);

                            Player player = event.getPlayer();
                            if (Utils.canDriveable(player)) {
                                Entity vehicleEntity = event.getPlayer().getVehicle();

                                VehicleSteerDirection direction = VehicleSteerDirection.FORWARD;
                                if (forwardsValue < 0) {
                                    direction = VehicleSteerDirection.BACKWARDS;
                                } else if (sidewaysValue < 0) {
                                    direction = VehicleSteerDirection.RIGHT;
                                } else if (sidewaysValue > 0) {
                                    direction = VehicleSteerDirection.LEFT;
                                }

                                System.out.println(direction.directionValue);

                                CarData carData = CarData.ALL_REGISTERED_CARS
                                        .get(vehicleEntity.getPersistentDataContainer().get(VEHICLE_NAME_KEY, PersistentDataType.STRING));

                                VehicleSteerEvent vehicleSteerEvent = new VehicleSteerEvent(carData, direction);
                                PLUGIN_MANAGER.callEvent(vehicleSteerEvent);
                                if (vehicleSteerEvent.isCancelled()) {
                                    return;
                                }


                            }
                        }
                    }
                });
    }
}
