package com.lockulockme.lockuchat.common;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.lockulockme.lockuchat.bean.BlockEvent;
import com.lockulockme.lockuchat.bean.HangUp;
import com.lockulockme.lockuchat.bean.HangUpWithChannelId;
import com.lockulockme.lockuchat.event.EventSubscriber;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventHelper {
    private Handler handler = new Handler(Looper.getMainLooper());
    private ConcurrentHashMap<String, ConcurrentHashMap<EventSubscriber, String>> eventSubscriberMap = new ConcurrentHashMap<>();

    private static class InstanceHelper {
        private static EventHelper INSTANCE = new EventHelper();
    }

    public static EventHelper getInstance() {
        return EventHelper.InstanceHelper.INSTANCE;
    }

    public void updateMe2MeBlockEventLiveData(BlockEvent blockEvent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, ConcurrentHashMap<EventSubscriber, String>> entry : eventSubscriberMap.entrySet()) {
                    ConcurrentHashMap<EventSubscriber, String> map = entry.getValue();
                    if (map != null) {
                        for (Map.Entry<EventSubscriber, String> entry1 : map.entrySet()) {
                            if ("observeMe2MeBlockEvent".equals(entry1.getValue()) && entry1.getKey() != null) {
                                entry1.getKey().onEvent(blockEvent);
                            }
                        }
                    }
                }
            }
        });
    }

    public void clear(String key) {
        ConcurrentHashMap<EventSubscriber, String> map = eventSubscriberMap.get(key);
        if (map != null) {
            map.clear();
        }
        eventSubscriberMap.remove(key);
    }

    public void observeMe2MeBlockEvent(String key,EventSubscriber<BlockEvent> eventSubscriber) {
        ConcurrentHashMap<EventSubscriber, String> map = eventSubscriberMap.get(key);
        if (map == null){
            map = new ConcurrentHashMap<>();
        }
        map.put(eventSubscriber, "observeMe2MeBlockEvent");
        eventSubscriberMap.put(key,map);
    }

    public void updateApp2MeBlockEventLiveData(BlockEvent blockEvent) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, ConcurrentHashMap<EventSubscriber, String>> entry : eventSubscriberMap.entrySet()) {
                    ConcurrentHashMap<EventSubscriber, String> map = entry.getValue();
                    if (map != null) {
                        for (Map.Entry<EventSubscriber, String> entry1 : map.entrySet()) {
                            if ("observeApp2MeBlockEvent".equals(entry1.getValue()) && entry1.getKey() != null) {
                                entry1.getKey().onEvent(blockEvent);
                            }
                        }
                    }
                }
            }
        });
    }

    public void observeApp2MeBlockEvent(String key,EventSubscriber<BlockEvent> eventSubscriber) {
        ConcurrentHashMap<EventSubscriber, String> map = eventSubscriberMap.get(key);
        if (map == null){
            map = new ConcurrentHashMap<>();
        }
        map.put(eventSubscriber, "observeApp2MeBlockEvent");
        eventSubscriberMap.put(key,map);
    }

    public void updateStrategyMsg(IMMessage msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, ConcurrentHashMap<EventSubscriber, String>> entry : eventSubscriberMap.entrySet()) {
                    ConcurrentHashMap<EventSubscriber, String> map = entry.getValue();
                    if (map != null) {
                        for (Map.Entry<EventSubscriber, String> entry1 : map.entrySet()) {
                            if ("observeStrategyMsg".equals(entry1.getValue()) && entry1.getKey() != null) {
                                entry1.getKey().onEvent(msg);
                            }
                        }
                    }
                }
            }
        });
    }


    public void observeStrategyMsg(String key,EventSubscriber<IMMessage> eventSubscriber) {
        ConcurrentHashMap<EventSubscriber, String> map = eventSubscriberMap.get(key);
        if (map == null){
            map = new ConcurrentHashMap<>();
        }
        map.put(eventSubscriber, "observeStrategyMsg");
        eventSubscriberMap.put(key,map);
    }
}
