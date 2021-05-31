package me.psek.vehicles.vehicletypes;

import lombok.Builder;
import me.psek.vehicles.Vehicles;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface IVehicle {
    void spawn(Vehicles plugin, int id, Location location);

    void move(int id, double length, Object direction);

    int getId(String name);

    UUID getCenterUUID(Entity entity);

    List<?> getSerializableData();

    Class<? extends Serializable> getSerializableClass();
}
