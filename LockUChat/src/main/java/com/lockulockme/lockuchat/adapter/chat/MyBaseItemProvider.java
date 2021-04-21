package com.lockulockme.lockuchat.adapter.chat;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.lockulockme.lockuchat.adapter.ChatAdapter;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.im.MsgSendController;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public abstract class MyBaseItemProvider<T> extends BaseItemProvider<T> {
    // 判断消息方向，是否是接收到的消息
    public static boolean isLeftMsg(IMMessage message) {
        return message.getDirect() == MsgDirectionEnum.In;
    }

    protected ChatAdapter getChatMsgAdapter() {
        return (ChatAdapter) getAdapter();
    }
    protected MsgSendController getMsgController() {
        return getChatMsgAdapter().getMsgSendController();
    }

    protected void downAttachFile(IMMessage message, RequestCallback<Void> callback) {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
            NIMClient.getService(MsgService.class).downloadAttachment(message, true).setCallback(callback);
    }

    protected void setImageViewWidthHeight(ImageView imageView, IMMessage message) {
        ImageAttachment attachment = (ImageAttachment) message.getAttachment();
        int srcImgWidth=attachment.getWidth();
        int srcImgHeight=attachment.getHeight();
        LogHelper.e("setImageViewWidthHeight",srcImgWidth+"+"+srcImgHeight);
        int maxWidth=imageView.getMaxWidth();
        int maxHeight=imageView.getMaxHeight();
        int dstWidth;
        int dstHeight;
        if (srcImgWidth>srcImgHeight){
            dstWidth=maxWidth;
            dstHeight= (int) (srcImgHeight*1f/srcImgWidth*maxWidth);
        }else {
            dstHeight=maxHeight;
            dstWidth= (int) (srcImgWidth*1f/srcImgHeight*maxHeight);
        }

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = dstWidth;
        layoutParams.height = dstHeight;
        imageView.setLayoutParams(layoutParams);
    }
}
