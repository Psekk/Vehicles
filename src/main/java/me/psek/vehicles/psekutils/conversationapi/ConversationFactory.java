package me.psek.vehicles.psekutils.conversationapi;

import lombok.Builder;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@Builder
public class ConversationFactory {
    private final Prompt firstPrompt;
    private final List<Conversable> participants;
    private final TextComponent prefix;
    private final boolean defaultClearChat;
    private final boolean restoreChatAtFinish;
    private final long restoreChatAtFinishDelay;
}
