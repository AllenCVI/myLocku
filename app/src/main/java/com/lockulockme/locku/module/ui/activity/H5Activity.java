package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.base.eventbus.WebPaySucEvent;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcCodaBinding;
import com.lockulockme.lockuchat.common.VipStatusListener;

import org.greenrobot.eventbus.EventBus;

import static android.view.KeyEvent.KEYCODE_BACK;

public class H5Activity extends BaseActivity<AcCodaBinding> {

    private LinearLayout mLayout;
    private WebView mWebView;
    private boolean isPay;

    private String payType = "";//    1成功0失败
    private static final String PAY_SUC_TYPE = "1";
    private static final String PAY_FAIL_TYPE = "0";

    public static final void go(Context context, String url) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }


    public static final void go(Context context, String url, boolean isPay) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra("url", url);
        intent.putExtra("isPay", isPay);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcCodaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra("url");
        isPay = getIntent().getBooleanExtra("isPay", false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        binding.lltCodaRoot.addView(mWebView);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);

        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "nativeApi");

        saveData(mWebSettings);

        newWin(mWebSettings);

        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(url);
    }

    @JavascriptInterface
    public void setPayStatus(String status) {
        payType = status;
    }

    @JavascriptInterface
    public void backByJS() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }

    @JavascriptInterface
    public void backWithPayStatus(String status) {
        payType = status;

        if (PAY_SUC_TYPE.equals(payType)){
            EventBus.getDefault().post(new WebPaySucEvent());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    private void newWin(WebSettings mWebSettings) {
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private void saveData(WebSettings mWebSettings) {
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(false);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    WebViewClient webViewClient = new WebViewClient(){

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPay) {
            VipManager.getInstance().reset();
            EventBus.getDefault().post(new VipStatusRefreshEvent());
            VipStatusListener.getInstance().notifyVipStatusChange();
        }
        if (mWebView != null) {
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK){
            if (PAY_SUC_TYPE.equals(payType)){
                EventBus.getDefault().post(new WebPaySucEvent());
                finish();
                return true;
            }else if (PAY_FAIL_TYPE.equals(payType)){
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
