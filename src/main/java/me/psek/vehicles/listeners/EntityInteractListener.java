package me.psek.vehicles.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EntityInteractListener implements Listener {
    public static List<Player> inVehiclePlayers = new ArrayList<>();

    private final NamespacedKey centerUUIDKey;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void listener(PlayerInteractAtEntityEvent event) {
        if (!event.getRightClicked().getPersistentDataContainer().has(centerUUIDKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        Entity clickedEntity = event.getRightClicked();
        if (!(clickedEntity instanceof ArmorStand) || clickedEntity.getPassengers().size() > 0) {
            return;
        }
        Player player = event.getPlayer();
        clickedEntity.addPassenger(player);
        if (inVehiclePlayers.contains(player)) {
            return;
        }
        inVehiclePlayers.add(player);
    }

    public EntityInteractListener(NamespacedKey centerUUIDKey) {
        this.centerUUIDKey = centerUUIDKey;
    }
}
