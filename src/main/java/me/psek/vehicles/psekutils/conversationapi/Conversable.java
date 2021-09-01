package me.psek.vehicles.psekutils.conversationapi;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.psekutils.conversationapi.roles.ConversationRole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Conversable {
    private static final Map<Player, Conversable> CONVERSABLES = new WeakHashMap<>();

    @Getter
    private final List<Conversation> Conversations = new ArrayList<>();
    @Getter
    private final Player player;

    @Getter
    @Setter
    private Map<Conversation, ConversationRole> roles = new HashMap<>();

    public Conversable(@NotNull Player player) {
        this.player = player;
        CONVERSABLES.put(player, this);
    }

    public static @NotNull Conversable getConversable(Player player) {
        if (CONVERSABLES.containsKey(player))
            return CONVERSABLES.get(player);
        Conversable conversable = new Conversable(player);
        CONVERSABLES.put(player, conversable);
        return conversable;
    }

    public void addToConversation(Conversation conversation) {
        Conversations.add(conversation);
    }

    //also allows you to give an existing conversation and change the role
    public void addRole(Conversation conversation, ConversationRole role) {
        if (!roles.containsKey(conversation)) {
            roles.put(conversation, role);
        }
        roles.replace(conversation, role);
    }

    public void removeRole(Conversation conversation) {
        if (!roles.containsKey(conversation)) {
            return;
        }
        roles.remove(conversation);
    }
}
