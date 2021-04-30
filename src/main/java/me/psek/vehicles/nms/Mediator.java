package me.psek.vehicles.nms;

import me.psek.vehicles.nms.versions.V1_14_R1;
import me.psek.vehicles.nms.versions.V1_15_R1;
import me.psek.vehicles.nms.versions.V1_16_R3;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Mediator {
    private static INms nms;

    static {
        switch (getNMSVersion()) {
            case "v1_16_R3":
                nms = new V1_16_R3();
            case "v1_15_R1":
                nms = new V1_15_R1();
            case "v1_14_R1":
                nms = new V1_14_R1();
        }
    }

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    @NotNull
    public static INms getNMS() {
        if (nms == null) {
            throw new IllegalStateException("No NMS implementation available for this server version.");
        }
        return nms;
    }
}

