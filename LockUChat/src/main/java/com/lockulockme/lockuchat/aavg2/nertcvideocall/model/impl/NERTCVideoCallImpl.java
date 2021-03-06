package com.lockulockme.lockuchat.aavg2.nertcvideocall.model.impl;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.CallOrderListener;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.JoinChannelCallBack;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCCallingDelegate;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.UserInfoInitCallBack;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.VideoCallOptions;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ControlInfo;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CustomInfo;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.NrtcCallStatus;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.RtcTokenHelper;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.Utils;
import com.lockulockme.lockuchat.utils.ContextHelper;
import com.lockulockme.lockuchat.utils.ReportConstant;
import com.lockulockme.lockuchat.utils.ReportUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.stats.NERtcAudioRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcAudioSendStats;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.stats.NERtcStats;
import com.netease.lava.nertc.sdk.stats.NERtcStatsObserver;
import com.netease.lava.nertc.sdk.stats.NERtcVideoRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcVideoSendStats;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.SignallingServiceObserver;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelStatus;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.constant.InviteAckStatus;
import com.netease.nimlib.sdk.avsignalling.constant.SignallingEventType;
import com.netease.nimlib.sdk.avsignalling.event.CanceledInviteEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCloseEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCommonEvent;
import com.netease.nimlib.sdk.avsignalling.event.ControlEvent;
import com.netease.nimlib.sdk.avsignalling.event.InviteAckEvent;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserJoinEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserLeaveEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelBaseInfo;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.avsignalling.model.MemberInfo;
import com.netease.nimlib.sdk.avsignalling.model.SignallingPushConfig;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.NetCallAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NERTCVideoCallImpl extends NERTCVideoCall {

    private static final String LOG_TAG = "NERTCVideoCallImpl";

    private static NERTCVideoCallImpl instance;

    private NERTCInternalDelegateManager delegateManager;

    private Context mContext;

    private long selfRtcUid;

    ExecutorService tokenLoaderService;

    //****************??????????????????start***********************
    private static final int STATE_IDLE = 0;//????????????

    private static final int STATE_INVITED = 1;//????????????

    private static final int STATE_CALL_OUT = 2;//??????????????????

    private static final int STATE_DIALOG = 3;//?????????

    private static final int STATE_CANCELED =  4;//????????????

    private int status;//??????????????? 0

    //****************??????????????????end***********************

    //****************?????????????????????start*******************
    private boolean haveJoinNertcChannel = false;//???????????????NERTC?????????

    private boolean handleUserAccept = false;//????????????????????????????????????

    private final CopyOnWriteArrayList<InviteParamBuilder> invitedParams;//????????????????????????????????????

    private String imChannelId;//IM?????????

    private CallOrderListener callOrderListener;//????????????

    private UserInfoInitCallBack userInfoInitCallBack;//???????????????????????????

    private final ArrayList<ChannelCommonEvent> offlineEvent = new ArrayList<>();

    //?????????????????????????????????
    //????????????
    private ChannelType callOutType;
    //???????????????ID
    private String callUserId;
    //????????????
    private int callType;

    //????????????????????????????????????????????????rtc?????????????????????
    private JoinChannelCallBack invitedChannelCallback;
    private ChannelFullInfo invitedChannelInfo;

    //****************?????????????????????end*******************

    //************************????????????start********************
    private static final int TIME_OUT_LIMITED = 2 * 60 * 1000;//??????????????????

    private int timeOut = TIME_OUT_LIMITED;//?????????????????????2??????

    private CountDownTimer timer;//???????????????
    //************************????????????end********************

    private final Map<Long, String> memberInfoMap;

    private static final String BUSY_LINE = "i_am_busy";
    private final int mVideoCountTime = 0;
    private int mAudioCountTime = 0;
    private long stats = 0;
    private long peerUid;

    NERtc neRtc;

    private String appKey;

    private Handler mHandler;

    /**
     * rtc ????????????
     */
    int NERTC_DISCONNECT = 2011;

    public static synchronized NERTCVideoCall sharedInstance() {
        if (instance == null) {
            instance = new NERTCVideoCallImpl();
        }
        return instance;
    }

    public static synchronized void destroySharedInstance() {
        if (instance != null) {
            instance.destroy();
            instance = null;
        }
    }

    @Override
    public Context getContext() {
        return ContextHelper.getInstance().getContext();
    }

    /**
     * ???????????????????????????
     */
    Observer<ChannelCommonEvent> nimOnlineObserver = new Observer<ChannelCommonEvent>() {
        @Override
        public void onEvent(ChannelCommonEvent event) {
            if(event.getChannelBaseInfo().getChannelStatus() == ChannelStatus.NORMAL) {
                handleNIMEvent(event);
            }else {
                Log.d(LOG_TAG,"this event is INVALID and cancel eventType = 0 " + event.getEventType());
            }
        }
    };

    /**
     * ??????????????????
     */
    Observer<ArrayList<ChannelCommonEvent>> nimOfflineObserver = (Observer<ArrayList<ChannelCommonEvent>>) channelCommonEvents -> {
        if (channelCommonEvents != null && channelCommonEvents.size() > 0) {
            if (delegateManager == null || delegateManager.isEmpty()) {
                //delegate ??????????????????????????????
                offlineEvent.clear();
                offlineEvent.addAll(channelCommonEvents);
            } else {
                handleOfflineEvents(channelCommonEvents);
            }
        }
    };

    /**
     * ??????????????????
     */
    private void handleOfflineEvents(ArrayList<ChannelCommonEvent> offlineEvent) {
//        Debug.waitForDebugger();
        if (offlineEvent != null && offlineEvent.size() > 0) {
            ArrayList<ChannelCommonEvent> usefulEvent = new ArrayList<>();
            for (ChannelCommonEvent event : offlineEvent) {
                if (event.getChannelBaseInfo().getChannelStatus() == ChannelStatus.NORMAL) {
                    if (event.getEventType() == SignallingEventType.CANCEL_INVITE) {
                        String channelId = event.getChannelBaseInfo().getChannelId();
                        for (ChannelCommonEvent event1 : usefulEvent) {
                            if (TextUtils.equals(channelId, event1.getChannelBaseInfo().getChannelId())) {
                                usefulEvent.remove(event1);
                                break;
                            }
                        }
                    } else {
                        usefulEvent.add(event);
                    }
                }
            }
            for (ChannelCommonEvent commonEvent : usefulEvent) {
                handleNIMEvent(commonEvent);
            }
        }
    }

    /**
     * ??????IM????????????
     *
     * @param event
     */
    private void handleNIMEvent(ChannelCommonEvent event) {
        SignallingEventType eventType = event.getEventType();
        Log.d(LOG_TAG, "handle IM Event type =  " + eventType + " channelId = " + event.getChannelBaseInfo().getChannelId());
        switch (eventType) {
            case CLOSE:
                //??????channel?????????
                ChannelCloseEvent channelCloseEvent = (ChannelCloseEvent) event;
                //??????imChannelId ???null ????????????iM close
                if(TextUtils.equals(channelCloseEvent.getChannelBaseInfo().getChannelId(),imChannelId)) {
                    imChannelId = null;
                }
                hangup(null);
                if (delegateManager != null) {
                    delegateManager.onCallEnd(channelCloseEvent.getFromAccountId());
                }
                break;
            case JOIN:
                mAudioCountTime = 0;
                stats = 0;
                UserJoinEvent userJoinEvent = (UserJoinEvent) event;
                updateMemberMap(userJoinEvent.getMemberInfo());
                break;
            case INVITE:
                InvitedEvent invitedEvent = (InvitedEvent) event;
                if (delegateManager != null) {
                    if (status != STATE_IDLE) { //?????????????????????
                        Log.d(LOG_TAG, "user is busy status =  " + status);
                        InviteParamBuilder paramBuilder = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                                invitedEvent.getFromAccountId(), invitedEvent.getRequestId());
                        paramBuilder.customInfo(BUSY_LINE);
                        reject(paramBuilder, false, null);
                        break;
                    } else {
                        startCount();
                        delegateManager.onInvited(invitedEvent);
                    }
                }
                status = STATE_INVITED;
                break;
            case CANCEL_INVITE:
                CanceledInviteEvent canceledInviteEvent = (CanceledInviteEvent) event;
                Log.d(LOG_TAG, "accept cancel signaling request Id = " + canceledInviteEvent.getRequestId());
                hangup(null);
                if (delegateManager != null) {
                    delegateManager.onCancelByUserId(canceledInviteEvent.getFromAccountId());
                }
                status = STATE_IDLE;
                break;
            case REJECT:
            case ACCEPT:
                InviteAckEvent ackEvent = (InviteAckEvent) event;
                if(!TextUtils.equals(ackEvent.getChannelBaseInfo().getChannelId(),imChannelId)){
                    break;
                }
                if (ackEvent.getAckStatus() == InviteAckStatus.ACCEPT && callType == 0 ) {
                    handleWhenUserAccept(ackEvent.getChannelBaseInfo().getChannelId());
                } else if (ackEvent.getAckStatus() == InviteAckStatus.REJECT) {
                    hangup(null);
                    if (TextUtils.equals(ackEvent.getCustomInfo(), BUSY_LINE)) {
                        Log.d(LOG_TAG,"reject as busy from " + ackEvent.getFromAccountId());
                        if (callOrderListener != null) {
                            callOrderListener.onBusy(ackEvent.getChannelBaseInfo().getType(), ackEvent.getFromAccountId(), callType);
                        }
                        delegateManager.onUserBusy(ackEvent.getFromAccountId());
                    } else {
                        Log.d(LOG_TAG,"reject by user from " + ackEvent.getFromAccountId());
                        if (callOrderListener != null) {
                            callOrderListener.onReject(ackEvent.getChannelBaseInfo().getType(), ackEvent.getFromAccountId(), callType);
                        }
                        delegateManager.onRejectByUserId(ackEvent.getFromAccountId());
                    }
                    status = STATE_IDLE;
                }

                break;
            case LEAVE:
                UserLeaveEvent userLeaveEvent = (UserLeaveEvent) event;
                break;
            case CONTROL:
                ControlEvent controlEvent = (ControlEvent) event;
                ControlInfo controlInfo = GsonUtils.fromJson(controlEvent.getCustomInfo(), ControlInfo.class);
                if(controlInfo != null && controlInfo.cid == 1 && invitedChannelInfo != null){
                    loadToken(selfRtcUid,  new RequestCallbackWrapper<String>() {
                        @Override
                        public void onResult(int i, String s, Throwable throwable) {
                            if(i == 200){
                                if(invitedChannelInfo != null) {
                                    int rtcResult = joinChannel(s, invitedChannelInfo.getChannelId());
                                    if(rtcResult != 0){
                                        ToastUtils.toastShow("join Rtc failed code = " + rtcResult );
                                        hangup(null);
                                    }
                                }
                            } else {
                                loadTokenError();
                            }
                        }
                    });
                }
                break;
        }
    }


    /**
     * rtc ????????????
     */
    private final NERtcStatsObserver statsObserver = new NERtcStatsObserver() {

        @Override
        public void onRtcStats(NERtcStats neRtcStats) {
            if (neRtcStats == null) {
                return;
            }

            if (neRtcStats.rxBytes - stats == 0) {
                mAudioCountTime++;
                if (mAudioCountTime == 7) {
                    mAudioCountTime = 0;
                    delegateManager.onUserDisconnect(memberInfoMap.get(peerUid));
                    if (callType == Utils.ONE_TO_ONE_CALL) {
                        status = STATE_IDLE;
                        hangup(null);
                        ReportUtils.report(LOG_TAG, "onRtcStats", ReportConstant.HUNG_UP_14S_NO_SOUND, "peerAccid:" + memberInfoMap.get(peerUid) + " selfAccid:" + memberInfoMap.get(selfRtcUid));
                    }
                }
            } else {
                mAudioCountTime = 0;
                stats = neRtcStats.rxBytes;
            }
        }

        @Override
        public void onLocalAudioStats(NERtcAudioSendStats neRtcAudioSendStats) {

        }

        @Override
        public void onRemoteAudioStats(NERtcAudioRecvStats[] neRtcAudioRecvStats) {
//            if (neRtcAudioRecvStats == null || neRtcAudioRecvStats.length == 0) {
//                return;
//            }
//
//            if (neRtcAudioRecvStats[0].kbps == 0) {
//                mAudioCountTime++;
//                if (mAudioCountTime == 7) {
//                    mAudioCountTime = 0;
//                    delegateManager.onUserDisconnect(memberInfoMap.get(peerUid));
//                    if(callType == Utils.ONE_TO_ONE_CALL){
//                        status = STATE_IDLE;
//                        leave(null);
//                        ReportUtils.report(LOG_TAG, "onRtcStats", ReportConstant.HUNG_UP_14S_NO_SOUND, "peerAccid:" + memberInfoMap.get(peerUid) + " selfAccid:" + memberInfoMap.get(selfRtcUid));
//                    }
//                }
//            } else {
//                mAudioCountTime = 0;
//            }
        }

        @Override
        public void onLocalVideoStats(NERtcVideoSendStats neRtcVideoSendStats) {

        }

        @Override
        public void onRemoteVideoStats(NERtcVideoRecvStats[] neRtcVideoRecvStats) {
//            if (neRtcVideoRecvStats == null || neRtcVideoRecvStats.length == 0) {
//                return;
//            }
//
//            if (neRtcVideoRecvStats[0].receivedBitrate == 0) {
//                mVideoCountTime++;
//                if (mVideoCountTime == 7) {
//                    mVideoCountTime = 0;
//                    delegateManager.onUserDisconnect(memberInfoMap.get(peerUid));
//                    if(callType == Utils.ONE_TO_ONE_CALL){
//                        status = STATE_IDLE;
//                        leave(null);
//                    }
//                }
//            } else {
//                mVideoCountTime = 0;
//            }
        }

        @Override
        public void onNetworkQuality(NERtcNetworkQualityInfo[] neRtcNetworkQualityInfos) {
            delegateManager.onUserNetworkQuality(neRtcNetworkQualityInfos);
        }
    };

    /**
     * ?????????????????????????????????
     * @param channelId
     */
    private void handleWhenUserAccept(String channelId){
        Log.d(LOG_TAG,"handleWhenUserAccept handleUserAccept = " + handleUserAccept + " status = " + status);
        if(!handleUserAccept && status == STATE_CALL_OUT) {
            loadToken(selfRtcUid, new RequestCallbackWrapper<String>() {
                @Override
                public void onResult(int i, String s, Throwable throwable) {
                    if(i == 200){
                        int rtcResult = joinChannel(s, channelId);
                        if (rtcResult != 0) {
                            delegateManager.onError(rtcResult, "join rtc channel failed");
                            status = STATE_IDLE;
                        }
                    } else {
                        loadTokenError();
                    }
                }
            });
            if(timer != null){
                timer.cancel();
            }

            handleUserAccept = true;
        }
    }

    /**
     * ??????????????????
     * @param controlInfo
     */
    private void sendControlEvent(String channelId,String accountId, ControlInfo controlInfo){
        NIMClient.getService(SignallingService.class).sendControl(channelId,accountId,GsonUtils.toJson(controlInfo));
    }


    /**
     * Nertc?????????
     */
    private final NERtcCallback rtcCallback = new NERtcCallback() {
        @Override
        public void onJoinChannel(int i, long l, long l1) {
            Log.d(LOG_TAG, "onJoinChannel");
            haveJoinNertcChannel = true;
            if (status == STATE_CALL_OUT && !TextUtils.isEmpty(callUserId) && !TextUtils.isEmpty(imChannelId)) {
                sendControlEvent(imChannelId, callUserId, new ControlInfo(1));
            }
            if (delegateManager != null) {
                delegateManager.onGetChannelId(l);
            }
            if (invitedChannelCallback != null && invitedChannelInfo != null) {
                invitedChannelCallback.onJoinChannel(invitedChannelInfo);
                invitedChannelCallback = null;
                invitedChannelInfo = null;
            }
        }

        @Override
        public void onLeaveChannel(int i) {
            haveJoinNertcChannel = false;
            Log.d(LOG_TAG, "onLeaveChannel set status idel when onleaveChannel");
            status = STATE_IDLE;
        }

        @Override
        public void onUserJoined(long l) {
            Log.d(LOG_TAG, "peerUid---->"+peerUid+"");
            if (!isCurrentUser(l)) {
                Log.d(LOG_TAG, "onUserJoined set status dialog");
                status = STATE_DIALOG;
                if(invitedParams != null){
                    invitedParams.clear();
                }
            }
            if (delegateManager != null) {
                peerUid = l;
                delegateManager.onUserEnter(l,memberInfoMap.get(l));
            }
        }

        @Override
        public void onUserLeave(long l, int i) {
            Log.d(LOG_TAG, "onUserLeave");
            if(status == STATE_DIALOG) {
                if(i == 0){//????????????
                    delegateManager.onUserLeave(memberInfoMap.get(l));
                } else {//???????????????
                    delegateManager.onUserDisconnect(memberInfoMap.get(l));
                    if(callType == Utils.ONE_TO_ONE_CALL){
                        status = STATE_IDLE;
                        leave(null);
                    }
                }
            }
            Log.d(LOG_TAG, String.format("onUserLeave uid = %d,reason = %d",l,i));
        }

        @Override
        public void onUserAudioStart(long l) {
            Log.d(LOG_TAG, "onUserAudioStart");
            if (!isCurrentUser(l)) {
                NERtcEx.getInstance().subscribeRemoteAudioStream(l, true);
            }
            if (delegateManager != null) {
                delegateManager.onAudioAvailable(l, true);
            }
        }

        @Override
        public void onUserAudioStop(long l) {
            Log.d(LOG_TAG, "onUserAudioStop");
            if (delegateManager != null) {
                delegateManager.onAudioAvailable(l, false);
            }
        }

        @Override
        public void onUserVideoStart(long l, int i) {
            Log.d(LOG_TAG, "onUserVideoStart");
            if (!isCurrentUser(l)) {
                NERtcEx.getInstance().subscribeRemoteVideoStream(l, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
            }
            if (delegateManager != null) {
                delegateManager.onCameraAvailable(l, true);
            }
        }

        @Override
        public void onUserVideoStop(long l) {
            Log.d(LOG_TAG, "onUserVideoStop");
            if (delegateManager != null) {
                delegateManager.onCameraAvailable(l, false);
            }
        }

        @Override
        public void onDisconnect(int i) {
            Log.d(LOG_TAG, "onDisconnect");
            NERtcEx.getInstance().leaveChannel();
            status = STATE_IDLE;
            delegateManager.onError(NERTC_DISCONNECT, "disconnect error");
        }

        @Override
        public void onClientRoleChange(int i, int i1) {

        }
    };


    private NERTCVideoCallImpl() {
        invitedParams = new CopyOnWriteArrayList<>();
        delegateManager = new NERTCInternalDelegateManager();
        memberInfoMap = new HashMap<>();
        tokenLoaderService = Executors.newSingleThreadExecutor();
    }

    private void updateMemberMap(MemberInfo memberInfo){
        memberInfoMap.put(memberInfo.getUid(),memberInfo.getAccountId());
    }

    @Override
    public void setupAppKey(Context context, String appKey, VideoCallOptions option) {
        mContext = context;

        //??????????????? destroy
        if (neRtc != null) {
            destroy();
        }

        mHandler = new Handler(context.getMainLooper());

        this.appKey = appKey;

        //?????????rtc sdk
        neRtc = NERtc.getInstance();
        NERtcParameters parameters = new NERtcParameters();
        neRtc.setParameters(parameters); //??????????????????????????????
        try {
            neRtc.init(context, appKey, rtcCallback, option.getRtcOption());
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.toastShow("SDK init error");
            return;
        }
        userInfoInitCallBack = option.getUserInfoInitCallBack();
        NERtcEx.getInstance().setStatsObserver(statsObserver);
        NIMClient.getService(SignallingServiceObserver.class).observeOnlineNotification(nimOnlineObserver, true);
        NIMClient.getService(SignallingServiceObserver.class).observeOfflineNotification(nimOfflineObserver, true);
        //??????UIService
        UIServiceManager.getInstance().setUiService(option.getUiService());
        //start ??????service???????????????
//        CallService.start(context, option.getUiService());
        CallServiceInstance.getInstance().setContext(context,option.getUiService());
        setCallOrderListener(new CallOrderListener() {
                                 @Override
                                 public void onCanceled(ChannelType channelType, String accountId, int callType) {
                                     makeCallOrder(channelType, accountId, NrtcCallStatus.NrtcCallStatusCanceled, callType);
                                 }

                                 @Override
                                 public void onReject(ChannelType channelType, String accountId, int callType) {
                                     makeCallOrder(channelType, accountId, NrtcCallStatus.NrtcCallStatusRejected, callType);
                                 }

                                 @Override
                                 public void onTimeout(ChannelType channelType, String accountId, int callType) {
                                     makeCallOrder(channelType, accountId, NrtcCallStatus.NrtcCallStatusTimeout, callType);
                                 }

                                 @Override
                                 public void onBusy(ChannelType channelType, String accountId, int callType) {
                                     makeCallOrder(channelType, accountId, NrtcCallStatus.NrtcCallStatusBusy, callType);
                                 }
                             }
        );
    }

    private void makeCallOrder(ChannelType channelType, String accountId, int nrtcCallStatusBusy, int callType) {
        if (callType == 0) {
            NetCallAttachment netCallAttachment = new NetCallAttachment.NetCallAttachmentBuilder()
                    .withType(channelType.getValue())
                    .withStatus(nrtcCallStatusBusy)
                    .build();
            IMMessage message = MessageBuilder.createNrtcNetcallMessage(accountId, SessionTypeEnum.P2P, netCallAttachment);
            NIMClient.getService(MsgService.class).sendMessage(message, false);
        }
    }

    private void setCallOrderListener(CallOrderListener callOrderListener) {
        this.callOrderListener = callOrderListener;
    }

    @Override
    public void login(String imAccount, String imToken, RequestCallback<LoginInfo> callback) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
            @Override
            public void onEvent(StatusCode statusCode) {
                if (statusCode == StatusCode.UNLOGIN) {
                    LoginInfo loginInfo = new LoginInfo(imAccount, imToken);
                    NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callback);
                } else if (statusCode == StatusCode.LOGINED) {
                    NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);
                    if (userInfoInitCallBack != null) {
                        userInfoInitCallBack.onUserLoginToIm(imAccount, imToken);
                    }
                }
            }
        }, true);

    }

    @Override
    public void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    @Override
    public void addDelegate(NERTCCallingDelegate delegate) {
        delegateManager.addDelegate(delegate);
    }

    public void addServiceDelegate(NERTCCallingDelegate delegate) {
        delegateManager.addDelegate(delegate);
        //???????????????offline ??????
        if (offlineEvent.size() > 0) {
            Log.d(LOG_TAG, "offline event dispatch to service");
            handleOfflineEvents(offlineEvent);
            offlineEvent.clear();
        }
    }

    @Override
    public void removeDelegate(NERTCCallingDelegate delegate) {
        delegateManager.removeDelegate(delegate);
    }

    @Override
    public void setupRemoteView(NERtcVideoView videoRender, long uid) {
        if (neRtc == null) {
            return;
        }
        videoRender.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        neRtc.setupRemoteVideoCanvas(videoRender, uid);
    }

    @Override
    public void setupLocalView(NERtcVideoView videoRender) {
        if (neRtc == null) {
            return;
        }
        neRtc.enableLocalAudio(true);
        neRtc.enableLocalVideo(true);
        videoRender.setZOrderMediaOverlay(true);
        videoRender.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        neRtc.setupLocalVideoCanvas(videoRender);
    }

    @Override
    public void setTimeOut(int timeOut) {
        if (timeOut < TIME_OUT_LIMITED) {
            this.timeOut = timeOut;
        }
    }

    private boolean isCurrentUser(long uid) {
        return selfRtcUid != 0 && selfRtcUid == uid;
    }

    /**
     * ??????????????????????????????timeout
     */
    private void startCount() {
        if (timer != null) {
            timer.cancel();
        } else {
            timer = new CountDownTimer(timeOut, 1000) {
                @Override
                public void onTick(long l) {
                    if (status != STATE_CALL_OUT && status != STATE_INVITED) {
                        timer.cancel();
                    }
                }

                @Override
                public void onFinish() {
                    if (callOrderListener != null &&
                            callOutType != null && !TextUtils.isEmpty(callUserId)) {
                        callOrderListener.onTimeout(callOutType, callUserId, callType);
                    }

                    callOutType = null;
                    callUserId = "";
                    if(status == STATE_CALL_OUT){
                        NERTCVideoCallImpl.this.cancel(null);
                    }else if(status == STATE_INVITED){
                        imChannelId = null;
                        hangup(null);
                    }
                    if (delegateManager != null) {
                        delegateManager.timeOut();
                    }
                }
            };
        }
        timer.start();
    }

    private String getCustomInfo(int callType, ArrayList<String> accounts, boolean isFromGroup, String groupId) {
        return GsonUtils.toJson(new CustomInfo(callType, accounts, isFromGroup, groupId));
    }

    @Override
    public void call(final String userId, String selfUserId, ChannelType type, @NotNull JoinChannelCallBack joinChannelCallBack) {
        status = STATE_CALL_OUT;
        startCount();//???????????????
        callType = Utils.ONE_TO_ONE_CALL;
        //?????????????????????????????????
        callOutType = type;
        callUserId = userId;
        handleUserAccept = false;

        if(type == ChannelType.AUDIO){
            neRtc.enableLocalVideo(false);
        }

        createIMChannelAndJoin(Utils.ONE_TO_ONE_CALL, null, type, selfUserId, null, userId, joinChannelCallBack);

    }

    @Override
    public void groupCall(ArrayList<String> userIds, String groupId, String selfUserId, ChannelType type, @NotNull JoinChannelCallBack joinChannelCallBack) {
        if (userIds == null || userIds.size() <= 0) {
            return;
        }
        status = STATE_CALL_OUT;
        startCount();//???????????????
        callType = Utils.GROUP_CALL;
        //1,??????channel
        createIMChannelAndJoin(Utils.GROUP_CALL, groupId, type, selfUserId, userIds, null, joinChannelCallBack);
    }

    /**
     * ??????IM???????????????
     *
     * @param callType            ????????????
     * @param type                ????????????
     * @param selfUserId          ???????????????ID
     * @param userIds             ???????????????list
     * @param callUserId          ?????????????????????
     * @param joinChannelCallBack ??????
     */
    private void createIMChannelAndJoin(int callType, String groupId, ChannelType type, String selfUserId,
                                        ArrayList<String> userIds, String callUserId, JoinChannelCallBack joinChannelCallBack) {
        NIMClient.getService(SignallingService.class).create(type, null, null).setCallback(new RequestCallback<ChannelBaseInfo>() {
            @Override
            public void onSuccess(ChannelBaseInfo param) {
                //2,join channel
                if (param != null) {
                    imChannelId = param.getChannelId();
                    if (delegateManager != null) {
                        delegateManager.onGetIMChannelId(imChannelId);
                    }
                    joinIMChannel(callType, groupId, type, param, selfUserId, userIds, callUserId, joinChannelCallBack);
                }
            }

            @Override
            public void onFailed(int code) {
                Log.d(LOG_TAG, "create channel failed code = " + code);
                joinChannelCallBack.onJoinFail("create channel failed code", code);
                callFailed(code, null);
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    /**
     * ??????IM?????????
     *
     * @param channelInfo
     * @param selfUserId
     */
    private void joinIMChannel(int callType, String groupId, ChannelType type, ChannelBaseInfo channelInfo, String selfUserId, ArrayList<String> userIds,
                               String callUserId, JoinChannelCallBack joinChannelCallBack) {
        //2,join channel for IM
        NIMClient.getService(SignallingService.class).join(channelInfo.getChannelId(), 0, "", true).setCallback(new RequestCallback<ChannelFullInfo>() {
            @Override
            public void onSuccess(ChannelFullInfo param) {

                //??????Uid
                storeUid(param.getMembers(), selfUserId);

                if (callType == Utils.GROUP_CALL) {
                    //????????????????????????rtc channel ??????????????????
                    loadToken(selfRtcUid, new RequestCallbackWrapper<String>() {
                        @Override
                        public void onResult(int i, String s, Throwable throwable) {
                            if(i == ResponseCode.RES_SUCCESS){
                                int rtcResult = joinChannel(s, param.getChannelId());
                                if (rtcResult == 0 && userIds != null && userIds.size() > 0) {
                                    //??????rtc???????????????????????????????????????
                                    ArrayList<String> allUserIds = new ArrayList<>(userIds);
                                    //????????????????????????????????????????????????
                                    for (String userId : userIds) {
                                        if (!TextUtils.isEmpty(userId)) {
                                            inviteOneUserWithIM(callType, type, userId, selfUserId, param, groupId, allUserIds);
                                        }
                                    }
                                    joinChannelCallBack.onJoinChannel(param);
                                } else {
                                    joinChannelCallBack.onJoinFail("join channel failed", rtcResult);
                                }
                            }else {
                                loadTokenError();
                            }
                        }
                    });

                } else if (callType == Utils.ONE_TO_ONE_CALL) {
                    //????????????????????????????????????????????????????????????????????????channel
                    inviteOneUserWithIM(callType, type, callUserId, selfUserId, param, null, null);
                    joinChannelCallBack.onJoinChannel(param);
                }


            }

            @Override
            public void onFailed(int code) {
                Log.d(LOG_TAG, "join channel failed code = " + code);
                joinChannelCallBack.onJoinFail("join im channel failed", code);
                callFailed(code, channelInfo.getChannelId());
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    /**
     * ?????????????????????requestID
     *
     * @return
     */
    private String getRequestId() {
        int randomInt = (int) (Math.random() * 100);
        Log.d(LOG_TAG, "random int = " + randomInt);
        return System.currentTimeMillis() + randomInt + "_id";
    }

    /**
     * ??????????????????channel
     *
     * @param callType
     * @param userId
     * @param selfUid
     * @param channelInfo
     * @param callUsers
     */
    private void inviteOneUserWithIM(int callType, ChannelType channelType, String userId, String selfUid, ChannelFullInfo channelInfo, String groupId, ArrayList<String> callUsers) {
        String invitedRequestId = getRequestId();
        InviteParamBuilder inviteParam = new InviteParamBuilder(channelInfo.getChannelId(), userId, invitedRequestId);
        inviteParam.customInfo(getCustomInfo(callType, callUsers, callType == Utils.GROUP_CALL, groupId));
        inviteParam.pushConfig(getPushConfig(callType, channelType, invitedRequestId, selfUid, channelInfo.getChannelId(), callUsers));
        inviteParam.offlineEnabled(true);

        Log.d(LOG_TAG, "sendInvited channelName = " + channelInfo.getChannelId() + " userId = " + userId + " requestId = " + invitedRequestId);

        NIMClient.getService(SignallingService.class).invite(inviteParam).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                //??????????????????????????????
                Log.d(LOG_TAG, "sendInvited success channelName = " + channelInfo.getChannelId() + " userId = " + userId + " requestId = " + invitedRequestId);
                saveInvitedInfo(inviteParam);
            }

            @Override
            public void onFailed(int code) {
                Log.d(LOG_TAG, "sendInvited failed channelName = " + code);
                //?????????????????????
                if (code == ResponseCode.RES_PEER_NIM_OFFLINE || code == ResponseCode.RES_PEER_PUSH_OFFLINE) {
                    saveInvitedInfo(inviteParam);
                }
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }


    private void saveInvitedInfo(InviteParamBuilder inviteParam) {
        invitedParams.add(inviteParam);
    }

    /**
     * ??????????????????
     *
     * @param callType
     * @param requestId
     * @param fromAccountId
     * @param imChannelId
     * @param userIds
     * @return
     */
    private SignallingPushConfig getPushConfig(int callType, ChannelType channelType, String requestId, String fromAccountId, String imChannelId, ArrayList<String> userIds) {
        String fromNickname;
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(fromAccountId);

        if (userInfo == null) {
            fromNickname = getContext().getString(R.string.the_other_party);
        } else {
            fromNickname = userInfo.getName();
        }

        String pushTitle;
        String pushContent;
        if (channelType == ChannelType.AUDIO) {
            pushTitle = getContext().getString(R.string.msg_voice_chat);
            pushContent = fromNickname + " " + getContext().getString(R.string.invite_4_voice);
        } else {
            pushTitle = getContext().getString(R.string.msg_video_chat);
            pushContent = fromNickname + " " + getContext().getString(R.string.invite_4_video);
        }

        return new SignallingPushConfig(true, pushTitle, pushContent);
    }

    /**
     * ??????????????????
     *
     * @param code
     */
    private void callFailed(int code, String imChannelId) {
        if (delegateManager != null) {
            delegateManager.onError(code, "????????????");
        }
        if (!TextUtils.isEmpty(imChannelId)) {
            closeIMChannel(imChannelId, null);
        }
        status = STATE_IDLE;
    }

    /**
     * ????????????????????????
     *
     * @param token       null??????????????????
     * @param channelName
     * @return 0 ?????????????????????????????????
     */
    private int joinChannel(String token, String channelName) {
        Log.d(LOG_TAG,"joinChannel token = " + token + " channelName = " + channelName);
        if (selfRtcUid != 0) {
            return NERtcEx.getInstance().joinChannel(token, channelName, selfRtcUid);
        }

        return -1;
    }

    /**
     * ???????????????rtc channel ??????uid
     *
     * @param memberInfos
     * @param selfAccid
     */
    private void storeUid(ArrayList<MemberInfo> memberInfos, String selfAccid) {
        for (MemberInfo member : memberInfos) {
            if (TextUtils.equals(member.getAccountId(), selfAccid)) {
                selfRtcUid = member.getUid();
            }
        }
    }

    @Override
    public void accept(InviteParamBuilder inviteParam, String selfAccId, JoinChannelCallBack joinChannelCallBack) {
        Log.d(LOG_TAG,"accept");
        if(timer != null){
            timer.cancel();
        }
        NIMClient.getService(SignallingService.class).acceptInviteAndJoin(inviteParam, 0).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {

                    @Override
                    public void onResult(int code, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            Log.d(LOG_TAG,"accept success");
                            if(channelFullInfo.getType() == ChannelType.AUDIO){
                                neRtc.enableLocalVideo(false);
                            }if(channelFullInfo.getType() == ChannelType.VIDEO){
                                neRtc.enableLocalVideo(true);
                            }
                            imChannelId = channelFullInfo.getChannelId();
                            if (delegateManager != null) {
                                delegateManager.onGetIMChannelId(imChannelId);
                            }
                            //??????rtc Channel
                            storeUid(channelFullInfo.getMembers(), selfAccId);

                            if (joinChannelCallBack != null) {
                                invitedChannelCallback = joinChannelCallBack;
                                invitedChannelInfo = channelFullInfo;
                            }
                            mAudioCountTime = 0;
                            stats = 0;
                            //??????channel ?????????meber ??????
                            for(MemberInfo memberInfo:channelFullInfo.getMembers()){
                                updateMemberMap(memberInfo);
                            }
                        } else {
                            Log.d(LOG_TAG,"accept failed code = "+ code);
                            joinChannelCallBack.onJoinFail("accept channel failed", code);
                        }
                    }
                });
    }

    @Override
    public void reject(InviteParamBuilder inviteParam, RequestCallback<Void> callback) {
        reject(inviteParam, true, callback);
    }

    /**
     * ??????
     *
     * @param inviteParam
     * @param byUser
     */
    private void reject(InviteParamBuilder inviteParam, boolean byUser, RequestCallback<Void> callback) {
        Log.d(LOG_TAG,"reject by user = " + byUser);
        NIMClient.getService(SignallingService.class).rejectInvite(inviteParam).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (byUser) {
                    status = STATE_IDLE;
                }
                if (callback != null) {
                    callback.onSuccess(aVoid);
                }
            }

            @Override
            public void onFailed(int i) {
                if (byUser) {
                    status = STATE_IDLE;
                }
                if (callback != null) {
                    callback.onFailed(i);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                if (callback != null) {
                    callback.onException(throwable);
                }
                if (byUser) {
                    status = STATE_IDLE;
                }
            }
        });

    }

    @Override
    public void hangup(RequestCallback<Void> callback) {
        Log.d(LOG_TAG, "hangup");
        //??????NERtc???channel
        int rtcResult = -1;
        if (neRtc != null) {
            rtcResult = neRtc.leaveChannel();
        }

        if (rtcResult != 0 && callback != null) {
            callback.onFailed(rtcResult);
        }
        //???????????????channel
        if (!TextUtils.isEmpty(imChannelId)) {
            closeIMChannel(imChannelId, callback);
        }else if(callback != null){
            callback.onFailed(-1);
        }
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        status = STATE_IDLE;
    }

    @Override
    public void leave(RequestCallback<Void> callback) {
        //????????????????????????????????????
        if (callType == Utils.GROUP_CALL && status == STATE_CALL_OUT) {
            cancel(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    singleLeave(callback);
                }

                @Override
                public void onFailed(int i) {
                    if (callback != null) {
                        callback.onFailed(i);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    if (callback != null) {
                        callback.onException(throwable);
                    }
                }
            });
        } else {
            singleLeave(callback);
            invitedParams.clear();
            invitedChannelCallback = null;
            invitedChannelInfo = null;
            mHandler.removeCallbacksAndMessages(null);
            status = STATE_IDLE;
        }


    }

    /**
     * ????????????
     *
     * @param callback
     */
    private void singleLeave(RequestCallback<Void> callback) {
        //??????NERtc???channel
        int result = -1;
        if (neRtc != null) {
            result = neRtc.leaveChannel();
        }
        if (result != 0 && callback != null) {
            callback.onFailed(result);
        }
        //???????????????channel
        if (!TextUtils.isEmpty(imChannelId)) {
            leaveIMChannel(imChannelId, callback);
        }else if(callback != null){
            callback.onFailed(-1);
        }
        status = STATE_IDLE;
    }

    @Override
    public void cancel(RequestCallback<Void> callback) {
        Log.d(LOG_TAG, "cancel");
        if(handleUserAccept){
            return;
        }
        final boolean[] needCallback = {callback != null};
        final int statusOld = status;
        status = STATE_CANCELED;
        if (invitedParams != null && invitedParams.size() > 0) {
            for (InviteParamBuilder inviteParam : invitedParams) {
                Log.d(LOG_TAG, "send cancel signaling");
                NIMClient.getService(SignallingService.class).cancelInvite(inviteParam).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callOrderListener != null && callOutType != null && !TextUtils.isEmpty(callUserId)) {
                            callOrderListener.onCanceled(callOutType, callUserId, callType);
                        }

                        status = STATE_CANCELED;

                        invitedParams.clear();
                        callOutType = null;
                        callUserId = "";
                        if (needCallback[0] && callback != null) {
                            callback.onSuccess(aVoid);
                            needCallback[0] = false;
                        }
                        if (callType == Utils.ONE_TO_ONE_CALL) {
                            hangup(null);
                        }
                    }

                    @Override
                    public void onFailed(int i) {
                        if (i != 10410) {
                            if (callOrderListener != null && callOutType != null && !TextUtils.isEmpty(callUserId)) {
                                callOrderListener.onCanceled(callOutType, callUserId, callType);
                            }
                        }

                        Log.d(LOG_TAG, "send cancel signaling failed code = " + i);
                        if (needCallback[0] && callback != null) {
                            callback.onFailed(i);
                            needCallback[0] = false;
                        }

                        if (callType == Utils.ONE_TO_ONE_CALL) {
                            if(i == 10410){//??????????????????
                                status = statusOld;
                                handleWhenUserAccept(imChannelId);
                            }else {
                                hangup(null);
                            }
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.d(LOG_TAG, "send cancel signaling exception", throwable);
                        if (callOrderListener != null && callOutType != null && !TextUtils.isEmpty(callUserId)) {
                            callOrderListener.onCanceled(callOutType, callUserId, callType);
                        }
                        if (needCallback[0] && callback != null) {
                            callback.onException(throwable);
                            needCallback[0] = false;
                        }

                        if (callType == Utils.ONE_TO_ONE_CALL) {
                            hangup(null);
                        }
                    }
                });
            }
        } else {
            if (needCallback[0]) {
                callback.onException(new Exception("invited params have clear"));
                needCallback[0] = false;
            }

            if (callType == Utils.ONE_TO_ONE_CALL) {
                hangup(null);
            }
        }
    }

    /**
     * ??????IMChannel
     *
     * @param channelId
     */
    private void leaveIMChannel(String channelId, RequestCallback<Void> callback) {
        NIMClient.getService(SignallingService.class).leave(channelId, false, null)
                .setCallback(new RequestCallbackWrapper<Void>() {

                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            imChannelId = null;
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailed(code);
                            }
                        }

                    }
                });


    }

    /**
     * ??????IMChannel
     *
     * @param channelId
     */
    private void closeIMChannel(String channelId, RequestCallback<Void> callback) {
        Log.d(LOG_TAG, "closeIMChannel ");
        NIMClient.getService(SignallingService.class).close(channelId, false, null)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            Log.d(LOG_TAG, "closeIMChannel success channelId = " + channelId);
                            imChannelId = null;
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else {
                            Log.d(LOG_TAG, "closeIMChannel failed code = " + code + "channelId" + channelId);
                            if (callback != null) {
                                callback.onFailed(code);
                            }
                        }
                        invitedParams.clear();
                        invitedChannelCallback = null;
                        invitedChannelInfo = null;
                        status = STATE_IDLE;
                    }
                });
    }

    @Override
    public void enableLocalVideo(boolean enable) {
        NERtcEx.getInstance().enableLocalVideo(enable);
    }

    @Override
    public void switchCamera() {
        NERtcEx.getInstance().switchCamera();
    }

    @Override
    public void muteLocalAudio(boolean isMute) {
        NERtcEx.getInstance().muteLocalAudioStream(isMute);
    }


    private void destroy() {
        NIMClient.getService(SignallingServiceObserver.class).observeOnlineNotification(nimOnlineObserver, false);
        NIMClient.getService(SignallingServiceObserver.class).observeOfflineNotification(nimOfflineObserver, false);
        if (neRtc != null) {
            NERtcEx.getInstance().setStatsObserver(null);
            neRtc.release();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //?????????????????????token??????????????????????????????????????????????????????????????????????????????????????????token??????????????????
    //Demo????????????URL?????????demoserver?????????????????????????????????
    //???????????????: http://dev.netease.im/docs?doc=server
    private void loadToken(final long uid, RequestCallbackWrapper<String> callback) {
        if(TextUtils.isEmpty(appKey)){
            callback.onResult(-1,null,null);
            return;
        }

        RtcTokenHelper.getInstance().getToken(uid,new RtcTokenHelper.OnGetTokenListener() {
            @Override
            public void onSuccess(String token) {
                if (!TextUtils.isEmpty(token)) {
                    Log.d(LOG_TAG,"load token success token = " + token);
                    callback.onResult(200,token,null);
                }else {
                    Log.d(LOG_TAG,"load token failed ");
                    callback.onResult(-1,null,null);
                }
            }

            @Override
            public void onFailed() {
                ToastUtils.toastShow(R.string.error_page_later);
//                EventBus.getDefault().post(new HangupEvent());
                Log.d(LOG_TAG,"load token failed ");
                callback.onResult(-1,null,null);
            }
        });

//        String demoServer = "https://nrtc.netease.im/demo/getChecksum.action";
//        tokenLoaderService.execute(() -> {
//            try {
//                String queryString = demoServer + "?uid=" +
//                        uid + "&appkey=" + appKey;
//                URL requestedUrl = new URL(queryString);
//                HttpURLConnection connection = (HttpURLConnection) requestedUrl.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setConnectTimeout(6000);
//                connection.setReadTimeout(6000);
//                if (connection.getResponseCode() == 200) {
//                    String result = readFully(connection.getInputStream());
//                    Log.d("Demo", result);
//                    if (!TextUtils.isEmpty(result)) {
//                        JSONObject object = new JSONObject(result);
//                        int code = object.getInt("code");
//                        if (code == 200) {
//                            String token = object.getString("checksum");
//                            if (!TextUtils.isEmpty(token)) {
//                                mHandler.post(() -> {
//                                    Log.d(LOG_TAG,"load token success token = " + token);
//                                    callback.onResult(200,token,null);
//                                });
//                                return;
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            mHandler.post(() -> {
//                Log.d(LOG_TAG,"load token failed ");
//                callback.onResult(-1,null,null);});
//        });
    }

    private void loadTokenError(){
        Log.d(LOG_TAG,"request token failed ");
        if(callType == Utils.ONE_TO_ONE_CALL) {
            hangup(null);
        }else {
            leave(null);
        }
    }

    private String readFully(InputStream inputStream) throws IOException {

        if (inputStream == null) {
            return "";
        }

        ByteArrayOutputStream byteArrayOutputStream;

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();

            final byte[] buffer = new byte[1024];
            int available;

            while ((available = bufferedInputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, available);
            }

            return byteArrayOutputStream.toString();

        } finally {
            bufferedInputStream.close();
        }
    }
}
