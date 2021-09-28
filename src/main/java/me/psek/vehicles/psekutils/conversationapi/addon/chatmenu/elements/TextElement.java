package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements;

import lombok.Getter;
import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.enums.ChatMenuAction;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TextElement implements Element {
    private Pair<Integer, Integer> position;
    private String message;
    private int length;
    @Getter
    private UUID cUUID;

    @Override
    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public @Nullable String getContent() {
        return null;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public ChatMenuAction getActionType() {
        return ChatMenuAction.TEXT;
    }

    @Override
    public void withCUUID(UUID uuid) {
        cUUID = uuid;
    }

    @Override
    public void performAction(Conversable player) { }

    public TextElement from(int x, int y) {
        position = new Pair<>(x, y);
        return this;
    }

    public TextElement with(String content) {
        this.message = content;
        this.length = content.length();
        return this;
    }
}
