package com.lockulockme.lockuchat.http;

public class HttpReqCacheClear {
    public static void clearHttp(String tag){
        UserBeanCacheUtils.getInstance().clearVipListeners(tag);
        VipDiamondsHelper.getInstance().clearListener(tag);
    }
}
