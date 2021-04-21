package com.lockulockme.lockuchat.adapter.chat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.common.Extra;
import com.lockulockme.lockuchat.databinding.ItemChatTextBinding;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TextMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.TEXT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_text;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatTextBinding binding = ItemChatTextBinding.bind(baseViewHolder.itemView);
        Map<String, Object> extraMap = imMessage.getRemoteExtension();
        if (extraMap != null) {
            String translateStr = (String) extraMap.get(Extra.TRANSFORM_KEY);
            if (!TextUtils.isEmpty(translateStr)) {
                binding.tvMsgTranslate.setText(translateStr);
                binding.line.setVisibility(View.VISIBLE);
                binding.tvMsgTranslate.setVisibility(View.VISIBLE);
            }else {
                binding.line.setVisibility(View.GONE);
                binding.tvMsgTranslate.setVisibility(View.GONE);
            }
        }else {
            binding.line.setVisibility(View.GONE);
            binding.tvMsgTranslate.setVisibility(View.GONE);
        }
        binding.tvMsg.setText(imMessage.getContent());
        binding.getRoot().setGravity(isLeftMsg(imMessage) ? Gravity.START : Gravity.END);
    }


}
