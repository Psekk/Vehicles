package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.versions;

import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.INMS;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class V1_16_R3 implements INMS {
    @Override
    public @Nullable String getNBT(ItemStack itemStack) {
        assert itemStack != null;
        NBTTagCompound NBTCompound = CraftItemStack.asNMSCopy(itemStack).getTag();
        if (NBTCompound == null) {
            return null;
        }
        return NBTCompound.asString();
    }
}
