package com.lockulockme.locku.zlocktwo.module.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.MyAlbumResponseBean;
import com.lockulockme.locku.databinding.ItemMySpacePictureBinding;
import com.lockulockme.locku.zlocktwo.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class MySpacePicturesAdapter extends BaseQuickAdapter<MyAlbumResponseBean, BaseViewHolder> {

    public MySpacePicturesAdapter() {
        super(R.layout.item_my_space_picture);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MyAlbumResponseBean s) {
        ItemMySpacePictureBinding binding = ItemMySpacePictureBinding.bind(holder.itemView);
        if (s.isAddPhoto) {
            binding.lltAddPhoto.setVisibility(View.VISIBLE);
            binding.ivMySpacePhoto.setVisibility(View.GONE);
        } else {
            binding.lltAddPhoto.setVisibility(View.GONE);
            binding.ivMySpacePhoto.setVisibility(View.VISIBLE);
            GlideUtils.loadRoundImage(getContext(), s.tmbUrl, binding.ivMySpacePhoto, R.mipmap.ic_album_placeholder);
        }
    }
}
