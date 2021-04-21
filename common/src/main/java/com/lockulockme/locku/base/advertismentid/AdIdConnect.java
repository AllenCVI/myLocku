package com.lockulockme.locku.base.advertismentid;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.LinkedBlockingQueue;

public final class AdIdConnect implements ServiceConnection {

    boolean searched = false;

    private final LinkedBlockingQueue<IBinder> linkedBlockingQueue = new LinkedBlockingQueue<>(1);

    public IBinder getBinder() throws InterruptedException {
        if (this.searched) {
            throw new IllegalStateException();

        }
        this.searched = true;
        return this.linkedBlockingQueue.take();
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            this.linkedBlockingQueue.put(service);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onServiceDisconnected(ComponentName name) {
    }

}