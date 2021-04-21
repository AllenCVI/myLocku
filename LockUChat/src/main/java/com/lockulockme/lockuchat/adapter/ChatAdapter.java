package com.lockulockme.lockuchat.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.adapter.chat.AVMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.Ask4GiftsProvider;
import com.lockulockme.lockuchat.adapter.chat.GiftMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.MsgType;
import com.lockulockme.lockuchat.adapter.chat.MyBaseItemProvider;
import com.lockulockme.lockuchat.adapter.chat.PictureMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.QAAnswerMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.QAMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.StrategyAVMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.StrategyImageProvider;
import com.lockulockme.lockuchat.adapter.chat.TextMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.UnkownProvider;
import com.lockulockme.lockuchat.adapter.chat.VideoMsgProvider;
import com.lockulockme.lockuchat.adapter.chat.VoiceMsgProvider;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.utils.DateUtils;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.im.MsgSendController;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatAdapter extends BaseProviderMultiAdapter<IMMessage> {
    private User myUser;
    private User otherUser;
    private boolean isHideLeftHead = false;
    private boolean isHideRightHead = true;
    private MsgSendController msgSendController;

    public ChatAdapter(User myUser, User otherUser) {
        super();
        this.myUser = myUser;
        this.otherUser = otherUser;
        // 注册 Provider
        addItemProvider(new TextMsgProvider());
        addItemProvider(new UnkownProvider());
        addItemProvider(new QAMsgProvider());
        addItemProvider(new QAAnswerMsgProvider());
        addItemProvider(new PictureMsgProvider());
        addItemProvider(new AVMsgProvider());
        addItemProvider(new StrategyAVMsgProvider());
        addItemProvider(new VoiceMsgProvider());
        addItemProvider(new GiftMsgProvider(myUser,otherUser));
        addItemProvider(new StrategyImageProvider());
        addItemProvider(new Ask4GiftsProvider(myUser,otherUser));
        addItemProvider(new VideoMsgProvider());
    }

    @Override
    public void addData(IMMessage data) {
        for (IMMessage datum : getData()) {
            if (datum.getUuid().equals(data.getUuid())){
                return;
            }
        }
        super.addData(data);
    }


    @Override
    protected int getItemType(@NotNull List<? extends IMMessage> list, int i) {
        return MsgType.getMsgType(list.get(i));
    }

//    @Override
//    protected void convert(@NotNull BaseViewHolder holder, IMMessage item, @NotNull List<?> payloads) {
//        super.convert(holder, item, payloads);
//        if (MyBaseItemProvider.isLeftMsg(item)){
//            holder.setGone(R.id.iv_head_left,isHideLeftHead);
//            holder.setGone(R.id.iv_head_right,true);
//        }else {
//            holder.setGone(R.id.iv_head_right,isHideRightHead);
//            holder.setGone(R.id.iv_head_left,true);
//        }
//    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, IMMessage item) {
        super.convert(holder, item);

        View contentView = holder.getViewOrNull(R.id.content_view);

        ImageView ivHeadLeft = holder.getViewOrNull(R.id.iv_head_left);
        ImageView ivHeadRight = holder.getViewOrNull(R.id.iv_head_right);

        if (MyBaseItemProvider.isLeftMsg(item)) {
            if (ivHeadLeft != null){
                holder.setGone(R.id.iv_head_left, isHideLeftHead);
            }
            if (ivHeadRight != null){
                holder.setGone(R.id.iv_head_right, true);
            }

            if (ivHeadLeft != null && !isHideLeftHead && otherUser != null) {
                ImageHelper.intoIV4Circle(ivHeadLeft, otherUser.smallUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
            }
            if (contentView != null)
                holder.setBackgroundResource(R.id.content_view, R.drawable.bg_msg_left);
        } else {

            if (ivHeadLeft != null){
                holder.setGone(R.id.iv_head_left, true);
            }
            if (ivHeadRight != null){
                holder.setGone(R.id.iv_head_right, isHideRightHead);
            }

            if (ivHeadRight != null && !isHideRightHead && myUser != null) {
                ImageHelper.intoIV4Circle(ivHeadRight, myUser.smallUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
            }
            if (contentView != null)
                holder.setBackgroundResource(R.id.content_view, R.drawable.bg_msg_right);

        }
        TextView tvTime = holder.getViewOrNull(R.id.tv_date);
        if (tvTime != null) {
            if (holder.getAdapterPosition() > 0) {
                IMMessage lastMsg = getItem(holder.getAdapterPosition() - 1);
                if (item.getTime() - lastMsg.getTime() > 5 * 60 * 1000) {
                    String text = DateUtils.formatDate(item.getTime(), false);
                    tvTime.setText(text);
                    tvTime.setVisibility(View.VISIBLE);
                } else {
                    tvTime.setVisibility(View.GONE);
                }
            } else {
                String text = DateUtils.formatDate(item.getTime(), false);
                tvTime.setText(text);
                tvTime.setVisibility(View.VISIBLE);
            }
        }
        MsgStatusEnum status = item.getStatus();
        if (status.getValue() == MsgStatusEnum.draft.getValue()){
            holder.itemView.setVisibility(View.GONE);
        }else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        if (holder.getViewOrNull(R.id.pb_msg_status) != null && holder.getViewOrNull(R.id.iv_msg_alert) != null) {


            switch (status) {
                case fail:
                    holder.setGone(R.id.pb_msg_status, true);
                    holder.setGone(R.id.iv_msg_alert, false);
                    break;
                case sending:
                    holder.setGone(R.id.pb_msg_status, false);
                    holder.setGone(R.id.iv_msg_alert, true);
                    break;
                default:
                    holder.setGone(R.id.pb_msg_status, true);
                    holder.setGone(R.id.iv_msg_alert, true);
                    break;
            }
        }
    }


    public boolean isHideLeftHead() {
        return isHideLeftHead;
    }

    public void setHideLeftHead(boolean hideLeftHead) {
        isHideLeftHead = hideLeftHead;
    }

    public boolean isHideRightHead() {
        return isHideRightHead;
    }

    public void setHideRightHead(boolean hideRightHead) {
        isHideRightHead = hideRightHead;
    }

    public MsgSendController getMsgSendController() {
        return msgSendController;
    }

    public void setMsgSendController(MsgSendController msgSendController) {
        this.msgSendController = msgSendController;
    }
}
