package com.lockulockme.lockuchat.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.attach.AskGiftsAttachment;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.attach.QAMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyImageAttachment;
import com.lockulockme.lockuchat.bean.RecentUser;
import com.lockulockme.lockuchat.databinding.ItemRecentUserBinding;
import com.lockulockme.lockuchat.utils.DateUtils;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NetCallAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import org.jetbrains.annotations.NotNull;

public class RecentUsersAdapter extends BaseQuickAdapter<RecentUser, BaseViewHolder> {
    private Drawable onlineDrawable;
    private Drawable offlineDrawable;
    private Drawable busyDrawable;

    public RecentUsersAdapter() {
        super(R.layout.item_recent_user);
    }

    @Override
    protected void onItemViewHolderCreated(@NotNull BaseViewHolder viewHolder, int viewType) {
        super.onItemViewHolderCreated(viewHolder, viewType);
        onlineDrawable = getContext().getResources().getDrawable(R.drawable.ic_point_online);
        onlineDrawable.setBounds(0, 0, onlineDrawable.getMinimumWidth(), onlineDrawable.getMinimumHeight());
        offlineDrawable = getContext().getResources().getDrawable(R.drawable.ic_point_offline);
        offlineDrawable.setBounds(0, 0, offlineDrawable.getMinimumWidth(), offlineDrawable.getMinimumHeight());
        busyDrawable = getContext().getResources().getDrawable(R.drawable.ic_point_busy);
        busyDrawable.setBounds(0, 0, busyDrawable.getMinimumWidth(), busyDrawable.getMinimumHeight());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, RecentUser recentUser) {
        ItemRecentUserBinding binding = ItemRecentUserBinding.bind(baseViewHolder.itemView);
        if (recentUser.user != null) {
            binding.tvNickname.setText(recentUser.user.nick);
            if (recentUser.user.bs) {
                binding.tvStatus.setText(ResouseUtils.getResouseString(R.string.locku_im_busy));
                binding.tvStatus.setCompoundDrawables(busyDrawable, null, null, null);
                binding.tvStatus.setVisibility(View.VISIBLE);
            } else {
                if (recentUser.user.isOn) {
                    binding.tvStatus.setText(ResouseUtils.getResouseString(R.string.locku_im_online));
                    binding.tvStatus.setCompoundDrawables(onlineDrawable, null, null, null);
                    binding.tvStatus.setVisibility(View.VISIBLE);
                } else {
                    binding.tvStatus.setText(ResouseUtils.getResouseString(R.string.locku_im_offline));
                    binding.tvStatus.setCompoundDrawables(offlineDrawable, null, null, null);
                    binding.tvStatus.setVisibility(View.GONE);
                }
            }
            if (TextUtils.isEmpty(recentUser.user.userIcon)) {
                binding.ivHeadimg.setImageResource(R.mipmap.icon_placeholder_user);
            } else {
                ImageHelper.intoIV4Circle(binding.ivHeadimg, recentUser.user.smallUserIcon, R.mipmap.icon_placeholder_user, R.mipmap.icon_placeholder_user);
            }
            ImageHelper.intoIV4Circle(binding.ivCountry, recentUser.user.countryUrl, R.mipmap.country_placeholder, R.mipmap.country_placeholder);

            if (recentUser.user.myLevelData != null && recentUser.user.myLevelData.myLevel > 0) {
                binding.ivLevel.setVisibility(View.VISIBLE);
                ImageHelper.intoIV4Circle(binding.ivLevel, recentUser.user.myLevelData.LevelIcon, R.mipmap.level_icon_place_holder, R.mipmap.level_icon_place_holder);
            } else {
                binding.ivLevel.setVisibility(View.GONE);
            }
        }
        if (recentUser.recentContact != null) {
            binding.tvContent.setText(getMsgContent(recentUser.recentContact));
            String timeString = DateUtils.formatDate(recentUser.recentContact.getTime(), true);
            binding.tvDate.setText(timeString);

            int unreadNum = recentUser.recentContact.getUnreadCount();
            binding.tvMsgNum.setVisibility(unreadNum > 0 ? View.VISIBLE : View.GONE);
            binding.tvMsgNum.setText(unreadCountShowRule(unreadNum));
        }

    }


    protected String unreadCountShowRule(int unread) {
        unread = Math.min(unread, 99);
        return String.valueOf(unread);
    }

    String getMsgContent(RecentContact recent) {
        switch (recent.getMsgType()) {
            case text:
            case tip:
                return recent.getContent();
            case image:
                return ResouseUtils.getResouseString(R.string.msg_picture_message);
            case video:
                return ResouseUtils.getResouseString(R.string.msg_video_message);
            case audio:
                return ResouseUtils.getResouseString(R.string.msg_audio_message);
            case notification:
                return "";
            default:
                String digest = ResouseUtils.getResouseString(R.string.msg_unknown);
                MsgAttachment attachment = recent.getAttachment();
                if (attachment instanceof QAMsgAttachment) {
                    return ((QAMsgAttachment) attachment).getQuestion();
                } else if (attachment instanceof QAAnswerMsgAttachment) {
                    return ((QAAnswerMsgAttachment) attachment).getAnswer();
                } else if (attachment instanceof GiftMsgAttachment) {
                    return (ResouseUtils.getResouseString(R.string.msg_gift));
                } else if (attachment instanceof AskGiftsAttachment) {
                    return (ResouseUtils.getResouseString(R.string.str_msg_ask_gift));
                } else if (attachment instanceof StrategyAVMsgAttachment) {
                    StrategyAVMsgAttachment customAVChatCallMsgAttachment = (StrategyAVMsgAttachment) attachment;
                    if (customAVChatCallMsgAttachment.getCallType() == StrategyAVMsgAttachment.AUDIO_CALL_TYPE) {
                        return (ResouseUtils.getResouseString(R.string.msg_voice_chat));
                    } else {
                        return (ResouseUtils.getResouseString(R.string.msg_video_chat));
                    }
                } else if (attachment instanceof StrategyImageAttachment) {
                    return ResouseUtils.getResouseString(R.string.msg_picture_message);
                } else if (attachment instanceof NetCallAttachment) {
                    NetCallAttachment netCallAttachment = (NetCallAttachment) attachment;
                    if (netCallAttachment.getType() == ChannelType.AUDIO.getValue()) {
                        return (ResouseUtils.getResouseString(R.string.msg_voice_chat));
                    } else {
                        return (ResouseUtils.getResouseString(R.string.msg_video_chat));
                    }
                } else if (recent.getSessionType() == SessionTypeEnum.Ysf) {
                    return recent.getContent();
                }
                return digest;
        }
    }

}
