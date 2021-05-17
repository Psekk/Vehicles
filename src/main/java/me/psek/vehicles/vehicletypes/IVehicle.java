package me.psek.vehicles.vehicletypes;

import org.bukkit.Location;

import java.io.Serializable;
import java.util.List;

public interface IVehicle {
    void spawn(int id, Location location);

    void move(int id, double length, Object direction);

    List<? extends Serializable> getSerializableData();

    Class<? extends Serializable> getSerializableClass();
}
