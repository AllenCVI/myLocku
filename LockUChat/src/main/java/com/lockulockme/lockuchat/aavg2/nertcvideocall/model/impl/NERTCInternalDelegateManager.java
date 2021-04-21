package com.lockulockme.lockuchat.aavg2.nertcvideocall.model.impl;

import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCCallingDelegate;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 收拢说有的Delegate
 */
public class NERTCInternalDelegateManager implements NERTCCallingDelegate {

    private CopyOnWriteArrayList<WeakReference<NERTCCallingDelegate>> mWeakReferenceList;

    public NERTCInternalDelegateManager() {
        mWeakReferenceList = new CopyOnWriteArrayList<>();
    }

    public void addDelegate(NERTCCallingDelegate listener) {
        WeakReference<NERTCCallingDelegate> listenerWeakReference = new WeakReference<>(listener);
        mWeakReferenceList.add(listenerWeakReference);
    }

    public boolean isEmpty() {
        return mWeakReferenceList == null || mWeakReferenceList.isEmpty();
    }

    public void removeDelegate(NERTCCallingDelegate listener) {
        Iterator iterator = mWeakReferenceList.iterator();
        while (iterator.hasNext()) {
            WeakReference<NERTCCallingDelegate> reference = (WeakReference<NERTCCallingDelegate>) iterator.next();
            if (reference != null && reference.get() == listener) {
                mWeakReferenceList.remove(reference);
            }
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onError(errorCode, errorMsg);
            }
        }
    }

    @Override
    public void onInvited(InvitedEvent invitedEvent) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onInvited(invitedEvent);
            }
        }
    }


    @Override
    public void onUserEnter(long uid,String accId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserEnter(uid,accId);
            }
        }
    }


    @Override
    public void onCallEnd(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onCallEnd(userId);
            }
        }
    }

    @Override
    public void onUserLeave(String accountID) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserLeave(accountID);
            }
        }
    }

    @Override
    public void onRejectByUserId(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onRejectByUserId(userId);
            }
        }
    }

    @Override
    public void onUserBusy(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserBusy(userId);
            }
        }
    }

    @Override
    public void onCancelByUserId(String userId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onCancelByUserId(userId);
            }
        }
    }

    @Override
    public void onCameraAvailable(long userId, boolean isVideoAvailable) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onCameraAvailable(userId, isVideoAvailable);
            }
        }
    }

    @Override
    public void onAudioAvailable(long userId, boolean isVideoAvailable) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onAudioAvailable(userId, isVideoAvailable);
            }
        }
    }

    @Override
    public void timeOut() {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.timeOut();
            }
        }
    }

    @Override
    public void onGetChannelId(long channelId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onGetChannelId(channelId);
            }
        }
    }

    @Override
    public void onGetIMChannelId(String channelId) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onGetIMChannelId(channelId);
            }
        }
    }

    @Override
    public void onUserDisconnect(String userId) {
            for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
                NERTCCallingDelegate listener = reference.get();
                if (listener != null) {
                    listener.onUserDisconnect(userId);
                }
            }
        }

    @Override
    public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {
        for (WeakReference<NERTCCallingDelegate> reference : mWeakReferenceList) {
            NERTCCallingDelegate listener = reference.get();
            if (listener != null) {
                listener.onUserNetworkQuality(stats);
            }
        }
    }
}
