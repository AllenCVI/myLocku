package com.lockulockme.lockuchat.adapter;


import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.databinding.ItemScanImageBinding;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class ScanImageAdapter extends BaseQuickAdapter<IMMessage, BaseViewHolder> {
    OnImageClickListener onImageClickListener;
    public ScanImageAdapter() {
        super(R.layout.item_scan_image);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemScanImageBinding binding=ItemScanImageBinding.bind(baseViewHolder.itemView);
        String path = ((ImageAttachment) imMessage.getAttachment()).getPath();
        if (path==null){
            path = ((ImageAttachment) imMessage.getAttachment()).getUrl();
        }
        ImageHelper.intoIV(binding.ivImage,path,-1,-1);
        binding.ivImage.setOnClickListener(v -> {
            if (onImageClickListener!=null) onImageClickListener.onImageClick();
        });
    }

    public interface OnImageClickListener{
        void onImageClick();
    }
}
