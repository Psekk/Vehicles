package me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.elements;

import me.psek.vehicles.psekutils.conversationapi.Conversable;
import me.psek.vehicles.psekutils.conversationapi.addon.chatmenu.enums.ChatMenuAction;
import me.psek.vehicles.psekutils.conversationapi.utils.Pair;

import java.util.UUID;

public interface Element {
    /*@Getter
    private Pair<Integer, Integer> position;
    @Getter
    private String content;

    public Element at(int x, int y) {
        position = new Pair<>(x, y);
        return this;
    }

    public Element withContent(String content) {
        this.content = content;
        return this;
    }*/
    Pair<Integer, Integer> getPosition();
    String getMessage();
    Object getContent();
    int getLength();
    ChatMenuAction getActionType();

    /**
     * Please do not do anything with this yourself
     * @param uuid input uuid
     * @return the current element instance
     */
    void withCUUID(UUID uuid);
    void performAction(Conversable player);
}
