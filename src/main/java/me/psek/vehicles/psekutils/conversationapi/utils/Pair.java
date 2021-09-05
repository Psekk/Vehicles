package me.psek.vehicles.psekutils.conversationapi.utils;

import lombok.Getter;

public class Pair<T ,E> {
    @Getter
    private final T firstValue;
    @Getter
    private final E secondValue;

    public Pair(T firstValue, E secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }
}
