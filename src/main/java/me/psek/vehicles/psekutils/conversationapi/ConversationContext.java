package me.psek.vehicles.psekutils.conversationapi;

import lombok.Getter;
import me.psek.vehicles.psekutils.Pair;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.Plugin;

import java.util.*;

@SuppressWarnings("unused")
public class ConversationContext {
    @Getter
    private final Conversation conversation;
    @Getter
    private final Plugin plugin;
    @Getter
    private final Map<String, Object> sessionData = new HashMap<>();
    @Getter
    private final Map<Prompt, List<Pair<Conversable, TextComponent>>> knownInput = new LinkedHashMap<>();

    public ConversationContext(Plugin plugin, Conversation conversation) {
        this.plugin = plugin;
        this.conversation = conversation;
    }

    public void addKnownInput(Prompt prompt, Pair<Conversable, TextComponent> pair) {
        if (!knownInput.containsKey(prompt)) {
            knownInput.put(prompt, new ArrayList<>(Collections.singletonList(pair)));
            return;
        }
        List<Pair<Conversable, TextComponent>> pairs = knownInput.get(prompt);
        pairs.add(pair);
        knownInput.replace(prompt, pairs);
    }

    public void removeKnownInputPair(Prompt prompt, Pair<Conversable, TextComponent> pair) {
        if (!knownInput.containsKey(prompt)) {
            return;
        }
        List<Pair<Conversable, TextComponent>> pairs = knownInput.get(prompt);
        if (!pairs.contains(pair)) {
            return;
        }
        pairs.remove(pair);
    }

    public void removeKnownInputPrompt(Prompt prompt) {
        if (!knownInput.containsKey(prompt)) {
            return;
        }
        knownInput.remove(prompt);
    }

    public void addSessionData(String key, Object value) {
        sessionData.put(key, value);
    }

    public boolean removeSessionData(String... keys) {
        for (String key : keys) {
            if (!sessionData.containsKey(key)) {
                return false;
            }
            sessionData.remove(key);
        }
        return true;
    }
}
