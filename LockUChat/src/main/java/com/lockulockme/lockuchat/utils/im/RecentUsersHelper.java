package com.lockulockme.lockuchat.utils.im;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

public class RecentUsersHelper {
    private RecentUsersHelper(){
    }
    private static class InstanceHelper{
        private static RecentUsersHelper INSTANCE = new RecentUsersHelper();
    }
    public static RecentUsersHelper getInstance(){
        return RecentUsersHelper.InstanceHelper.INSTANCE;
    }

    public void getRecentUsers(RequestCallbackWrapper<List<RecentContact>> requestCallbackWrapper){
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(requestCallbackWrapper);
    }
}
