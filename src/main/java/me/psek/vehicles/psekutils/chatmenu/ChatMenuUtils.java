package me.psek.vehicles.psekutils.chatmenu;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

public class ChatMenuUtils {
    public static Stack<Pair<Player, String>> chatBuffer = new Stack<>();

    public static void chatBufferAdd(Player player, String string) {
        Pair<Player, String> pair = new Pair<>(player, string);
        if (chatBuffer.size() < 256) {
            chatBuffer.push(pair);
            return;
        }
        chatBuffer.pop();
        chatBuffer.push(pair);
    }

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
                .captured(true)
                .isolated(true)
                .participants(Bukkit.getOnlinePlayers().stream().map(Conversable::getConversable).collect(Collectors.toList()))
                .prefix(new TextComponent("[sonaryMC] "))
                .firstPrompt(new Prompt() {
                    @Override
                    public @NotNull TextComponent getMessage(ConversationContext conversationContext) {
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
                }).build();
        new Conversation(plugin, conversationFactory);
    }

    static {
        Vehicles.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), Vehicles.getInstance());
    }
}
