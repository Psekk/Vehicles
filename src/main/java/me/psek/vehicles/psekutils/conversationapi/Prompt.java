package me.psek.vehicles.psekutils.conversationapi;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface Prompt extends Cloneable {
    public static Prompt END_OF_CONVERSATION = null;

    @NotNull
    TextComponent getMessage(ConversationContext conversationContext);
    boolean waitForUserInput(ConversationContext conversationContext);
    //gets next prompt on reaction
    @Nullable
    Prompt nextPrompt(ConversationContext conversationContext);
}
