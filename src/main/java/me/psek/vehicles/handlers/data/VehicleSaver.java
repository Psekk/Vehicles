package me.psek.vehicles.handlers.data;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.api.DataAPI;
import me.psek.vehicles.handlers.data.serialization.Serializer;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//todo fix the not writing issue and the sometimes occurring exception
public class VehicleSaver extends Serializer {
    @SuppressWarnings({"ignored", "ResultOfMethodCallIgnored"})
    public void storeData(Vehicles plugin) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/data";
        for (IVehicle vehicleType : DataAPI.getVehicleTypes()) {
            String serializedData = serialize(vehicleType.getSerializableData());
            File file = new File(path + "/" + "data." + vehicleType.getClass().getSimpleName().toLowerCase());
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(serializedData);
                fileWriter.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void retrieveData(Vehicles plugin) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/data";
        for (IVehicle vehicleType : DataAPI.getVehicleTypes()) {
            try {
                Path localPath = Path.of(path, "data.",  vehicleType.getClass().getSimpleName().toLowerCase() + "s");
                if (!Files.exists(localPath)) {
                    continue;
                }
                List<String> deserializedLines = Files.readAllLines(localPath);
                Class<? extends Serializable> clazz = vehicleType.getSerializableClass();
                List<Object> objects = new ArrayList<>();
                for (String deserializedLine : deserializedLines) {
                    objects.add(deserialize(deserializedLine, clazz));
                }
                vehicleType.loadFromData(plugin, objects);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
