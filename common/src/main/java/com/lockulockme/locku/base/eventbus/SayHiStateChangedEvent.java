package com.lockulockme.locku.base.eventbus;

public class SayHiStateChangedEvent {
    public String userStringId;

    public SayHiStateChangedEvent(String userStringId) {
        this.userStringId = userStringId;
    }
}
