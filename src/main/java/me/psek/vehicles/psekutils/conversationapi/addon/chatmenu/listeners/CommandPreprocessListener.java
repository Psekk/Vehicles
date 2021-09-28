package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.listeners;

import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.ChatMenu;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.Element;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.enums.ChatMenuAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@SuppressWarnings("ConstantConditions")
public class CommandPreprocessListener implements Listener {
    public void listener(PlayerCommandPreprocessEvent event) {
        //todo test thoroughly - not sure how the message is altered by bukkit
        if (!event.getMessage().equals("/ClIcK24")) return;
        Player player = event.getPlayer();
        if (!ChatMenu.getCHAT_MENUS().containsKey(player.getUniqueId())) return;
        for (Element element : ChatMenu.getChatMenu(player.getUniqueId()).getElements()) {
            if (element.getActionType() != ChatMenuAction.CLICK) {
                continue;
            }
            //todo make conversable so you can send the next prompt or make this automatic
            element.performAction(Conversable.getConversable(player));
        }
    }
}
