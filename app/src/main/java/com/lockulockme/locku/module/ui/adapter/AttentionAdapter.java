package com.lockulockme.locku.module.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.databinding.ItemHotBinding;

import org.jetbrains.annotations.NotNull;

public class AttentionAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public AttentionAdapter() {
        super(R.layout.item_hot);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, UserInfo item) {
        ItemHotBinding binding = ItemHotBinding.bind(holder.itemView);

        holder.setText(R.id.tv_name, item.name);

        if (item.online) {
            if (item.busy) {
                binding.tvBusy.setVisibility(View.VISIBLE);
                binding.tvOnline.setVisibility(View.GONE);
            } else {
                binding.tvBusy.setVisibility(View.GONE);
                binding.tvOnline.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tvBusy.setVisibility(View.GONE);
            binding.tvOnline.setVisibility(View.GONE);
        }
//        VipResponseBean vipResponseBean = VipManager.getInstance().getVipStateFromCache();
//        if ("3".equals(item.userType)) {
//            holder.setVisible(R.id.iv_hi, !item.hello);
//            holder.setVisible(R.id.iv_msg, item.hello);
//            binding.ivAudio.setVisibility(View.GONE);
//            binding.ivVideo.setVisibility(View.GONE);
//        } else {
//            if (vipResponseBean == null) {
//                binding.ivVideo.setVisibility(View.GONE);
//                binding.ivHi.setVisibility(View.GONE);
//                binding.ivMsg.setVisibility(View.GONE);
//                binding.ivAudio.setVisibility(View.VISIBLE);
//            } else {
//                if (vipResponseBean.isVip && item.online) {
//                    binding.ivVideo.setVisibility(View.VISIBLE);
//                    binding.ivHi.setVisibility(View.GONE);
//                    binding.ivMsg.setVisibility(View.GONE);
//                    binding.ivAudio.setVisibility(View.GONE);
//                } else {
//                    binding.ivVideo.setVisibility(View.GONE);
//                    binding.ivHi.setVisibility(View.GONE);
//                    binding.ivMsg.setVisibility(View.GONE);
//                    binding.ivAudio.setVisibility(View.VISIBLE);
//                }
//            }
//        }
        if (item.myLevelData != null && item.myLevelData.myLevel > 0) {
            binding.ivLevel.setVisibility(View.VISIBLE);
            GlideUtils.loadCircleImg(getContext(), item.myLevelData.LevelIcon, binding.ivLevel, R.mipmap.level_icon_place_holder);
        } else {
            binding.ivLevel.setVisibility(View.GONE);
        }

        GlideUtils.loadRoundImage(getContext(), item.middleAvatar, 10, holder.getView(R.id.iv_img), R.mipmap.bg_she_detail_head);
        GlideUtils.loadCircleImg(getContext(), item.countryUr, holder.getView(R.id.iv_country), R.mipmap.country_placeholder);
    }
}
