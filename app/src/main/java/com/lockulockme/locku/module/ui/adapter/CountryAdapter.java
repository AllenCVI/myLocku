package com.lockulockme.locku.module.ui.adapter;

import android.view.Gravity;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.CountryResponseBean;
import com.lockulockme.locku.base.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CountryAdapter extends BaseQuickAdapter<CountryResponseBean, BaseViewHolder> {


    private String checkStr = "";

    public String getCheckStr() {
        return checkStr;
    }

    public void setCheckStr(String checkStr) {
        this.checkStr = checkStr;
        notifyDataSetChanged();
    }

    public CountryAdapter(@Nullable List<CountryResponseBean> data) {
        super(R.layout.item_country, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, CountryResponseBean item) {
        LinearLayout linearLayout = holder.getView(R.id.ll_root);
        linearLayout.setBackgroundResource(item.valueStr.equals(checkStr) ? R.drawable.shape_cff4a52_c3 : R.drawable.shape_c1d1a4d_c3);
        if (item.type == CountryResponseBean.NORMAL_TYPE) {
            GlideUtils.loadCircleImg(getContext(), item.iconUrl, holder.getView(R.id.iv_country), R.mipmap.country_placeholder);
            holder.setGone(R.id.iv_country, false);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            holder.setGone(R.id.iv_country, true);
            linearLayout.setGravity(Gravity.CENTER);
        }
        holder.setText(R.id.tv_country, item.label);

    }
}
