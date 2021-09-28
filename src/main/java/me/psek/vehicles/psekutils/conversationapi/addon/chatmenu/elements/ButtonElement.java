package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements;

import lombok.Getter;
import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.enums.ChatMenuAction;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;
import net.md_5.bungee.api.chat.ClickEvent;

import java.util.UUID;

public class ButtonElement implements Element {
    private Pair<Integer, Integer> position;
    private String message;
    private String content;
    private ElementAction action;
    private ClickEvent.Action clickAction;
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
    public String getContent() {
        return content;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public ChatMenuAction getActionType() {
        return ChatMenuAction.CLICK;
    }

    @Override
    public void withCUUID(UUID uuid) {
        cUUID = uuid;
    }

    @Override
    public void performAction(Conversable conversable) {
        if (action == null) {
            conversable.getActiveConversations().get(cUUID).sendNextPrompt();
            return;
        }
        //todo maybe add option/default sendNextPrompt()
        action.onAction(conversable);
    }

    public ButtonElement from(int x, int y) {
        position = new Pair<>(x, y);
        return this;
    }

    public ButtonElement withAction(ElementAction action) {
        this.action = action;
        return this;
    }

    public ButtonElement withMessage(String message) {
        this.message = message;
        return this;
    }

    /*todo look if this content is useful and if the click action thing is useful
    public ButtonElement withContent(String content) {
        this.content = content;
        return this;
    }*/
}
