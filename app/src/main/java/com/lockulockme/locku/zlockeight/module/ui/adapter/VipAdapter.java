package com.lockulockme.locku.zlockeight.module.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.databinding.ItemVipBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VipAdapter extends BaseQuickAdapter<ProductResponseBean, BaseViewHolder> {
    private int selectedPosition = -1;

    public VipAdapter(@Nullable List<ProductResponseBean> data) {
        super(R.layout.item_vip, data);
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ProductResponseBean productResp) {
        ItemVipBinding binding = ItemVipBinding.bind(holder.itemView);
        if (selectedPosition == getItemPosition(productResp)) {
            binding.lltItemVip.setBackgroundResource(R.drawable.bg_item_vip_select);
        } else {
            binding.lltItemVip.setBackgroundResource(R.drawable.bg_item_vip_default);
        }
        binding.tvVipCurrency.setText(productResp.currency);
        binding.tvVipMonthNum.setText(getContext().getString(R.string.month_vip, productResp.num));
        binding.tvVipPrice.setText(productResp.goodsPrice);
        binding.tvVipCurrency.setText(productResp.currency);
        binding.tvVipMonthNum.setText(getContext().getString(R.string.month_vip, productResp.num));
        binding.tvVipPrice.setText(productResp.goodsPrice);
        if (!TextUtils.isEmpty(productResp.rebate)) {
            binding.tvVipOffText.setText(getContext().getString(R.string.product_off, productResp.rebate));
            binding.tvVipOffText.setVisibility(View.VISIBLE);
        } else {
            binding.tvVipOffText.setVisibility(View.GONE);
        }
    }
}
