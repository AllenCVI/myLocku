package com.lockulockme.lockuchat.adapter.chat;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.databinding.ItemChatAudioVideoBinding;
import com.lockulockme.lockuchat.databinding.ItemChatVoiceBinding;
import com.lockulockme.lockuchat.utils.AudioMsgPlayer;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class VoiceMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.VOICE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_voice;
    }



    @Override
    public void convert(@NotNull BaseViewHolder helper, IMMessage item, @NotNull List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.isEmpty()) {
            convert(helper, item);
        } else {
            ItemChatVoiceBinding binding = ItemChatVoiceBinding.bind(helper.itemView);
            String key = (String) payloads.get(0);
            switch (key) {
                case "audioPlay":
                    setPlaying(binding,item);
                    break;

            }
        }
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage message) {
        ItemChatVoiceBinding binding=ItemChatVoiceBinding.bind(baseViewHolder.itemView);

        binding.getRoot().setGravity(isLeftMsg(message)? Gravity.START :Gravity.END);

        boolean isLeftMsg=isLeftMsg(message);
        if (isLeftMsg){
            binding.ivVoiceLeft.setVisibility(View.VISIBLE);
            binding.ivVoiceRight.setVisibility(View.GONE);
            binding.tvChatMsg.setGravity(Gravity.END);
//            binding.ivVoiceLeft.setImageResource(R.drawable.ic_voice_left3);
        }else {
            binding.ivVoiceLeft.setVisibility(View.GONE);
            binding.ivVoiceRight.setVisibility(View.VISIBLE);
            binding.tvChatMsg.setGravity(Gravity.START);
//            binding.ivVoiceRight.setImageResource(R.drawable.ic_voice_right3);
        }
        AudioAttachment attachment = (AudioAttachment) message.getAttachment();
        binding.tvChatMsg.setText(attachment.getDuration()/1000+"\"");


        binding.contentView.setOnClickListener(v -> {
            AudioAttachment audioAttachment = (AudioAttachment) message.getAttachment();
            File file = new File(audioAttachment.getPathForSave());
            if (!file.exists()) {
                downAttachFile(message,new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {

                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
                return;
            }
            if (ChatAnswerUtils.getInstance().isAnsweringOrCanChat()){
                return;
            }
            AudioMsgPlayer.getInstance().playAudio(audioAttachment.getPath());
            AudioMsgPlayer.getInstance().setOnPlayListener(new AudioMsgPlayer.OnPlayListener() {
                @Override
                public void onPlaying(long pos, long duration) {

                }

                @Override
                public void onPlayend(long duration) {
                    AudioMsgPlayer.getInstance().setFilePath(null);
                    getChatMsgAdapter().notifyDataSetChanged();
                }
            });
            getChatMsgAdapter().notifyDataSetChanged();
        });
        setPlaying(binding,message);
        setStatus(binding,message);
    }

    private void setPlaying(ItemChatVoiceBinding binding,IMMessage message) {
        final AudioAttachment msgAttachment = (AudioAttachment) message.getAttachment();
        long duration = msgAttachment.getDuration();
        int minWidth= ScreenInfo.getInstance().dip2px(100);
        int maxWidth= ScreenInfo.getInstance().dip2px(220);
        float scale=duration*1f/120000;
        int width= (int) (minWidth+(maxWidth-minWidth)*scale);
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) binding.contentView.getLayoutParams();
        layoutParams.width=width;
        binding.contentView.setLayoutParams(layoutParams);


        binding.tvChatMsg.setTag(message.getUuid());
        if (AudioMsgPlayer.getInstance().isSameMsg(msgAttachment.getPath())) {
            if (AudioMsgPlayer.getInstance().isPlaying()){
                if (isLeftMsg(message)){
                    binding.ivVoiceLeft.setBackgroundResource(R.drawable.voice_anim_left);
                }else {
                    binding.ivVoiceRight.setBackgroundResource(R.drawable.voice_anim_right);
                }
                play(binding,message);
            }else {
                stop(binding,message);
            }
        } else {
            stop(binding,message);
        }
    }

    private void play(ItemChatVoiceBinding binding,IMMessage message) {
        if (isLeftMsg(message)){
            if (binding.ivVoiceLeft.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) binding.ivVoiceLeft.getBackground();
                animation.start();
            }
        }else {
            if (binding.ivVoiceRight.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) binding.ivVoiceRight.getBackground();
                animation.start();
            }
        }

    }

    private void stop(ItemChatVoiceBinding binding,IMMessage message) {
        if (isLeftMsg(message)){
            if (binding.ivVoiceLeft.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) binding.ivVoiceLeft.getBackground();
                animation.stop();
            }
            binding.ivVoiceLeft.setBackgroundResource(R.drawable.ic_voice_left3);
        }else {
            if (binding.ivVoiceRight.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) binding.ivVoiceRight.getBackground();
                animation.stop();
            }
            binding.ivVoiceRight.setBackgroundResource(R.drawable.ic_voice_right3);
        }
    }


    private void setStatus(ItemChatVoiceBinding binding,IMMessage message) {// 消息状态
        // 调整加载中的位置
        int index = isLeftMsg(message) ? 1 : 2;
        if (binding.llMsgContent.getChildAt(index) != binding.contentView) {
            binding.llMsgContent.removeView(binding.contentView);
            binding.llMsgContent.addView(binding.contentView, index);
        }

        AudioAttachment attachment = (AudioAttachment) message.getAttachment();
        MsgStatusEnum status = message.getStatus();
        AttachStatusEnum attachStatus = message.getAttachStatus();

        // alert button
        if (TextUtils.isEmpty(attachment.getPath())) {
            if (attachStatus == AttachStatusEnum.fail || status == MsgStatusEnum.fail) {
                binding.layoutMsgProgress.ivMsgAlert.setVisibility(View.VISIBLE);
            } else {
                binding.layoutMsgProgress.ivMsgAlert.setVisibility(View.GONE);
            }
        }

        if (status == MsgStatusEnum.sending || attachStatus == AttachStatusEnum.transferring) {
            binding.layoutMsgProgress.pbMsgStatus.setVisibility(View.VISIBLE);
        } else {
            binding.layoutMsgProgress.pbMsgStatus.setVisibility(View.GONE);
        }

    }
}
