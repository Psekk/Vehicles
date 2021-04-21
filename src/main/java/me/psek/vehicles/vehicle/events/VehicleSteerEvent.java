package me.psek.vehicles.vehicle.events;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.vehicle.builders.CarData;
import me.psek.vehicles.vehicle.enums.VehicleSteerDirection;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VehicleSteerEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private boolean isCancelled;

    @Getter
    @Setter
    private VehicleSteerDirection direction;

    @Getter
    private final CarData carData;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public VehicleSteerEvent(CarData carData, VehicleSteerDirection direction) {
        this.carData = carData;
        direction = VehicleSteerDirection.FORWARD;
    }
}
