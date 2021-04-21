package com.lockulockme.lockuchat.event;

public interface EventSubscriber<T> {
    void onEvent(T t);
}
