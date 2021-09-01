package me.psek.vehicles.psekutils.conversationapi.roles;

import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;

public class DefaultRole implements ConversationRole {
    @Setter
    private boolean isolated = true;
    @Setter
    private boolean captured = true;
    @Setter
    private boolean canSpeak = true;
    @Setter
    private boolean canHear = true;

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
        return canSpeak;
    }

    @Override
    public boolean canHear() {
        return canHear;
    }

    @Override
    public boolean isIsolated() {
        return isolated;
    }

    @Override
    public boolean isCaptured() {
        return captured;
    }
}
