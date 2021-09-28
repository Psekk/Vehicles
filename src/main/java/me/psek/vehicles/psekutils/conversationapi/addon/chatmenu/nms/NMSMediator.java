package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms;

import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.versions.V1_14_R1;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.versions.V1_15_R1;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.versions.V1_16_R3;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.versions.V1_17_R1;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class NMSMediator {
    private static final INMS INMS;

    static {
        switch (getNMSVersion()) {
            case "17" -> INMS = new V1_17_R1();
            case "v1_16_R3" -> INMS = new V1_16_R3();
            case "v1_15_R1" -> INMS = new V1_15_R1();
            case "v1_14_R1" -> INMS = new V1_14_R1();
            default -> INMS = null;
        }
    }

    @NotNull
    public static INMS getNMS() {
        if (INMS == null) {
            throw new IllegalStateException("No NMS implementation was found for your server version.");
        }
        return INMS;
    }

    private static String getNMSVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        int majorVersion = Integer.parseInt(version.split("_")[1]);
        return majorVersion < 17 ? Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] : majorVersion + "";
    }
}
