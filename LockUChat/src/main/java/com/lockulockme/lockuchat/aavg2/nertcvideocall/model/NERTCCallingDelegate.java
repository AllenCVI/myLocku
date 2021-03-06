package com.lockulockme.lockuchat.aavg2.nertcvideocall.model;

import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;

public interface NERTCCallingDelegate {

    /**
     * 返回操作
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    void onError(int errorCode, String errorMsg);

    /**
     * 被邀请通话回调
     *
     * @param invitedEvent 邀请参数
     */
    void onInvited(InvitedEvent invitedEvent);

    /**
     * 如果有用户同意进入通话频道，那么会收到此回调
     *
     * @param uid 进入通话的用户
     */
    void onUserEnter(long uid, String accId);


    /**
     * 如果有用户同意离开通话，那么会收到此回调
     *
     * @param accountId 离开通话的用户
     */
    void onCallEnd(String accountId);

    /**
     * 用户离开时回调
     * @param accountId
     */
    void onUserLeave(String accountId);

    /**
     * 拒绝通话
     *
     * @param userId 拒绝通话的用户
     */
    void onRejectByUserId(String userId);


    /**
     * 邀请方忙线
     *
     * @param userId 忙线用户
     */
    void onUserBusy(String userId);

    /**
     * 作为被邀请方会收到，收到该回调说明本次通话被取消了
     */
    void onCancelByUserId(String userId);


    /**
     * 远端用户开启/关闭了摄像头
     *
     * @param userId           远端用户ID
     * @param isVideoAvailable true:远端用户打开摄像头  false:远端用户关闭摄像头
     */
    void onCameraAvailable(long userId, boolean isVideoAvailable);

    /**
     * 远端用户开启/关闭了麦克风
     *
     * @param userId           远端用户ID
     * @param isAudioAvailable true:远端用户打开麦克风  false:远端用户关闭麦克风
     */
    void onAudioAvailable(long userId, boolean isAudioAvailable);

    /**
     * 呼叫超时
     */
    void timeOut();

    /**
     * 通话channelid
     */
    void onGetChannelId(long channelId);
    /**
     * IM的channelid
     */
    void onGetIMChannelId(String channelId);

    /**
     * 非正常挂断
     */
    void onUserDisconnect(String userId);


    /**
     * 网络状态回调
     *
     * @param stats
     */
    void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats);
}
