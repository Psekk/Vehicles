package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu;

import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.ButtonElement;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.Element;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements.TextElement;
import me.psek.vehicles.psekutils.conversationapi.utils.ColorUtils;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class ChatMenuTest {
    private final List<Element> elements = new ArrayList<>();
    private final int width = 25;

    @BeforeEach
    void setUp() {
        elements.add(new TextElement().from(0, 0).with("&7test1"));
        elements.add(new TextElement().from(29, 0).with("&9t&uest2"));
    }

    @Test
    void test() {
        TextComponent mainTextComponent = new TextComponent();
        for (Element element : elements) {
            String coloredMessage = ColorUtils.formatted(element.getMessage());
            System.out.println(coloredMessage);
            //todo figure out what the coloredMessage is formatted in and convert that to a TextComponent and utilize the extra array
        }
        //return mainTextComponent;
    }

    @Test
    void addElements() {
        if (elementsCollide(elements)) {
            throw new IllegalStateException("Elements may not collide!");
        }
        //this.elements.addAll(elements);
    }

    boolean elementsCollide(List<Element> elements) {
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
}