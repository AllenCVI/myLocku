package com.lockulockme.lockuchat.adapter.chat;

import android.view.Gravity;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.databinding.ItemChatTextBinding;
import com.lockulockme.lockuchat.databinding.ItemChatUnkownBinding;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class UnkownProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.UNKOWN;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_unkown;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatUnkownBinding binding=ItemChatUnkownBinding.bind(baseViewHolder.itemView);
        binding.getRoot().setGravity(Gravity.CENTER );
    }
}
