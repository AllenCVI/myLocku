package com.lockulockme.locku.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.ItemRateStarBinding;

import org.jetbrains.annotations.NotNull;

public class RatingStarAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int selectItem = -1;

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }

    public RatingStarAdapter() {
        super(R.layout.item_rate_star);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        ItemRateStarBinding bind = ItemRateStarBinding.bind(baseViewHolder.itemView);
        bind.ivStar.setImageResource(selectItem >= baseViewHolder.getAdapterPosition() ? R.mipmap.rate_star_press : R.mipmap.rate_star_normal);
    }
}
