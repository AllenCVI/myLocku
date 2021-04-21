package com.lockulockme.lockuchat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.lockulockme.lockuchat.R;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

public class PictureSelectorGlide implements ImageEngine {

    private static class InstanceHelper{
        private static PictureSelectorGlide INSTANCE = new PictureSelectorGlide();
    }
    public static PictureSelectorGlide getInstance(){
        return PictureSelectorGlide.InstanceHelper.INSTANCE;
    }

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(imageView);
        }catch (Exception e){

        }

    }

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url,
                          @NonNull final ImageView imageView,
                          final SubsamplingScaleImageView longImageView, final OnImageCompleteCallback callback) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(url)
                    .into(new ImageViewTarget<Bitmap>(imageView) {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            try {
                                if (callback != null) {
                                    callback.onShowLoading();
                                }
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            try {
                                if (callback != null) {
                                    callback.onHideLoading();
                                }
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        protected void setResource(@Nullable Bitmap resource) {
                            try {
                                if (callback != null) {
                                    callback.onHideLoading();
                                }
                                if (resource != null) {
                                    boolean eqLongImage = MediaUtils.isLongImg(resource.getWidth(),
                                            resource.getHeight());
                                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                                    imageView.setVisibility(eqLongImage ? View.GONE : View.VISIBLE);
                                    if (eqLongImage) {
                                        // 加载长图
                                        longImageView.setQuickScaleEnabled(true);
                                        longImageView.setZoomEnabled(true);
                                        longImageView.setPanEnabled(true);
                                        longImageView.setDoubleTapZoomDuration(100);
                                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                        longImageView.setImage(ImageSource.bitmap(resource),
                                                new ImageViewState(0, new PointF(0, 0), 0));
                                    } else {
                                        // 普通图片
                                        imageView.setImageBitmap(resource);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
        }catch (Exception e){

        }

    }

    @Override
    public void loadImage(@NonNull Context context, @NonNull String url,
                          @NonNull final ImageView imageView,
                          final SubsamplingScaleImageView longImageView) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(new ImageViewTarget<Bitmap>(imageView) {
                        @Override
                        protected void setResource(@Nullable Bitmap resource) {
                            try {
                                if (resource != null) {
                                    boolean eqLongImage = MediaUtils.isLongImg(resource.getWidth(),
                                            resource.getHeight());
                                    longImageView.setVisibility(eqLongImage ? View.VISIBLE : View.GONE);
                                    imageView.setVisibility(eqLongImage ? View.GONE : View.VISIBLE);
                                    if (eqLongImage) {
                                        // 加载长图
                                        longImageView.setQuickScaleEnabled(true);
                                        longImageView.setZoomEnabled(true);
                                        longImageView.setPanEnabled(true);
                                        longImageView.setDoubleTapZoomDuration(100);
                                        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                        longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                        longImageView.setImage(ImageSource.bitmap(resource),
                                                new ImageViewState(0, new PointF(0, 0), 0));
                                    } else {
                                        // 普通图片
                                        imageView.setImageBitmap(resource);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
        }catch (Exception e){

        }


    }

    @Override
    public void loadFolderImage(@NonNull final Context context, @NonNull String url, @NonNull final ImageView imageView) {
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .override(180, 180)
                    .centerCrop()
                    .sizeMultiplier(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(new RequestOptions().placeholder(R.drawable.picture_image_placeholder))
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            try {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.
                                                create(context.getResources(), resource);
                                circularBitmapDrawable.setCornerRadius(8);
                                imageView.setImageDrawable(circularBitmapDrawable);
                            } catch (Exception e) {
                            }
                        }
                    });
        }catch (Exception e){

        }


    }


    @Override
    public void loadAsGifImage(@NonNull Context context, @NonNull String url,
                               @NonNull ImageView imageView) {
        try {
            Glide.with(context)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(url)
                    .into(imageView);
        }catch (Exception e){

        }


    }

    @Override
    public void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .override(200, 200)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(new RequestOptions().placeholder(R.drawable.picture_image_placeholder))
                    .into(imageView);
        }catch (Exception e){

        }


    }



}
