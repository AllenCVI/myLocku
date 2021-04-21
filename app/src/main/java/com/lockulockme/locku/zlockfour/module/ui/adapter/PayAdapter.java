package com.lockulockme.locku.zlockfour.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.databinding.ItemPayBinding;
import com.lockulockme.locku.zlockfour.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PayAdapter extends BaseQuickAdapter<PayCenterResponseBean, BaseViewHolder> {
    private int selectedPosition = -1;

    public PayAdapter(@Nullable List<PayCenterResponseBean> data) {
        super(R.layout.item_pay, data);
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, PayCenterResponseBean payCenterResp) {
        ItemPayBinding binding = ItemPayBinding.bind(holder.itemView);
        if (selectedPosition == getItemPosition(payCenterResp)) {
            binding.viewSelected.setBackgroundResource(R.drawable.bg_pay_selected);
        } else {
            binding.viewSelected.setBackgroundResource(R.drawable.bg_pay_default);
        }
        GlideUtils.loadImage(getContext(), payCenterResp.icon, binding.ivPayIcon, R.mipmap.pay_placeholder);
    }
}
