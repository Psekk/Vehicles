package me.psek.vehicles.handlers.data;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.handlers.data.serialization.Serializer;
import me.psek.vehicles.spawnedvehiclesdata.SpawnedCarData;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VehicleSaver extends Serializer {
    @SuppressWarnings({"ignored", "ResultOfMethodCallIgnored"})
    public void storeData(Vehicles plugin) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/data";
        for (IVehicle vehicleType : plugin.vehicleTypes) {
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
        for (IVehicle vehicleType : plugin.vehicleTypes) {
            try {
                Path localPath = Path.of(path, vehicleType.getClass().getSimpleName().toLowerCase());
                if (!Files.exists(localPath)) {
                    continue;
                }
                List<String> deserializedLines = Files.readAllLines(localPath);
                Class<? extends Serializable> clazz = vehicleType.getSerializableClass();
                for (String deserializedLine : deserializedLines) {
                    SpawnedCarData deserialized = (SpawnedCarData) deserialize(deserializedLine, clazz);
                    System.out.println(deserialized.isElectric());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
