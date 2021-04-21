package com.lockulockme.lockuchat.adapter.chat;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyImageAttachment;
import com.lockulockme.lockuchat.databinding.ItemChatAudioVideoBinding;
import com.lockulockme.lockuchat.databinding.ItemChatPictureBinding;
import com.lockulockme.lockuchat.ui.ScanImageActivity;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class StrategyImageProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.STRATEGY_IMAGE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_picture;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage message) {
        ItemChatPictureBinding binding=ItemChatPictureBinding.bind(baseViewHolder.itemView);
        StrategyImageAttachment attachment= (StrategyImageAttachment) message.getAttachment();
        ViewGroup.LayoutParams layoutParams=binding.contentView.getLayoutParams();
        layoutParams.width=ScreenInfo.getInstance().dip2px(150);
        layoutParams.height=ScreenInfo.getInstance().dip2px(150);
        binding.contentView.setLayoutParams(layoutParams);
//        setImageViewWidthHeight(binding.contentView,message);
        binding.pbDowning.setVisibility(View.VISIBLE);
        binding.contentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.contentView.setImageResource(R.drawable.img_transparent);
        Glide.with(binding.contentView).asBitmap().load(attachment.getImage())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(ScreenInfo.getInstance().dip2px(22))))
                .override(layoutParams.width,layoutParams.height).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                binding.pbDowning.setVisibility(View.GONE);
                binding.contentView.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        binding.contentView.setOnClickListener(v -> {
            ScanImageActivity.startMe(getContext(),attachment.getImage());
        });
        binding.getRoot().setGravity(isLeftMsg(message)? Gravity.START :Gravity.END);
    }
}
