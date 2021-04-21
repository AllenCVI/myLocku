package com.lockulockme.locku.base.advertismentid;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public final class AdIdImpl implements IInterface {

    private IBinder iBinder;

    public static final String INTERFACE_NAME = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";

    public AdIdImpl(IBinder pBinder) {
        iBinder = pBinder;
    }

    public IBinder asBinder() {
        return iBinder;
    }

    public String getAdvertisingId() throws RemoteException {
        Parcel dataParcel = Parcel.obtain();
        Parcel replyParcel = Parcel.obtain();
        String advertisingId;
        try {
            dataParcel.writeInterfaceToken(INTERFACE_NAME);
            iBinder.transact(1, dataParcel, replyParcel, 0);
            replyParcel.readException();
            advertisingId = replyParcel.readString();
        } finally {
            replyParcel.recycle();
            dataParcel.recycle();
        }
        return advertisingId;
    }

}
