package com.lockulockme.locku.module.ui.adapter;

import android.text.TextPaint;

import androidx.viewbinding.ViewBinding;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.LevelConfig;
import com.lockulockme.locku.databinding.ItemRvLevelBinding;

import org.jetbrains.annotations.NotNull;

public class LevelAdapter extends BaseQuickAdapter<LevelConfig, BaseViewHolder> {

    public LevelAdapter() {
        super(R.layout.item_rv_level);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LevelConfig levelConfig) {
        ItemRvLevelBinding binding = ItemRvLevelBinding.bind(baseViewHolder.itemView);
        int layoutPosition = getItemPosition(levelConfig);


        if (layoutPosition == 0){
            binding.frameLayout.setBackgroundResource(R.drawable.shape_item_rv_bg_level_top);
        }else if (layoutPosition == getData().size()-1){
            binding.frameLayout.setBackgroundResource(R.drawable.shape_item_rv_bg_level_bottom);
        }else {
            if (layoutPosition%2==0){
                binding.frameLayout.setBackgroundResource(R.color.color_1d1a4d);
            }else {
                binding.frameLayout.setBackgroundResource(R.color.color_07FFFFFF);
            }
        }

        if (layoutPosition==0){
            binding.tvLevel.setTextSize(14);
            binding.tvIntegral.setTextSize(14);
            binding.tvIntegral.setText(R.string.DiamondConsumption);
            binding.tvLevel.setText(R.string.Level);
        }else {
            binding.tvLevel.setTextSize(12);
            binding.tvIntegral.setTextSize(12);
            binding.tvLevel.setText(getContext().getResources().getString(R.string.Lv)+levelConfig.mLevelNum);
            binding.tvIntegral.setText(levelConfig.mLevelIntegral+"");

        }

    }
}
