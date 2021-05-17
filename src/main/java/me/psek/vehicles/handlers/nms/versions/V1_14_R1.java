package me.psek.vehicles.handlers.nms.versions;

import me.psek.vehicles.handlers.nms.INMS;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;

public class V1_14_R1 implements INMS {
    @Override
    public void setNoClip(ArmorStand armorStand, boolean value) {
        ((CraftEntity) armorStand).getHandle().noclip = value;
    }
}
