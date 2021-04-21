package com.lockulockme.locku.zlockfive.module.ui.adapter;


import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;

import org.jetbrains.annotations.NotNull;

public class ImagePreviewAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int selectPos = -1;

    public ImagePreviewAdapter() {
        super(R.layout.item_image_preview);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String item) {
        ImageView imageView = (ImageView) baseViewHolder.getView(R.id.iv_image);
        Glide.with(getContext()).asDrawable().load(item).into(new CustomTarget<Drawable>() {

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
                baseViewHolder.setGone(R.id.progress, true);
                baseViewHolder.setVisible(R.id.iv_image, true);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }

}