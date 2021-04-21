package com.lockulockme.lockuchat.http;

import com.lockulockme.lockuchat.bean.rst.DiamondEnoughRst;
import com.lockulockme.lockuchat.bean.rst.GetRtcTokenRst;
import com.lockulockme.lockuchat.bean.rst.ReduceDiamondsRst;
import com.lockulockme.lockuchat.bean.rst.ReportUserRst;
import com.lockulockme.lockuchat.bean.rst.SendGiftRst;
import com.lockulockme.lockuchat.bean.rst.UpChannelId;

import java.util.List;

public interface NetData {
    void batchGetUserDetail(List<String> ids, NetDataListener netDataListener);

    void getGiftList(NetDataListener netDataListener,Object tag);

    void getDiamondsNum(NetDataListener netDataListener,Object tag);

    void sendGift(SendGiftRst sendGift, NetDataListener netDataListener,Object tag);

    void getSelfIsVip(NetDataListener netDataListener);

    void getDiamondsIsEnough(DiamondEnoughRst diamondEnough, NetDataListener netDataListener);

    void reduceDiamonds(ReduceDiamondsRst reduceDiamondsRst, NetDataListener netDataListener);

    void getRtcToken(GetRtcTokenRst getRtcTokenRst, NetDataListener netDataListener);

    void blockUser(ReportUserRst reportUserRst, NetDataListener netDataListener,Object tag);

    void allowCall(ReduceDiamondsRst reportUserRst, NetDataListener netDataListener);

    void userAcceptStrategy(Object tag, NetDataListener netDataListener);

    void userAddStrategyNum(Object tag, NetDataListener netDataListener);

    void downloadVideo(String url, DownloadListener netDataListener);

    void hasRate(Object tag, String imId, NetDataListener listener);

    void reportException(String page, String subType, int errorCode, String desc);

    void upChannelId(Object tag, UpChannelId upChannelId, NetDataListener listener);
}
