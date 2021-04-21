package com.lockulockme.lockuchat.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;


public class BitmapDecoder {

    public static boolean extractThumbnail(String videoPath, String thumbPath) {
        if (!AttachmentStore.isFileExist(thumbPath)) {
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            if (thumbnail != null) {
                AttachmentStore.saveBitmap(thumbnail, thumbPath, true);
                return true;
            }
        }
        return false;
    }
}
