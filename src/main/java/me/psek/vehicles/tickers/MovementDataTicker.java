package me.psek.vehicles.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class MovementDataTicker {
    private final NamespacedKey vehicleSortClassName;

    private void run(Vehicles plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : EntityInteractListener.playersInVehicle) {
                if (player.getVehicle() == null) {
                    EntityInteractListener.playersInVehicle.remove(player);
                    continue;
                }
                Entity vehicle = player.getVehicle();
                ISpawnedVehicle iSpawnedVehicle = null;
                for (ISpawnedVehicle sv : plugin.getSpawnedVehicles()) {
                    if (!sv.getName().equalsIgnoreCase(vehicle.getPersistentDataContainer().get(vehicleSortClassName, PersistentDataType.STRING))) {
                        continue;
                    }
                    iSpawnedVehicle = sv;
                    break;
                }
                if (iSpawnedVehicle == null) {
                    throw new NullPointerException("Something went horribly wrong! Please contact the developer!");
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(iSpawnedVehicle.getCurrentSpeed() + " m/s"));
            }
        }, 5L, 5L);
    }

    //todo fix: what the fuck is this namespacedkey
    public MovementDataTicker(Vehicles plugin, NamespacedKey vehicleSortClassName) {
        run(plugin);
        this.vehicleSortClassName = vehicleSortClassName;
    }
}
