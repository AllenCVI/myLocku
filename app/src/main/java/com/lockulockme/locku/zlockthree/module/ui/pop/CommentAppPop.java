package com.lockulockme.locku.zlockthree.module.ui.pop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.PopRateBinding;
import com.lockulockme.locku.databinding.PopRateReviewBinding;
import com.lockulockme.locku.zlockthree.base.utils.SharedPreferencesUtil;
import com.lockulockme.locku.zlockthree.common.PopupWindowBuilder;

public class CommentAppPop implements PopupWindowBuilder.OnBackListener {
    private Activity activity;
    private boolean isFocusable=true;
    private PopupWindowBuilder.OnBackListener onBackListener;

    public void setOnBackListener(PopupWindowBuilder.OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public CommentAppPop(Activity activity) {
        this.activity = activity;
    }

    public int rate = 0;

    public void goRateUs() {
        rate = 0;
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(activity)
                .setContentView(R.layout.pop_rate)
                .setFocusable(isFocusable)
                .setOutsideTouchable(false)
                .build();
        popBuilder.setOnBackListener(this);
        final PopRateBinding popRateBing = PopRateBinding.bind(popBuilder.getRootView());
        popBuilder.show(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        popRateBing.lltSubmit.setVisibility(View.GONE);
        popRateBing.tvNotNow.setVisibility(View.VISIBLE);
        popRateBing.ivFirstStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = 1;
                popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.tvNotNow.setVisibility(View.GONE);
                popRateBing.lltGoReview.setVisibility(View.VISIBLE);
                popRateBing.lltSubmit.setVisibility(View.GONE);
            }
        });

        popRateBing.ivSecondStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = 2;
                popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.tvNotNow.setVisibility(View.GONE);
                popRateBing.lltGoReview.setVisibility(View.VISIBLE);
                popRateBing.lltSubmit.setVisibility(View.GONE);
            }
        });

        popRateBing.ivThirdStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = 3;
                popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.tvNotNow.setVisibility(View.GONE);
                popRateBing.lltGoReview.setVisibility(View.VISIBLE);
                popRateBing.lltSubmit.setVisibility(View.GONE);
            }
        });
        popRateBing.ivForthStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = 4;
                popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivForthStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
                popRateBing.lltSubmit.setVisibility(View.VISIBLE);
                popRateBing.tvNotNow.setVisibility(View.GONE);
                popRateBing.lltGoReview.setVisibility(View.GONE);
            }
        });
        popRateBing.ivFifthStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = 5;
                popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivForthStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_red_star);
                popRateBing.lltSubmit.setVisibility(View.VISIBLE);
                popRateBing.tvNotNow.setVisibility(View.GONE);
                popRateBing.lltGoReview.setVisibility(View.GONE);
            }
        });
        popRateBing.tvRateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackListener != null) onBackListener.onBack();
                popBuilder.dismiss();
            }
        });
        popRateBing.tvRateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                    intent.setPackage("com.android.vending");
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intent);
                    } else {
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
                        if (intent2.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intent2);
                        }
                    }
                    SharedPreferencesUtil.put(activity, "rate", true);
                    popBuilder.dismiss();
                    if (onBackListener != null) onBackListener.onBack();
                } catch (Exception e) {
                }
            }
        });
        popRateBing.lltGoReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goReview();
                popBuilder.dismiss();
            }
        });
        popRateBing.tvNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackListener != null) onBackListener.onBack();
                popBuilder.dismiss();
            }
        });
        popRateBing.tvRateOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackListener != null) onBackListener.onBack();
                popBuilder.dismiss();
            }
        });
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;
    }

    public void goReview() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(activity)
                .setContentView(R.layout.pop_rate_review)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        popBuilder.setOnBackListener(this);
        final PopRateReviewBinding popRateBing = PopRateReviewBinding.bind(popBuilder.getRootView());
        popBuilder.show(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        if (onBackListener!=null)
        popBuilder.setOnDismissListener(onDismissListener);

        if (rate == 1) {
            popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
        } else if (rate == 2) {
            popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
        } else if (rate == 3) {
            popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivForthStar.setImageResource(R.mipmap.ic_black_star);
            popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
        } else if (rate == 4) {
            popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivForthStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_black_star);
        } else {
            popRateBing.ivFirstStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivSecondStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivThirdStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivForthStar.setImageResource(R.mipmap.ic_red_star);
            popRateBing.ivFifthStar.setImageResource(R.mipmap.ic_red_star);
        }
        popRateBing.tvRateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.etRateReview.requestFocus();
        popRateBing.tvRateSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(popRateBing.etRateReview.getText().toString().trim())) {
                    Toast.makeText(activity, R.string.please_write_review, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, R.string.review_sent, Toast.LENGTH_SHORT).show();
                    popBuilder.dismiss();
                }

            }
        });
    }

    PopupWindow.OnDismissListener onDismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (onBackListener != null) onBackListener.onBack();
        }
    };


    @Override
    public void onBack() {
        if (onBackListener != null) onBackListener.onBack();
    }
}
