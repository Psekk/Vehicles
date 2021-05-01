package me.psek.vehicles.vehicle.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.vehicle.Actions;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.data.SpawnedCarData;
import me.psek.vehicles.vehicle.enums.VehicleSteerDirection;
import me.psek.vehicles.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;


public class OnVehicleSteerPacket {
    private static final Vehicles PLUGIN_INSTANCE = Vehicles.getPluginInstance();

    static {
        Vehicles.getProtocolManager().addPacketListener(
                new PacketAdapter(Vehicles.getPluginInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            float sidewaysValue = event.getPacket().getFloat().read(0);
                            float forwardsValue = event.getPacket().getFloat().read(1);

                            if (event.getPacket().getBooleans().read(0)) {
                                Player player = event.getPlayer();
                                Entity vehicleEntity = player.getVehicle();
                                SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                        .get(Utils.bytesAsUuid(Objects.requireNonNull(vehicleEntity)
                                        .getPersistentDataContainer()
                                        .get(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)));
                                Actions.toggleHandBrake(spawnedCarData);
                            }

                            Player player = event.getPlayer();
                            if (Utils.canDrive(player)) {
                                Entity vehicleEntity = event.getPlayer().getVehicle();
                                VehicleSteerDirection direction = null;
                                if (forwardsValue > 0) {
                                    direction = VehicleSteerDirection.FORWARD;
                                } else if (forwardsValue < 0){
                                    direction = VehicleSteerDirection.BACKWARDS;
                                } else if (sidewaysValue < 0) {
                                    direction = VehicleSteerDirection.RIGHT;
                                } else if (sidewaysValue > 0) {
                                    direction = VehicleSteerDirection.LEFT;
                                }

                                if (direction != null) {
                                    CarData carData = CarData.ALL_REGISTERED_CARS
                                            .get(Objects.requireNonNull(vehicleEntity).getPersistentDataContainer().get(Vehicles.vehicleNameKey, PersistentDataType.STRING));
                                    SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                            .get(Utils.bytesAsUuid(vehicleEntity.getPersistentDataContainer().get(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)));
                                    VehicleSteerDirection finalDirection = direction;
                                    Bukkit.getScheduler().runTask(PLUGIN_INSTANCE, () -> Actions.steerVehicle(spawnedCarData, finalDirection));
                                }
                            }
                        }
                    }
                });
    }
}
