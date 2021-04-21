package com.lockulockme.locku.zlockfour.module.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.databinding.ItemReportContentBinding;

import org.jetbrains.annotations.NotNull;

public class ReportAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private String checkStr = "";

    public ReportAdapter() {
        super(R.layout.item_report_content);
    }

    public void setCheckStr(String str) {
        this.checkStr = str;
        notifyDataSetChanged();
    }

    public String getCheckStr() {
        return checkStr;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        ItemReportContentBinding binding = ItemReportContentBinding.bind(baseViewHolder.itemView);
        binding.tvTitle.setText(s);
        if (s.equals(checkStr)) {
            binding.rlContent.setBackgroundResource(R.drawable.shape_cff4a52_c18);
            binding.ivCheck.setVisibility(View.VISIBLE);
        } else {
            binding.rlContent.setBackgroundResource(R.drawable.shape_c1aff4a52_c18);
            binding.ivCheck.setVisibility(View.GONE);
        }
    }
}
