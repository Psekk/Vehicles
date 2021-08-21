package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.psek.vehicles.api.RegisteringAPI;
import me.psek.vehicles.commands.VehiclesCommand;
import me.psek.vehicles.handlers.config.ConfigHandler;
import me.psek.vehicles.handlers.data.VehicleSaver;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.handlers.nms.Mediator;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.listeners.ItemHeldListener;
import me.psek.vehicles.listeners.JoinListener;
import me.psek.vehicles.packetlisteners.VehicleSteerPacket;
import me.psek.vehicles.vehicleentites.IVehicleEntity;
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
    public static final Map<UUID, IVehicleEntity> spawnedVehicles = new HashMap<>();
    @Getter
    private static Vehicles instance;

    public final List<IVehicle> vehicleTypes = new ArrayList<>();
    public final Map<String, IVehicle> subVehicleTypes = new HashMap<>();

    @Getter
    private ProtocolManager protocolManager;
    private VehicleSaver vehicleSaver;
    private NamespacedKey centerUUIDKey;
    private NamespacedKey childUUIDsKey;
    private NamespacedKey vehicleSortClassNameKey;
    private INMS NMSInstance;
    private Car carInstance;
    private ConfigHandler configHandler;

    @Override
    public void onEnable() {
        instance = this;
        NMSInstance = new Mediator(this).getNMS();
        protocolManager = ProtocolLibrary.getProtocolManager();
        vehicleSaver = new VehicleSaver();
        connectDatabase();
        registerNamespacedKeys();
        registerListeners(new EntityInteractListener(centerUUIDKey),
                new JoinListener(centerUUIDKey),
                new ItemHeldListener(this, vehicleSortClassNameKey, centerUUIDKey));
        registerCommands();
        registerPacketListeners(centerUUIDKey, vehicleSortClassNameKey);
        registerBaseVehicles();
        registerTestCar(); //todo remove in production model
        registerTickers();
        vehicleSaver.retrieveData(this);
        configHandler = new ConfigHandler(this);
    }

    @Override
    public void onDisable() {
        vehicleSaver.storeData(this);
        spawnedVehicles.clear();
        EntityInteractListener.inVehiclePlayers.clear();
        instance = null;
    }

    private void connectDatabase() {

    }

    private void registerTestCar() {
        Car.Builder carType = Car.Builder.builder()
                .name("lada")
                .horsepower(100)
                .brakingForce(4050)
                .gearRatios(new double[] {
                        5.714,
                        4.143,
                        3.106,
                        2.1,
                        1.6285,
                        1.000,
                        0.839,
                        0.567
                })
                .seatCount(5)
                .seatVectors(new Vector[] {
                        new Vector(1,-0.5,1),
                        new Vector(-1,-0.5,1),
                        new Vector(1,-0.3,-1),
                        new Vector(0,-0.3,-1),
                        new Vector(-1,-0.3,-1)
                })
                .boundingBoxVectors(new Vector[] {
                        new Vector(2.5, 1, 2.3),
                        new Vector(-2.5, 0.15, -2.35)
                })
                .tireVectors(new Vector[] {
                        new Vector(1.15, 0.3, 1.3),
                        new Vector(-1.15, 0.3, 1.3),
                        new Vector(1.15, 0.3, -1.3),
                        new Vector(-1.15, 0.3, -1.3)
                })
                .gearCount(7)
                .RPMs(new double[] {
                        8000,
                        1100,
                        7000
                })
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

    private void registerBaseVehicles() {
        Car car = new Car(NMSInstance, centerUUIDKey, vehicleSortClassNameKey, childUUIDsKey);
        carInstance = car;
        RegisteringAPI.registerVehicleTypes(car);
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
        new MovementTicker(this, NMSInstance, carInstance);
    }
}
