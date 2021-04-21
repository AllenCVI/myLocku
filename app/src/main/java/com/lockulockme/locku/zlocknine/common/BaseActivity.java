package com.lockulockme.locku.zlocknine.common;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.ProgressDialogUtils;
import com.lockulockme.locku.zlocknine.application.MyApplication;

import org.greenrobot.eventbus.EventBus;

import me.jessyan.autosize.AutoSizeCompat;


/**
 * Description: BaseActivity
 */
public abstract class BaseActivity<BD extends ViewBinding> extends FragmentActivity {
    private final String TAG = "BaseActivity";
    protected Context mContext;
    protected BD binding;
    private final String className = getClass().getSimpleName();
    private TextView tvNavTitle;
    private ImageView ivNavBack;
    private ProgressDialogUtils progressDialogUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.LogE(TAG, "------>onCreate");
        MyApplication.application.setCurActivity(this);
        mContext = this;
        init();
    }

    protected void init() {
        BaseApplication.getInstance().registerActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//不可以旋转屏幕
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//软键盘默认不弹出
        progressDialogUtils = new ProgressDialogUtils(mContext);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onSetNavView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onSetNavView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.LogE(TAG, "------>onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.LogE(TAG, "------>onResume");
        MyApplication.application.setCurActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.LogE(TAG, "------>onPause");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.LogE(TAG, "------>onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.LogE(TAG, "------>onStop");
//        MyApplication.application.setCurActivity(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onDestroy() {
        BaseApplication.getInstance().unregisterActivity(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public void onNavClick(View view) {
        switch (view.getId()) {
            case R.id.iv_nav_back:
                onNavBackClick();
                break;
        }
    }


    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        AutoSizeCompat.autoConvertDensityOfGlobal((res));//如果没有自定义需求用这个方法
        return res;
    }


    public void onNavBackClick() {
        finish();
    }

    public void onSetNavView() {
        tvNavTitle = findViewById(R.id.tv_nav_title);
        ivNavBack = findViewById(R.id.iv_nav_back);

        setNavTitle(getTitle());
    }

    public void setNavTitle(CharSequence title) {
        if (tvNavTitle != null)
            tvNavTitle.setText(title);
    }

    public void showLoading() {
        if (progressDialogUtils != null) {
            progressDialogUtils.showCancelDialog();
        }
    }

    public void hideLoading() {
        if (progressDialogUtils != null) {
            progressDialogUtils.dismissDialog();
        }
    }
}
