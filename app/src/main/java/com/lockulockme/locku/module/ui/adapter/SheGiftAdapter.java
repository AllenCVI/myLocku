package com.lockulockme.locku.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.SheGiftBean;
import com.lockulockme.locku.databinding.ItemSheGiftBinding;
import com.lockulockme.locku.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class SheGiftAdapter extends BaseQuickAdapter<SheGiftBean, BaseViewHolder> {
    public SheGiftAdapter() {
        super(R.layout.item_she_gift);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SheGiftBean sheGiftBean) {
        ItemSheGiftBinding binding = ItemSheGiftBinding.bind(baseViewHolder.itemView);
        GlideUtils.loadImage(getContext(), sheGiftBean.giftIcon, binding.ivGift);
        binding.tvName.setText(sheGiftBean.giftName);
        binding.tvNumber.setText(sheGiftBean.giftNumber + "");
        binding.tvPrice.setText(sheGiftBean.giftPrice + "");

    }
}
