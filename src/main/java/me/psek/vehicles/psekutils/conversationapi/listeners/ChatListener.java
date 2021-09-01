package me.psek.vehicles.psekutils.conversationapi.listeners;

import me.psek.vehicles.psekutils.Pair;
import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.Conversation;
import me.psek.vehicles.psekutils.conversationapi.roles.ConversationRole;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

//todo unregister when no conversation are active to make it more lightweight :D
public class ChatListener implements Listener {
    public static @Nullable ChatListener listener;

    //todo test the isolated part (untested as of rn)
    @EventHandler(priority = EventPriority.HIGHEST)
    private void listener(AsyncPlayerChatEvent event) {
        //todo look at this code again cus it might be bad
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
            if (conversation == null) return; //todo maybe look into automatically making the conversation state ended
            if (conversation.getConversationState() != Conversation.ConversationState.STARTED) continue;
            if (!roles.get(conversation).canSpeak()) continue;
            if (roles.get(conversation).isCaptured()) {
                event.setCancelled(true);
            }
            conversation.getConversationContext().addKnownInput(conversation.getPreviousPrompt(), new Pair<>(Conversable.getConversable(player), message));
            //if (!conversation.getCurrentPrompt().waitForUserInput(conversation.getConversationContext())) return; //todo fix this, this makes no sense
            conversation.sendNextPrompt();
        }
    }
}
