package me.psek.vehicles.psekutils.conversationapi;

import me.psek.vehicles.psekutils.conversationapi.listeners.ChatListener;
import me.psek.vehicles.psekutils.conversationapi.listeners.packet.ChatPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public class ConversationAPI {
    public ConversationAPI(JavaPlugin plugin, long timeout) {
        //Objects.requireNonNull(plugin.getCommand("24vs73x501")).setExecutor(new ChatMenuCommand());
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
