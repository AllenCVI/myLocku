package com.lockulockme.lockuchat.aavg2.ui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.data.SPHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.ui.BaseActivity;
import com.lzf.easyfloat.interfaces.OnPermissionResult;


public class NERTCSmallScreenActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, NERTCSmallScreenActivity.class);
        context.startActivity(intent);
    }

    boolean isShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isShow) {
            isShow = true;
            com.lzf.easyfloat.permission.PermissionUtils.requestPermission(this, new OnPermissionResult() {
                @Override
                public void permissionResult(boolean b) {
                    if (b) {
                        CallServiceInstance.getInstance().setSmallScreenQuested(true);
                    }
                    finish();
                }
            });
        }
    }
}
