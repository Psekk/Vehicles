package me.psek.vehicles.vehicletypes;

import me.psek.vehicles.Vehicles;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface IVehicle {
    void spawn(Vehicles plugin, String name, Location location);

    void movementHandler(Vehicles plugin, Entity vehicle, Player player, float forwards, float sideways, boolean flag1, boolean flag2);

    UUID getCenterUUID(Entity entity);

    List<? extends Serializable> getSerializableData();

    Class<? extends Serializable> getSerializableClass();

    void loadFromData(Vehicles plugin, List<Object> input);
}
