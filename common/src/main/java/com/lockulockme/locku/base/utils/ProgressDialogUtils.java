package com.lockulockme.locku.base.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;

import com.lockulockme.locku.common.R;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

public class ProgressDialogUtils {

    private Context mContext;
    private ZLoadingDialog dialog;

    public ProgressDialogUtils(Context context) {
        this.mContext = context;
        dialog = new ZLoadingDialog(mContext);
    }

    public void showDialog() {
        LogUtil.LogD("LoginHelper", "mContext != null:" + (mContext != null) + "  !((Activity) mContext).isFinishing():" + !((Activity) mContext).isFinishing());
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            dialog.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#4dffffff"))//颜色
                    .setHintText(mContext.getString(R.string.str_loading))
                    .setHintTextSize(16) // 设置字体大小 dp
                    .setHintTextColor(Color.WHITE)  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)//点击空白不关闭
                    .setDialogBackgroundColor(Color.TRANSPARENT);// 设置背景色，默认白色
            Dialog mDialog = dialog.create();
            // 监听 Dialog 的 Key 事件
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        // 关闭 Dialog
                        dialog.dismiss();
                        // 关闭当前 Activity
                        ((Activity) mContext).onKeyDown(keyCode, event);
                        // 返回 true，表示返回事件已被处理，不再向下传递
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            dialog.show();


        }
    }

    public void showCancelDialog() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()&& !((Activity) mContext).isDestroyed()) {
            dialog.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#ffffff"))//颜色
                    .setHintText(mContext.getString(R.string.str_loading))
                    .setHintTextSize(16) // 设置字体大小 dp
                    .setHintTextColor(Color.WHITE)  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(true)//点击空白关闭
                    .setDialogBackgroundColor(Color.TRANSPARENT)// 设置背景色，默认白色
                    .show();

        }
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public ZLoadingDialog getDidlog() {
        return dialog;
    }


}
