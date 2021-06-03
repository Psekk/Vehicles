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
    public static List<Player> playersInVehicle = new ArrayList<>();

    private final NamespacedKey centerUUIDKey;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (!event.getRightClicked().getPersistentDataContainer().has(centerUUIDKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        Entity clickedEntity = event.getRightClicked();
        if (!(clickedEntity instanceof ArmorStand) || clickedEntity.getPassengers().size() > 0) {
            return;
        }
        clickedEntity.addPassenger(event.getPlayer());
    }

    public EntityInteractListener(NamespacedKey centerUUIDKey) {
        this.centerUUIDKey = centerUUIDKey;
    }
}
