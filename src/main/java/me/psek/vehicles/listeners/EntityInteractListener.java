package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityInteractListener implements Listener {
    private final NamespacedKey uuidOfCenterSeatKey;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (!event.getRightClicked().getPersistentDataContainer().has(uuidOfCenterSeatKey, PersistentDataType.BYTE_ARRAY)) {
            return;
        }
        Entity clickedEntity = event.getRightClicked();
        if (!(clickedEntity instanceof ArmorStand) || clickedEntity.getPassengers().size() > 0) {
            return;
        }
        clickedEntity.addPassenger(event.getPlayer());
    }

    public EntityInteractListener(NamespacedKey uuidOfCenterSeatKey) {
        this.uuidOfCenterSeatKey = uuidOfCenterSeatKey;
    }
}
