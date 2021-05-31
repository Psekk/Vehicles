package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Builder;
import lombok.Getter;
import me.psek.vehicles.commands.VehiclesCommand;
import me.psek.vehicles.handlers.data.VehicleSaver;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.handlers.nms.Mediator;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.listeners.QuitListener;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.tickers.MovementDataTicker;
import me.psek.vehicles.vehicletypes.Car;
import me.psek.vehicles.vehicletypes.IVehicle;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public final class Vehicles extends JavaPlugin {
    @Getter
    private ProtocolManager protocolManager;
    private VehicleSaver vehicleSaver;

    @Override
    public void onEnable() {
        registerNamespacedKeys();
        registerListeners(new EntityInteractListener(uuidOfCenterSeatKey), new QuitListener(uuidOfCenterSeatKey));
        registerCommands();
        registerTickers(vehicleSortClassName);
        registerTestCar();

        protocolManager = ProtocolLibrary.getProtocolManager();
        INMS NMSInstance = new Mediator().getNMS();
        vehicleSaver = new VehicleSaver();

        registerVehicleTypes(new Car(NMSInstance, this));

        vehicleSaver.retrieveData(this);
    }

    @Override
    public void onDisable() {
        vehicleSaver.storeData(this);
    }

    private void registerTestCar() {
        Car.Builder carType = Car.Builder.builder()
                .name("lada")
                .accelerationSpeed(0.5)
                .brakingSpeed(0.01)
                .backwardsAccelerationSpeed(0.0025)
                .seatCount(5)
                .seatPositions(Arrays.asList(
                        new Vector(1,-0.5,1),
                        new Vector(-1,-0.5,1),
                        new Vector(1,-0.3,-1),
                        new Vector(0,-0.3,-1),
                        new Vector(-1,-0.3,-1) ))
                .boundingBoxVectors(Arrays.asList(
                        new Vector(2.5, 1, 2.3),
                        new Vector(-2.5, 0.15, -2.35) ))
                .gearCount(5)
                .RPMs(Arrays.asList(
                        9000,
                        6000,
                        7500
                ))
                .shiftTime(6*20)
                .steeringSeatIndex(0)
                .RPMIncreasePerGear(Arrays.asList(
                        25D,
                        22.5,
                        17D,
                        14D,
                        12D
                ))
                .accelerationMultipliers(Arrays.asList(
                        1D,
                        0.85,
                        0.7,
                        0.55,
                        0.35
                ))
                .isAutomaticShifting(false)
                .gripFactor(0.78921)
                .maxRedRPMTicks(45)
                .build();
        Car.registerCarSubtype(carType);
    }

    private NamespacedKey uuidOfCenterSeatKey;
    private NamespacedKey vehicleSortClassName;

    private void registerNamespacedKeys() {
        uuidOfCenterSeatKey = new NamespacedKey(this, "uuidOfCenterSeat");
        vehicleSortClassName = new NamespacedKey(this, "vehicleSortClassName");
    }

    @Getter
    private final List<IVehicle> vehicleTypes = new ArrayList<>();

    public void registerVehicleTypes(IVehicle... vehicleTypes) {
        this.vehicleTypes.addAll(Arrays.asList(vehicleTypes));
    }

    @Getter
    private final Map<String, Builder> vehicleSubTypes = new HashMap<>();

    @SuppressWarnings("unused")
    public void registerVehicleSubType(String name, Builder builder) {
        vehicleSubTypes.put(name, builder);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        this.getCommand("vehicles").setExecutor(new VehiclesCommand(this));
    }

    private void registerTickers(NamespacedKey vehicleSortClassName) {
        new MovementDataTicker(this, vehicleSortClassName);
    }

    @Getter
    private final List<ISpawnedVehicle> spawnedVehicles = new ArrayList<>();

    public void registerSpawnedVehicle(ISpawnedVehicle iSpawnedVehicle) {
        spawnedVehicles.add(iSpawnedVehicle);
    }

    @SuppressWarnings("unused")
    public void unregisterSpawnedVehicle(byte[] UUID) {
        for (ISpawnedVehicle iSpawnedVehicle : spawnedVehicles) {
            if (iSpawnedVehicle.getCenterUUID() != UUID) {
                continue;
            }
            spawnedVehicles.remove(iSpawnedVehicle);
            break;
        }
    }
}
