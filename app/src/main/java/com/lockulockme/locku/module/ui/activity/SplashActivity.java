package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lockulockme.locku.application.MyApplication;
import com.lockulockme.locku.base.CommonUtils;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.common.LockUMixPushMessageHandler;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends BaseActivity {

    public static void startMe(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        if (!firstEnter) {
            onIntent(); // APP进程还在，Activity被重新调度起来
        }
        CommonUtils.killedToRestart = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().hasExtra(CallParams.INVENT_NOTIFICATION_FLAG) && getIntent().getBooleanExtra(CallParams.INVENT_NOTIFICATION_FLAG, false)) {
        } else {
            setIntent(intent);
        }

        onIntent();

        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstEnter) {
            firstEnter = false;
            if (canAutoLogin()) {
                onIntent();
            } else {
                LoginIndexActivity.StartMe(SplashActivity.this);
                finish();
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }


    private static boolean firstEnter = true; // 是否首次进入



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        if (!canAutoLogin()) {
            if (TextUtils.isEmpty(AccountManager.getInstance().getAccount())){
                LoginIndexActivity.StartMe(this);
            }else {
                LoginActivity.StartMe(this);
            }
            finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(CallParams.INVENT_NOTIFICATION_FLAG) && intent.getBooleanExtra(CallParams.INVENT_NOTIFICATION_FLAG, false)) {
                    parseG2Intent(intent);
                    return;
                }
                LogUtil.LogE("parseNotifyIntent","-11---0");
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    LogUtil.LogE("parseNotifyIntent","-11---1");
                    parseNotifyIntent(intent);
                    return;
                } else if (NIMClient.getService(MixPushService.class).isFCMIntent(intent)) {
                    LogUtil.LogE("parseNotifyIntent","-11---2");
                    parseFCMNotifyIntent(NIMClient.getService(MixPushService.class).parseFCMPayload(intent));
                }
                LogUtil.LogE("parseNotifyIntent","-11---3");
            }

            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        String account = null;
        if ( AccountManager.getInstance().getCurrentUser()!=null){
            account= AccountManager.getInstance().getCurrentUser().nimId;
        }
        String nimToken = AccountManager.getInstance().getNimToken();
        String token = AccountManager.getInstance().getToken();

        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(nimToken) && !TextUtils.isEmpty(token);
    }

    private void parseNotifyIntent(Intent intent) {
        LogUtil.LogE("parseNotifyIntent","----1");
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() == 0 || messages.size() > 1) {
            LogUtil.LogE("parseNotifyIntent","----2");
            showMainActivity();
        } else {
            LogUtil.LogE("parseNotifyIntent","----3");
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
    }

    private void parseG2Intent(Intent intent) {
        Intent mainIntent = new Intent();
        mainIntent.setClass(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle extraIntent = intent.getBundleExtra(CallParams.INVENT_NOTIFICATION_EXTRA);
        mainIntent.putExtra(CallParams.INVENT_NOTIFICATION_EXTRA, extraIntent);
        mainIntent.putExtra(CallParams.INVENT_NOTIFICATION_FLAG, true);

        startActivity(mainIntent);

        intent.removeExtra(CallParams.INVENT_NOTIFICATION_FLAG);
        intent.removeExtra(CallParams.INVENT_NOTIFICATION_EXTRA);

        finish();
    }

    private void parseFCMNotifyIntent(String payloadString) {
        LogUtil.LogE("parseFCMNotifyIntent","----1");
        try {
            JSONObject payload=new JSONObject(payloadString);
            String sessionId = null;
            if (payload.has(LockUMixPushMessageHandler.PAYLOAD_SESSION_ID)){
                sessionId = payload.getString(LockUMixPushMessageHandler.PAYLOAD_SESSION_ID);
            }

            String type = null ;
            if (payload.has(LockUMixPushMessageHandler.PAYLOAD_SESSION_TYPE)){
                type = payload.getString(LockUMixPushMessageHandler.PAYLOAD_SESSION_TYPE);
            }

            String jumpTo = null ;
            if (payload.has(LockUMixPushMessageHandler.PAYLOAD_JUMP_TO)){
                jumpTo = payload.getString(LockUMixPushMessageHandler.PAYLOAD_JUMP_TO);
            }

            if (sessionId != null && type != null && jumpTo != null) {
                if (LockUMixPushMessageHandler.JUMPCHAT.equals(jumpTo)){
                    int typeValue = Integer.valueOf(type);
                    IMMessage message = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
                    showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, message));
                }else {
                    showMainActivity();
                }

            } else {
                showMainActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showMainActivity();
        }
    }
    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        if (TextUtils.isEmpty(MyApplication.application.type)) {
            MainActivity.StartMe(this,intent);
            finish();
        } else if ("2".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlocktwo.module.ui.activity.MainActivity.StartMe(this);
            finish();
        } else if ("3".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockthree.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("4".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockfour.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("5".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockfive.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("6".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlocksix.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("7".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockseven.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("8".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockeight.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("9".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlocknine.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }else if ("10".equals(MyApplication.application.type)) {
            com.lockulockme.locku.zlockten.module.ui.activity.MainActivity.StartMe(this);
            finish();
        }

    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
        }
    };
}
