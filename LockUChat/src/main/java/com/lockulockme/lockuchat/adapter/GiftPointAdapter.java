package com.lockulockme.lockuchat.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.rsp.GiftBeanRsp;
import com.lockulockme.lockuchat.databinding.ItemGiftPointBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GiftPointAdapter extends BaseQuickAdapter<List<GiftBeanRsp>, BaseViewHolder> {
    private int pos;

    public GiftPointAdapter() {
        super(R.layout.item_gift_point);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, List<GiftBeanRsp> giftBeans) {
        ItemGiftPointBinding binding = ItemGiftPointBinding.bind(baseViewHolder.itemView);
        binding.ivSelect.setVisibility(baseViewHolder.getAdapterPosition()==pos? View.VISIBLE:View.GONE);
        binding.ivNoselect.setVisibility(baseViewHolder.getAdapterPosition()==pos? View.GONE:View.VISIBLE);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
