package com.lockulockme.locku.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.SheTagResponseBean;
import com.lockulockme.locku.databinding.ItemSheTagBinding;

import org.jetbrains.annotations.NotNull;

public class SheTagAdapter extends BaseQuickAdapter<SheTagResponseBean.TagsBean, BaseViewHolder> {
    public SheTagAdapter() {
        super(R.layout.item_she_tag);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SheTagResponseBean.TagsBean item) {
        ItemSheTagBinding binding = ItemSheTagBinding.bind(baseViewHolder.itemView);
        binding.tvName.setText(item.name);
        binding.tvNum.setText(String.valueOf(item.num));
    }
}
