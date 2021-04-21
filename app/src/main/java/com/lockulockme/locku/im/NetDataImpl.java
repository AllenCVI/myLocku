package com.lockulockme.locku.im;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.lockulockme.locku.application.MyApplication;
import com.lockulockme.locku.base.beans.BaseEntity;
import com.lockulockme.locku.base.beans.requestbean.HasRatingRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ReportExceptionRequestBean;
import com.lockulockme.locku.base.callback.StringCall;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.DownloadHelper;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.LoginoutManager;
import com.lockulockme.lockuchat.bean.rst.BatchGetUserRst;
import com.lockulockme.lockuchat.bean.rst.DiamondEnoughRst;
import com.lockulockme.lockuchat.bean.rst.GetGiftsRst;
import com.lockulockme.lockuchat.bean.rst.GetRtcTokenRst;
import com.lockulockme.lockuchat.bean.rst.ReduceDiamondsRst;
import com.lockulockme.lockuchat.bean.rst.ReportUserRst;
import com.lockulockme.lockuchat.bean.rst.SendGiftRst;
import com.lockulockme.lockuchat.bean.rst.UpChannelId;
import com.lockulockme.lockuchat.http.DownloadListener;
import com.lockulockme.lockuchat.http.NetData;
import com.lockulockme.lockuchat.http.NetDataListener;

import java.util.List;
import java.util.Locale;

import okhttp3.Response;

public class NetDataImpl implements NetData {

    @Override
    public void batchGetUserDetail(List<String> ids, final NetDataListener netDataListener) {
        BatchGetUserRst batchGetUserRst = new BatchGetUserRst(ids);
        netDataListener.onStart();
        OkGoUtils.getInstance().getRecentUsers(this, batchGetUserRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void getGiftList(NetDataListener netDataListener, Object tag) {
        GetGiftsRst getGiftsRst = new GetGiftsRst(Locale.getDefault().getLanguage());
        netDataListener.onStart();
        OkGoUtils.getInstance().getGiftList(tag, getGiftsRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void getDiamondsNum(NetDataListener netDataListener, Object tag) {
        netDataListener.onStart();
        OkGoUtils.getInstance().getDiamondsNum(tag, new Object(), new NetDataStringCall(netDataListener));
    }

    @Override
    public void sendGift(SendGiftRst sendGift, NetDataListener netDataListener, Object tag) {
        netDataListener.onStart();
        OkGoUtils.getInstance().sendGift(tag, sendGift, new NetDataStringCall(netDataListener));
    }

    @Override
    public void getSelfIsVip(NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().getVipStatus(this, new Object(), new NetDataStringCall(netDataListener));
    }

    @Override
    public void getDiamondsIsEnough(DiamondEnoughRst diamondEnough, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().diamondsIsEnough(this, diamondEnough, new NetDataStringCall(netDataListener));
    }

    @Override
    public void reduceDiamonds(ReduceDiamondsRst reduceDiamondsRst, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().reduceDiamonds(this, reduceDiamondsRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void getRtcToken(GetRtcTokenRst getRtcTokenRst, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().getNimToken(this, getRtcTokenRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void blockUser(ReportUserRst reportUserRst, NetDataListener netDataListener, Object tag) {
        netDataListener.onStart();
        OkGoUtils.getInstance().blockUser4String(tag, reportUserRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void allowCall(ReduceDiamondsRst reportUserRst, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().allowCall(this, reportUserRst, new NetDataStringCall(netDataListener));
    }

    @Override
    public void userAcceptStrategy(Object tag, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().userAcceptStrategy(tag, new NetDataStringCall(netDataListener));
    }

    @Override
    public void userAddStrategyNum(Object tag, NetDataListener netDataListener) {
        netDataListener.onStart();
        OkGoUtils.getInstance().userAddStrategyNum(tag, new NetDataStringCall(netDataListener));
    }

    @Override
    public void downloadVideo(String url, DownloadListener listener) {
        DownloadHelper.getInstance().downloadTask(url, new DownloadHelper.OnDownloadListener() {
            @Override
            public void onSuccess(String path) {
                listener.onSuccess(path);
            }

            @Override
            public void onProgress(float progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFailed() {
                listener.onFailed();
            }
        });
    }

    @Override
    public void hasRate(Object tag, String imId, NetDataListener listener) {
        listener.onStart();
        OkGoUtils.getInstance().checkUserRate(this, new HasRatingRequestBean(imId), new NetDataStringCall(listener));
    }

    @Override
    public void reportException(String page, String subType, int errorCode, String desc) {
        ReportExceptionRequestBean reportExceptionRequestBean = new ReportExceptionRequestBean();
        reportExceptionRequestBean.page = page;
        reportExceptionRequestBean.subType = subType;
        reportExceptionRequestBean.errorCode = errorCode;
        reportExceptionRequestBean.desc = desc;
        OkGoUtils.getInstance().reportException("report", reportExceptionRequestBean);
    }

    @Override
    public void upChannelId(Object tag, UpChannelId upChannelId, NetDataListener listener) {
        OkGoUtils.getInstance().upChannelId(tag, upChannelId, new StringCall() {
            @Override
            public void onSuccess(Response response) {

            }
        });
    }


    class NetDataStringCall extends StringCall {
        NetDataListener netDataListener;

        public NetDataStringCall(NetDataListener netDataListener) {
            this.netDataListener = netDataListener;
        }

        @Override
        public void onError(Response response) {
            super.onError(response);
            if (response!=null) {
                netDataListener.onFailed(response.message(), response.code());
            }else {
                netDataListener.onFailed("error", -1);
            }
        }

        @Override
        public void onSuccess(Response response) {
            if (response.isSuccessful()) {
                String result = convertResponse(response);
                LogUtil.LogD("OkGo", "" + result);
                BaseEntity baseEntity = new Gson().fromJson(result, BaseEntity.class);
                if (baseEntity.code == 1) {
                    AccountManager.getInstance().putPwd("");
                    LoginoutManager.loginout(MyApplication.getInstance().getCurActivity());
                    return;
                }
                netDataListener.onSuccess(result);
            } else {
                netDataListener.onFailed(response.message(), response.code());
            }
        }
    }
}
