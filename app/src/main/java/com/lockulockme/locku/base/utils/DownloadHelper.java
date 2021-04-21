package com.lockulockme.locku.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import com.lockulockme.locku.base.netbase.LockUOKHttpClient;
import com.lockulockme.lockuchat.utils.SDCardUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadHelper {

    static DownloadHelper downloadHelper;
    private final String TAG = "DownloadHelper";
    private Context context;
    private String saveDir;
    private final OkHttpClient okHttpClient;

    public static DownloadHelper getInstance() {
        if (downloadHelper == null) {
            downloadHelper = new DownloadHelper();
        }
        return downloadHelper;
    }

    private DownloadHelper() {
        okHttpClient = new OkHttpClient();
    }

    public void init(Context context) {
        this.context = context;
        this.saveDir = SDCardUtils.getDiskCacheDir(context).getAbsolutePath() + "/videos";
    }


    @SuppressLint("WrongConstant")
    public void downloadTask(String url, OnDownloadListener listener) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Download", "no Permission");
            listener.onFailed();
            return;
        }

        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                Log.w(TAG, "存储下载目录：" + saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    String fileName = UUID.randomUUID().toString().replaceAll("-", "");
                    File file = new File(saveDir, fileName);
                    Log.w(TAG, "最终路径：" + file);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onProgress(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onSuccess(file.getAbsolutePath());
                } catch (Exception e) {
                    listener.onFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public interface OnDownloadListener {

        void onSuccess(String path);

        void onProgress(float progress);

        void onFailed();
    }


}
