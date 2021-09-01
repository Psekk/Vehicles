package me.psek.vehicles.psekutils.conversationapi.events;

import lombok.Getter;
import me.psek.vehicles.psekutils.conversationapi.Conversation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ConversationEndEvent extends Event {
    private static final @NotNull HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Conversation.ConversationEndReason reason;
    @Getter
    private final Conversation conversation;

    public ConversationEndEvent(Conversation.ConversationEndReason reason, Conversation conversation) {
        this.reason = reason;
        this.conversation = conversation;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
