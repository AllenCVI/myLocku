package com.lockulockme.locku.zlockseven.module.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.ActivityMainBinding;
import com.lockulockme.locku.zlockseven.base.utils.VipManager;
import com.lockulockme.locku.zlockseven.common.BaseActivity;
import com.lockulockme.locku.zlockseven.im.IMInitHelper;
import com.lockulockme.locku.zlockseven.module.entity.TabEntity;
import com.lockulockme.locku.zlockseven.module.ui.adapter.ContentNormalPagerAdapter;
import com.lockulockme.locku.zlockseven.module.ui.fragment.ChartsFragment;
import com.lockulockme.locku.zlockseven.module.ui.fragment.HomeFragment;
import com.lockulockme.locku.zlockseven.module.ui.fragment.MeFragment;
import com.lockulockme.locku.zlockseven.module.ui.fragment.MsgListFragment;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.UIService;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.VideoCallOptions;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.impl.UIServiceManager;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.aavg2.ui.model.ProfileManager;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
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


    public static void StartMe(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
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
        VipManager.getInstance().startTimer();
        initView();
        requestPermissions();

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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void requestPermissions() {
        rxPermissions
                .request(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        ToastUtils.toastShow(getString(R.string.no_camera_permission));
                    }
                });
    }

    private void initView() {
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
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
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
                }
            }
        }, true);
    }

}
