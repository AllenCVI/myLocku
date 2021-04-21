package com.lockulockme.locku.base.eventbus;

public class ReportEvent {

    public static final int NORMAL_TYPE = 1;//从其他页面进入
    public static final int SHOW_TYPE = 2;//从SHOW页面进入
    public String userStringId;
    public int type;

    public ReportEvent(String userStringId, int type) {
        this.userStringId = userStringId;
        this.type = type;
    }
}
