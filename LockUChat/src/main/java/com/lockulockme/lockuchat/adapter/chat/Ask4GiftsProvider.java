package com.lockulockme.lockuchat.adapter.chat;


import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.adapter.AskGiftAdapter;
import com.lockulockme.lockuchat.attach.AskGiftsAttachment;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.bean.AskForGifts;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.SendGiftRsp;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.bean.rst.SendGiftRst;
import com.lockulockme.lockuchat.databinding.ItemChatAskGiftBinding;
import com.lockulockme.lockuchat.http.GsonUtils;
import com.lockulockme.lockuchat.http.NetDataListener;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Ask4GiftsProvider extends MyBaseItemProvider<IMMessage> {
    private User myUser;
    private User otherUser;

    public Ask4GiftsProvider(User myUser, User otherUser) {
        this.myUser = myUser;
        this.otherUser = otherUser;
    }

    @Override
    public int getItemViewType() {
        return MsgType.ASK_4_GIFTS;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_ask_gift;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatAskGiftBinding binding=ItemChatAskGiftBinding.bind(baseViewHolder.itemView);
        String title = ResouseUtils.getResouseString(R.string.str_title_ask_for);
        binding.tvTitleAsk.setText(Html.fromHtml(String.format(title,otherUser.nick)));

        AskGiftsAttachment attachment = (AskGiftsAttachment) imMessage.getAttachment();
        List<AskForGifts> askForGifts = attachment.getAskForGifts();
        if (askForGifts == null){
            askForGifts = new ArrayList<>();
        }
        AskGiftAdapter askGiftsAdapter = new AskGiftAdapter(askForGifts);
        GridLayoutManager layoutManager = new GridLayoutManager(context,3);
        binding.rvAskGifts.setLayoutManager(layoutManager);
        binding.rvAskGifts.setAdapter(askGiftsAdapter);
        askGiftsAdapter.addChildClickViewIds(R.id.tv_send_gift);
        askGiftsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, final int position) {
                if (view.getId() == R.id.tv_send_gift) {

                    AskForGifts askGift = askGiftsAdapter.getItem(position);
                    sendGift(askGift);
                }
            }
        });
    }

    private void sendGift(AskForGifts askGift) {
        if (askGift == null) return;
        SendGiftRst sendGift = new SendGiftRst();
        sendGift.anchorStringId = otherUser.stringId;
        sendGift.giftId = askGift.gId;
        NetDataUtils.getInstance().getNetData().sendGift(sendGift, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                LogHelper.e("sendGift=",""+response);
                BaseBean baseBean = GsonUtils.getInstance().getData(response, SendGiftRsp.class);
                if (baseBean.code != 0) {
                    if (baseBean.code == 9) {
                        GoNeedUIUtils.getInstance().getGoNeedUI().goDiamondsPage(getContext());
                    }else {
                        ToastUtils.toastShow(R.string.send_failed);
                    }
                    return;
                }

                SendGiftRsp sendGiftRsp = (SendGiftRsp) baseBean.data;
                GiftMsgAttachment attachment = new GiftMsgAttachment(sendGiftRsp.giftUrl, sendGiftRsp.meGiftName,sendGiftRsp.id,
                        sendGiftRsp.diamond,sendGiftRsp.meGiftDesc,sendGiftRsp.score,sendGiftRsp.sheGiftName,sendGiftRsp.sheGiftDesc);

                IMMessage message = MessageBuilder.createCustomMessage(otherUser.accid, SessionTypeEnum.P2P, attachment);
                getMsgController().sendMsg(message);
            }

            @Override
            public void onFailed(String msg, int code) {
                ToastUtils.toastShow(R.string.send_failed);
            }
        },getContext());
    }
}
