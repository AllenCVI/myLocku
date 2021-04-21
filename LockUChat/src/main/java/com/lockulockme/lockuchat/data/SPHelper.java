package com.lockulockme.lockuchat.data;

import com.blankj.utilcode.util.SPUtils;

public class SPHelper {
    private static final String sp_name="kankan_im";
    private static class InstanceHelper{
        private static SPHelper INSTANCE = new SPHelper();
    }
    public static SPHelper getInstance(){
        return SPHelper.InstanceHelper.INSTANCE;
    }

    public SPUtils getSPUtils(){
        return SPUtils.getInstance(sp_name);
    }

}
