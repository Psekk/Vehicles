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
import me.psek.vehicles.vehicle.events.VehicleSteerEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Objects;


public class OnVehicleSteerPacket {
    private final Vehicles PLUGIN_INSTANCE = Vehicles.getPluginInstance();
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

    private static final HashMap<Player, Long> LAST_TIME_JUMPED = new HashMap<>();
    private final World WORLD = Bukkit.getWorlds().get(0);

    public OnVehicleSteerPacket() {
        Vehicles.getProtocolManager().addPacketListener(
                new PacketAdapter(Vehicles.getPluginInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                            float sidewaysValue = event.getPacket().getFloat().read(0);
                            float forwardsValue = event.getPacket().getFloat().read(1);

                            if (event.getPacket().getBooleans().read(0)) {
                                Player player = event.getPlayer();
                                Long lastTime = LAST_TIME_JUMPED.getOrDefault(player, 0L);
                                if (lastTime + 15 > WORLD.getFullTime()) {
                                    if (lastTime != 0) {
                                        LAST_TIME_JUMPED.remove(player);
                                    }
                                    return;
                                }
                                SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                        .get(Utils.bytesAsUuid(Objects.requireNonNull(player.getVehicle())
                                        .getPersistentDataContainer()
                                        .get(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)));
                                Actions.toggleHandBrake(spawnedCarData);
                                if (!LAST_TIME_JUMPED.containsKey(player)) {
                                    LAST_TIME_JUMPED.put(player, WORLD.getFullTime());
                                }
                                return;
                            }

                            Player player = event.getPlayer();
                            if (Utils.canDrive(player)) {
                                Entity vehicleEntity = event.getPlayer().getVehicle();

                                VehicleSteerDirection sidewaysDirection = null;
                                if (sidewaysValue < 0) {
                                    sidewaysDirection = VehicleSteerDirection.RIGHT;
                                } else if (sidewaysValue > 0) {
                                    sidewaysDirection = VehicleSteerDirection.LEFT;
                                }

                                VehicleSteerDirection forwardsDirection = null;
                                if (forwardsValue > 0) {
                                    forwardsDirection = VehicleSteerDirection.FORWARD;
                                } else if (forwardsValue < 0) {
                                    forwardsDirection = VehicleSteerDirection.BACKWARDS;
                                }
                                VehicleSteerDirection[] directions = new VehicleSteerDirection[] { sidewaysDirection, forwardsDirection};
                                if (forwardsDirection != null || sidewaysDirection != null) {
                                    CarData carData = CarData.ALL_REGISTERED_CARS
                                            .get(Objects.requireNonNull(vehicleEntity).getPersistentDataContainer().get(Vehicles.vehicleNameKey, PersistentDataType.STRING));
                                    SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                                            .get(Utils.bytesAsUuid(vehicleEntity.getPersistentDataContainer().get(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)));

                                    for (int i = 0; i < 2; i++) {
                                        if (directions[i] == null) {
                                            continue;
                                        }
                                        VehicleSteerEvent vehicleSteerEvent = new VehicleSteerEvent(carData, directions[i]);
                                        Bukkit.getScheduler().runTask(PLUGIN_INSTANCE, () -> PLUGIN_MANAGER.callEvent(vehicleSteerEvent));
                                        if (vehicleSteerEvent.isCancelled()) {
                                            return;
                                        }

                                        VehicleSteerDirection finalDirection = vehicleSteerEvent.getDirection();
                                        Bukkit.getScheduler().runTask(PLUGIN_INSTANCE, () -> Actions.steerVehicle(spawnedCarData, finalDirection));
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @SuppressWarnings("unused")
    public static void put(Player player, Long fullTime) {
        if (LAST_TIME_JUMPED.containsKey(player)) {
            return;
        }
        LAST_TIME_JUMPED.put(player, fullTime);
    }

    public static void remove(Player player) {
        if (!LAST_TIME_JUMPED.containsKey(player)) {
            return;
        }
        LAST_TIME_JUMPED.remove(player);
    }
}
