package com.lockulockme.lockuchat.data;

public class SelfDataUtils {
    private SelfData selfData;
    private SelfDataUtils(){
    }

    private static class InstanceHelper{
        private static SelfDataUtils INSTANCE = new SelfDataUtils();
    }
    public static SelfDataUtils getInstance(){
        return SelfDataUtils.InstanceHelper.INSTANCE;
    }

    public SelfData getSelfData() {
        return selfData;
    }

    public void setSelfData(SelfData selfData) {
        this.selfData = selfData;
    }
}
