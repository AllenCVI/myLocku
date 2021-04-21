package com.lockulockme.locku.module.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.databinding.ItemMineAttentionBinding;

import org.jetbrains.annotations.NotNull;

public class MineAttentionAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public MineAttentionAdapter() {
        super(R.layout.item_mine_attention);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, UserInfo userInfo) {
        ItemMineAttentionBinding binding = ItemMineAttentionBinding.bind(baseViewHolder.itemView);
        GlideUtils.loadCircleImg(getContext(), userInfo.avatar, binding.ivHead, R.mipmap.icon_placeholder_user);
        binding.tvName.setText(userInfo.name);
        binding.tvSign.setText(TextUtils.isEmpty(userInfo.signature) ? "" : userInfo.signature);
        if (userInfo.followed) {
            binding.tvFollow.setVisibility(View.GONE);
            binding.tvUnfollow.setVisibility(View.VISIBLE);
        } else if (!userInfo.followed) {
            binding.tvFollow.setVisibility(View.VISIBLE);
            binding.tvUnfollow.setVisibility(View.GONE);
        }
    }
}
