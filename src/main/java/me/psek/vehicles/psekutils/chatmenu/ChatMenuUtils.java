package me.psek.vehicles.psekutils.chatmenu;

import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.ChatMenu;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.ButtonElement;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.TextElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChatMenuUtils {
    public static void test(Player player, JavaPlugin plugin) {
        final int width = 30;
        ChatMenu chatMenu = new ChatMenu(width, true);
        chatMenu.addElements(
                new TextElement().with("&7Welcome &uto the server&f!").from(5, 0),
                new ButtonElement().from(0, 1).withMessage("&l&tTo get a tour of the shitty server we developed, please click here!")
                        .withAction(c -> {
                            c.getActiveConversations().get(chatMenu.getCUUID()).sendNextPrompt();
                            Player p = c.getPlayer();
                            p.setHealth(0);
                            Bukkit.getServer().broadcast(String.format("%s Has died from cancer!)", p.getName()), " w ");
                        }),
                new TextElement().with("End of cancer").from(11, 7)
        ).send(List.of(player), plugin);
    }
}
