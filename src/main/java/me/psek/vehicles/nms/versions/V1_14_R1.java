package me.psek.vehicles.nms.versions;

import me.psek.vehicles.nms.INms;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class V1_14_R1 implements INms {
    @Override
    public void setNoClip(Entity entity, boolean bool) {
        ((CraftEntity) entity).getHandle().noclip = bool;
    }
}
