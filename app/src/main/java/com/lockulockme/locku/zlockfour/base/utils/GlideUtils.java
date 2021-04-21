package com.lockulockme.locku.zlockfour.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lockulockme.locku.R;
import com.lockulockme.locku.zlockfour.module.custom.GlideCircleTransform;
import com.lockulockme.locku.zlockfour.module.custom.GlideRoundTransform;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * Created by zhangyy on 2017/4/25.
 */

public class GlideUtils {

    /**
     * 因为都是七牛云上的图片 所以返回的路径就是最终路径
     *
     * @param context
     * @param path
     * @param imageView
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadImage(Context context, String path, ImageView imageView) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            if (!((Activity) context).isDestroyed()) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.normal_placeholder);
                options.diskCacheStrategy(DiskCacheStrategy.DATA);
                Glide.with(context).load(path).apply(options).into(imageView);
            }
        }

    }

    /**
     * 加载不同的
     *
     * @param context
     * @param path
     * @param imageView
     * @param placeHolder
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadImage(Context context, String path, ImageView imageView, int placeHolder) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(placeHolder);
        } else {
            if (!((Activity) context).isDestroyed()) {
                RequestOptions options = new RequestOptions();
                options.placeholder(placeHolder);
                options.diskCacheStrategy(DiskCacheStrategy.DATA);
                Glide.with(context).load(path).apply(options).into(imageView);
            }
        }

    }

    public static void loadImage(Context context, int path, ImageView imageView) {
        if (path == 0) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            Glide.with(context).load(path).apply(options).into(imageView);
        }

    }

    public static void loadRoundImage(Context context, String path, ImageView imageView) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, 6)));
            Glide.with(context).load(path).apply(options).into(imageView);
        }

    }

    public static void loadRoundImage(Context context, String path, ImageView imageView, int placeholder) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(placeholder != 0 ? placeholder : R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, 6)));
            Glide.with(context).load(path).apply(options).into(imageView);
        }
    }

    public static void loadRoundImage(Context context, String path, int radius, ImageView imageView, int placeholder) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(placeholder != 0 ? placeholder : R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, radius)));
            Glide.with(context).load(path).apply(options).into(imageView);
        }
    }

    public static void loadRoundImage(Context context, String path, ImageView imageView, int placeholder, int radius) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(placeholder != 0 ? placeholder : R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, radius)));
            Glide.with(context).load(path).apply(options).into(imageView);
        }

    }

    public static void loadRoundImage(Context context, int path, ImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.normal_placeholder);
        options.diskCacheStrategy(DiskCacheStrategy.DATA);
        options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, 5)));
        Glide.with(context).load(path).apply(options).into(imageView);


    }

    public static void loadRoundBlurImage(Context context, String path, ImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.normal_placeholder);
        options.diskCacheStrategy(DiskCacheStrategy.DATA);
        options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, 5)), new BlurTransformation());
        Glide.with(context).load(path).apply(options).into(imageView);
    }

    public static void loadRoundBlurImage(Context context, String path, ImageView imageView, int placeholder) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeholder);
        options.diskCacheStrategy(DiskCacheStrategy.DATA);
        options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, 5)), new BlurTransformation());
        Glide.with(context).load(path).apply(options).into(imageView);
    }

    public static void loadRoundBlurImage(Context context, String path, ImageView imageView, int placeholder, int roundedCorners) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeholder);
        options.diskCacheStrategy(DiskCacheStrategy.DATA);
        options.transform(new GlideRoundTransform(context, UtilDpOrPx.dp2px(context, roundedCorners)), new BlurTransformation());
        Glide.with(context).load(path).apply(options).into(imageView);
    }

    public static void loadImage(Context context, String path, ImageView imageView, int width, int height) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.normal_placeholder);
            options.diskCacheStrategy(DiskCacheStrategy.DATA);
            Glide.with(context).load(path).apply(options).into(imageView);
        }

    }


//    //    加载圆形图片
//    public static void loadCircleImage(Context context, String path, ImageView imageView, String rgb, int width, int height) {
//        if (TextUtils.isEmpty(path)) {
//            imageView.setImageResource(R.drawable.ic_default);
//        } else {
//            if (context!=null){
//                Glide.with(context).load(path + "@" + height + "h_" + width + "w")
//                        .transform(new GlideCircleTransform(context))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
////                    .placeholder(R.drawable.ic_default)
//                        .error(R.drawable.ic_default)
//                        .into(imageView);
//            }
//        }
//    }
//    //    加载圆形图片
//    public static void loadCircleImage(Context context,int resId, ImageView imageView, String rgb, int width, int height) {
//        if (resId!=0) {
//            imageView.setImageResource(R.drawable.ic_default);
//        } else {
//            if (context!=null){
//                Glide.with(context).load(resId + "@" + height + "h_" + width + "w")
//                        .transform(new GlideCircleTransform(context))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
////                    .placeholder(R.drawable.ic_default)
//                        .error(R.drawable.ic_default)
//                        .into(imageView);
//            }
//        }
//    }
//

    /**
     * 加载带边圆形图片
     *
     * @param context
     * @param path
     * @param imageView
     * @param borderWidth 边的宽度
     * @param borderColor 边的颜色
     */
    public static void loadBorderCircleImg(Context context, String path, ImageView imageView, int borderWidth, String borderColor) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            if (context != null) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.normal_placeholder);
                options.transform(new GlideCircleTransform(borderWidth, Color.parseColor(borderColor)));
                options.circleCrop();
                Glide.with(context).load(path)
                        .apply(options)
                        .into(imageView);
            }
        }
    }

    /**
     * 加载圆形图片
     */
    public static void loadCircleImg(Context context, String path, ImageView imageView) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(R.mipmap.normal_placeholder);
        } else {
            if (context != null) {
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.normal_placeholder);
                options.apply(RequestOptions.circleCropTransform());
                options.circleCrop();
                Glide.with(context).load(path)
                        .apply(options)
                        .into(imageView);
            }
        }
    }

    /**
     * 加载圆形图片
     */
    public static void loadCircleImg(Context context, String path, ImageView imageView, int resId) {
        if (TextUtils.isEmpty(path)) {
            imageView.setImageResource(resId);
        } else {
            if (context != null) {
                RequestOptions options = new RequestOptions();
                options.placeholder(resId);
                options.error(resId);
                options.apply(RequestOptions.circleCropTransform());
                options.circleCrop();
                Glide.with(context).load(path)
                        .apply(options)
                        .into(imageView);
            }
        }
    }

//
//    //    加载圆角图片
//    public static void loadRoundImage(Context context, String path, ImageView imageView, String rgb, int width, int height,int radius) {
//        if (TextUtils.isEmpty(path)){
//            imageView.setImageResource(R.drawable.ic_default);
//        }else {
//            Glide.with(context).load(path+ "@" + height + "h_" + width + "w")
//                    .transform(new CenterCrop(context),new GlideRoundTransform(context,radius))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .error(R.drawable.ic_default)
//                    .into(imageView);
//        }
//    }


    /**
     * 加载圆角带边图片
     * @param context
     * @param path
     * @param imageView
     * @param width
     * @param height
     * @param radius
     */
//    public static void loadRoundImage(Context context, String path, ImageView imageView,  int width, int height,int radius,int color,int line_wid) {
//        if (TextUtils.isEmpty(path)){
//            imageView.setImageResource(R.drawable.ic_default);
//        }else {
//            Glide.with(context).load(path+ "@" + height + "h_" + width + "w")
//                    .transform(new GlideRoundWithLineTransform(context,radius,color,line_wid))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .error(R.drawable.ic_default)
//                    .into(imageView);
//        }
//    }

}
