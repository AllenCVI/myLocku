package com.lockulockme.lockuchat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.lockulockme.lockuchat.databinding.AcScanVideoBinding;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;

public class ScanVideoActivity extends BaseActivity {

    private AcScanVideoBinding binding;
    public static final String MSG_DATA="msg";
    private IMMessage videoMessage;
    private String videoUrl;
    private AbortableFuture downAttachFileFuture;

    public static void startMe(Context context, IMMessage message){
        Intent intent=new Intent(context, ScanVideoActivity.class);
        intent.putExtra(MSG_DATA,message);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcScanVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoMessage = (IMMessage) getIntent().getSerializableExtra(MSG_DATA);
        if (isVideoHasDownloaded(videoMessage)){
            onDownloadSuccess(videoMessage);
        }else {
            downAttachFileFuture = downAttachFile(videoMessage, new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    if (ScanVideoActivity.this == null || ScanVideoActivity.this.isFinishing() || ScanVideoActivity.this.isDestroyed()) {
                        return;
                    }
                    onDownloadSuccess(videoMessage);
                }

                @Override
                public void onFailed(int code) {
                    onDownloadFailed();
                }

                @Override
                public void onException(Throwable exception) {
                    onDownloadFailed();
                }
            });
            binding.pbDowning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downAttachFileFuture != null){
            downAttachFileFuture.abort();
        }
    }

    protected AbortableFuture downAttachFile(IMMessage message, RequestCallback<Void> callback) {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment){
            AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(message, false);
            future.setCallback(callback);
            return future;
        }
        return null;
    }

    private void onDownloadSuccess(final IMMessage message) {
        videoUrl = ((VideoAttachment) message.getAttachment()).getPath();
        JZDataSource jzDataSource = new JZDataSource(videoUrl);
        jzDataSource.looping = true;
        binding.jzVideo.setUp(jzDataSource, Jzvd.SCREEN_NORMAL);
        binding.jzVideo.startVideo();

        binding.pbDowning.setVisibility(View.GONE);
    }

    private void onDownloadFailed() {
        binding.pbDowning.setVisibility(View.GONE);
    }
    private boolean isVideoHasDownloaded(final IMMessage message) {
        if (message.getAttachStatus() == AttachStatusEnum.transferred &&
                !TextUtils.isEmpty(((VideoAttachment) message.getAttachment()).getPath())) {
            return true;
        }
        return false;
    }
}
