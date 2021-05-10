package me.psek.vehicles.vehicletypes;

import org.bukkit.Location;

public interface IVehicle {
    void spawn(int id, Location location);

    void move(int id, double length, Object direction);
}
