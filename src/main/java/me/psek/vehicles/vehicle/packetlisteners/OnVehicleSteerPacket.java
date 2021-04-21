package me.psek.vehicles.vehicle.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.Actions;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.builders.SpawnedCarData;
import me.psek.vehicles.vehicle.enums.VehicleSteerDirection;
import me.psek.vehicles.vehicle.events.VehicleSteerEvent;
import me.psek.vehicles.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;

public class OnVehicleSteerPacket {
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private static final Actions ACTIONS_INSTANCE = Vehicles.getActionsInstance();

    static {
        Vehicles.getProtocolManager().addPacketListener(
                new PacketAdapter(Vehicles.getPluginInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            float sidewaysValue = event.getPacket().getFloat().read(0);
                            float forwardsValue = event.getPacket().getFloat().read(1);

                            Player player = event.getPlayer();
                            if (Utils.canDrive(player)) {
                                Entity vehicleEntity = event.getPlayer().getVehicle();

                                VehicleSteerDirection direction = VehicleSteerDirection.FORWARD;
                                if (forwardsValue < 0) {
                                    direction = VehicleSteerDirection.BACKWARDS;
                                } else if (sidewaysValue < 0) {
                                    direction = VehicleSteerDirection.RIGHT;
                                } else if (sidewaysValue > 0) {
                                    direction = VehicleSteerDirection.LEFT;
                                }

                                CarData carData = CarData.ALL_REGISTERED_CARS
                                        .get(vehicleEntity.getPersistentDataContainer().get(Vehicles.vehicleNameKey, PersistentDataType.STRING));

                                VehicleSteerEvent vehicleSteerEvent = new VehicleSteerEvent(carData, direction);
                                PLUGIN_MANAGER.callEvent(vehicleSteerEvent);
                                if (vehicleSteerEvent.isCancelled()) {
                                    return;
                                }
                                SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                        .get(UUID.fromString(vehicleEntity.getPersistentDataContainer().get(Vehicles.uuidOfCenterAsKey, PersistentDataType.STRING)));
                                ACTIONS_INSTANCE.steerVehicle(spawnedCarData, direction);
                            }
                        }
                    }
                });
    }
}
