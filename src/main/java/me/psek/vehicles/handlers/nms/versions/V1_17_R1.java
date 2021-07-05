package me.psek.vehicles.handlers.nms.versions;

import me.psek.vehicles.handlers.nms.INMS;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class V1_17_R1 implements INMS {

    @Override
    public void setNoClip(Entity armorStand, boolean noClip) {
        ((CraftEntity) armorStand).getHandle().P = noClip;
    }
}
