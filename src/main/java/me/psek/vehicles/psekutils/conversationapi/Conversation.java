package me.psek.vehicles.psekutils.conversationapi;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.psekutils.conversationapi.events.ConversationEndEvent;
import me.psek.vehicles.psekutils.conversationapi.listeners.ChatListener;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//todo make a ConversationAPI.class and make a constructor that initializes the listeners and make a timeout ticker that unregisters and registers at a new request

@SuppressWarnings("unused")
public class Conversation {
    @Getter
    private static final List<Conversable> IN_CONVERSATION = new ArrayList<>();

    @Getter
    private final Set<Conversable> participants = new HashSet<>();
    @Getter
    private final ConversationContext conversationContext;
    @Getter
    private final Prompt firstPrompt;
    private final Plugin plugin;

    @Getter
    @Setter
    private ConversationState conversationState = ConversationState.UNSTARTED;
    @Getter
    @Setter
    private TextComponent prefix;
    @Getter
    @Setter
    private Prompt previousPrompt;
    @Getter
    private Prompt currentPrompt;

    public Conversation(Plugin plugin, ConversationFactory conversationFactory) {
        conversationContext = new ConversationContext(plugin, this);
        Prompt previousPrompt = conversationFactory.getFirstPrompt();
        this.currentPrompt = previousPrompt;
        this.firstPrompt = previousPrompt;
        this.previousPrompt = previousPrompt;
        this.plugin = plugin;
        this.prefix = conversationFactory.getPrefix();
        startConversation(conversationFactory);
    }

    public void startConversation(ConversationFactory conversationFactory) {
        if (ChatListener.listener == null) {
            ChatListener chatListener = new ChatListener();
            Bukkit.getServer().getPluginManager().registerEvents(chatListener, plugin);
            ChatListener.listener = chatListener;
        }
        participants.addAll(conversationFactory.getParticipants());
        for (Conversable conversable : conversationFactory.getParticipants()) {
            conversable.addToConversation(this);
            if (IN_CONVERSATION.contains(conversable)) {
                continue;
            }
            IN_CONVERSATION.add(conversable);
        }
        conversationState = ConversationState.STARTED;
        sendNextPrompt();
    }

    public void sendNextPrompt() {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(prefix);
        componentBuilder.append(currentPrompt.getMessage(conversationContext));
        BaseComponent[] baseComponents = componentBuilder.create();
        for (Conversable conversable : participants) {
            if (!conversable.getRoles().get(this).canHear()) {
                continue;
            }
            conversable.getPlayer().spigot().sendMessage(baseComponents);
        }
        Prompt prompt = getCurrentPrompt().nextPrompt(conversationContext);
        if (prompt == null) {
            endConversation(ConversationEndReason.NORMAL);
            return;
        }
        previousPrompt = currentPrompt;
        currentPrompt = prompt;
        if (currentPrompt.waitForUserInput(conversationContext)) {
            sendNextPrompt();
        }
    }

    public synchronized void endConversation(@NotNull ConversationEndReason reason) {
        conversationState = ConversationState.ENDED;
        ConversationEndEvent event = new ConversationEndEvent(reason, this);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(event));
        for (Conversable conversable : participants) {
            if (!IN_CONVERSATION.contains(conversable)) {
                continue;
            }
            IN_CONVERSATION.remove(conversable);
        }
    }

    public void addParticipants(@NotNull Conversable... participants) {
        for (Conversable conversable : participants) {
            this.participants.add(conversable);
            conversable.addToConversation(this);
        }
    }

    public boolean removeParticipant(@NotNull Conversable participant) {
        if (!participants.contains(participant)) {
            return false;
        }
        participants.remove(participant);
        return true;
    }

    public enum ConversationState {
        UNSTARTED,
        STARTED,
        ENDED
    }

    public enum ConversationEndReason {
        INTERRUPTED,
        EXTERNAL,
        EXIT_WORD,
        NORMAL
    }
}
