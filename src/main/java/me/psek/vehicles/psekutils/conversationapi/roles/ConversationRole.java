package me.psek.vehicles.psekutils.conversationapi.roles;

import net.md_5.bungee.api.chat.TextComponent;

public interface ConversationRole {
    TextComponent getPrefix();
    TextComponent getSuffix();
    boolean canSpeak();
    boolean canHear();
    boolean isIsolated();
    boolean isCaptured();
}
