package com.lockulockme.lockuchat.aavg2.ui.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.JoinChannelCallBack;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCCallingDelegate;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.aavg2.ui.model.ProfileManager;
import com.lockulockme.lockuchat.aavg2.ui.model.UserModel;
import com.lockulockme.lockuchat.bean.HangUp;
import com.lockulockme.lockuchat.bean.HangUpWithChannelId;
import com.lockulockme.lockuchat.bean.rsp.AllowCallRsp;
import com.lockulockme.lockuchat.bean.rsp.PriceItems;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.bean.rst.UpChannelId;
import com.lockulockme.lockuchat.common.HangUpHelper;
import com.lockulockme.lockuchat.databinding.AcVideoBinding;
import com.lockulockme.lockuchat.event.EventSubscriber;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.ui.FloatAnsweringWindow;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.ReportConstant;
import com.lockulockme.lockuchat.utils.ReportUtils;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.permission.OnPermissionsListener;
import com.lockulockme.lockuchat.utils.permission.PermissionsUtils;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.video.NERtcEncodeConfig;
import com.netease.lava.nertc.sdk.video.NERtcVideoConfig;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.avsignalling.model.MemberInfo;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NERTCVideoCallActivity extends NERTCBaseActivity {

    private static final String LOG_TAG = NERTCVideoCallActivity.class.getSimpleName();

    public static final String CALL_OUT_USER = "call_out_user";

    private NERTCVideoCall nertcVideoCall;


    private UserModel callOutUser;//????????????
    private boolean callReceived;

    private String inventRequestId;
    private String inventChannelId;
    private String inventFromAccountId;

    private boolean isMute;//????????????
    private boolean isCamOff;//?????????????????????

    private static final int DELAY_TIME = 0;//??????

    private String peerAccid;
    private long peerUid;

    ScheduledThreadPoolExecutor scheduled;
    public String otherId;
    private InviteParamBuilder invitedParam;

    private boolean isShowPage = false;

    private String callChannelId;
    private String callIMChannelId;
    private boolean isRating = true;
    private MyOnPermissionsListener myOnPermissionsListener;
    private final int mCountTime = 0;
    private final String EXCEPTION_TAG = "NERTCVideoCallActivity";

    private class ConsumeStoneTask implements Runnable {

        private String targetImId = "";

        public ConsumeStoneTask(String accId) {
            targetImId = accId;
        }

        @Override
        public void run() {
            VipDiamondsHelper.getInstance().ruduceDiamonds4VideoChat(NERTCVideoCallActivity.this.toString(), targetImId, callChannelId, new VipDiamondsHelper.OnReduceListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailed(int errorCode) {
                    ReportUtils.report(EXCEPTION_TAG, "ConsumeDiamond", ReportConstant.CONSUME_DIAMOND, "errorCode:" + errorCode);
                    if (9 == errorCode) {
                        ToastUtils.toastShow(R.string.diamond_is_not_enough);
                    } else if (35 == errorCode) {
                        ToastUtils.toastShow(R.string.vip_time_expired);
                    }
                    stopConsume();
                    //todo recheck
                    hungUpAndFinish();
                }
            });

        }
    }

    private void checkUserRate(String imId) {
        VipDiamondsHelper.getInstance().checkUserRate(this, imId, new VipDiamondsHelper.onUserRateListener() {
            @Override
            public void onSuccess(boolean isRate) {
                isRating = isRate;
            }

            @Override
            public void onFailed(int code) {
                ReportUtils.report(EXCEPTION_TAG, "getHasRateUser", ReportConstant.HAS_RATE_USER, "errorCode:" + code);
            }
        });
    }


    private final NERTCCallingDelegate nertcCallingDelegate = new NERTCCallingDelegate() {
        @Override
        public void onError(int errorCode, String errorMsg) {
            ReportUtils.report(EXCEPTION_TAG, "NERTCCallingDelegate", ReportConstant.NERTC_CALLING_DELEGATE, errorCode + errorMsg);
            ToastUtils.toastShow(errorMsg + " errorCode:" + errorCode, Toast.LENGTH_LONG);
            finish();
        }

        @Override
        public void onInvited(InvitedEvent invitedEvent) {

        }


        @Override
        public void onUserEnter(long uid, String accId) {
            otherId = accId;
            ReportUtils.report(EXCEPTION_TAG, "NERTCCallingDelegateOnUserEnter", ReportConstant.USER_ENTER, "accid:" + accId);
            hideLoading();
            showRecordView();

            if (scheduled != null) {
                scheduled.shutdownNow();
                scheduled = null;
            }
            scheduled = new ScheduledThreadPoolExecutor(2);
            scheduled.scheduleAtFixedRate(new ConsumeStoneTask(accId), 0, 60 * 1000, TimeUnit.MILLISECONDS);
            binding.llyCancel.setVisibility(View.GONE);
            binding.llyDialogOperation.setVisibility(View.VISIBLE);
//            binding.rlyTopUserInfo.setVisibility(View.GONE);
            binding.llyInvitedOperation.setVisibility(View.GONE);
            setupLocalVideo();
            AVChatSoundPlayer.instance().stop();
            checkUserRate(otherId);
            peerAccid = accId;
            peerUid = uid;
            Log.i(LOG_TAG, String.format("onUserEnter uid:%d, accId:%s", uid, accId));
        }

        @Override
        public void onCallEnd(String userId) {
            onCallEnd(userId, R.string.she_hung_up);
        }

        @Override
        public void onUserLeave(String accountId) {
            Log.i(LOG_TAG, "onUserLeave:" + accountId);
            if (TextUtils.equals(accountId, peerAccid)) {
//                onCallEnd(accountId, "??????????????????");
                onCallEnd(accountId, R.string.she_hung_up);
            }
        }

        private void onCallEnd(String userId, int tip) {
            Log.i(LOG_TAG, "onUserLeave:" + userId);
            if (!isDestroyed() && !ProfileManager.getInstance().isCurrentUser(userId)) {
                ToastUtils.toastShow(tip, Toast.LENGTH_LONG);
                handler.postDelayed(() -> finish(), DELAY_TIME);
                AVChatSoundPlayer.instance().stop();
            }
        }

        @Override
        public void onRejectByUserId(String userId) {
            if (!isDestroyed() && !callReceived) {
                ToastUtils.toastShow(R.string.avcall_is_reject, Toast.LENGTH_LONG);
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.PEER_REJECT);
            }
        }


        @Override
        public void onUserBusy(String userId) {
            if (!isDestroyed() && !callReceived) {
                ToastUtils.toastShow(R.string.other_busy, Toast.LENGTH_LONG);
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY);
            }
        }

        @Override
        public void onCancelByUserId(String uid) {
            if (!isDestroyed() && callReceived) {
                ToastUtils.toastShow(R.string.avchat_call_finish, Toast.LENGTH_LONG);
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
                AVChatSoundPlayer.instance().stop();
            }


        }


        @Override
        public void onCameraAvailable(long userId, boolean isVideoAvailable) {
            if (!ProfileManager.getInstance().isCurrentUser(userId)) {
                if (isVideoAvailable) {
//                    binding.rlyTopUserInfo.setVisibility(View.GONE);
                    binding.remoteVideoView.setVisibility(View.VISIBLE);
                    binding.ivSmall.setVisibility(View.VISIBLE);
                    binding.tvCallComment.setText(R.string.answer_ing);
                    setupRemoteVideo(userId);
                } else {
                    binding.remoteVideoView.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onAudioAvailable(long userId, boolean isAudioAvailable) {

        }

        @Override
        public void timeOut() {
            if (callReceived) {
                ToastUtils.toastShow(R.string.avcall_no_pick_up, Toast.LENGTH_LONG);
            } else {
                ToastUtils.toastShow(R.string.call_timeout, Toast.LENGTH_LONG);
            }
            AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.NO_RESPONSE);
            handler.postDelayed(() -> {
                AVChatSoundPlayer.instance().stop();
                finish();
            }, DELAY_TIME);
        }

        @Override
        public void onGetChannelId(long channelId) {
            callChannelId = String.valueOf(channelId);
            updateChannelId();
        }

        @Override
        public void onGetIMChannelId(String channelId) {
            callIMChannelId = channelId;
            updateChannelId();
        }

        @Override
        public void onUserDisconnect(String userId) {
            Log.i(LOG_TAG, "onUserDisconnect:" + userId);
            if (TextUtils.equals(userId, peerAccid)) {
                onCallEnd(userId, R.string.she_hung_up);
            }
        }

        @Override
        public void onUserNetworkQuality(NERtcNetworkQualityInfo[] neRtcNetworkQualityInfos) {
            if (neRtcNetworkQualityInfos == null || neRtcNetworkQualityInfos.length == 0) {
                return;
            }

            for (NERtcNetworkQualityInfo networkQualityInfo : neRtcNetworkQualityInfos) {
                if (networkQualityInfo.userId == peerUid) {
                    if (networkQualityInfo.upStatus >= 4) {
                        ToastUtils.toastShow(R.string.orther_internet_quality_no);
                    } else if (networkQualityInfo.upStatus == 0) {
                        Log.e(LOG_TAG, "network is unKnow");
                    }

                    Log.i(LOG_TAG, String.format("NERtcNetworkQualityInfo: %d", networkQualityInfo.downStatus));
                }
            }
        }
    };
    private void updateChannelId() {
        if (!TextUtils.isEmpty(callChannelId) && !TextUtils.isEmpty(callIMChannelId)) {
            NetDataUtils.getInstance().getNetData().upChannelId(this, new UpChannelId(callIMChannelId, callChannelId), null);
        }
    }
    private void stopConsume() {
        if (scheduled != null) {
            scheduled.shutdownNow();
            scheduled = null;
        }

        stopRecordView();
    }

    private final Handler handler = new Handler();
    private AcVideoBinding binding;


    public static void startCallOther(Context context, UserModel callOutUser) {
        Intent intent = new Intent(context, NERTCVideoCallActivity.class);
        intent.putExtra(CALL_OUT_USER, callOutUser);
        intent.putExtra(CallParams.INVENT_CALL_RECEIVED, false);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ?????????????????????????????????????????????
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        CallServiceInstance.getInstance().cancelNotification();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = AcVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        observeHangUpEvent();
        ChatAnswerUtils.getInstance().setAnswering(true);
        initIntent();
        initData();
//        NERtcEx.getInstance().setStatsObserver(new NERtcStatsObserver() {
//            @Override
//            public void onRtcStats(NERtcStats neRtcStats) {
//
//            }
//
//            @Override
//            public void onLocalAudioStats(NERtcAudioSendStats neRtcAudioSendStats) {
//
//            }
//
//            @Override
//            public void onRemoteAudioStats(NERtcAudioRecvStats[] neRtcAudioRecvStats) {
//
//            }
//
//            @Override
//            public void onLocalVideoStats(NERtcVideoSendStats neRtcVideoSendStats) {
//
//            }
//
//            @Override
//            public void onRemoteVideoStats(NERtcVideoRecvStats[] neRtcVideoRecvStats) {
//                if (neRtcVideoRecvStats == null || neRtcVideoRecvStats.length == 0) {
//                    return;
//                }
//
//                if (neRtcVideoRecvStats[0].receivedBitrate == 0) {
//                    mCountTime++;
//                    if (mCountTime == 7) {
//                        mCountTime = 0;
//                        hungUpAndFinish();
//                    }
//                } else {
//                    mCountTime = 0;
//                }
//            }
//
//            //            0	??????????????????
////            1	??????????????????
////            2	?????????????????????????????????????????????????????????????????????
////            3	?????????????????????
////            4	???????????????
////            5	??????????????????
//            @Override
//            public void onNetworkQuality(NERtcNetworkQualityInfo[] neRtcNetworkQualityInfos) {
//                if (neRtcNetworkQualityInfos == null && neRtcNetworkQualityInfos.length == 0) {
//                    return;
//                }
//
//                for (NERtcNetworkQualityInfo networkQualityInfo : neRtcNetworkQualityInfos) {
//                    if (networkQualityInfo.userId == peerUid) {
//                        if (networkQualityInfo.upStatus >= 4) {
//                            ToastUtils.toastShow(R.string.orther_internet_quality_no);
//                        } else if (networkQualityInfo.upStatus == 0) {
//                            Log.e(LOG_TAG, "network is unKnow");
//                        }
//
//                        Log.i(LOG_TAG, String.format("NERtcNetworkQualityInfo: %d", networkQualityInfo.downStatus));
//                    }
//                }
//            }
//        });
    }

    boolean isStrategyMsg = false;

    private void initIntent() {
        inventRequestId = getIntent().getStringExtra(CallParams.INVENT_REQUEST_ID);
        inventChannelId = getIntent().getStringExtra(CallParams.INVENT_CHANNEL_ID);
        inventFromAccountId = getIntent().getStringExtra(CallParams.INVENT_FROM_ACCOUNT_ID);

        callOutUser = (UserModel) getIntent().getSerializableExtra(CALL_OUT_USER);
        isStrategyMsg = getIntent().getBooleanExtra(CallParams.INVENT_STRATEGY, false);
        callReceived = getIntent().getBooleanExtra(CallParams.INVENT_CALL_RECEIVED, false);
    }

    private void initData() {
        nertcVideoCall = NERTCVideoCall.sharedInstance();
        nertcVideoCall.setTimeOut(30 * 1000);
        nertcVideoCall.addDelegate(nertcCallingDelegate);
        binding.ivCameraSwitch.setOnClickListener(v -> {
            nertcVideoCall.switchCamera();
        });
        binding.ivAudioControl.setOnClickListener(view -> {
            isMute = !isMute;
            nertcVideoCall.muteLocalAudio(isMute);
            if (isMute) {
                binding.ivAudioControl.setImageResource(R.drawable.img_voice_off);
            } else {
                binding.ivAudioControl.setImageResource(R.drawable.img_voice_on);
            }
        });

        binding.ivHangup.setOnClickListener(view -> {
            hungUpAndFinish();
        });

        if (!callReceived && callOutUser != null) {
            binding.tvCallUser.setText(callOutUser.nickname);
            binding.llyCancel.setVisibility(View.VISIBLE);
            binding.llyInvitedOperation.setVisibility(View.GONE);
            binding.llyCancel.setOnClickListener(v -> {
                cancel();
            });
//            ImageHelper.intoIV4Round(NERTCVideoCallActivity.this, binding.ivFlag, callOutUser.countryFlagUrl, ScreenInfo.getInstance().dip2px(5), -1, -1);
//            ImageHelper.intoIV4Circle(NERTCVideoCallActivity.this, binding.ivCallUser, callOutUser.avatar, -1, -1);
//            ImageHelper.intoIV4Blur(NERTCVideoCallActivity.this, binding.ivUserIconBg, callOutUser.avatar, 1, -1, -1);
        } else if (!TextUtils.isEmpty(inventRequestId) && !TextUtils.isEmpty(inventFromAccountId) && !TextUtils.isEmpty(inventChannelId)) {
            binding.llyCancel.setVisibility(View.GONE);
            binding.llyInvitedOperation.setVisibility(View.VISIBLE);
        }

        myOnPermissionsListener = new MyOnPermissionsListener(this);
        PermissionsUtils.getInstance().getPermissions().request(this, myOnPermissionsListener, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        String imid;
        if (callOutUser != null && !TextUtils.isEmpty(callOutUser.imAccid)) {
            imid = callOutUser.imAccid;
        } else {
            imid = inventFromAccountId;
        }
        List<String> userAccount = new ArrayList<>();
        userAccount.add(imid);
        if (callReceived) {
            binding.tvCallComment.setText(ResouseUtils.getResouseString(R.string.invite_4_video));
        } else {
            binding.tvCallComment.setText(ResouseUtils.getResouseString(R.string.str_wait_for_an_answer));
        }
        UserBeanCacheUtils.getInstance().getNetworkUsers(NERTCVideoCallActivity.this.toString(), userAccount, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                if (NERTCVideoCallActivity.this != null && !NERTCVideoCallActivity.this.isDestroyed()) {
                    if (userList != null && userList.size() > 0) {
                        userBean = userList.get(0);
//                        if (callReceived) {
//                        }
                        binding.tvCallUser.setText(userBean.nick);
                        ImageHelper.intoIV4Circle(NERTCVideoCallActivity.this, binding.ivCallUser, userBean.smallUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
                        ImageHelper.intoIV4Circle(NERTCVideoCallActivity.this, binding.ivFlag, userBean.countryUrl, -1, -1);
                        ImageHelper.intoIV4Blur(NERTCVideoCallActivity.this, binding.ivUserIconBg, userBean.userIcon, 1, -1, -1);
                        List<PriceItems> priceItems = userBean.pItems;
                        for (PriceItems priceItem : priceItems) {
                            if (VipDiamondsHelper.videoChat.equals(priceItem.type)) {
                                binding.tvPrice.setText(priceItem.cost + ResouseUtils.getResouseString(R.string.each_min));
                                binding.tvPrice.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    } else {
                        ReportUtils.report(EXCEPTION_TAG, "NERTCGetUserBeans", ReportConstant.NERTC_GET_USER_BEANS, "response == null || response.size == 0");
                    }
                }

            }

            @Override
            public void onGetFailed() {
                ReportUtils.report(EXCEPTION_TAG, "NERTCGetUserBeans", ReportConstant.NERTC_GET_USER_BEANS, "getUserBeanFailed");
            }
        });

    }

    private static class MyOnPermissionsListener implements OnPermissionsListener {
        private final WeakReference<NERTCVideoCallActivity> weakReference;

        public MyOnPermissionsListener(NERTCVideoCallActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onResult(boolean granted) {
            NERTCVideoCallActivity activity = null;
            if (weakReference != null) {
                activity = weakReference.get();
            }
            if (activity == null) {
                return;
            }
            if (granted) {
                if (activity.callReceived) {
                    activity.callIn();
                } else {
                    activity.callOUt();
                }
            }
        }

        public void clear() {
            if (weakReference != null) {
                weakReference.clear();
            }
        }
    }

    private void cancel() {
        nertcVideoCall.cancel(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AVChatSoundPlayer.instance().stop();
                finish();
            }

            @Override
            public void onFailed(int i) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCCancelFailed", ReportConstant.NERTC_CANCEL, i + "");
                // 10410 ?????????????????????
                if (i == 10410) {
                    return;
                }

                AVChatSoundPlayer.instance().stop();
                finish();
            }

            @Override
            public void onException(Throwable throwable) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCCancelError", ReportConstant.NERTC_CANCEL, throwable.getMessage());
                Log.e("NERTCVideoCallActivity", "cancel Failed", throwable);
                AVChatSoundPlayer.instance().stop();
                finish();
            }
        });
    }


    private void callOUt() {
        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.CONNECTING);
        String selfUserId = ProfileManager.getInstance().getUserModel().imAccid;
        nertcVideoCall.call(callOutUser.imAccid, selfUserId, ChannelType.VIDEO, new JoinChannelCallBack() {
            @Override
            public void onJoinChannel(ChannelFullInfo channelFullInfo) {
                resetUid(channelFullInfo, selfUserId);
            }

            @Override
            public void onJoinFail(String msg, int code) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCCallOut", ReportConstant.NERTC_JOIN_CHANNEL, code + msg);
                if (code == 10201) {
                    return;
                }

                finishOnFailed();
            }
        });
        NERtcVideoConfig videoConfig = new NERtcVideoConfig();
        videoConfig.frontCamera = true;
        videoConfig.frameRate = NERtcEncodeConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_15;
        NERtc.getInstance().setLocalVideoConfig(videoConfig);
    }

    private void resetUid(ChannelFullInfo channelFullInfo, String selfUserId) {
        if (channelFullInfo != null) {
            for (MemberInfo member : channelFullInfo.getMembers()) {
                if (TextUtils.equals(member.getAccountId(), selfUserId)) {
                    ProfileManager.getInstance().getUserModel().g2Uid = member.getUid();
                    break;
                }
            }
        }
    }

    private void showLoading() {
        binding.llAcceptLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.llAcceptLoading.setVisibility(View.GONE);
    }

    private void setLoadingText(@StringRes int resid) {
        binding.tvAcceptLoading.setText(resid);
    }

    private void callIn() {
        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
        invitedParam = new InviteParamBuilder(inventChannelId, inventFromAccountId, inventRequestId);
        binding.ivAccept.setOnClickListener(view -> {
            String imid;
            if (callOutUser != null && !TextUtils.isEmpty(callOutUser.imAccid)) {
                imid = callOutUser.imAccid;
            } else {
                imid = inventFromAccountId;
            }
            showLoading();
            VipDiamondsHelper.getInstance().allowCall4Video(NERTCVideoCallActivity.this.toString(), imid, new VipDiamondsHelper.OnAllowCallListener() {
                @Override
                public void onSuccess(AllowCallRsp allowCallRsp) {
                    if (allowCallRsp.allowCall) {
                        setLoadingText(R.string.str_connectting);
                        accept(invitedParam);
                        AVChatSoundPlayer.instance().stop();
                    } else {
                        hideLoading();
                        if (allowCallRsp.member) {
                            if (!allowCallRsp.enough) {
                                AVChatSoundPlayer.instance().stop();
//                                hungUpAndFinish();
                                reject();
                                goDiamonds();
                            }
                        } else {
                            GoNeedUIUtils.getInstance().goVipPage(NERTCVideoCallActivity.this);
                            if (!TextUtils.isEmpty(inventRequestId) && inventRequestId.contains("custom_call")) {
                                //todo recheck
                                AVChatSoundPlayer.instance().stop();
//                                hungUpAndFinish();
                                reject();
                            }
                        }
                    }
                }

                @Override
                public void onFailed() {
                    ReportUtils.report(EXCEPTION_TAG, "NERTCAllowCall4Video", ReportConstant.ALLOW_CALL, "AllowCallOnFailed");
                    hideLoading();
                    ToastUtils.toastShow(getString(R.string.get_vip_failed));
                    stopConsume();
                    //todo recheck
                    AVChatSoundPlayer.instance().stop();
//                    hungUpAndFinish();
                    reject();
                }
            });
        });

        binding.ivReject.setOnClickListener(view -> {
            if (isStrategyMsg) {
                AVChatSoundPlayer.instance().stop();
                finish();
                return;
            }
            reject();
        });


    }

    private void reject() {
        nertcVideoCall.reject(invitedParam, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }

            @Override
            public void onFailed(int i) {
                Log.e(LOG_TAG, "reject failed error code = " + i);
                ReportUtils.report(EXCEPTION_TAG, "NERTCRejectFailed", ReportConstant.NERTC_REJECT, i + "");
                if (i == 408) {
                    ToastUtils.toastShow(R.string.call_timeout);
//                        ToastUtils.toastShow("Reject timeout");
                } else {
                    Log.e(LOG_TAG, "Reject failed:" + i);
                    if (i == 10404 || i == 10408 || i == 10409 || i == 10201) {
                        finishOnFailed();
                    }
                }
            }

            @Override
            public void onException(Throwable throwable) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCRejectException", ReportConstant.NERTC_REJECT, "throwable???" + throwable.getMessage());
                Log.e(LOG_TAG, "reject failed onException", throwable);
                finishOnFailed();
            }
        });
        AVChatSoundPlayer.instance().stop();
    }

    private void goDiamonds() {
        GoNeedUIUtils.getInstance().goDiamondsPage(this);
    }

    public User userBean;

    /**
     * ????????????????????????
     */
    private void setupLocalVideo() {
        binding.localVideoView.setVisibility(View.VISIBLE);
        binding.ivUserIconBg.setVisibility(View.GONE);
        nertcVideoCall.setupLocalView(binding.localVideoView);
    }

    private void accept(InviteParamBuilder invitedParam) {
        String selfUserId = String.valueOf(ProfileManager.getInstance().getUserModel().imAccid);
        nertcVideoCall.accept(invitedParam, selfUserId, new JoinChannelCallBack() {
            @Override
            public void onJoinChannel(ChannelFullInfo channelFullInfo) {
                resetUid(channelFullInfo, selfUserId);
            }

            @Override
            public void onJoinFail(String msg, int code) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCAcceptFailed", ReportConstant.NERTC_ACCEPT, code + msg);
                if (code == 408) {
//                    ToastUtils.toastShow("Accept timeout normal");
                    ToastUtils.toastShow(R.string.call_timeout);
                } else {
                    Log.e(LOG_TAG, "Accept normal failed:" + code);
                    if (code == 10404 || code == 10408 || code == 10409 || code == 10201) {
                        if (code == 10201) {
                            ToastUtils.toastShow(R.string.she_hung_up);
//                            ToastUtils.toastShow("??????????????????");
                        }
                        handler.postDelayed(() -> {
                            finishOnFailed();
                        }, DELAY_TIME);
                    }
                }
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param uid ????????????Id
     */
    private void setupRemoteVideo(long uid) {
        remoteUid = uid;
        nertcVideoCall.setupRemoteView(binding.remoteVideoView, uid);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        final AlertDialog.Builder confirmDialog =
                new AlertDialog.Builder(NERTCVideoCallActivity.this);
        confirmDialog.setTitle(ResouseUtils.getResouseString(R.string.tips));
        confirmDialog.setMessage(ResouseUtils.getResouseString(R.string.confirm_hung_up));
        confirmDialog.setPositiveButton(ResouseUtils.getResouseString(R.string.lockchat_ok),
                (dialog, which) -> {
                    stopConsume();
                    AVChatSoundPlayer.instance().stop();
                    hungUpAndFinish();
                });
        confirmDialog.setNegativeButton(ResouseUtils.getResouseString(R.string.lockchat_cancel),
                (dialog, which) -> {

                });
        confirmDialog.show();
    }

    private void hungUpAndFinish() {
        ReportUtils.report(EXCEPTION_TAG, "hungUpAndFinish", ReportConstant.NERTC_HANGUP, "hungUpAndFinish");
        nertcVideoCall.hangup(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
            }

            @Override
            public void onFailed(int i) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCHangUptFailed", ReportConstant.NERTC_HANGUP, i + "");
                Log.e(LOG_TAG, "error when hangup code = " + i);
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
            }

            @Override
            public void onException(Throwable throwable) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCHangUpException", ReportConstant.NERTC_HANGUP, throwable.getMessage());
                Log.e(LOG_TAG, "onException when hangup", throwable);
                handler.postDelayed(() -> {
                    finish();
                }, DELAY_TIME);
            }
        });
    }

    private void finishOnFailed() {
        try {
            NERTCVideoCall.sharedInstance().leave(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e(LOG_TAG, "finishOnFailed leave onSuccess");
                }

                @Override
                public void onFailed(int i) {
                    ReportUtils.report(EXCEPTION_TAG, "NERTCLeaveFailed", ReportConstant.NERTC_LEAVE, i + "");
                    Log.e(LOG_TAG, "finishOnFailed leave onFailed code = " + i);
                }

                @Override
                public void onException(Throwable throwable) {
                    ReportUtils.report(EXCEPTION_TAG, "NERTCLeaveException", ReportConstant.NERTC_LEAVE, throwable.getMessage());
                    Log.e(LOG_TAG, "finishOnFailed leave onException", throwable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        ReportUtils.report(EXCEPTION_TAG, "NERTCVideoCallOnDestroy", ReportConstant.DESTROY, "onDestroy");
        HangUpHelper.getInstance().clearHungup();
        CallServiceInstance.getInstance().cancelNotification();
        AVChatSoundPlayer.instance().stop();

        stopConsume();
//        NERtcEx.getInstance().setStatsObserver(null);
        handler.removeCallbacksAndMessages(null);
        if (nertcCallingDelegate != null && nertcVideoCall != null) {
            nertcVideoCall.removeDelegate(nertcCallingDelegate);
        }
        binding.remoteVideoView.release();
        binding.localVideoView.release();
        if (smallRemoteVideoView != null) {
            smallRemoteVideoView.release();
        }
        EasyFloat.dismissAppFloat("videoFloat");
        ChatAnswerUtils.getInstance().setAnswering(false);

        if (!isExitLogin && !isRating && !TextUtils.isEmpty(otherId)) {
            GoNeedUIUtils.getInstance().goRating(NERTCVideoCallActivity.this, 1, otherId);
        }

        if (myOnPermissionsListener != null) {
            myOnPermissionsListener.clear();
        }
        super.onDestroy();
    }

    private void observeHangUpEvent() {

        HangUpHelper.getInstance().observeHangUp(new EventSubscriber<HangUp>() {
            @Override
            public void onEvent(HangUp hangUp) {
                onHangUpEvent(hangUp);
            }
        });
        HangUpHelper.getInstance().observeHangUpWithChannelId(new EventSubscriber<HangUpWithChannelId>() {
            @Override
            public void onEvent(HangUpWithChannelId hangUpWithChannelId) {
                onHangUpEvent(hangUpWithChannelId);
            }
        });
//        HangUpHelper.getInstance().observeHangUp(this, hangUp -> {
//            LogHelper.e("observeHangUpEvent","observeHangUp--------");
//            onHangUpEvent(hangUp);
//        });
//
//        HangUpHelper.getInstance().observeHangUpWithChannelId(this, hangUpWithChannelId -> onHangUpEvent(hangUpWithChannelId));
    }

    public void onHangUpEvent(HangUpWithChannelId event) {
        if (inventChannelId != null && inventChannelId.equalsIgnoreCase(event.channelId)) {
            //todo recheck
            AVChatSoundPlayer.instance().stop();
            hungUpAndFinish();
        }
    }

    public void onHangUpEvent(HangUp event) {
        isExitLogin = event.isExitLogin;
        AVChatSoundPlayer.instance().stop();
        stopConsume();
        hungUpAndFinish();
        //todo  recheck

    }

    boolean isUserHangup = false;
    private NERtcVideoView smallRemoteVideoView;
    long remoteUid = -1;

    @Override
    public void finish() {
        isUserHangup = true;
        EasyFloat.dismissAppFloat("videoFloat");
        callParentFinish();
    }

    public void callParentFinish() {
        FloatAnsweringWindow.getInstance().hideFloatWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            super.finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isShowPage = hasFocus;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isShowPage && isStrategyMsg) {
            AVChatSoundPlayer.instance().stop();
            hungUpAndFinish();
            return;
        }
        if (!isUserHangup && isShowPage) {
            toBack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (remoteUid != -1) {
            nertcVideoCall.setupRemoteView(binding.remoteVideoView, remoteUid);
            nertcVideoCall.enableLocalVideo(false);
            nertcVideoCall.enableLocalVideo(true);
        }
    }

    boolean isQuest;
    boolean isExitLogin = false;

    public void onSmallClick(View view) {
        if (com.lzf.easyfloat.permission.PermissionUtils.checkPermission(this)) {
            moveTaskToBack(true);
            showFloatWindow();
        } else {
            isQuest = true;
            NERTCSmallScreenActivity.start(this);
//            com.lzf.easyfloat.permission.PermissionUtils.requestPermission(this, new OnPermissionResult() {
//                @Override
//                public void permissionResult(boolean b) {
//                    if (b){
//                        CallServiceInstance.getInstance().setSmallScreenQuested(true);
//                        moveTaskToBack(true);
//                        showFloatWindow();
////                        FloatAnsweringWindow.getInstance().hideFloatWindow();
//                    }
//                }
//            });
        }
    }

    private void toBack() {
        if (isQuest) {
            moveTaskToBack(true);
            FloatAnsweringWindow.getInstance().showFirstFloatWindow(this, NERTCVideoCallActivity.class);
//            FloatAnsweringWindow.getInstance().showFirstFloatWindow(getIntent());
            isQuest = false;
        } else {
            if (com.lzf.easyfloat.permission.PermissionUtils.checkPermission(this)) {
                moveTaskToBack(true);
                showFloatWindow();
            } else {
                moveTaskToBack(true);
                FloatAnsweringWindow.getInstance().showFirstFloatWindow(this, NERTCVideoCallActivity.class);
//                FloatAnsweringWindow.getInstance().showFirstFloatWindow(getIntent());
//            AnsweringNotification.getInstance().addAnsweringNotification(getIntent());
//            stopChat();
            }
        }
    }

    private void stopChat() {
        if (callReceived) {
//                callIn();
            if (binding.llyDialogOperation.getVisibility() == View.VISIBLE) {
                AVChatSoundPlayer.instance().stop();
                hungUpAndFinish();
            } else {
                reject();
            }

        } else {
//                callOUt();
            if (binding.llyDialogOperation.getVisibility() == View.VISIBLE) {
                AVChatSoundPlayer.instance().stop();
                hungUpAndFinish();
            } else {
                cancel();
            }
        }
    }

    private void showFloatWindow() {
        FloatAnsweringWindow.getInstance().hideFloatWindow();
        EasyFloat.with(this)
                // ????????????xml???????????????????????????????????????
                .setLayout(R.layout.float_window_video, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
//                        view.findViewById(R.id.ll_out).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                moveToFront();
//                            }
//                        });
                        smallRemoteVideoView = view.findViewById(R.id.remote_video_view);
                        smallRemoteVideoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveToFront();
                            }
                        });
                    }
                })
                // ?????????????????????????????????????????????Activity???????????????????????????????????????????????????????????????
                .setShowPattern(ShowPattern.ALL_TIME)
                // ????????????????????????15????????????????????????SidePattern
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                // ????????????????????????????????????????????????
                .setTag("videoFloat")
                // ?????????????????????????????????????????????
                .setDragEnable(true)
                // ????????????????????????EditText??????????????????????????????????????????
                .hasEditText(false)
                // ???????????????????????????ps????????????????????????Gravity?????????offset???????????????
                .setLocation(0, 200)
                // ?????????????????????????????????????????????
                .setGravity(Gravity.END | Gravity.CENTER_VERTICAL, 0, 200)
                // ?????????????????????????????????????????????xml??????match_parent????????????
                .setMatchParent(false, false)
                // ??????Activity???????????????????????????????????????????????????????????????????????????????????????????????????????????????null
                .setAnimator(new DefaultAnimator())
                // ????????????????????????????????????????????????
                .setAppFloatAnimator(new AppFloatDefaultAnimator())
                // ?????????????????????????????????????????????
                .setFilter(NERTCVideoCallActivity.class, NERTCAudioCallActivity.class)
                // ??????????????????????????????????????????????????????????????????????????????touchEvent?????????????????????????????????
                // ps?????????Kotlin DSL??????????????????????????????????????????????????????????????????
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @org.jetbrains.annotations.Nullable String s, @org.jetbrains.annotations.Nullable View view) {

                    }

                    @Override
                    public void show(@NotNull View view) {
                        NERtcVideoView remoteVideoView = view.findViewById(R.id.remote_video_view);
                        if (remoteUid != -1) {
                            nertcVideoCall.setupRemoteView(remoteVideoView, remoteUid);
                        }
                    }

                    @Override
                    public void hide(@NotNull View view) {
//                        if (remoteUid!=-1){
//                            nertcVideoCall.setupRemoteView(binding.remoteVideoView, remoteUid);
//                        }
                    }

                    @Override
                    public void dismiss() {

                    }

                    @Override
                    public void touchEvent(@NotNull View view, @NotNull MotionEvent motionEvent) {
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                    }
                })
                .show();
    }

    private void moveToFront() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            if (recentTasks != null && !recentTasks.isEmpty()) {
                for (ActivityManager.RunningTaskInfo taskInfo : recentTasks) {
                    ComponentName cpn = taskInfo.baseActivity;
                    if (null != cpn && TextUtils.equals(NERTCVideoCallActivity.class.getName(), cpn.getClassName())) {
                        manager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_NO_USER_ACTION);
                        break;
                    }
                }
            }
        }
    }

    boolean isStartRecord = false;

    private void showRecordView() {
        if (isStartRecord) {
            return;
        }
        isStartRecord = true;
        binding.cRecordTime.setVisibility(View.VISIBLE);
        binding.tvCallComment.setVisibility(View.GONE);
        binding.cRecordTime.setBase(SystemClock.elapsedRealtime());
        binding.cRecordTime.start();
    }

    private void stopRecordView() {
        binding.cRecordTime.stop();
    }
}
