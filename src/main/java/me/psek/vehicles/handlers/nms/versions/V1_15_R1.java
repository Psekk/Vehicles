package me.psek.vehicles.handlers.nms.versions;

import me.psek.vehicles.handlers.nms.INMS;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class V1_15_R1 implements INMS {
    @Override
    public void setNoClip(Entity armorStand, boolean value) {
        ((CraftEntity) armorStand).getHandle().noclip = value;
    }
}
