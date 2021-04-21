package com.lockulockme.lockuchat.adapter.chat;

import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.databinding.ItemChatStrategyAvBinding;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class StrategyAVMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.STRATEGY_AUDIO_VIDEO;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_strategy_av;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage message) {
        ItemChatStrategyAvBinding binding = ItemChatStrategyAvBinding.bind(baseViewHolder.itemView);
        StrategyAVMsgAttachment attachment = (StrategyAVMsgAttachment) message.getAttachment();
        if (isLeftMsg(message)) {
            binding.ivTypeRight.setVisibility(View.GONE);
            binding.ivTypeLeft.setVisibility(View.VISIBLE);
        } else {
            binding.ivTypeRight.setVisibility(View.VISIBLE);
            binding.ivTypeLeft.setVisibility(View.GONE);
        }
        binding.tvChatMsg.setText(ResouseUtils.getResouseString(R.string.click_to_call_back_now));
        if (attachment.getCallType() == StrategyAVMsgAttachment.AUDIO_CALL_TYPE){
            binding.tvTitle.setText(ResouseUtils.getResouseString(R.string.send_voice_invitation));
        }else {
            binding.tvTitle.setText(ResouseUtils.getResouseString(R.string.send_video_invitation));
        }
        binding.tvDeputy.setText(ResouseUtils.getResouseString(R.string.i_am_waiting_for_you));
        binding.getRoot().setGravity(isLeftMsg(message) ? Gravity.START : Gravity.END);
    }
}
