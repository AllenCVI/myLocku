package com.lockulockme.lockuchat.adapter.chat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.QAAnswer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QAAnswerAdapter extends BaseQuickAdapter<QAAnswer, BaseViewHolder> {
    public QAAnswerAdapter(@Nullable List<QAAnswer> data) {
        super(R.layout.item_chat_qa_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, QAAnswer qaAnswer) {
        baseViewHolder.setText(R.id.tv_answer,qaAnswer.answer);
    }
}
