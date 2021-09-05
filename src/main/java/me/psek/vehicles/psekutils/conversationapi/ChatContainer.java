package me.psek.vehicles.psekutils.conversationapi;

import lombok.Getter;
import me.psek.vehicles.psekutils.Pair;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class ChatContainer {
    private static final Map<UUID, ChatContainer> UUIDS = new HashMap<>();

    @Getter
    private final List<Pair<UUID, TextComponent>> chatBuffer = new LinkedList<>();
    @Getter
    private final UUID uuid;

    private ChatContainer(UUID uuid) {
        this.uuid = uuid;
    }

    public static ChatContainer getChatContainer(UUID uuid) {
        if (!UUIDS.containsKey(uuid)) {
            ChatContainer chatContainer = new ChatContainer(uuid);
            UUIDS.put(uuid, chatContainer);
            return chatContainer;
        }
        return UUIDS.get(uuid);
    }

    public void add(TextComponent component) {
        if (chatBuffer.size() > 100) {
            chatBuffer.remove(0);
            chatBuffer.add(new Pair<>(uuid, component));
            return;
        }
        chatBuffer.add(new Pair<>(uuid, component));
    }
}
