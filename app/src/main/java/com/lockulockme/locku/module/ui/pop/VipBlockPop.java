package com.lockulockme.locku.module.ui.pop;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lockulockme.locku.R;


public class VipBlockPop {

    private final Activity activity;
    private TextView tvLabel, tvBtn;
    private ImageView ivBack;
    private PopupWindow popupWindow;

    public VipBlockPop(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        View view = View.inflate(activity, R.layout.pop_vip_intercept, null);
        tvLabel = view.findViewById(R.id.tv_label);
        ivBack = view.findViewById(R.id.iv_back);
        tvBtn = view.findViewById(R.id.tv_btn);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        tvBtn.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
        ivBack.setOnClickListener(v -> {
            popupWindow.dismiss();
            activity.finish();
        });
    }

    public void setLabel(String label) {
        if (tvLabel != null) {
            tvLabel.setText(Html.fromHtml(label));
        }
    }


    public void showDialog() {
        if (popupWindow == null || popupWindow.isShowing() || activity == null || activity.isDestroyed()) {
            return;
        }
        popupWindow.update();
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}
