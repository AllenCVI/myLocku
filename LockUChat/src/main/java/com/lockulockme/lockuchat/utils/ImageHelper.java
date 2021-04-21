package com.lockulockme.lockuchat.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ImageHelper {
    private ImageHelper(){
    }
    private static class InstanceHelper{
        private static ImageHelper INSTANCE = new ImageHelper();
    }
    private static ImageHelper getInstance(){
        return ImageHelper.InstanceHelper.INSTANCE;
    }

//    private void intoIV4Instance(ImageView imageView,String url,int placeholder, int error){
//        Glide.with(imageView).load(url).into(imageView);
//    }
//
//    public static void intoIV(ImageView imageView,String url,int placeholder, int error){
//        getInstance().intoIV4Instance(imageView,url,placeholder,error);
//    }
    private void intoIVCircle4Instance(ImageView imageView,String url,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new CenterCrop(), new CircleCrop());
        RequestBuilder requestBuilder=Glide.with(imageView).load(url).apply(mRequestOptions);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformCircle(imageView,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformCircle(imageView,error));
        }
        requestBuilder.into(imageView);
    }
    private void intoIVCircle4Instance(FragmentActivity activity,ImageView imageView,String url,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new CenterCrop(), new CircleCrop());
        RequestBuilder requestBuilder=Glide.with(activity).load(url).apply(mRequestOptions);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformCircle(imageView,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformCircle(imageView,error));
        }
        requestBuilder.into(imageView);
    }
    private static RequestBuilder<Drawable> transformCircle(ImageView imageView, @DrawableRes int placeholderId) {
        return Glide.with(imageView)
                .load(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

                .apply(new RequestOptions().transform(new CenterCrop(), new CircleCrop()));

    }
    public static void intoIV4Circle(ImageView imageView,String url,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVCircle4Instance(imageView,url,placeholder,error);
    }

    public static void intoIV4Circle(FragmentActivity activity, ImageView imageView, String url, @DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVCircle4Instance(activity,imageView,url,placeholder,error);
    }

    private void intoIVRound4Instance(ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
        RequestBuilder requestBuilder=Glide.with(imageView).load(url).apply(mRequestOptions);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformRound(imageView,radius,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformRound(imageView,radius,error));
        }
        requestBuilder.into(imageView);
    }

    private void intoIVRound4Instance(FragmentActivity activity,ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
        RequestBuilder requestBuilder=Glide.with(activity).load(url).apply(mRequestOptions);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformRound(imageView,radius,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformRound(imageView,radius,error));
        }
        requestBuilder.into(imageView);
    }
    private static RequestBuilder<Drawable> transformRound(ImageView imageView,int radius, @DrawableRes int placeholderId) {
        return Glide.with(imageView)
                .load(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius)));

    }
    public static void intoIV4Round(ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVRound4Instance(imageView,url,radius,placeholder,error);
    }

    public static void intoIV4Round(FragmentActivity activity,ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVRound4Instance(activity,imageView,url,radius,placeholder,error);
    }



    private void intoIV4Instance(ImageView imageView,String url,@DrawableRes int placeholder, @DrawableRes int error){
        RequestBuilder requestBuilder=Glide.with(imageView).load(url);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (placeholder!=-1){
            requestBuilder.placeholder(placeholder);
        }
        if (error!=-1){
            requestBuilder.error(error);
        }
        requestBuilder.into(imageView);
    }
    public static void intoIV(ImageView imageView,String url,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIV4Instance(imageView,url,placeholder,error);
    }

    private void intoIVBlur4Instance(ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new BlurTransformation(), new CenterCrop(),new RoundedCorners(radius));
        RequestBuilder requestBuilder=Glide.with(imageView).load(url);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        requestBuilder
                .apply(mRequestOptions);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformBlur(imageView,radius,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformBlur(imageView,radius,error));
        }
        requestBuilder.into(imageView);
    }
    private static RequestBuilder<Drawable> transformBlur(ImageView imageView,int radius, @DrawableRes int placeholderId) {
        RequestOptions mRequestOptions = new RequestOptions().transform(new BlurTransformation(), new CenterCrop(),new RoundedCorners(radius));
        return Glide.with(imageView)
                .load(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(mRequestOptions);

    }

    private void intoIVBlur4Instance(FragmentActivity activity,ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        RequestOptions mRequestOptions = new RequestOptions().transform(new BlurTransformation(), new CenterCrop(),new RoundedCorners(radius));
        RequestBuilder requestBuilder=Glide.with(activity).load(url);
        requestBuilder.diskCacheStrategy(DiskCacheStrategy.DATA);
        requestBuilder
                .apply(mRequestOptions);
        if (placeholder!=-1){
            requestBuilder.thumbnail(transformBlur(imageView,radius,placeholder));
        }
        if (error!=-1){
            requestBuilder.thumbnail(transformBlur(imageView,radius,error));
        }
        requestBuilder.into(imageView);
    }
    public static void intoIV4Blur(ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVBlur4Instance(imageView,url,radius,placeholder,error);
    }

    public static void intoIV4Blur(FragmentActivity activity,ImageView imageView,String url,int radius,@DrawableRes int placeholder, @DrawableRes int error){
        getInstance().intoIVBlur4Instance(activity,imageView,url,radius,placeholder,error);
    }
}
