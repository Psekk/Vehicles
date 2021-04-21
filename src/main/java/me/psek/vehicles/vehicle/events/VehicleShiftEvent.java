package me.psek.vehicles.vehicle.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VehicleShiftEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private boolean isCancelled;

    @Getter
    @Setter
    private int shiftDirection;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
