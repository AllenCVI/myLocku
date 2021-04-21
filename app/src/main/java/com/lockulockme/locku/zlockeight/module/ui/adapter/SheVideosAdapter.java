package com.lockulockme.locku.zlockeight.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.SheVideoResponseBean;
import com.lockulockme.locku.zlockeight.base.utils.GlideUtils;
import com.lockulockme.locku.zlockeight.base.utils.VipManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SheVideosAdapter extends BaseQuickAdapter<SheVideoResponseBean, BaseViewHolder> {
    public SheVideosAdapter(@Nullable List<SheVideoResponseBean> data) {
        super(R.layout.item_she_detail_video, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SheVideoResponseBean s) {
        if (VipManager.getInstance().getVipStateFromCache() != null && VipManager.getInstance().getVipStateFromCache().isVip) {
            GlideUtils.loadRoundImage(getContext(), s.videoIcon, holder.getView(R.id.iv_image), R.mipmap.video_placeholder);
        } else {
            if (holder.getAdapterPosition() > 0) {
                GlideUtils.loadRoundBlurImage(getContext(), s.videoIcon, holder.getView(R.id.iv_image), R.mipmap.ic_album_placeholder);
            } else {
                GlideUtils.loadRoundImage(getContext(), s.videoIcon, holder.getView(R.id.iv_image), R.mipmap.ic_album_placeholder);
            }
        }
    }
}
