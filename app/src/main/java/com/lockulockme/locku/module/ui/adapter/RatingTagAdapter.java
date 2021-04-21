package com.lockulockme.locku.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.RateTagResponseBean;
import com.lockulockme.locku.databinding.ItemTagBinding;

import org.jetbrains.annotations.NotNull;

public class RatingTagAdapter extends BaseQuickAdapter<RateTagResponseBean, BaseViewHolder> {
    public RatingTagAdapter() {
        super(R.layout.item_tag);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, RateTagResponseBean s) {
        ItemTagBinding bind = ItemTagBinding.bind(baseViewHolder.itemView);
        if (!s.check) {
            bind.tvTitle.setBackgroundResource(R.drawable.shape_c4a4871_c15);
        } else {
            bind.tvTitle.setBackgroundResource(R.drawable.shape_cff4a52_c15);
        }
        bind.tvTitle.setText(s.name);
    }
}
