package me.psek.vehicles;

import me.psek.vehicles.handlers.data.VehicleSaver;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.listeners.QuitListener;
import me.psek.vehicles.vehicletypes.Car;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Vehicles extends JavaPlugin {
    private VehicleSaver IOInstance;

    @Override
    public void onEnable() {
        registerNamespacedKeys();
        registerListeners(new EntityInteractListener(uuidOfCenterSeatKey), new QuitListener(uuidOfCenterSeatKey));

        IOInstance = new VehicleSaver();
        registerVehicleTypes(new Car());
    }

    @Override
    public void onDisable() {
        IOInstance.storeData();
    }

    private NamespacedKey uuidOfCenterSeatKey;

    private void registerNamespacedKeys() {
        uuidOfCenterSeatKey = new NamespacedKey(this, "uuidOfCenterSeat");
    }

    public List<IVehicle> vehicleTypes = new ArrayList<>();

    private void registerVehicleTypes(IVehicle... vehicleTypes) {
        this.vehicleTypes.addAll(Arrays.asList(vehicleTypes));
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }
}
