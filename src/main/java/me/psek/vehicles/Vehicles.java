package me.psek.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.psek.vehicles.api.APIHandler;
import me.psek.vehicles.commands.VehiclesCommand;
import me.psek.vehicles.handlers.data.VehicleSaver;
import me.psek.vehicles.handlers.nms.INMS;
import me.psek.vehicles.handlers.nms.Mediator;
import me.psek.vehicles.listeners.EntityInteractListener;
import me.psek.vehicles.listeners.KickListener;
import me.psek.vehicles.listeners.QuitListener;
import me.psek.vehicles.packetlisteners.VehicleSteerPacket;
import me.psek.vehicles.spawnedvehicledata.ISpawnedVehicle;
import me.psek.vehicles.tickers.cartickers.MovementDataTicker;
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
    private APIHandler apiHandler;

    private NamespacedKey centerUUIDKey;
    private NamespacedKey vehicleSortClassName;
    private NamespacedKey childUUIDsKey;

    @Override
    public void onEnable() {
        registerNamespacedKeys();
        registerListeners(new EntityInteractListener(centerUUIDKey), new QuitListener(centerUUIDKey), new KickListener(centerUUIDKey));
        registerCommands();
        registerTickers(vehicleSortClassName);

        protocolManager = ProtocolLibrary.getProtocolManager();
        registerPacketListeners(centerUUIDKey, vehicleSortClassName);

        INMS NMSInstance = new Mediator().getNMS();
        apiHandler = new APIHandler(this);
        vehicleSaver = new VehicleSaver();

        apiHandler.getRegisteringAPI().registerVehicleTypes(new Car(NMSInstance, centerUUIDKey, vehicleSortClassName, childUUIDsKey));
        registerTestCar();
        vehicleSaver.retrieveData(this);
    }

    @Override
    public void onDisable() {
        vehicleSaver.storeData(this);
    }

    private void registerTestCar() {
        Car.Builder carType = Car.Builder.builder()
                .name("lada")
                .horsepower(100)
                .brakingForce(20)
                .gearRatios(Arrays.asList(
                        4.714,
                        3.143,
                        2.106,
                        1.667,
                        1.285,
                        1.000,
                        0.839,
                        0.667
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
                .gearCount(5)
                .RPMs(Arrays.asList(
                        10000,
                        1200,
                        8500
                ))
                .shiftTime(6*20)
                .steeringSeatIndex(0)
                .isAutomatic(false)
                .gripFactor(0.78921)
                .maxRedRPMTicks(45)
                .drivetrainWheelCount(2)
                .wheelRadius(0.33)
                .tirePressure(2.9)
                .vehicleMass(1500)
                .build();
        Car.registerCarSubtype("lada", carType);
        for (IVehicle iVehicle : vehicleTypes) {
            if (!iVehicle.getClass().getSimpleName().equalsIgnoreCase("car")) {
                continue;
            }
            apiHandler.getRegisteringAPI().registerSubVehicleType("lada", iVehicle);
            break;
        }
    }

    private void registerNamespacedKeys() {
        centerUUIDKey = new NamespacedKey(this, "centerUUID");
        vehicleSortClassName = new NamespacedKey(this, "vehicleSortClassName");
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

    private void registerTickers(NamespacedKey vehicleSortClassName) {
        new MovementDataTicker(this, centerUUIDKey);
    }

    public APIHandler getAPIHandler() {
        return apiHandler;
    }

    public final Map<UUID, ISpawnedVehicle> spawnedVehicles = new HashMap<>();
    public final List<IVehicle> vehicleTypes = new ArrayList<>();
    public final Map<String, IVehicle> subVehicleTypes = new HashMap<>();
}
