package com.lockulockme.lockuchat.adapter.chat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.NrtcCallStatus;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.databinding.ItemChatAudioVideoBinding;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.attachment.NetCallAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class AVMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.AUDIO_VIDEO;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_audio_video;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage message) {
        ItemChatAudioVideoBinding binding=ItemChatAudioVideoBinding.bind(baseViewHolder.itemView);
        NetCallAttachment attachment= (NetCallAttachment) message.getAttachment();
        if (isLeftMsg(message)){
            binding.ivTypeRight.setVisibility(View.GONE);
            binding.ivTypeLeft.setVisibility(View.VISIBLE);
            if (attachment.getType() == ChannelType.AUDIO.getValue()){
                binding.ivTypeLeft.setImageResource(R.mipmap.icon_chat_msg_audio);
            }else {
                binding.ivTypeLeft.setImageResource(R.mipmap.icon_chat_msg_video_left);
            }
        }else {
            binding.ivTypeRight.setVisibility(View.VISIBLE);
            binding.ivTypeLeft.setVisibility(View.GONE);
            if (attachment.getType() == ChannelType.AUDIO.getValue()){
                binding.ivTypeRight.setImageResource(R.mipmap.icon_chat_msg_audio);
            }else {
                binding.ivTypeRight.setImageResource(R.mipmap.icon_chat_msg_video_right);
            }
        }

        String textStatus = "";
        switch (attachment.getStatus()) {
            case NrtcCallStatus.NrtcCallStatusComplete: //成功接听
                if (attachment.getDurations() == null) {
                    break;
                }

                for (NetCallAttachment.Duration duration : attachment.getDurations()) {
                    if (TextUtils.equals(duration.getAccid(), SelfDataUtils.getInstance().getSelfData().getSelfUser().accid)) {
                        textStatus = formatTime(duration.getDuration());
                    }
                }

                break;
            case NrtcCallStatus.NrtcCallStatusCanceled:
                textStatus = context.getString(R.string.avcall_cancel);
                break;

            case NrtcCallStatus.NrtcCallStatusRejected: { // "被拨打方" 拒绝接听电话或者正忙
                int strID = message.getDirect() == MsgDirectionEnum.In ? R.string.avcall_is_reject : R.string.avcall_be_rejected;
                textStatus = context.getString(strID);
                break;
            }
            case NrtcCallStatus.NrtcCallStatusTimeout: //未接听
                textStatus = context.getString(R.string.avcall_no_pick_up);
                break;
            case NrtcCallStatus.NrtcCallStatusBusy: { //对方正忙
                int strID = message.getDirect() == MsgDirectionEnum.In ? R.string.avcall_is_busy_self : R.string.avcall_is_busy_opposite;
                textStatus = context.getString(strID);
                break;
            }

            default:
                break;
        }

        binding.tvChatMsg.setText(textStatus);
        binding.getRoot().setGravity(isLeftMsg(message)? Gravity.START :Gravity.END);
    }


    public String formatTime(int time) {
        String timeStr ;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = formatUnit(minute) + ":" + formatUnit(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = formatUnit(hour) + ":" + formatUnit(minute) + ":" + formatUnit(second);
            }
        }
        return timeStr;
    }

    public String formatUnit(int i) {
        String retStr ;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else retStr = "" + i;
        return retStr;
    }
}
