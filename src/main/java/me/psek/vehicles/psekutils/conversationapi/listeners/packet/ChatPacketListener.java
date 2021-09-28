package me.psek.vehicles.psekutils.conversationapi.listeners.packet;

import static me.psek.vehicles.psekutils.conversationapi.utils.ServerUtils.isSpigot;
import static me.psek.vehicles.psekutils.conversationapi.utils.ServerUtils.isPaper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.psek.vehicles.psekutils.conversationapi.ChatContainer;
import me.psek.vehicles.psekutils.conversationapi.Conversation;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
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
                if (!isSpigot()) {
                    throw new UnsupportedOperationException("Running unsupported minecraft server fork, only Spigot and its forks are supported.");
                }
                if (packet.getUUIDs().read(0).equals(Conversation.getIDENTIFIER())) {
                    return;
                }
                StructureModifier<Object> modifier = packet.getModifier();
                Object component;
                ChatType chatType = packet.getChatTypes().read(0);
                if (chatType == ChatType.GAME_INFO) return;
                UUID uuid = event.getPlayer().getUniqueId();
                //todo optimize this code and make it less weird, because this is dogshit
                component =
                        chatType == ChatType.CHAT
                        ? !isPaper()
                                ? modifier.read(0)
                                : modifier.read(1)
                        : modifier.read(0);
                ChatContainer.getChatContainer(uuid).add(uuid, new TextComponent(
                        chatType == ChatType.CHAT
                                ? !isPaper()
                                    ? ComponentConverter.fromWrapper(WrappedChatComponent.fromHandle(component))
                                    : BungeeComponentSerializer.get().serialize(((net.kyori.adventure.text.TextComponent) component))
                                : ComponentConverter.fromWrapper(WrappedChatComponent.fromHandle(component)) ));
                //ChatContainer.getChatContainer(uuid).getChatBuffer().forEach(p -> event.getPlayer().spigot().sendMessage(Conversation.getIDENTIFIER(), new TextComponent(p.getSecondValue().getExtra().get(0))));
            }
        });
    }
}
