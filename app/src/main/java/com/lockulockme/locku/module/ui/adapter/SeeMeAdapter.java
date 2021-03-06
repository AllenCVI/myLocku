package com.lockulockme.locku.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.LookMeResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.databinding.ItemSeemeBinding;
import com.lockulockme.locku.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeeMeAdapter extends BaseQuickAdapter<LookMeResponseBean, BaseViewHolder> {

    public SeeMeAdapter(@Nullable List<LookMeResponseBean> data) {
        super(R.layout.item_seeme, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, LookMeResponseBean s) {
        ItemSeemeBinding binding = ItemSeemeBinding.bind(holder.itemView);
        VipResponseBean vipResponseBean = VipManager.getInstance().getVipStateFromCache();
        if (vipResponseBean != null) {
            if (vipResponseBean.isVip) {
                GlideUtils.loadRoundImage(getContext(), s.smallAvatar, binding.ivSeeme, R.mipmap.ic_album_placeholder, 4);
            } else {
                if (getItemPosition(s) < 4) {
                    GlideUtils.loadRoundImage(getContext(), s.smallAvatar, binding.ivSeeme, R.mipmap.ic_album_placeholder, 4);
                } else {
                    GlideUtils.loadRoundBlurImage(getContext(), s.smallAvatar, binding.ivSeeme, R.mipmap.ic_album_placeholder,4);
                }
            }
        } else {
            if (getItemPosition(s) < 4) {
                GlideUtils.loadRoundImage(getContext(), s.smallAvatar, binding.ivSeeme, R.mipmap.ic_album_placeholder, 4);
            } else {
                GlideUtils.loadRoundBlurImage(getContext(), s.smallAvatar, binding.ivSeeme, R.mipmap.ic_album_placeholder,4);
            }
        }
    }
}
