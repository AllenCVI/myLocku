package com.lockulockme.locku.module.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.FirstGuideResponseBean;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.RetentionBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.HeartManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.common.PopupWindowBuilder;
import com.lockulockme.locku.databinding.ActivityMainBinding;
import com.lockulockme.locku.databinding.LayoutRetentionDialogBinding;
import com.lockulockme.locku.databinding.LayoutSwitchUserBinding;
import com.lockulockme.locku.im.IMInitHelper;
import com.lockulockme.locku.module.entity.TabEntity;
import com.lockulockme.locku.module.ui.adapter.ContentNormalPagerAdapter;
import com.lockulockme.locku.module.ui.fragment.ChartsFragment;
import com.lockulockme.locku.module.ui.fragment.HomeFragment;
import com.lockulockme.locku.module.ui.fragment.MeFragment;
import com.lockulockme.locku.module.ui.fragment.MsgListFragment;
import com.lockulockme.locku.module.ui.pop.FirstGuidePop;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.UIService;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.VideoCallOptions;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.impl.UIServiceManager;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.aavg2.ui.model.ProfileManager;
import com.lockulockme.lockuchat.ui.ChatActivity;
import com.lockulockme.lockuchat.ui.FloatNotificationManager;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private final int[] mIconUnSelectIds = {
            R.mipmap.home_normal, R.mipmap.rank_normal, R.mipmap.msg_normal, R.mipmap.mine_normal};//R.mipmap.msg_normal,
    private final int[] mIconSelectIds = {
            R.mipmap.home_press, R.mipmap.rank_press, R.mipmap.msg_press, R.mipmap.mine_press};// R.mipmap.msg_press,
    private final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private final List<Fragment> fragments = new ArrayList<>();

    final RxPermissions rxPermissions = new RxPermissions(this);
    private Handler mHandler;
    private int currentIndex = 0;
    private static final int defaultIndex = 0;
    private PopupWindowBuilder popBuilder;
    private LayoutRetentionDialogBinding popRetention;
    private FirstGuidePop firstGuidePop;

    public static void StartMe(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void StartMe(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }


    public void setMsgCurrent() {
        if (fragments != null && binding.viewpager != null) {
            binding.viewpager.setCurrentItem(2);
            binding.tabLayout.setCurrentTab(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mHandler = new Handler(getMainLooper());
        VipManager.getInstance().startTimer();
        initView();
        requestPermissions();
        parseIntent();
        HeartManager.getInstance().startTimer();

//        String account = "jktqylqbqxrgafji";
//        SessionTypeEnum sessionType = SessionTypeEnum.P2P;
//        String text = "this is an example";
//        IMMessage textMessage = MessageBuilder.createTextMessage(account, sessionType, text);
//
//        Map<String, Object> extensions = textMessage.getRemoteExtension();
//        if (extensions == null) {
//            extensions = new HashMap<>();
//        }
//        extensions.put("transformContent", "this is an example");
//        textMessage.setRemoteExtension(extensions);
//        NIMClient.getService(MsgService.class).sendMessage(textMessage, false).setCallback(new RequestCallback<Void>() {
//            @Override
//            public void onSuccess(Void param) {
//
//            }
//
//            @Override
//            public void onFailed(int code) {
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//
//            }
//        });
        initG2();
        downloadPayIcons();
        initRegisterGuideDialog();
        loadFirstGuide();
    }

    private void initRegisterGuideDialog() {
        firstGuidePop = new FirstGuidePop(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent();
    }

    private void requestPermissions() {
        rxPermissions
                .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        ToastUtils.toastShow(getString(R.string.no_camera_permission));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        OkGoUtils.getInstance().promptBind(MainActivity.this, new NewJsonCallback<Boolean>() {
            @Override
            public void onSuc(Boolean response, String msg) {
                if (response) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showBindPop();
                        }
                    });
                }
            }
        });
    }

    private void loadFirstGuide() {
        OkGoUtils.getInstance().getFirstGuideDialog(this, new NewJsonCallback<FirstGuideResponseBean>() {
            @Override
            public void onSuc(FirstGuideResponseBean response, String msg) {
                if (response != null) {
                    firstGuidePop.setBean(response).showDialog();
                }

            }
        });
    }

    private void showBindPop() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(MainActivity.this)
                .setContentView(R.layout.layout_switch_user)
                .build();
        final LayoutSwitchUserBinding popRateBing = LayoutSwitchUserBinding.bind(popBuilder.getRootView());
        popBuilder.setFocusable(true);
        popBuilder.setOutsideTouchable(false);
        popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
        popRateBing.tvContent.setText(getResources().getString(R.string.bindTip));
        popRateBing.tvSwitchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.tvSwitchConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordInfoActivity.go(MainActivity.this);
                popBuilder.dismiss();
            }
        });
    }

    private RetentionBean retentionBean;

    private void getRetentionInfo() {
        OkGoUtils.getInstance().getRetentionInfo(MainActivity.this, new NewJsonCallback<RetentionBean>() {
            @Override
            public void onSuc(RetentionBean response, String msg) {
                canQuit = true;
                retentionBean = response;
                showRetentionPop();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, RetentionBean response) {
                canQuit = true;
                moveTaskToBack(true);
            }
        });
    }

    private void showRetentionPop() {
        if (popRetention == null) {
            moveTaskToBack(true);
            return;
        }
        if (retentionBean != null && !TextUtils.isEmpty(retentionBean.retentionStr)) {
            popRetention.tvContent.setText(retentionBean.retentionStr);
        }

        if (retentionBean != null && retentionBean.user != null) {
            if (!TextUtils.isEmpty(retentionBean.user.name)) {
                popRetention.tvName.setText(retentionBean.user.name);
            }
            GlideUtils.loadCircleImg(MainActivity.this, retentionBean.user.smallAvatar, popRetention.ivAvatar, R.mipmap.img_charts_head_small);
            GlideUtils.loadCircleImg(MainActivity.this, retentionBean.user.countryUr, popRetention.ivCountyFlag, R.mipmap.country_placeholder);
            if (retentionBean.user.myLevelData != null && retentionBean.user.myLevelData.myLevel > 0) {
                popRetention.ivLevel.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(MainActivity.this, retentionBean.user.myLevelData.LevelIcon, popRetention.ivLevel, R.mipmap.level_icon_place_holder);
            } else {
                popRetention.ivLevel.setVisibility(View.GONE);
            }
        }
        popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
    }

    private void initView() {
        initRetentionPop();
        for (int i = 0; i < mIconSelectIds.length; i++) {
            mTabEntities.add(new TabEntity(null, mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        fragments.add(new HomeFragment());
        fragments.add(new ChartsFragment());
        fragments.add(new MsgListFragment());
        fragments.add(new MeFragment());
        binding.tabLayout.setTabData(mTabEntities);
        binding.tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.viewpager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        binding.viewpager.setAdapter(new ContentNormalPagerAdapter(getSupportFragmentManager(), fragments, null));
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                binding.tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.viewpager.setOffscreenPageLimit(fragments.size());
        binding.viewpager.setCurrentItem(0);
        binding.tabLayout.setCurrentTab(0);
        initTabTitle();
        IMLoginHelper.getInstance().observeUnreadMsgNum(this, unreadNum -> {
            setMsgDot(unreadNum);
        });
    }


    private void initRetentionPop() {
        popBuilder = PopupWindowBuilder.newBuilder(MainActivity.this)
                .setFocusable(true)
                .setOutsideTouchable(false)
                .setContentView(R.layout.layout_retention_dialog)
                .build();

        popRetention = LayoutRetentionDialogBinding.bind(popBuilder.getRootView());

        popRetention.tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveTaskToBack(true);
                    }
                }, 200);
            }
        });
        popRetention.tvGoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retentionBean.user != null) {
                    ChatActivity.startMe(MainActivity.this, retentionBean.user);
                }
                popBuilder.dismiss();
            }
        });
    }

    private void setMsgDot(Integer unreadNum) {
        if (unreadNum > 0) {
            binding.tabLayout.showMsg(2, unreadNum);
        } else {
            binding.tabLayout.hideMsg(2);
        }
    }


    private void initTabTitle() {
        for (int i = 0; i < fragments.size(); i++) {
            binding.tabLayout.getTitleView(i).setVisibility(View.GONE);
        }
    }

    private void initG2() {
        CallServiceInstance.getInstance().setSmallScreenQuested(PermissionUtils.checkPermission(this));

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineObserver, true);
    }

    private final Observer<StatusCode> onlineObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode == StatusCode.LOGINED) {
                NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);

                // TODO G2 用户根据实际配置方式获取
                LoginInfo loginInfo = IMInitHelper.getLoginInfo();
                if (loginInfo == null) {
                    return;
                }

                String imAccount = loginInfo.getAccount();
                LogHelper.e("initG2=", "" + imAccount);
                String imToken = loginInfo.getToken();
                NERTCVideoCall.sharedInstance().setupAppKey(getApplicationContext(), IMInitHelper.getKey(), new VideoCallOptions(null, null, new UIService() {
                    @Override
                    public String getOneToOneAudioChat() {
                        return "com.lockulockme.lockuchat.aavg2.ui.ui.NERTCAudioCallActivity";
                    }

                    @Override
                    public String getOneToOneVideoChat() {
                        return "com.lockulockme.lockuchat.aavg2.ui.ui.NERTCVideoCallActivity";
                    }

                    @Override
                    public String getGroupVideoChat() {
                        return "com.lockulockme.lockuchat.aavg2.ui.ui.team.TeamG2Activity";
                    }
                }, ProfileManager.getInstance()));

                NERTCVideoCall.sharedInstance().login(imAccount, imToken, new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {

                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });

                Intent intent = getIntent();
                if (intent.hasExtra(CallParams.INVENT_NOTIFICATION_FLAG) && intent.getBooleanExtra(CallParams.INVENT_NOTIFICATION_FLAG, false)) {
                    Bundle extraIntent = intent.getBundleExtra(CallParams.INVENT_NOTIFICATION_EXTRA);
                    intent.removeExtra(CallParams.INVENT_NOTIFICATION_FLAG);
                    intent.removeExtra(CallParams.INVENT_NOTIFICATION_EXTRA);

                    Intent avChatIntent = new Intent();
                    for (String key : CallParams.CallParamKeys) {
                        avChatIntent.putExtra(key, extraIntent.getString(key));
                    }

                    String callType = extraIntent.getString(CallParams.INVENT_CALL_TYPE);
                    String channelType = extraIntent.getString(CallParams.INVENT_CHANNEL_TYPE);

                    if (TextUtils.equals(String.valueOf(CallParams.CallType.TEAM), callType)) {
                        avChatIntent.setClassName(MainActivity.this, UIServiceManager.getInstance().getUiService().getGroupVideoChat());

                        try {
                            String userIdsBase64 = extraIntent.getString(CallParams.INVENT_USER_IDS);
                            String userIdsJson = new String(Base64.decode(userIdsBase64, Base64.DEFAULT));
                            JSONArray jsonArray = new JSONArray(userIdsJson);

                            ArrayList<String> userIds = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String userId = jsonArray.getString(i);
                                userIds.add(userId);
                            }

                            avChatIntent.putExtra(CallParams.INVENT_USER_IDS, userIds);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (TextUtils.equals(String.valueOf(ChannelType.AUDIO.getValue()), channelType)) {
                            avChatIntent.setClassName(MainActivity.this, UIServiceManager.getInstance().getUiService().getOneToOneAudioChat());
                        } else {
                            avChatIntent.setClassName(MainActivity.this, UIServiceManager.getInstance().getUiService().getOneToOneVideoChat());
                        }
                    }

                    avChatIntent.putExtra(CallParams.INVENT_CALL_RECEIVED, true);
                    startActivity(avChatIntent);
                }

                FloatNotificationManager.getInstance().registerMsgObserver(true);
                IMLoginHelper.getInstance().observeReceiveMessage(true);
            }
        }
    };

    private void downloadPayIcons() {
        OkGoUtils.getInstance().getPayCenterList(MainActivity.this, new Object(), new NewJsonCallback<List<PayCenterResponseBean>>() {
            @Override
            public void onSuc(List<PayCenterResponseBean> response, String msg) {
                if (response != null) {
                    for (int i = 0; i < response.size(); i++) {
                        GlideUtils.loadImageSimpleTarget(MainActivity.this, response.get(i).icon);
                    }
                }

            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<PayCenterResponseBean> response) {
                hideLoading();
            }
        });
    }

    private boolean parseIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) intent.getSerializableExtra(
                    NimIntent.EXTRA_NOTIFY_CONTENT);
            intent.removeExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    ChatActivity.startMe(this, message.getSessionId());
                    break;
            }
            return true;
        }
        return false;
    }

    private boolean canQuit = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentIndex != defaultIndex) {
                binding.viewpager.setCurrentItem(defaultIndex);
            } else {
                if (canQuit) {
                    canQuit = false;
                    getRetentionInfo();
                }
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineObserver, true);
    }
}
