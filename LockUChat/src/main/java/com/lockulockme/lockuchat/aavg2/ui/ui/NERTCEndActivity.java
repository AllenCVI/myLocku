package com.lockulockme.lockuchat.aavg2.ui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.data.SPHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.ui.BaseActivity;


public class NERTCEndActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, NERTCEndActivity.class);
        context.startActivity(intent);
    }

    boolean isShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_nertc_end);
        int count = SPHelper.getInstance().getSPUtils().getInt("firstCall", 0);
        SPHelper.getInstance().getSPUtils().put("firstCall", count + 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isShow) {
            isShow = true;
            GoNeedUIUtils.getInstance().getGoNeedUI().showCommentAppDialog(this);
        }
    }
}
