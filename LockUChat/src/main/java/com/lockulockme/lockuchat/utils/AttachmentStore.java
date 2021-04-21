
package com.lockulockme.lockuchat.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 用于把附件保存到文件系统中
 */
public class AttachmentStore {

    public static boolean isFileExist(String path) {
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean saveBitmap(Bitmap bitmap, String path, boolean recycle) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return false;
        }

        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            return true;

        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
            if (recycle) {
                bitmap.recycle();
            }
        }
    }
}
