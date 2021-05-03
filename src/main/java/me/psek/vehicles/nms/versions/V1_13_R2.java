package me.psek.vehicles.nms.versions;

import me.psek.vehicles.nms.INms;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class V1_13_R2 implements INms {
    @Override
    public void setNoClip(Entity entity, boolean bool) {
        ((CraftEntity) entity).getHandle().noclip = bool;
    }
}
