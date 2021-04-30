package me.psek.vehicles.nms.versions;

import me.psek.vehicles.nms.INms;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class V1_16_R3 implements INms {
    public void setNoClip(Entity entity, boolean bool) {
        ((CraftEntity) entity).getHandle().noclip = bool;
    }
}
