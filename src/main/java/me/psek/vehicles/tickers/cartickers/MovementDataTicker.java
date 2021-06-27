package me.psek.vehicles.tickers.cartickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.utility.UUIDUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MovementDataTicker {
    private void run(Vehicles plugin, NamespacedKey centerUUIDKey) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : EntityInteractListener.inVehiclePlayers) {
                if (player.getVehicle() == null) {
                    EntityInteractListener.inVehiclePlayers.remove(player);
                    continue;
                }
                UUID centerUUID = UUIDUtils.bytesToUUID(player.getVehicle().getPersistentDataContainer().get(centerUUIDKey, PersistentDataType.BYTE_ARRAY));
                double speed = plugin.getAPIHandler().getSpawnedVehicles().get(centerUUID).getCurrentSpeed();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(speed + " m/s or " + speed * 3.6 + " km/h"));
            }
        }, 5L, 5L);
    }

    public MovementDataTicker(Vehicles plugin, NamespacedKey centerUUIDKey) {
        run(plugin, centerUUIDKey);
    }
}
