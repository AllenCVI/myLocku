package com.lockulockme.locku.zlockfour.module.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lockulockme.locku.zlockfour.base.utils.AccountManager;
import com.lockulockme.locku.zlockfour.common.BaseActivity;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallParams;
import com.lockulockme.lockuchat.common.LockUMixPushMessageHandler;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        if (!firstEnter) {
            onIntent(); // APP进程还在，Activity被重新调度起来
        }
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
        if (TextUtils.isEmpty(IMLoginHelper.getInstance().getAccount())) {
            LoginIndexActivity.StartMe(this);
            finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(CallParams.INVENT_NOTIFICATION_FLAG) && intent.getBooleanExtra(CallParams.INVENT_NOTIFICATION_FLAG, false)) {
                    parseG2Intent(intent);
                    return;
                }
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                } else if (NIMClient.getService(MixPushService.class).isFCMIntent(intent)) {
                    parseFCMNotifyIntent(NIMClient.getService(MixPushService.class).parseFCMPayload(intent));
                }
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
        String token = AccountManager.getInstance().getNimToken();

        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity();
        } else {
            showMainActivity();
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
        try {
            JSONObject payload=new JSONObject(payloadString);
            String sessionId = payload.getString(LockUMixPushMessageHandler.PAYLOAD_SESSION_ID);
            String type = payload.getString(LockUMixPushMessageHandler.PAYLOAD_SESSION_TYPE);
            if (sessionId != null && type != null) {
//                int typeValue = Integer.valueOf(type);
//                IMMessage message = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
                showMainActivity();
            } else {
                showMainActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showMainActivity() {
        MainActivity.StartMe(this);
        finish();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
        }
    };
}
