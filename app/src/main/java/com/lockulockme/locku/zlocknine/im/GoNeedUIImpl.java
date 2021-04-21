package com.lockulockme.locku.zlocknine.im;

import android.app.Activity;
import android.content.Context;

import com.lockulockme.locku.base.beans.requestbean.ReportRequestBean;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.zlocknine.common.PopupWindowBuilder;
import com.lockulockme.locku.zlocknine.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.zlocknine.module.ui.activity.ReportActivity;
import com.lockulockme.locku.zlocknine.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.zlocknine.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.locku.zlocknine.module.ui.pop.CommentAppPop;
import com.lockulockme.lockuchat.router.GoNeedUI;

import org.greenrobot.eventbus.EventBus;

public class GoNeedUIImpl implements GoNeedUI {
    @Override
    public void goVipPage(Activity activity) {
        VipGoodsListActivity.go(activity);
    }

    @Override
    public void goDiamondsPage(Activity activity) {
        CurrentDiamondActivity.go(activity);
    }

    @Override
    public void goDiamondsPage(Context context) {

    }

    @Override
    public void goUserInfo(Activity activity, String userStringId) {
        SheDetailActivity.StartMe(activity,userStringId);
    }

    @Override
    public void goReport4ChatActivity(Activity activity, String userStringId) {
        ReportActivity.StartMe(activity,userStringId, ReportRequestBean.CHAT_TYPE);
    }

    @Override
    public void showCommentAppDialog(Activity activity) {
        CommentAppPop commentAppPop=new CommentAppPop(activity);
        commentAppPop.setFocusable(false);
        commentAppPop.setOnBackListener(new PopupWindowBuilder.OnBackListener() {
            @Override
            public void onBack() {
                activity.finish();
            }
        });
        commentAppPop.goRateUs();
    }

    @Override
    public void notifyVIPExpired() {
        EventBus.getDefault().post(new VipStatusRefreshEvent());
    }

    @Override
    public void goRating(Activity activity, int type, String imId) {

    }
}
