package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.psek.vehicles.api.RegisteringAPI;
import me.psek.vehicles.commands.VehiclesCommand;
import me.psek.vehicles.handlers.data.VehicleSaver;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.handlers.nms.Mediator;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.listeners.ItemHeldListener;
import me.psek.vehicles.listeners.KickListener;
import me.psek.vehicles.listeners.QuitListener;
import me.psek.vehicles.packetlisteners.VehicleSteerPacket;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.tickers.cartickers.MovementDataTicker;
import me.psek.vehicles.tickers.cartickers.MovementTicker;
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
    private static Vehicles instance;
    public static final Map<UUID, ISpawnedVehicle> spawnedVehicles = new HashMap<>();

    public final List<IVehicle> vehicleTypes = new ArrayList<>();
    public final Map<String, IVehicle> subVehicleTypes = new HashMap<>();

    @Getter
    private ProtocolManager protocolManager;
    private VehicleSaver vehicleSaver;
    private NamespacedKey centerUUIDKey;
    private NamespacedKey childUUIDsKey;
    private NamespacedKey vehicleSortClassNameKey;
    private INMS NMSInstance;

    @Override
    public void onEnable() {
        instance = this;
        NMSInstance = new Mediator(this).getNMS();
        protocolManager = ProtocolLibrary.getProtocolManager();
        registerNamespacedKeys();
        registerListeners(new EntityInteractListener(centerUUIDKey),
                new QuitListener(centerUUIDKey),
                new KickListener(centerUUIDKey),
                new ItemHeldListener(this, vehicleSortClassNameKey, centerUUIDKey));
        registerCommands();
        registerPacketListeners(centerUUIDKey, vehicleSortClassNameKey);
        vehicleSaver = new VehicleSaver();
        RegisteringAPI.registerVehicleTypes(new Car(this, NMSInstance, centerUUIDKey, vehicleSortClassNameKey, childUUIDsKey));
        registerTestCar();
        registerTickers();
        vehicleSaver.retrieveData(this);
    }

    @Override
    public void onDisable() {
        vehicleSaver.storeData(this);
        instance = null;
    }

    private void registerTestCar() {
        Car.Builder carType = Car.Builder.builder()
                .name("lada")
                .horsepower(100)
                .brakingForce(2750)
                .gearRatios(Arrays.asList(
                        5.714,
                        4.143,
                        3.106,
                        2.1,
                        1.6285,
                        1.000,
                        0.839,
                        0.567
                        ))
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
                .gearCount(7)
                .RPMs(Arrays.asList(
                        9000,
                        1100,
                        7500
                ))
                .shiftTime(7)
                .steeringSeatIndex(0)
                .isAutomatic(false)
                .gripFactor(0.78921)
                .maxRedRPMTicks(45)
                .drivetrainWheelCount(2)
                .tireRadius(0.33)
                .tirePressure(3.4)
                .vehicleMass(1670)
                .build();
        Car.registerCarSubtype("lada", carType);
        for (IVehicle iVehicle : vehicleTypes) {
            if (!iVehicle.getClass().getSimpleName().equalsIgnoreCase("car")) {
                continue;
            }
            RegisteringAPI.registerSubVehicleType("lada", iVehicle);
            break;
        }
    }

    private void registerNamespacedKeys() {
        centerUUIDKey = new NamespacedKey(this, "centerUUID");
        vehicleSortClassNameKey = new NamespacedKey(this, "vehicleSortClassName");
        childUUIDsKey = new NamespacedKey(this, "childUUIDsKey");
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = this.getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerPacketListeners(NamespacedKey centerUUIDKey, NamespacedKey vehicleSortClassName) {
        new VehicleSteerPacket(this, centerUUIDKey, vehicleSortClassName);
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        this.getCommand("vehicles").setExecutor(new VehiclesCommand(this));
    }

    private void registerTickers() {
        new MovementDataTicker(this, centerUUIDKey, vehicleSortClassNameKey);
        new MovementTicker(this, NMSInstance);
    }
}
