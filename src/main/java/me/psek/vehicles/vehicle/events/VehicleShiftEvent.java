package me.psek.vehicles.vehicle.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VehicleShiftEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private boolean isCancelled;

    @Setter
    @Getter
    private int newGear;

    @Getter
    private final int previousGear;

    @Override
    public final @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public VehicleShiftEvent(int previousGear, int newGear) {
        this.previousGear = previousGear;
        this.newGear = newGear;
    }
}
