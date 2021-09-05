package me.psek.vehicles.psekutils.conversationapi.listeners;

import me.psek.vehicles.psekutils.Pair;
import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.Conversation;
import me.psek.vehicles.psekutils.conversationapi.roles.ConversationRole;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

//todo unregister when no conversation are active to make it more lightweight :D
public class ChatListener implements Listener {
    public static @Nullable ChatListener listener;

    public static void setListener(ChatListener listener, Plugin plugin) {
        if (ChatListener.listener != null) return;
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        ChatListener.listener = listener;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void listener(AsyncPlayerChatEvent event) {
        //todo add incoming and outgoing chat blocking if the delay is still active (the delay for restoreChat())
        for (Conversable conversable : Conversation.getIN_CONVERSATION()) {
            for (Conversation conversation : conversable.getConversations()) {
                if (!conversable.getRoles().get(conversation).isIsolated()) {
                    continue;
                }
                event.getRecipients().remove(conversable.getPlayer());
            }
        }
        Player player = event.getPlayer();
        Conversable conversable = Conversable.getConversable(player);
        List<Conversation> conversations = conversable.getConversations();
        if (conversations.size() < 1) return;
        TextComponent message = new TextComponent(event.getMessage());
        Map<Conversation, ConversationRole> roles = conversable.getRoles();
        for (Conversation conversation : conversations) {
            if (conversation == null) continue;
            if (conversation.getConversationState() != Conversation.ConversationState.STARTED) continue;
            if (!roles.get(conversation).canSpeak()) continue;
            if (roles.get(conversation).isCaptured()) {
                event.setCancelled(true);
            }
            conversation.getConversationContext().addKnownInput(conversation.getPreviousPrompt(), new Pair<>(Conversable.getConversable(player), message));
            conversation.sendNextPrompt();
        }
    }
}
