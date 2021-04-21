package com.lockulockme.locku.base.eventbus;

public class AvatarModifiedEvent {
    public String filePath;

    public AvatarModifiedEvent(String file) {
        this.filePath = file;
    }
}
