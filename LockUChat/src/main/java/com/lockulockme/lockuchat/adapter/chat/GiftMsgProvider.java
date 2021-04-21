package com.lockulockme.lockuchat.adapter.chat;

import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.databinding.ItemChatGiftBinding;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class GiftMsgProvider extends MyBaseItemProvider<IMMessage> {
    private User myUser;
    private User otherUser;

    public GiftMsgProvider(User myUser, User otherUser) {
        this.myUser = myUser;
        this.otherUser = otherUser;
    }

    @Override
    public int getItemViewType() {
        return MsgType.GIFT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_gift;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage message) {
        ItemChatGiftBinding binding = ItemChatGiftBinding.bind(baseViewHolder.itemView);
        GiftMsgAttachment attachment = (GiftMsgAttachment) message.getAttachment();
        String url=attachment.getGiftUrl();
        ImageHelper.intoIV(binding.ivGift,url,-1,-1);
        String title = "";
        if (isLeftMsg(message)){
            title = ResouseUtils.getResouseString(R.string.str_title_receive_send_gift);
        }else {
            title = ResouseUtils.getResouseString(R.string.str_title_send_send_gift);
        }
        binding.tvWhoSend.setText(Html.fromHtml(String.format(title,otherUser.nick)));
        binding.tvSendGiftName.setText(attachment.getGiftName());

        if (!TextUtils.isEmpty(attachment.getGiftDesc())){
            binding.tvSendGiftDesc.setText(attachment.getGiftDesc());
        }else {
            binding.tvSendGiftDesc.setText(R.string.str_default_send_gift_desc);
        }
    }
}
