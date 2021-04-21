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
import android.widget.LinearLayout;
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
import com.lockulockme.lockuchat.databinding.AcAudioBinding;
import com.lockulockme.lockuchat.event.EventSubscriber;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.ui.FloatAnsweringWindow;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
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

public class NERTCAudioCallActivity extends NERTCBaseActivity {

    private static final String LOG_TAG = NERTCAudioCallActivity.class.getSimpleName();

    public static final String INVENT_EVENT = "call_in_event";
    public static final String CALL_OUT_USER = "call_out_user";

    private NERTCVideoCall nertcVideoCall;


    private UserModel callOutUser;//呼出用户
    private boolean callReceived;

    private String inventRequestId;
    private String inventChannelId;
    private String inventFromAccountId;

    private boolean isMute;//是否静音
    private boolean isCamOff;//是否关闭摄像头

    private static final int DELAY_TIME = 0;//延时

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
    private final String EXCEPTION_TAG = "NERTCAudioCallActivity";

    private class ConsumeStoneTask implements Runnable {

        private String targetImId = "";

        public ConsumeStoneTask(String accId) {
            targetImId = accId;
        }

        @Override
        public void run() {
            VipDiamondsHelper.getInstance().ruduceDiamonds4VoiceChat(NERTCAudioCallActivity.this.toString(), targetImId, callChannelId, new VipDiamondsHelper.OnReduceListener() {
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
        VipDiamondsHelper.getInstance().checkUserRate(NERTCAudioCallActivity.this, imId, new VipDiamondsHelper.onUserRateListener() {
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
//            ToastUtils.showToast(R.string.call_failed, Toast.LENGTH_LONG);
            finish();
        }

        @Override
        public void onInvited(InvitedEvent invitedEvent) {

        }


        @Override
        public void onUserEnter(long uid, String accId) {
            otherId = accId;
            ReportUtils.report(EXCEPTION_TAG, "NERTCCallingDelegateOnUserEnter", ReportConstant.USER_ENTER, "accid:" + accId);
            showRecordView();
            hideLoading();
            if (scheduled != null) {
                scheduled.shutdownNow();
                scheduled = null;
            }
            scheduled = new ScheduledThreadPoolExecutor(2);
            scheduled.scheduleAtFixedRate(new ConsumeStoneTask(accId), 0, 60 * 1000, TimeUnit.MILLISECONDS);
            binding.llyCancel.setVisibility(View.GONE);
            binding.llyDialogOperation.setVisibility(View.VISIBLE);
            binding.llyInvitedOperation.setVisibility(View.GONE);
            binding.ivSmall.setVisibility(View.VISIBLE);
            setupLocalAudio();
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
                onCallEnd(accountId, R.string.she_hung_up);
            }
        }

        private void onCallEnd(String userId, int tip) {
            stopConsume();
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

        }

        @Override
        public void onAudioAvailable(long userId, boolean isAudioAvailable) {
            if (!ProfileManager.getInstance().isCurrentUser(userId)) {
                if (isAudioAvailable) {
                    setUserInfoOnDialog();
                }
            }
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
    private AcAudioBinding binding;

    public static void startBeingCall(Context context, InvitedEvent invitedEvent) {
        Intent intent = new Intent(context, NERTCAudioCallActivity.class);
        intent.putExtra(INVENT_EVENT, invitedEvent);
        intent.putExtra(CallParams.INVENT_CALL_RECEIVED, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startCallOther(Context context, UserModel callOutUser) {
        Intent intent = new Intent(context, NERTCAudioCallActivity.class);
        intent.putExtra(CALL_OUT_USER, callOutUser);
        intent.putExtra(CallParams.INVENT_CALL_RECEIVED, false);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 应用运行时，保持不锁屏、全屏化
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        CallServiceInstance.getInstance().cancelNotification();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = AcAudioBinding.inflate(getLayoutInflater());
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
//                if (neRtcAudioRecvStats == null || neRtcAudioRecvStats.length == 0) {
//                    return;
//                }
//
//                if (neRtcAudioRecvStats[0].kbps == 0) {
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
//            @Override
//            public void onLocalVideoStats(NERtcVideoSendStats neRtcVideoSendStats) {
//
//            }
//
//            @Override
//            public void onRemoteVideoStats(NERtcVideoRecvStats[] neRtcVideoRecvStats) {
//
//            }
//
//            //            0	网络质量未知
////            1	网络质量极好
////            2	用户主观感觉和极好差不多，但码率可能略低于极好
////            3	能沟通但不顺畅
////            4	网络质量差
////            5	完全无法沟通
//            @Override
//            public void onNetworkQuality(NERtcNetworkQualityInfo[] neRtcNetworkQualityInfos) {
//                if (neRtcNetworkQualityInfos == null && neRtcNetworkQualityInfos.length == 0) {
//                    return;
//                }
//
//                for (NERtcNetworkQualityInfo networkQualityInfo : neRtcNetworkQualityInfos) {
//                    if (networkQualityInfo.userId == peerUid) {
//                        if (networkQualityInfo.upStatus >= 4 || networkQualityInfo.downStatus >= 4) {
//                            ToastUtils.toastShow(R.string.orther_internet_quality_no);
//                        } else if (networkQualityInfo.upStatus == 0 || networkQualityInfo.downStatus == 0) {
//                            ToastUtils.toastShow(R.string.orther_internet_quality_no_maybe);
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
//            ImageHelper.intoIV4Circle(NERTCAudioCallActivity.this, binding.ivFlag, callOutUser.countryFlagUrl, -1, -1);
//            ImageHelper.intoIV4Circle(NERTCAudioCallActivity.this, binding.ivCallUser, callOutUser.avatar, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
//            ImageHelper.intoIV4Blur(NERTCAudioCallActivity.this, binding.ivUserIconBg, callOutUser.avatar, 1, -1, -1);
        } else if (!TextUtils.isEmpty(inventRequestId) && !TextUtils.isEmpty(inventFromAccountId) && !TextUtils.isEmpty(inventChannelId)) {
            binding.llyCancel.setVisibility(View.GONE);
            binding.llyInvitedOperation.setVisibility(View.VISIBLE);
        }
        myOnPermissionsListener = new MyOnPermissionsListener(this);
        PermissionsUtils.getInstance().getPermissions().request(this, myOnPermissionsListener, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        String imid;
        if (callOutUser != null && !TextUtils.isEmpty(callOutUser.imAccid)) {
            imid = callOutUser.imAccid;
        } else {
            imid = inventFromAccountId;
        }
        List<String> userAccount = new ArrayList<>();
        userAccount.add(imid);
        if (callReceived) {
            binding.tvCallComment.setText(ResouseUtils.getResouseString(R.string.invite_4_voice));
        } else {
            binding.tvCallComment.setText(ResouseUtils.getResouseString(R.string.str_wait_for_an_answer));
        }
        UserBeanCacheUtils.getInstance().getNetworkUsers(NERTCAudioCallActivity.this.toString(), userAccount, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                if (NERTCAudioCallActivity.this != null && !NERTCAudioCallActivity.this.isDestroyed()) {
                    if (userList != null && userList.size() > 0) {
                        userBean = userList.get(0);
                        binding.tvCallUser.setText(userBean.nick);
                        ImageHelper.intoIV4Circle(NERTCAudioCallActivity.this, binding.ivCallUser, userBean.middleUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
                        ImageHelper.intoIV4Circle(NERTCAudioCallActivity.this, binding.ivFlag, userBean.countryUrl, -1, -1);
                        ImageHelper.intoIV4Blur(NERTCAudioCallActivity.this, binding.ivUserIconBg, userBean.userIcon, 1, -1, -1);
                        LogHelper.e("getUserDetail", "----------0" + userBean);
                        LogHelper.e("getUserDetail", "----------1");
                        List<PriceItems> priceItems = userBean.pItems;
                        for (PriceItems priceItem : priceItems) {
                            LogHelper.e("getUserDetail", "----------2");
                            if (VipDiamondsHelper.voiceChat.equals(priceItem.type)) {
                                LogHelper.e("getUserDetail", "----------3");
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
        private final WeakReference<NERTCAudioCallActivity> weakReference;

        public MyOnPermissionsListener(NERTCAudioCallActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onResult(boolean granted) {
            NERTCAudioCallActivity activity = null;
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
                // 10410 邀请已经接受了
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
        nertcVideoCall.call(callOutUser.imAccid, selfUserId, ChannelType.AUDIO, new JoinChannelCallBack() {
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
            VipDiamondsHelper.getInstance().allowCall4Audio(NERTCAudioCallActivity.this.toString(), imid, new VipDiamondsHelper.OnAllowCallListener() {

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
                            GoNeedUIUtils.getInstance().goVipPage(NERTCAudioCallActivity.this);
                            if (!TextUtils.isEmpty(inventRequestId) && inventRequestId.contains("custom_call")) {
                                //todo recheck
                                AVChatSoundPlayer.instance().stop();
                                reject();
//                                hungUpAndFinish();

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
                    ToastUtils.toastShow(R.string.call_timeout);//.showShort("Reject timeout");
                } else {
                    Log.e(LOG_TAG, "Reject failed:" + i);
                    if (i == 10404 || i == 10408 || i == 10409 || i == 10201) {
                        finishOnFailed();
                    }
                }
            }

            @Override
            public void onException(Throwable throwable) {
                ReportUtils.report(EXCEPTION_TAG, "NERTCRejectException", ReportConstant.NERTC_REJECT, "throwable：" + throwable.getMessage());
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
     * 设置本地视频视图
     */
    private void setupLocalAudio() {
        NERtc.getInstance().enableLocalAudio(true);
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
                    ToastUtils.toastShow("Accept timeout");
                } else {
                    Log.e(LOG_TAG, "Accept failed:" + code);
                    if (code == 10404 || code == 10408 || code == 10409 || code == 10201) {
                        if (code == 10201) {
                            ToastUtils.toastShow(R.string.she_hung_up);
//                             ToastUtils.toastShow("对方已经掉线");
                        }
                        handler.postDelayed(() -> {
                            finishOnFailed();
                        }, DELAY_TIME);
                    }
                }
            }
        });
    }

    private void setUserInfoOnDialog() {
        binding.rlyTopUserInfo.setVisibility(View.VISIBLE);
        binding.tvCallComment.setText(ResouseUtils.getResouseString(R.string.answer_ing));

    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        final AlertDialog.Builder confirmDialog =
                new AlertDialog.Builder(NERTCAudioCallActivity.this);
        confirmDialog.setTitle(ResouseUtils.getResouseString(R.string.tips));
        confirmDialog.setMessage(ResouseUtils.getResouseString(R.string.confirm_hung_up));
        confirmDialog.setPositiveButton(ResouseUtils.getResouseString(R.string.lockchat_ok),
                (dialog, which) -> {
                    AVChatSoundPlayer.instance().stop();
                    stopConsume();
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
        ReportUtils.report(EXCEPTION_TAG, "NERTCAudioCallOnDestroy", ReportConstant.DESTROY, "onDestroy");
        HangUpHelper.getInstance().clearHungup();
        CallServiceInstance.getInstance().cancelNotification();
        AVChatSoundPlayer.instance().stop();

//        NERtcEx.getInstance().setStatsObserver(null);
        stopConsume();
        handler.removeCallbacksAndMessages(null);
        if (nertcCallingDelegate != null && nertcVideoCall != null) {
            nertcVideoCall.removeDelegate(nertcCallingDelegate);
        }
        EasyFloat.dismissAppFloat("videoFloat");
        ChatAnswerUtils.getInstance().setAnswering(false);

        if (!isExitLogin && !isRating && !TextUtils.isEmpty(otherId)) {
            GoNeedUIUtils.getInstance().goRating(NERTCAudioCallActivity.this, 2, otherId);
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
//        HangUpHelper.getInstance().observeHangUp(this, new Observer<HangUp>() {
//            @Override
//            public void onChanged(HangUp hangUp) {
//                onHangUpEvent(hangUp);
//            }
//        });
//
//        HangUpHelper.getInstance().observeHangUpWithChannelId(this, new Observer<HangUpWithChannelId>() {
//            @Override
//            public void onChanged(HangUpWithChannelId hangUpWithChannelId) {
//                onHangUpEvent(hangUpWithChannelId);
//            }
//        });
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
    }

    boolean isUserHangup = false;
    boolean isExitLogin = false;

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

    boolean isQuest;

    public void onSmallClick(View view) {
        if (com.lzf.easyfloat.permission.PermissionUtils.checkPermission(this)) {
            moveTaskToBack(true);
            showFloatWindow();
        } else {
            isQuest = true;
            NERTCSmallScreenActivity.start(this);
        }

    }

    private void toBack() {
        if (isQuest) {
            moveTaskToBack(true);
            FloatAnsweringWindow.getInstance().showFirstFloatWindow(this, NERTCAudioCallActivity.class);
//            FloatAnsweringWindow.getInstance().showFirstFloatWindow(getIntent());
            isQuest = false;
        } else {
            if (com.lzf.easyfloat.permission.PermissionUtils.checkPermission(this)) {
                moveTaskToBack(true);
                showFloatWindow();
            } else {
                moveTaskToBack(true);
                FloatAnsweringWindow.getInstance().showFirstFloatWindow(this, NERTCAudioCallActivity.class);
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
                // 设置浮窗xml布局文件，并可设置详细信息
                .setLayout(R.layout.float_window_audio, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {
                        LinearLayout llin = view.findViewById(R.id.ll_in);
                        llin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveToFront();
                            }
                        });
                    }
                })
                // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示、仅后台显示
                .setShowPattern(ShowPattern.ALL_TIME)
                // 设置吸附方式，共15种模式，详情参考SidePattern
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                // 设置浮窗的标签，用于区分多个浮窗
                .setTag("videoFloat")
                // 设置浮窗是否可拖拽，默认可拖拽
                .setDragEnable(true)
                // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
                .hasEditText(false)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                .setLocation(0, 200)
                // 设置浮窗的对齐方式和坐标偏移量
                .setGravity(Gravity.END | Gravity.CENTER_VERTICAL, 0, 200)
                // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
                .setMatchParent(false, false)
                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
                .setAnimator(new DefaultAnimator())
                // 设置系统浮窗的出入动画，使用同上
                .setAppFloatAnimator(new AppFloatDefaultAnimator())
                // 设置系统浮窗的不需要显示的页面
                .setFilter(NERTCVideoCallActivity.class, NERTCAudioCallActivity.class)
                // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
                // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @org.jetbrains.annotations.Nullable String s, @org.jetbrains.annotations.Nullable View view) {

                    }

                    @Override
                    public void show(@NotNull View view) {
                    }

                    @Override
                    public void hide(@NotNull View view) {
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
                    if (null != cpn && TextUtils.equals(NERTCAudioCallActivity.class.getName(), cpn.getClassName())) {
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
