package me.psek.vehicles.tickers;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.listeners.EntityInteractListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MovementDataTicker {
    private void ticker(Vehicles plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : EntityInteractListener.playersInVehicle) {
                if (player.getVehicle() == null) {
                    EntityInteractListener.playersInVehicle.remove(player);
                    continue;
                }
                Entity vehicle = player.getVehicle();



                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("x"));
            }
        }, 5L, 5L);
    }

    private double convert(double input) {
        return 0D;
    }

    public MovementDataTicker(Vehicles plugin) {
        ticker(plugin);
    }
}
