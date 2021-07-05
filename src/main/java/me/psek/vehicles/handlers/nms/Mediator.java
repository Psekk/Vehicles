package me.psek.vehicles.handlers.nms;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.handlers.nms.versions.V1_17_R1;
import me.psek.vehicles.handlers.nms.versions.V1_14_R1;
import me.psek.vehicles.handlers.nms.versions.V1_15_R1;
import me.psek.vehicles.handlers.nms.versions.V1_16_R3;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Mediator {
    private final INMS INMS;

    public Mediator(Vehicles plugin) {
        switch (getNMSVersion(plugin)) {
            case "17":
                INMS = new V1_17_R1();
                break;
            case "v1_16_R3":
                INMS = new V1_16_R3();
                break;
            case "v1_15_R1":
                INMS = new V1_15_R1();
                break;
            case "v1_14_R1":
                INMS = new V1_14_R1();
                break;
            default:
                INMS = null;
                break;
        }
    }

    @NotNull
    public INMS getNMS() {
        if (INMS == null) {
            throw new IllegalStateException("No NMS implementation was found for your server version.");
        }
        return INMS;
    }

    private String getNMSVersion(Vehicles plugin) {
        String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        int majorVersion = Integer.parseInt(version.split("_")[1]);
        return majorVersion < 17 ? Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] : majorVersion + "";
    }
}
