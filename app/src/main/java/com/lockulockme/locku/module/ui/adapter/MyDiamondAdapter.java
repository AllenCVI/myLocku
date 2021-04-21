package com.lockulockme.locku.module.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.databinding.ItemDiamondBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyDiamondAdapter extends BaseQuickAdapter<ProductResponseBean, BaseViewHolder> {
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public MyDiamondAdapter(@Nullable List<ProductResponseBean> data) {
        super(R.layout.item_diamond, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ProductResponseBean productResp) {
        ItemDiamondBinding binding = ItemDiamondBinding.bind(holder.itemView);
        if (selectedPosition == getItemPosition(productResp)) {
            binding.lltItemDiamond.setBackgroundResource(R.drawable.bg_diamond_select);
        } else {
            binding.lltItemDiamond.setBackgroundResource(R.drawable.bg_diamond_default);
        }
        binding.tvDiamondNum.setText(productResp.num + "");

        if (0 == productResp.showPrice) {
            binding.llPrice.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(productResp.rebate)) {
                binding.tvNoDiscount.setText(getContext().getString(R.string.product_off, productResp.rebate));
                binding.tvNoDiscount.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoDiscount.setVisibility(View.GONE);
            }
        } else if (productResp.showPrice == null || productResp.showPrice == 1) {
            binding.llPrice.setVisibility(View.VISIBLE);
            binding.tvNoDiscount.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(productResp.rebate)) {
                binding.tvDiamondOffText.setText(getContext().getString(R.string.product_off, productResp.rebate));
                binding.tvDiamondOffText.setVisibility(View.VISIBLE);
            } else {
                binding.tvDiamondOffText.setVisibility(View.INVISIBLE);
            }

            if (TextUtils.isEmpty(productResp.currency) || TextUtils.isEmpty(productResp.goodsPrice)) {
                binding.tvDiamondPrice.setVisibility(View.INVISIBLE);
            } else {
                binding.tvDiamondPrice.setText(productResp.currency + productResp.goodsPrice);
                binding.tvDiamondPrice.setVisibility(View.VISIBLE);
            }
        }


    }
}
