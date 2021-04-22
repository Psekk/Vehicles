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
import me.psek.vehicles.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;


public class OnVehicleSteerPacket {
    private static final Actions ACTIONS_INSTANCE = Vehicles.getActionsInstance();

    static {
        Vehicles.getProtocolManager().addPacketListener(
                //todo fix the motherfucking annoying yee yee ass sync error bs
                new PacketAdapter(Vehicles.getPluginInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            float sidewaysValue = event.getPacket().getFloat().read(0);
                            float forwardsValue = event.getPacket().getFloat().read(1);

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
                                            .get(vehicleEntity.getPersistentDataContainer().get(Vehicles.vehicleNameKey, PersistentDataType.STRING));

                                    SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                            .get(vehicleEntity.getPersistentDataContainer().get(Vehicles.uuidOfCenterAsKey, PersistentDataType.STRING));
                                    ACTIONS_INSTANCE.steerVehicle(spawnedCarData, direction);
                                }
                            }
                        }
                    }
                });
    }
}
