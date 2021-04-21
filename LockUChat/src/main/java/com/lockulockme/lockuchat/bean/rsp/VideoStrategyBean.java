package com.lockulockme.lockuchat.bean.rsp;

public class VideoStrategyBean {
    public String url;
    public String filePath;
    public long time;

    public VideoStrategyBean(String url, String filePath, long time) {
        this.url = url;
        this.filePath = filePath;
        this.time = time;
    }
}
