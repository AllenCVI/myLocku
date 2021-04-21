package com.lockulockme.lockuchat.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.AskForGifts;
import com.lockulockme.lockuchat.databinding.ItemChatAskGiftItemBinding;
import com.lockulockme.lockuchat.utils.ImageHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AskGiftAdapter extends BaseQuickAdapter<AskForGifts, BaseViewHolder> {
    public AskGiftAdapter(@Nullable List<AskForGifts> data) {
        super(R.layout.item_chat_ask_gift_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, AskForGifts askForGifts) {
        ItemChatAskGiftItemBinding binding = ItemChatAskGiftItemBinding.bind(holder.itemView);
        ImageHelper.intoIV(binding.ivAsk4Gift,askForGifts.url,-1,-1);
        binding.tvAsk4GiftScore.setText(String.valueOf(askForGifts.diamondNum));
        binding.tvAsk4GiftName.setText(askForGifts.meGiftName);

    }
}
