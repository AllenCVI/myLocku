package com.lockulockme.lockuchat.common;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.lockulockme.lockuchat.bean.HangUp;
import com.lockulockme.lockuchat.bean.HangUpWithChannelId;
import com.lockulockme.lockuchat.event.EventSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HangUpHelper {
    private ConcurrentHashMap<EventSubscriber, String> eventSubscriberMap = new ConcurrentHashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    private static class InstanceHelper {
        private static HangUpHelper INSTANCE = new HangUpHelper();
    }

    public static HangUpHelper getInstance() {
        return HangUpHelper.InstanceHelper.INSTANCE;
    }

    public void updateHangUpLiveData(HangUp hangUp) {
        updateHangUp(hangUp);
    }

    public void updateHangUpWithChannelIdLiveData(HangUpWithChannelId hangUpWithChannelId) {
        updateHangUpWithChannelId(hangUpWithChannelId);
    }

    public void clearHungup() {
        handler.removeCallbacksAndMessages(null);
        eventSubscriberMap.clear();
    }


    public void updateHangUp(HangUp hangUp) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<EventSubscriber, String> entry : eventSubscriberMap.entrySet()) {
                    if ("observeHangUp".equals(entry.getValue()) && entry.getKey() != null) {
                        entry.getKey().onEvent(hangUp);
                    }
                }
            }
        });
    }

    public void updateHangUpWithChannelId(HangUpWithChannelId hangUpWithChannelId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<EventSubscriber, String> entry : eventSubscriberMap.entrySet()) {
                    if ("observeHangUpWithChannelId".equals(entry.getValue()) && entry.getKey() != null) {
                        entry.getKey().onEvent(hangUpWithChannelId);
                    }
                }
            }
        });

    }

    public void observeHangUpWithChannelId(EventSubscriber<HangUpWithChannelId> eventSubscriber) {
        eventSubscriberMap.put(eventSubscriber, "observeHangUpWithChannelId");
    }

    public void observeHangUp(EventSubscriber<HangUp> eventSubscriber) {
        eventSubscriberMap.put(eventSubscriber, "observeHangUp");
    }
}
