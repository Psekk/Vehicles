package me.psek.vehicles.psekutils.chatmenu;

import me.psek.vehicles.Vehicles;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void listener(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Vehicles.getInstance(), () -> {ChatMenuUtils.chatBufferAdd(event.getPlayer(), event.getMessage());});
    }
}
