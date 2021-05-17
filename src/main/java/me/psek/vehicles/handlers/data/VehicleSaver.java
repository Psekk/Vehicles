package me.psek.vehicles.handlers.data;

import com.google.common.primitives.Bytes;
import me.psek.vehicles.Vehicles;
import me.psek.vehicles.handlers.data.serialization.Serialization;
import me.psek.vehicles.vehicletypes.IVehicle;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleSaver extends Serialization {
    @SuppressWarnings({"ignored", "ResultOfMethodCallIgnored"})
    public void storeData(Vehicles plugin) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/data";
        for (IVehicle vehicleType : plugin.vehicleTypes) {
            String serializedData = serialize(vehicleType.getSerializableData());
            File file = new File(path + vehicleType.getClass().getName().toLowerCase());
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

    //todo fix this thing
    public void retrieveData(Vehicles plugin) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/data";
        for (IVehicle vehicleType : plugin.vehicleTypes) {
            try {
                List<String> deserializedLines = Files.readAllLines(Path.of(path, vehicleType.getClass().getName().toLowerCase()));
                Class<? extends Serializable> clazz = vehicleType.getSerializableClass();
                List<Class<? extends Serializable>> deserializedData = new ArrayList<>();
                for (String deserializedLine : deserializedLines) {
                    deserializedData.add(deserialize(deserializedLine, clazz));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
