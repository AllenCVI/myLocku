package com.lockulockme.lockuchat.aavg2.nertcvideocall.utils;

import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.GetRtcTokenRsp;
import com.lockulockme.lockuchat.bean.rst.GetRtcTokenRst;
import com.lockulockme.lockuchat.http.GsonUtils;
import com.lockulockme.lockuchat.http.NetDataListener;
import com.lockulockme.lockuchat.http.NetDataUtils;

public class RtcTokenHelper {
    private RtcTokenHelper() {

    }
    private static class InstanceHolder {
        private static RtcTokenHelper INSTANCE = new RtcTokenHelper();
    }
    public static RtcTokenHelper getInstance() {
        return RtcTokenHelper.InstanceHolder.INSTANCE;
    }

    public void getToken(long uid, OnGetTokenListener onGetTokenListener){
        if (uid==0) uid=System.currentTimeMillis();
        GetRtcTokenRst getRtcTokenRst =new GetRtcTokenRst(uid);
        NetDataUtils.getInstance().getNetData().getRtcToken(getRtcTokenRst, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, GetRtcTokenRsp.class);
                if (baseBean.code != 0) {
                    onGetTokenListener.onFailed();
                    return;
                }
                GetRtcTokenRsp getRtcTokenRsp = (GetRtcTokenRsp) baseBean.data;
                onGetTokenListener.onSuccess(getRtcTokenRsp.nimAuthToken);
            }

            @Override
            public void onFailed(String msg, int code) {
                onGetTokenListener.onFailed();
            }
        });
    }


    public interface OnGetTokenListener {
        void onSuccess(String token);
        void onFailed();
    }
}
