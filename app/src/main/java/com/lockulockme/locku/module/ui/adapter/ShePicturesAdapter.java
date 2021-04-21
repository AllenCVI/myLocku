package com.lockulockme.locku.module.ui.adapter;

import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.SheImageResponseBean;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.VipManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShePicturesAdapter extends BaseQuickAdapter<SheImageResponseBean, BaseViewHolder> {
    public ShePicturesAdapter(@Nullable List<SheImageResponseBean> data) {
        super(R.layout.item_she_detail_picture, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SheImageResponseBean s) {
        if (VipManager.getInstance().getVipStateFromCache() != null && VipManager.getInstance().getVipStateFromCache().isVip) {
            GlideUtils.loadRoundImage(getContext(), s.tbUrl, holder.getView(R.id.iv_image), R.mipmap.ic_album_placeholder);
        } else {
            LinearLayout llMask = holder.getView(R.id.ll_mask);
            if (holder.getAdapterPosition() > 0) {
                llMask.setVisibility(View.VISIBLE);
                GlideUtils.loadRoundBlurImage(getContext(), s.tbUrl, holder.getView(R.id.iv_image), R.mipmap.ic_album_placeholder);
            } else {
                llMask.setVisibility(View.GONE);
                GlideUtils.loadRoundImage(getContext(), s.tbUrl, holder.getView(R.id.iv_image), R.mipmap.ic_album_placeholder);
            }
        }
    }
}
