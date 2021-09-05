package me.psek.vehicles.psekutils.conversationapi;

import me.psek.vehicles.psekutils.conversationapi.listeners.ChatListener;
import me.psek.vehicles.psekutils.conversationapi.listeners.packet.ChatPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class ConversationAPI {
    public ConversationAPI(Plugin plugin, long timeout) {
        run(plugin, timeout);
    }

    private void run(Plugin plugin, long timeout) {
        new ChatPacketListener(plugin);
        ChatListener.setListener(new ChatListener(), plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (ChatListener.listener == null) {
                return;
            }
            if (Conversation.getIN_CONVERSATION().size() != 0) {
                return;
            }
            AsyncPlayerChatEvent.getHandlerList().unregister(ChatListener.listener);
            ChatListener.listener = null;
        }, timeout, timeout);
    }
}
