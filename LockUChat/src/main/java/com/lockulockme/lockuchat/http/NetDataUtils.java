package com.lockulockme.lockuchat.http;


public class NetDataUtils{
    private NetData netData;
    private NetDataUtils(){
    }

    private static class InstanceHelper{
        private static NetDataUtils INSTANCE = new NetDataUtils();
    }
    public static NetDataUtils getInstance(){
        return NetDataUtils.InstanceHelper.INSTANCE;
    }

    public NetData getNetData() {
        return netData;
    }

    public void setNetData(NetData netData) {
        this.netData = netData;
    }


}
