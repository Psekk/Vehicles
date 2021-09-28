package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu;

import lombok.Getter;
import me.psek.vehicles.psekutils.conversationapi.*;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.Element;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.HoverElement;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.nms.NMSMediator;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.threads.ChatThread;
import me.psek.vehicles.psekutils.conversationapi.utils.ColorUtils;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;
import me.psek.vehicles.psekutils.conversationapi.utils.TextUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ChatMenu {
    private static final BungeeComponentSerializer bungeeComponentSerializer = BungeeComponentSerializer.get();
    private static final ChatThread CHAT_THREAD;
    @Getter
    private static final Map<UUID, ChatMenu> CHAT_MENUS = new HashMap<>();

    private final int width;
    private final boolean overflow;

    @Getter
    private final @NotNull UUID cUUID = UUID.randomUUID();
    @Getter
    private final @NotNull Map<Integer, LinkedList<Element>> grid = new TreeMap<>();

    @Getter
    private List<Element> elements = new ArrayList<>();

    static {
        CHAT_THREAD = new ChatThread();
    }

    public ChatMenu(int width, boolean overflow) {
        this.width = width;
        this.overflow = overflow;
    }

    public static ChatMenu getChatMenu(UUID uuid) {
        if (!CHAT_MENUS.containsKey(uuid)) {
            return null;
        }
        return CHAT_MENUS.get(uuid);
    }

    @SuppressWarnings("unused")
    public static void removeChatMenu(UUID uuid) {
        if (!CHAT_MENUS.containsKey(uuid)) {
            return;
        }
        CHAT_MENUS.remove(uuid);
    }

    public void send(List<Player> players, JavaPlugin plugin) {
        CHAT_THREAD.add(() -> {
            Conversation conversation = buildConversation(plugin, players.stream().map(Conversable::getConversable).collect(Collectors.toList()));
            conversation.startConversation();
        });
    }

    public ChatMenu addElements(Element... elements) {
        for (Element element : elements) {
            element.withCUUID(cUUID);
            this.elements.add(element);
        }
        this.elements.addAll(Arrays.stream(elements).toList()); //todo maybe add sorting (TreeMap)
        return this;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    private boolean elementsCollide() {
        Map<Integer, char[]> grid = new TreeMap<>();
        for (Element element : elements) {
            Pair<Integer, Integer> position = element.getPosition();
            int secondValue = position.getSecondValue();
            if (!grid.containsKey(secondValue)) {
                grid.put(secondValue, new char[width]);
            }
            char[] chars = grid.get(secondValue);
            int i = position.getFirstValue();
            for (char c : ChatColor.stripColor(element.getMessage()).toCharArray()) {
                if (i > width) {
                    if (!overflow) {
                        throw new IllegalStateException("The String may not overflow the current row! Consider enabling overflow.");
                    }
                    i = 0;
                    if (!grid.containsKey(secondValue + 1)) {
                        grid.put(secondValue + 1, new char[width]);
                    }
                    chars = grid.get(secondValue + 1);
                }
                if (chars[i] != 0) {
                    return true;
                }
                chars[i] = c;
                i++;
            }
        }
        return false;
    }

    private void updateGrid() {
        //todo add splitting at end of width and adding to next y line and duplicating the element - message diff
        for (Element element : elements) {
            int y = element.getPosition().getSecondValue();
            if (!grid.containsKey(y)) {
                grid.put(y, new LinkedList<>());
            }
            LinkedList<Element> elements = grid.get(y);
            elements.clear(); //todo look if this is the best approach or if its better to just clear the entire grid
            elements.add(element);
            grid.replace(y, elements);
        }
    }

    private @NotNull TextComponent buildTextComponent() {
        updateGrid();
        List<TextComponent> yTextComponents = new LinkedList<>();
        for (int y : grid.keySet()) {
            LinkedList<Element> elements = grid.get(y);
            TextComponent yTextComponent = new TextComponent();
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                int x = element.getPosition().getFirstValue();
                TextComponent xTextComponent = getHoverTextComponent(new TextComponent(ColorUtils.formatted(element.getMessage())), element);
                yTextComponent.addExtra(xTextComponent);
                yTextComponents.add(yTextComponent);
                if (i + 1 == elements.size()) {
                    continue;
                }
                Element nextElement = elements.get(i + 1);
                //potential bug - test this thoroughly
                if (element.getLength() + x + 1 == nextElement.getPosition().getFirstValue()) {
                    continue;
                }
                yTextComponent.addExtra(new TextComponent(TextUtils.newWhitespaceString(nextElement.getPosition().getFirstValue() - (element.getLength() + x))));
                yTextComponents.remove(yTextComponent);
                yTextComponents.add(yTextComponent);
            }
        }
        TextComponent gridTextComponent = new TextComponent();
        yTextComponents.forEach(gridTextComponent::addExtra);
        return gridTextComponent;
    }

    private TextComponent getHoverTextComponent(TextComponent startingComponent, Element element) {
        switch (element.getActionType()) {
            case CLICK -> startingComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "ClIcK24"));
            case HOVER -> {
                HoverElement hoverElement = (HoverElement) element;
                Object object = hoverElement.getContent();
                switch (hoverElement.getHoverAction()) {
                    case SHOW_TEXT -> {
                        if (!(object instanceof String string)) {
                            throw new IllegalArgumentException("Provided content is not an instanceof String");
                        }
                        startingComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorUtils.formatted(string))));
                    }
                    case SHOW_ENTITY -> {
                        if (!(object instanceof Entity entity)) {
                            throw new IllegalArgumentException("Provided content is not an instanceof Entity");
                        }
                        startingComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorUtils.formatted(String.format("%s\nType: %s\n%s", entity.getName(), entity.getType().name(), entity.getUniqueId())))));
                    }
                    case SHOW_ITEM -> {
                        if (!(object instanceof ItemStack itemStack)) {
                            throw new IllegalArgumentException("Provided content is not an instanceof ItemStack");
                        }
                        String nbt = NMSMediator.getNMS().getNBT(itemStack);
                        net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.event.HoverEvent.ShowItem> hoverEvent =
                                net.kyori.adventure.text.event.HoverEvent.showItem(Key.key(itemStack.getType().getKey().getKey()), itemStack.getAmount(), BinaryTagHolder.of(nbt));
                        startingComponent = new TextComponent(bungeeComponentSerializer.serialize(Component.text(ColorUtils.formatted(element.getMessage())).hoverEvent(hoverEvent)));
                    }
                }
            }
        }
        return startingComponent;
    }

    private Conversation buildConversation(JavaPlugin plugin, List<Conversable> conversables) {
        ConversationFactory conversationFactory = ConversationFactory.builder().
                participants(conversables)
                .firstPrompt(new Prompt() {
                    @Override
                    public @NotNull TextComponent getMessage(ConversationContext conversationContext) {
                        if (elementsCollide()) {
                            return new TextComponent("null");
                        }
                        return buildTextComponent();
                    }

                    @Override
                    public boolean waitForUserInput(ConversationContext conversationContext) {
                        return true;
                    }

                    @Override
                    public Prompt nextPrompt(ConversationContext conversationContext) {
                        return this;
                    }
                })
                .defaultClearChat(true)
                .restoreChatAtFinish(true)
                .restoreChatAtFinishDelay(0)
                .build();

        return new Conversation(plugin, conversationFactory, cUUID);
    }
}