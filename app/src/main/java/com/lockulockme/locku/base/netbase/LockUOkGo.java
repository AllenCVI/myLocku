package com.lockulockme.locku.base.netbase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.lockulockme.locku.application.MyApplication;
import com.lockulockme.locku.base.beans.BaseEntity;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.callback.StringCall;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.common.LoginoutManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LockUOkGo {

    private static final String TAG = "okhttp";

    public static LockUOkGo getInstance() {
        return MyOkGoHolder.holder;
    }

    private static class MyOkGoHolder {
        private static LockUOkGo holder = new LockUOkGo();
    }

    public <T> void post(final Object tag, final String requestBody, final String url, NewJsonCallback<T> tNewJsonCallback) {

        if (tag instanceof  LifecycleOwner){
            LifecycleOwner lifecycleOwner = (LifecycleOwner) tag;
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        cancelTag(tag);
                        lifecycleOwner.getLifecycle().removeObserver(this);
                    }
                }
            });
        }


        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            //默认在主线程里执行该方法
            public void subscribe(@NonNull final ObservableEmitter<Response> observableEmitter) throws Exception {
                MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(mediaType, requestBody))
                        .tag(tag)
                        .build();
                LockUOKHttpClient.client().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            observableEmitter.onComplete();
                            return;
                        }
                        if (call.isCanceled()) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            //TODO 需要手动取消订阅吗？
                            observableEmitter.onComplete();
                        } else {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            observableEmitter.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            observableEmitter.onComplete();
                            return;
                        }
                        observableEmitter.onNext(response);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
            Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Response response) {
                if (tNewJsonCallback != null) {
                    BaseEntity<T> baseEntity = tNewJsonCallback.convertResponse(response);
                    if (baseEntity != null) {
                        if (baseEntity.code == 0) {
                            tNewJsonCallback.onSuc(baseEntity.data, baseEntity.msg);
                        } else if (baseEntity.code == 1) {
                            AccountManager.getInstance().putPwd("");
                            LoginoutManager.loginout(MyApplication.getInstance().getCurActivity());
                        } else {
                            tNewJsonCallback.onE(response.code(), baseEntity.code, baseEntity.msg, baseEntity.data);
                        }
                    } else {
                        tNewJsonCallback.onE(-1, -1, null, null);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                tNewJsonCallback.onE(-1, -1, null, null);
            }

            @Override
            public void onComplete() {
                mDisposable.dispose();
            }
        });
    }


    public void postStr(final Object tag, final String requestBody, final String url, StringCall tNewJsonCallback) {

        if (tag instanceof  LifecycleOwner){
            LifecycleOwner lifecycleOwner = (LifecycleOwner) tag;
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        cancelTag(tag);
                        lifecycleOwner.getLifecycle().removeObserver(this);
                    }
                }
            });
        }

        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            //默认在主线程里执行该方法
            public void subscribe(@NonNull final ObservableEmitter<Response> observableEmitter) throws Exception {
                MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(mediaType, requestBody))
                        .tag(tag)
                        .build();
                LockUOKHttpClient.client().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            observableEmitter.onComplete();
                            return;
                        }
                        if (call.isCanceled()) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            //TODO 需要手动取消订阅吗？
                            observableEmitter.onComplete();
                        } else {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            observableEmitter.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            observableEmitter.onComplete();
                            return;
                        }
                        observableEmitter.onNext(response);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
            Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Response response) {
                if (tNewJsonCallback != null) {
                    if (response.code() == 200) {
                        tNewJsonCallback.onSuccess(response);
                    } else {
                        tNewJsonCallback.onError(response);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                tNewJsonCallback.onError(null);
            }

            @Override
            public void onComplete() {
                mDisposable.dispose();
            }
        });
    }


    public <T> void postFile(final Object tag,String url,File file, Map<String,String> paramMap,String param,NewJsonCallback<T> tNewJsonCallback){

        if (tag instanceof  LifecycleOwner){
            LifecycleOwner lifecycleOwner = (LifecycleOwner) tag;
            lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY){
                        cancelTag(tag);
                        lifecycleOwner.getLifecycle().removeObserver(this);
                    }
                }
            });
        }
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> subscriber) throws Exception {
                //String fileContent_Type = FileUtil.getFileByFile(file);
                String fileContent_Type = "image/png";
                RequestBody fileType = RequestBody.create(MediaType.parse(fileContent_Type), file);

                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (paramMap != null && paramMap.size() > 0) {
                    for (String key : paramMap.keySet()) {
                        builder.addFormDataPart(key, paramMap.get(key));
                    }
                }

                builder.addFormDataPart(param, file.getAbsolutePath(), fileType);


                Request request = new Request.Builder()
                        .url(url)
                        .tag(tag)
                        .post(builder.build())
                        .build();
                LockUOKHttpClient.client().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            subscriber.onComplete();
                            return;
                        }
                        if (call.isCanceled()) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            //TODO 需要手动取消订阅吗？
                            subscriber.onComplete();
                        } else {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (isTagHasFinished(call)) {
                            Log.d(TAG, "onFailure: " + "网络请求取消了");
                            subscriber.onComplete();
                            return;
                        }
                        subscriber.onNext(response);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
                    Disposable mDisposable;
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onError(Throwable e) {
                tNewJsonCallback.onE(-1, -1, null, null);
            }

            @Override
            public void onNext(Response response) {
                if (tNewJsonCallback != null) {
                    BaseEntity<T> baseEntity = tNewJsonCallback.convertResponse(response);
                    if (baseEntity != null) {
                        if (baseEntity.code == 0) {
                            tNewJsonCallback.onSuc(baseEntity.data, baseEntity.msg);
                        } else if (baseEntity.code == 1) {
                            AccountManager.getInstance().putPwd("");
                            LoginoutManager.loginout(MyApplication.getInstance().getCurActivity());
                        } else {
                            tNewJsonCallback.onE(response.code(), baseEntity.code, baseEntity.msg, baseEntity.data);
                        }
                    } else {
                        tNewJsonCallback.onE(-1, -1, null, null);
                    }
                }
            }

            @Override
            public void onComplete() {
                mDisposable.dispose();
            }
        });
    }




    private boolean isTagHasFinished(Call call) {
        if (call != null && call.request() != null && call.request().tag() != null) {
            if (call.request().tag() instanceof Fragment) {
                Activity activity = ((Fragment) call.request().tag()).getActivity();
                return activity == null || activity.isFinishing() || activity.isDestroyed();
            } else if (call.request().tag() instanceof Activity) {
                Activity activity = (Activity) call.request().tag();
                return activity == null || activity.isFinishing() || activity.isDestroyed();
            }
        }
        return false;
    }


    //页面destroy的时候调用
    public void cancelTag(Object tag) {
        if (tag == null)
            return;
        for (Call call : LockUOKHttpClient.client().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : LockUOKHttpClient.client().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
