package com.lockulockme.locku.module.ui.adapter;

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
        notifyDataSetChanged();
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
        binding.tvVipMonthNum.setText(getContext().getString(R.string.month_vip, productResp.num));
        //0不显示价格
        if (0 == productResp.showPrice) {
            binding.llPrice.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(productResp.rebate)) {
                binding.tvNoPrice.setVisibility(View.VISIBLE);
                binding.tvNoPrice.setText(getContext().getString(R.string.product_off, productResp.rebate));
            } else {
                binding.tvNoPrice.setVisibility(View.GONE);
            }
        } else if (productResp.showPrice == null || productResp.showPrice == 1) {
            binding.llPrice.setVisibility(View.VISIBLE);
            binding.tvNoPrice.setVisibility(View.GONE);
            binding.tvVipCurrency.setText(productResp.currency);
            binding.tvVipPrice.setText(productResp.goodsPrice);
            if (!TextUtils.isEmpty(productResp.rebate)) {
                binding.tvVipOffText.setText(getContext().getString(R.string.product_off, productResp.rebate));
                binding.tvVipOffText.setVisibility(View.VISIBLE);
            } else {
                binding.tvVipOffText.setVisibility(View.GONE);
            }
        }

    }
}
