package me.psek.vehicles.psekutils.conversationapi;

import me.psek.vehicles.psekutils.conversationapi.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class ConversationResourceSaver {
    public ConversationResourceSaver(Plugin plugin, long timeout) {
        run(plugin, timeout);
    }

    private void run(Plugin plugin, long timeout) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (ChatListener.listener == null) {
                return;
            }
            AsyncPlayerChatEvent.getHandlerList().unregister(ChatListener.listener);
            ChatListener.listener = null;
        }, timeout, timeout);
    }
}
