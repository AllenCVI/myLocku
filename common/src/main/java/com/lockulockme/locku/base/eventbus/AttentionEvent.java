package com.lockulockme.locku.base.eventbus;

public class AttentionEvent {
    public static int ATTENTION_TYPE = 1001;
    public static int UN_ATTENTION_TYPE = 1002;
    public int eventType;
    public String userStringId;

    public AttentionEvent(int eventType, String userStringId) {
        this.eventType = eventType;
        this.userStringId = userStringId;
    }
}
