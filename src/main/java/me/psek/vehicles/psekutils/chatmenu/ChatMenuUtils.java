package me.psek.vehicles.psekutils.chatmenu;

import me.psek.vehicles.Vehicles;
import me.psek.vehicles.psekutils.conversationapi.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class ChatMenuUtils {
    public static void test(Player player, Vehicles plugin) {
        assert player != null;
        Prompt test = new Prompt() {
            @Override
            public @NotNull TextComponent getMessage(ConversationContext conversationContext) {
                TextComponent textComponent = new TextComponent("superkewl hoverable and clickable text");
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sonarymc.com"));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                        conversationContext
                                .getKnownInput()
                                .get(conversationContext.getConversation().getPreviousPrompt())
                                .get(0)
                                .getSecondValue()
                                .getText()) ));
                return textComponent;
            }

            @Override
            public boolean waitForUserInput(ConversationContext conversationContext) {
                return false;
            }

            @Override
            public @Nullable Prompt nextPrompt(ConversationContext conversationContext) {
                return Prompt.END_OF_CONVERSATION;
            }
        };
        ConversationFactory conversationFactory = ConversationFactory.builder()
                .participants(Bukkit.getOnlinePlayers().stream().map(Conversable::getConversable).collect(Collectors.toList()))
                .prefix(new TextComponent("[SonaryMC] "))
                .firstPrompt(new Prompt() {
                    @Override
                    public @NotNull TextComponent getMessage(ConversationContext conversationContext) {
                        //conversationContext.getConversation().getParticipants().get(1).addRole(conversationContext.getConversation(), new SpeakerOnly());
                        return new TextComponent("beginning");
                    }

                    @Override
                    public boolean waitForUserInput(ConversationContext conversationContext) {
                        return true;
                    }

                    @Override
                    public Prompt nextPrompt(ConversationContext conversationContext) {
                        return test;
                    }
                })
                .defaultClearChat(false)
                .restoreChatAtFinish(false)
                .restoreChatAtFinishDelay(0)
                .build();
        new Conversation(plugin, conversationFactory);
    }

    static {
        new ConversationAPI(Vehicles.getInstance(), 500);
    }
}
