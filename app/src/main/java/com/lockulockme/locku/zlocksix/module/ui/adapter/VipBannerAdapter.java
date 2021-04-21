package com.lockulockme.locku.zlocksix.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.VipBannerImageResponseBean;
import com.lockulockme.locku.databinding.ItemVipBannerBinding;
import com.lockulockme.locku.zlocksix.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class VipBannerAdapter extends BaseQuickAdapter<VipBannerImageResponseBean, BaseViewHolder> {
    public VipBannerAdapter() {
        super(R.layout.item_vip_banner);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, VipBannerImageResponseBean s) {
        ItemVipBannerBinding binding = ItemVipBannerBinding.bind(holder.itemView);
        GlideUtils.loadImage(binding.ivVipBanner.getContext(), s.image, binding.ivVipBanner, R.mipmap.ic_vip_banner_placeholder);
    }
}
