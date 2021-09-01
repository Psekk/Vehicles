package me.psek.vehicles.psekutils.conversationapi.roles;

import net.md_5.bungee.api.chat.TextComponent;

public class ListenerOnly implements ConversationRole {
    @Override
    public TextComponent getPrefix() {
        return new TextComponent("");
    }

    @Override
    public TextComponent getSuffix() {
        return new TextComponent("");
    }

    @Override
    public boolean canSpeak() {
        return false;
    }

    @Override
    public boolean canHear() {
        return true;
    }

    @Override
    public boolean isIsolated() {
        return true;
    }

    @Override
    public boolean isCaptured() {
        return false;
    }
}
