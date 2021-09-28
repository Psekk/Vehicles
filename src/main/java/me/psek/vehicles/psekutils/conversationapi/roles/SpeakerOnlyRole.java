package me.psek.vehicles.psekutils.conversationapi.roles;

import net.md_5.bungee.api.chat.TextComponent;

public class SpeakerOnlyRole implements ConversationRole{
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
        return true;
    }

    @Override
    public boolean canHear() {
        return false;
    }

    @Override
    public boolean isIsolated() {
        return false;
    }

    @Override
    public boolean isCaptured() {
        return true;
    }
}
