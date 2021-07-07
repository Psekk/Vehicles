package me.psek.vehicles.tickers.cartickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.spawnedvehicledata.SpawnedCarData;
import me.psek.vehicles.utility.MathUtils;
import me.psek.vehicles.utility.UUIDUtils;
import me.psek.vehicles.vehicletypes.Car;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MovementDataTicker {
    public MovementDataTicker(Vehicles plugin, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassNameKey) {
        run(plugin, centerUUIDKey, vehicleSortClassNameKey);
    }

    private void run(Vehicles plugin, NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassNameKey) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : EntityInteractListener.inVehiclePlayers) {
                if (player.getVehicle() == null) {
                    if (!EntityInteractListener.inVehiclePlayers.contains(player)) {
                        continue;
                    }
                    EntityInteractListener.inVehiclePlayers.remove(player);
                    continue;
                }
                if (player.getVehicle().getPersistentDataContainer().has(vehicleSortClassNameKey, PersistentDataType.STRING)) {

                }
                UUID centerUUID = UUIDUtils.bytesToUUID(player.getVehicle().getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
                SpawnedCarData spawnedCarData = (SpawnedCarData) DataAPI.getSpawnedVehicles().get(centerUUID);
                Car.Builder builder = Car.getCarSubTypes().get(player.getVehicle().getPersistentDataContainer().get(vehicleSortClassNameKey, PersistentDataType.STRING));
                double speed = spawnedCarData.getCurrentSpeed();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(MathUtils.precisionRoundNumber(100, speed * 10) + " km/h " +
                                MathUtils.precisionRoundNumber(1, spawnedCarData.getCurrentRPM() * 9 - builder.getRPMs().get(1) * 9 + builder.getRPMs().get(1)) + "/min"));
            }
        }, 1L, 1L);
    }
}
