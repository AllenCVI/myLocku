package com.lockulockme.lockuchat.adapter.chat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.databinding.ItemChatPictureBinding;
import com.lockulockme.lockuchat.ui.ScanImageActivity;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.jetbrains.annotations.NotNull;

public class PictureMsgProvider extends MyBaseItemProvider<IMMessage> {
    @Override
    public int getItemViewType() {
        return MsgType.PICTURE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_picture;
    }
    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, IMMessage imMessage) {
        ItemChatPictureBinding binding=ItemChatPictureBinding.bind(baseViewHolder.itemView);
        FileAttachment attachment = (FileAttachment) imMessage.getAttachment();
        String path = attachment.getPath();
        String thumbPath = attachment.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)) {
            loadImage(binding.contentView,imMessage,thumbPath, attachment.getExtension());
        } else if (!TextUtils.isEmpty(path)) {
            loadImage(binding.contentView,imMessage,path, attachment.getExtension());
        } else {
            loadImage(binding.contentView,imMessage,null, attachment.getExtension());
            if (imMessage.getAttachStatus() == AttachStatusEnum.transferred
                    || imMessage.getAttachStatus() == AttachStatusEnum.def) {
                downAttachFile(imMessage,new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        loadImage(binding.contentView,imMessage,attachment.getThumbPath(), attachment.getExtension());
                        setStatus(binding,imMessage);
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        }
        binding.contentView.setOnClickListener(v -> {
            ScanImageActivity.startMe(getContext(),imMessage);
        });

        setStatus(binding,imMessage);
        binding.getRoot().setGravity(isLeftMsg(imMessage)? Gravity.START :Gravity.END);
    }

    private void setStatus(ItemChatPictureBinding binding,IMMessage message) {
        // 调整加载中的位置
        int index = isLeftMsg(message) ? 1 : 2;
        if (binding.llMsgContent.getChildAt(index) != binding.flContent) {
            binding.llMsgContent.removeView(binding.flContent);
            binding.llMsgContent.addView(binding.flContent, index);
        }

        FileAttachment attachment = (FileAttachment) message.getAttachment();
        if (TextUtils.isEmpty(attachment.getPath()) && TextUtils.isEmpty(attachment.getThumbPath())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getStatus() == MsgStatusEnum.fail) {
                binding.layoutMsgProgress.ivMsgAlert.setVisibility(View.VISIBLE);
            } else {
                binding.layoutMsgProgress.ivMsgAlert.setVisibility(View.GONE);
            }
        }
        if (message.getStatus() == MsgStatusEnum.sending
                || (isLeftMsg(message) && message.getAttachStatus() == AttachStatusEnum.transferring)) {
            if (isLeftMsg(message)){
                binding.layoutMsgProgress.pbMsgStatus.setVisibility(View.GONE);
                binding.pbDowning.setVisibility(View.VISIBLE);
            }else {
                binding.layoutMsgProgress.pbMsgStatus.setVisibility(View.VISIBLE);
                binding.pbDowning.setVisibility(View.GONE);
            }
        } else {
            binding.layoutMsgProgress.pbMsgStatus.setVisibility(View.GONE);
        }
    }

    private void loadImage(ImageView imageView,IMMessage message,String path,String ext){
        setImageViewWidthHeight(imageView,message);
        if (path==null){
            imageView.setImageResource(R.drawable.img_transparent);
        }else {
            ImageHelper.intoIV4Round(imageView,path, ScreenInfo.getInstance().dip2px(22),R.drawable.img_transparent,R.mipmap.img_failed);
        }
    }


}
