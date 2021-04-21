package com.lockulockme.locku.zlockeight.module.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.IndexUserResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.databinding.ItemHotBinding;
import com.lockulockme.locku.zlockeight.base.utils.GlideUtils;
import com.lockulockme.locku.zlockeight.base.utils.VipManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotAdapter extends BaseQuickAdapter<IndexUserResponseBean, BaseViewHolder> {
    public HotAdapter(@Nullable List<IndexUserResponseBean> data) {
        super(R.layout.item_hot, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, IndexUserResponseBean item) {
        ItemHotBinding binding = ItemHotBinding.bind(holder.itemView);

        holder.setText(R.id.tv_name, item.name);

        if (item.isOnline) {
            if (item.isBusy) {
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
        VipResponseBean vipResponseBean = VipManager.getInstance().getVipStateFromCache();
        if ("3".equals(item.userType)) {
            holder.setVisible(R.id.iv_hi, !item.hello);
            holder.setVisible(R.id.iv_msg, item.hello);
            binding.ivAudio.setVisibility(View.GONE);
            binding.ivVideo.setVisibility(View.GONE);
        } else {
            if (vipResponseBean == null) {
                binding.ivVideo.setVisibility(View.GONE);
                binding.ivHi.setVisibility(View.GONE);
                binding.ivMsg.setVisibility(View.GONE);
                binding.ivAudio.setVisibility(View.VISIBLE);
            } else {
                if (vipResponseBean.isVip && item.isOnline) {
                    binding.ivVideo.setVisibility(View.VISIBLE);
                    binding.ivHi.setVisibility(View.GONE);
                    binding.ivMsg.setVisibility(View.GONE);
                    binding.ivAudio.setVisibility(View.GONE);
                } else {
                    binding.ivVideo.setVisibility(View.GONE);
                    binding.ivHi.setVisibility(View.GONE);
                    binding.ivMsg.setVisibility(View.GONE);
                    binding.ivAudio.setVisibility(View.VISIBLE);
                }
            }
        }


        GlideUtils.loadRoundImage(getContext(), item.avatar, 10, holder.getView(R.id.iv_img), R.mipmap.bg_she_detail_head);
        GlideUtils.loadCircleImg(getContext(), item.countryUrl, holder.getView(R.id.iv_country), R.mipmap.country_placeholder);
    }
}
