package com.lockulockme.locku.zlockfour.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;

import com.lockulockme.locku.R;

public class PopupWindowBuilder {
    PopupWindow mPopupWindow;
    private Context mContext;
    @LayoutRes
    private int layoutRes;
    private boolean isOutsideTouchable;
    private boolean isFocusable;
    private boolean isFullStatus = true;
    private View rootView;
    private OnBackListener onBackListener;
    @ColorInt
    private int bgColor;

    public PopupWindowBuilder(Context context) {
        this.mContext = context;
        bgColor = context.getResources().getColor(R.color.bg_pop_color);
    }

    public static PopupWindowBuilder newBuilder(Context context) {
        return new PopupWindowBuilder(context);
    }

    public PopupWindowBuilder setContentView(@LayoutRes int resource) {
        layoutRes = resource;
        return this;
    }

    public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
        isOutsideTouchable = outsideTouchable;
        return this;
    }

    public PopupWindowBuilder setFocusable(boolean focusable) {
        isFocusable = focusable;
        return this;
    }

    public PopupWindowBuilder setBgColor(@ColorInt int color) {
        bgColor = color;
        return this;
    }

    public PopupWindowBuilder setFullStatus(boolean fullStatus) {
        isFullStatus = fullStatus;
        return this;
    }

    public PopupWindowBuilder build() {
        rootView = View.inflate(mContext, layoutRes, null);
        mPopupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(bgColor));
        mPopupWindow.setClippingEnabled(!isFullStatus);
        mPopupWindow.setOutsideTouchable(isOutsideTouchable);
        mPopupWindow.setFocusable(isFocusable);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (onBackListener != null) onBackListener.onBack();
                    dismiss();
                    return false;
                }
                return false;

            }
        });

        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view != rootView) {
                    return true;
                }
                if (isOutsideTouchable)
                    dismiss();
                return false;
            }
        });
        return this;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener){
        mPopupWindow.setOnDismissListener(onDismissListener);
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getRootView() {
        return rootView;
    }

    public void show(View parent, int gravity, int x, int y) {
        mPopupWindow.showAtLocation(parent, gravity, x, y);
    }

    public interface OnBackListener {
        void onBack();
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }
}
