package com.lockulockme.lockuchat.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.rsp.GiftBeanRsp;
import com.lockulockme.lockuchat.databinding.ItemGiftBinding;
import com.lockulockme.lockuchat.utils.ImageHelper;

import org.jetbrains.annotations.NotNull;

public class GiftAdapter extends BaseQuickAdapter<GiftBeanRsp, BaseViewHolder> {
    private int pos = -1;

    public GiftAdapter() {
        super(R.layout.item_gift);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, GiftBeanRsp giftBean) {
        ItemGiftBinding binding = ItemGiftBinding.bind(baseViewHolder.itemView);
        ImageHelper.intoIV(binding.ivGift, giftBean.image, -1, -1);
        binding.tvPrice.setText(String.valueOf(giftBean.price));
        binding.tvName.setText(giftBean.name);
        binding.getRoot().setSelected(pos == baseViewHolder.getAdapterPosition());
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
