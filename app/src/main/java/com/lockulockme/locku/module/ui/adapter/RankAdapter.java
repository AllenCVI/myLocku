package com.lockulockme.locku.module.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.RankItemResponseBean;
import com.lockulockme.locku.module.ui.fragment.ChartsChildFragment;
import com.lockulockme.locku.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RankAdapter extends BaseQuickAdapter<RankItemResponseBean, BaseViewHolder> {

    private int type;

    public RankAdapter(@Nullable List<RankItemResponseBean> data, int type) {
        super(R.layout.item_charts, data);
        this.type = type;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, RankItemResponseBean item) {
        holder.setText(R.id.tv_number, (holder.getAdapterPosition() + 4) + "");
        holder.setText(R.id.tv_nickname, item.userName);
        holder.setText(R.id.tv_score, type == ChartsChildFragment.MALE_TYPE ? "+" + item.consumePrice : "+" + item.scores);
        holder.setText(R.id.tv_country, item.age + "   " + item.country);
        GlideUtils.loadCircleImg(getContext(), item.smallIcon, holder.getView(R.id.iv_headimg), R.mipmap.img_charts_head_small);
        GlideUtils.loadCircleImg(getContext(), item.countryUrl, holder.getView(R.id.iv_country), R.mipmap.country_placeholder);
        if (item.myLevelData != null && item.myLevelData.myLevel > 0) {
            holder.getView(R.id.iv_level).setVisibility(View.VISIBLE);
            GlideUtils.loadCircleImg(getContext(), item.myLevelData.LevelIcon, holder.getView(R.id.iv_level), R.mipmap.level_icon_place_holder);
        } else {
            holder.getView(R.id.iv_level).setVisibility(View.GONE);
        }
    }
}
