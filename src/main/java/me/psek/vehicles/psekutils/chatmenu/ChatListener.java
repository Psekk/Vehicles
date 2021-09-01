package me.psek.vehicles.psekutils.chatmenu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void listener(AsyncPlayerChatEvent event) {
        ChatMenuUtils.chatBufferAdd(event.getPlayer(), event.getMessage());
    }
}
