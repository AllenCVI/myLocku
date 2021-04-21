package com.lockulockme.lockuchat.adapter.chat;

import android.view.Gravity;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.databinding.ItemChatTextBinding;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class QAAnswerMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.QA_ANSWER;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_text;
    }
    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatTextBinding binding=ItemChatTextBinding.bind(baseViewHolder.itemView);
        QAAnswerMsgAttachment attachment= (QAAnswerMsgAttachment) imMessage.getAttachment();
        binding.tvMsg.setText(attachment.getAnswer());
        binding.getRoot().setGravity(isLeftMsg(imMessage)? Gravity.START :Gravity.END);
    }


}
