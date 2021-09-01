package me.psek.vehicles.psekutils.conversationapi.listeners;

import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.Conversation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST)
    public void listener(PlayerQuitEvent event) {
        Conversable conversable = Conversable.getConversable(event.getPlayer());
        if (!Conversation.getIN_CONVERSATION().contains(conversable)) {
            return;
        }
        Conversation.getIN_CONVERSATION().remove(conversable);
    }
}
