package me.psek.vehicles.psekutils.conversationapi.listeners.packet;

import static me.psek.vehicles.psekutils.conversationapi.utils.ServerUtils.isSpigot;
import static me.psek.vehicles.psekutils.conversationapi.utils.ServerUtils.isPaper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ChatPacketListener {
    public ChatPacketListener(Plugin plugin) {
        run(plugin);
    }

    private void run(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                /*int i = 0;
                for (Object value : packet.getModifier().getValues()) {
                    @Nullable Class<?> clazz = Optional.ofNullable(value).map(Object::getClass).orElse(null);
                    System.out.println(String.format("Field: %s, Value: %s, Index: %s", clazz, value, i));
                    i++;
                }*/
                if (!isPaper() || !isSpigot()) {
                    throw new UnsupportedOperationException("Running unsupported minecraft server version, only Spigot+ is supported.");
                }
                StructureModifier<Object> modifier = packet.getModifier();
                Object component = modifier.read(1);
                EnumWrappers.ChatType chatMessageType = EnumWrappers.getChatTypeConverter().getSpecific(modifier.read(3));
                UUID uuid = (UUID) modifier.read(4);
                var y = ((Component) component);
                System.out.println(String.format("Text: %s, Type: %s, UUID: %s", isPaper() ? ((net.kyori.adventure.text.TextComponent) y).content() : ((TextComponent) component).getText(), chatMessageType, uuid ));
            }
        });
    }
}
