package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.utils.Utils;
import me.psek.vehicles.vehicle.Actions;
import me.psek.vehicles.vehicle.data.SpawnedCarData;
import me.psek.vehicles.vehicle.events.VehicleShiftEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;

public class OnPlayerItemHeldEvent implements Listener {
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    @EventHandler
    public void onItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!player.isInsideVehicle()) {
            return;
        }
        if (!Objects.requireNonNull(player.getVehicle()).getPersistentDataContainer().has(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        SpawnedCarData spawnedCarData = SpawnedCarData.ALL_SPAWNED_CAR_DATA
                .get(Utils.bytesAsUuid(player.getVehicle().getPersistentDataContainer().get(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)));
        int previousGear = spawnedCarData.getCurrentGear();
        int newGear = event.getNewSlot() - 36;
        System.out.println(newGear + " // " + event.getNewSlot());
        VehicleShiftEvent vehicleShiftEvent = new VehicleShiftEvent(previousGear, newGear);
        PLUGIN_MANAGER.callEvent(vehicleShiftEvent);
        if (vehicleShiftEvent.isCancelled()) {
            return;
        }
        if (!Actions.tryShift(spawnedCarData, vehicleShiftEvent.getNewGear())) {
            //todo shifting failure
        }
    }
}
