package me.psek.vehicles.psekutils.chatmenu;

import com.mojang.datafixers.util.Pair;
import me.psek.vehicles.Vehicles;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Stack;

import static org.bukkit.conversations.Prompt.END_OF_CONVERSATION;

public class ChatMenuUtils {
    public static Stack<Pair<Player, String>> chatBuffer = new Stack<>();

    public static void chatBufferAdd(Player player, String string) {
        Pair<Player, String> pair = new Pair<>(player, string);
        if (chatBuffer.size() < 32) {
            chatBuffer.push(pair);
            return;
        }
        chatBuffer.pop();
        chatBuffer.push(pair);
    }

    public static void clearPlayerChat(Vehicles plugin, Player player) {
        ConversationFactory conversationFactory = new ConversationFactory(plugin);
        conversationFactory.withModality(true);
        conversationFactory.withLocalEcho(false);
        Prompt prompt = new StringPrompt() {
            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return "";
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                return END_OF_CONVERSATION;
            }
        };
        conversationFactory.withFirstPrompt(prompt);
        Conversation conversation = conversationFactory.buildConversation(player);
        for (int i = 0; i < 250; i++) {
            conversation.begin();
            conversation.abandon();
        }
    }

    public static void restorePlayerChat(Vehicles plugin, Player player) {
        clearPlayerChat(plugin, player);
        for (Pair<Player, String> pair : chatBuffer) {
            ConversationFactory conversationFactory = new ConversationFactory(plugin);
            conversationFactory.withModality(true);
            conversationFactory.withLocalEcho(false);
            Prompt prompt = new StringPrompt() {
                @NotNull
                @Override
                public String getPromptText(@NotNull ConversationContext conversationContext) {
                    return pair.getSecond();
                }

                @Nullable
                @Override
                public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                    return END_OF_CONVERSATION;
                }
            };
            Conversation conversation = conversationFactory.withFirstPrompt(prompt).buildConversation(player);
            System.out.println(pair.getSecond());
            conversation.begin();
            conversation.abandon();
        }
        System.out.println(chatBuffer);
    }

    public static void sendTestMenu(Vehicles plugin, Player player) {
        ConversationFactory conversationFactory = new ConversationFactory(plugin);
        conversationFactory.withModality(true);
        conversationFactory.withLocalEcho(false);
        Prompt prompt = new StringPrompt() {
            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("test hover"));
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lulz");
                TextComponent textComponent = new TextComponent("test conversation");
                textComponent.setHoverEvent(hoverEvent);
                textComponent.setClickEvent(clickEvent);
                textComponent.setBold(true);
                textComponent.setColor(ChatColor.RED);
                return textComponent.toLegacyText();
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                return END_OF_CONVERSATION;
            }
        };
        Conversation conversation = conversationFactory.withFirstPrompt(prompt).buildConversation(player);
        conversation.begin();
        conversation.abandon();
    }

    static {
        Vehicles.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), Vehicles.getInstance());
    }
}
