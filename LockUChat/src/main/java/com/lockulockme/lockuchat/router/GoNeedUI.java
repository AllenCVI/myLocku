package com.lockulockme.lockuchat.router;

import android.app.Activity;
import android.content.Context;

public interface GoNeedUI {
    void goVipPage(Activity activity);

    void goDiamondsPage(Activity activity);

    void goDiamondsPage(Context context);

    void goUserInfo(Activity activity, String userStringId);

    void goReport4ChatActivity(Activity activity, String userStringId);

    void showCommentAppDialog(Activity activity);

    void notifyVIPExpired();

    void goRating(Activity activity, int type, String imId);
}
