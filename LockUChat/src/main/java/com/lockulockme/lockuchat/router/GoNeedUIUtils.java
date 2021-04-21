package com.lockulockme.lockuchat.router;

import android.app.Activity;

public class GoNeedUIUtils {
    private GoNeedUI goNeedUI;

    private GoNeedUIUtils() {
    }

    private static class InstanceHelper {
        private static final GoNeedUIUtils INSTANCE = new GoNeedUIUtils();
    }

    public static GoNeedUIUtils getInstance() {
        return GoNeedUIUtils.InstanceHelper.INSTANCE;
    }

    public GoNeedUI getGoNeedUI() {
        return goNeedUI;
    }

    public void setGoNeedUI(GoNeedUI goNeedUI) {
        this.goNeedUI = goNeedUI;
    }

    public void goVipPage(Activity context) {
        goNeedUI.goVipPage(context);
    }

    public void goDiamondsPage(Activity context) {
        goNeedUI.goDiamondsPage(context);
    }

    /**
     * 去评价
     *
     * @param context
     * @param type    ratingRequestBean type 1 video  2 audio
     * @param imId
     */
    public void goRating(Activity context, int type, String imId) {
        goNeedUI.goRating(context, type, imId);
    }
}
