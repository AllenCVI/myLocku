package com.lockulockme.lockuchat.http;

public interface DownloadListener {

    void onSuccess(String path);

    void onProgress(float progress);

    void onFailed();
}
