package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements;

import lombok.Getter;
import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.enums.ChatMenuAction;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;
import net.md_5.bungee.api.chat.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HoverElement implements Element {
    private String message;
    private Object content;
    private Pair<Integer, Integer> position;
    private HoverEvent.Action hoverAction;
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
    public ChatMenuAction getActionType() {
        return ChatMenuAction.HOVER;
    }

    @Override
    public void withCUUID(UUID uuid) {
        cUUID = uuid;
    }

    @Override
    public void performAction(Conversable player) { }

    @Override
    public @Nullable Object getContent() {
        return content;
    }

    @Override
    public int getLength() {
        return length;
    }

    public @NotNull HoverEvent.Action getHoverAction() {
        if (hoverAction == null) {
            throw new NullPointerException("HoverAction is null which is not allowed!");
        }
        return hoverAction;
    }

    public HoverElement from(int x, int y) {
        position = new Pair<>(x, y);
        return this;
    }

    public HoverElement withMessage(String message) {
        this.message = message;
        this.length = message.length();
        return this;
    }

    public HoverElement withHoverAction(HoverEvent.Action action) {
        hoverAction = action;
        return this;
    }

    public HoverElement withContent(Object content) {
        if (hoverAction == null) {
            throw new IllegalStateException("HoverElement#withContent() may only be called after HoverElement#withHoverAction() has been called!");
        }
        this.content = content;
        return this;
    }
}
