package me.psek.vehicles.listeners;

import me.psek.vehicles.Vehicles;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class OnPlayerEntityInteract implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getPersistentDataContainer().has(Vehicles.uuidOfCenterAsKey, PersistentDataType.BYTE_ARRAY)) {
            Entity clickedEntity = event.getRightClicked();
            System.out.println(clickedEntity instanceof ArmorStand);
            System.out.println(clickedEntity.getPassengers().size() < 1);
            if (clickedEntity instanceof ArmorStand && clickedEntity.getPassengers().size() < 1) {
                clickedEntity.addPassenger(event.getPlayer());
            }
        }
    }
}
