package com.lockulockme.lockuchat.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class SDCardUtils {

    public static File getDiskCacheDir(Context context) {
        final String cachePath = checkSDCard() ? getExternalCacheDir(context).getPath() : getAppCacheDir(context);

        File cacheDirFile = new File(cachePath);
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdirs();
        }

        return cacheDirFile;
    }

    static File getExternalCacheDir(Context context) {
        // 这个sd卡中文件路径下的内容会随着，程序卸载或者设置中清除缓存后一起清空
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    static boolean checkSDCard() {
        final String status = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(status);
    }

    static String getAppCacheDir(Context context) {
        return context.getCacheDir().getPath();
    }

}
