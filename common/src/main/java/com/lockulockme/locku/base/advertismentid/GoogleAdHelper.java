package com.lockulockme.locku.base.advertismentid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class GoogleAdHelper {
    private static String googleAdId = null;
    public static final String ID_SERVICE_START_ACTION = "com.google.android.gms.ads.identifier.service.START";
    public static String VENDING_PACKAGE_NAME = "com.android.vending";
    public static final String GMS_PACKAGE_NAME = "com.google.android.gms";

    public static void initAAId(final Context context) {
        new AdIdThread(context).start();;
    }

    public static class AdIdThread extends Thread {
        Context context;

        public AdIdThread(Context context) {
            this.context = context;
        }

        public void run() {
            try {
                googleAdId = getGoogleAdFromVending(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getGoogleAdId() {
        if (!TextUtils.isEmpty(googleAdId)) {
            return googleAdId;
        }
        return "";
    }

    private static String getGoogleAdFromVending(Context context) throws Exception {
        PackageManager pm = context.getPackageManager();
        pm.getPackageInfo(VENDING_PACKAGE_NAME, 0);
        AdIdConnect connection = new AdIdConnect();
        Intent intent = new Intent(ID_SERVICE_START_ACTION);
        intent.setPackage(GMS_PACKAGE_NAME);
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdIdImpl adInterface = new AdIdImpl(
                        connection.getBinder());
                return adInterface.getAdvertisingId();
            } finally {
                context.unbindService(connection);
            }
        }
        return "";
    }
}
