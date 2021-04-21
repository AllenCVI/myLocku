package com.lockulockme.locku.zlocksix.im;

import com.google.gson.Gson;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.zlocksix.base.utils.AccountManager;
import com.lockulockme.locku.zlocksix.base.utils.VipManager;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.data.SelfData;
import com.lockulockme.lockuchat.utils.LogHelper;

public class SelfDataImpl implements SelfData {
    @Override
    public User getSelfUser() {
        UserInfo userInfo = AccountManager.getInstance().getCurrentUser();
        Gson gson=new Gson();
        String userStr=gson.toJson(userInfo);
        LogHelper.e("SelfDataImpl",userStr);
        User user= gson.fromJson(userStr,User.class);
        return user;
    }

    @Override
    public int getSelfIsVip4Sync() {
        int isVip= -1;
        VipResponseBean vipResp = VipManager.getInstance().getVipResp();
        if (vipResp!=null){
            isVip=vipResp.isVip ? 1 : 0;
        }
        return isVip;
    }
}
