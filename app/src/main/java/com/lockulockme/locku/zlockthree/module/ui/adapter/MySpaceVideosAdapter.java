package com.lockulockme.locku.zlockthree.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MySpaceVideosAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MySpaceVideosAdapter(@Nullable List<String> data) {
        super(R.layout.item_my_space_video, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, String s) {

    }
}
