package me.psek.vehicles.psekutils.conversationapi;

import lombok.Getter;
import lombok.Setter;
import me.psek.vehicles.psekutils.Pair;
import me.psek.vehicles.psekutils.conversationapi.events.ConversationEndEvent;
import me.psek.vehicles.psekutils.conversationapi.listeners.ChatListener;
import me.psek.vehicles.psekutils.conversationapi.roles.DefaultRole;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//todo make a ConversationAPI.class and make a constructor that initializes the listeners and make a timeout ticker that unregisters and registers at a new request

@SuppressWarnings("unused")
public class Conversation {
    @Getter
    private static final UUID IDENTIFIER = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
    @Getter
    private static final List<Conversable> IN_CONVERSATION = new ArrayList<>();

    @Getter
    private final UUID cUUID;
    @Getter
    private final List<Conversable> participants = new ArrayList<>();
    @Getter
    private final ConversationContext conversationContext;
    @Getter
    private final Prompt firstPrompt;
    private final Plugin plugin;
    private final ConversationFactory conversationFactory;

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

    public Conversation(Plugin plugin, ConversationFactory conversationFactory, @Nullable UUID cUUID) {
        conversationContext = new ConversationContext(plugin, this);
        Prompt previousPrompt = conversationFactory.getFirstPrompt();
        this.conversationFactory = conversationFactory;
        this.currentPrompt = previousPrompt;
        this.firstPrompt = previousPrompt;
        this.previousPrompt = previousPrompt;
        this.plugin = plugin;
        this.prefix = conversationFactory.getPrefix();
        this.cUUID = cUUID == null ? UUID.randomUUID() : cUUID;
    }

    //todo look at new fancy ways to clear chat
    private void clearChat(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage(IDENTIFIER, "");
        }
    }

    public void restoreChat() {
        for (Conversable conversable : participants) {
            if (!conversable.getRoles().get(this).canHear()) {
                continue;
            }
            Player player = conversable.getPlayer();
            System.out.printf("Restored player %s's chat%n", player);
            clearChat(player);
            ChatContainer chatContainer = ChatContainer.getChatContainer(player.getUniqueId());
            System.out.printf("size of chat buffer: %s", chatContainer.getChatBuffer().size());
            for (Pair<UUID, TextComponent> pair : chatContainer.getChatBuffer()) {
                player.spigot().sendMessage(IDENTIFIER, pair.getSecondValue());
            }
        }
    }

    public void startConversation() {
        ChatListener.setListener(new ChatListener(), plugin);
        participants.addAll(conversationFactory.getParticipants());
        for (Conversable conversable : conversationFactory.getParticipants()) {
            conversable.addToConversation(cUUID, this);
            if (conversable.getRoles().get(this) == null) {
                conversable.addRole(this, new DefaultRole());
            }
            if (IN_CONVERSATION.contains(conversable)) {
                continue;
            }
            IN_CONVERSATION.add(conversable);
            if (!conversable.getRoles().get(this).canHear() || !conversationFactory.isDefaultClearChat()){
                continue;
            }
            clearChat(conversable.getPlayer());
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
            if (conversationFactory.isDefaultClearChat()) {
                clearChat(conversable.getPlayer());
            }
            conversable.getPlayer().spigot().sendMessage(ChatMessageType.SYSTEM, IDENTIFIER, baseComponents);
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

    public void endConversation(@NotNull ConversationEndReason reason) {
        conversationState = ConversationState.ENDED;
        ConversationEndEvent event = new ConversationEndEvent(reason, this);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(event));
        for (Conversable conversable : participants) {
            if (!IN_CONVERSATION.contains(conversable)) {
                continue;
            }
            IN_CONVERSATION.remove(conversable);
            //conversable.removeFromConversation(this); //todo maybe keep it for a certain period to keep a small history (independent List to keep iteration time down) and yet keep max size down (implementing max size + overwriting)
        }
        if (!conversationFactory.isRestoreChatAtFinish() || conversationFactory.getRestoreChatAtFinishDelay() == 0L) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::restoreChat, conversationFactory.getRestoreChatAtFinishDelay());
    }

    public void addParticipants(@NotNull Conversable... participants) {
        for (Conversable conversable : participants) {
            this.participants.add(conversable);
            conversable.addToConversation(cUUID, this);
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
