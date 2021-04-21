package com.lockulockme.lockuchat.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.databinding.PopConfirmBinding;

public class ConfirmPop extends PopupWindow {
    Activity mContext;
    private PopConfirmBinding binding;
    private OnBtnListener onBtnListener;

    public ConfirmPop(Activity context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = PopConfirmBinding.inflate(inflater);
        setContentView(binding.getRoot());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.confirm_style);
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 判断是不是点击了外部
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                //不是点击外部
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f);
            }
        });
        initView();
    }

    public ConfirmPop setTitle(String title){
        binding.tvTitle.setText(title);
        return this;
    }

    public ConfirmPop setOnBtnListener(OnBtnListener onBtnListener) {
        this.onBtnListener = onBtnListener;
        return this;
    }

    private void initView() {
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onBtnListener!=null) onBtnListener.onCancelClick(v);
            }
        });
        binding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onBtnListener!=null) onBtnListener.onConfirmClick(v);
            }
        });
    }

    private void darkenBackground(Float bgcolor){
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgcolor;
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mContext.getWindow().setAttributes(lp);
    }


    public void show(View parent) {
        darkenBackground(0.5f);
        showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    public interface OnBtnListener{
        void onConfirmClick(View v);
        void onCancelClick(View v);
    }
}
