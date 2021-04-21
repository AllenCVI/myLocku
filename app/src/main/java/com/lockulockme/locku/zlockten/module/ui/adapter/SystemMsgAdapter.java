package com.lockulockme.locku.zlockten.module.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.SystemMsgResponseBean;
import com.lockulockme.locku.zlockten.base.utils.SysNotifyUtils;
import com.lockulockme.locku.zlockten.module.ui.activity.VipGoodsListActivity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemMsgAdapter extends BaseQuickAdapter<SystemMsgResponseBean.DataBean, BaseViewHolder> {
    public SystemMsgAdapter() {
        super(R.layout.item_system_notification);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SystemMsgResponseBean.DataBean dataBean) {
        baseViewHolder.setText(R.id.tv_time, formatTime(dataBean.createTime));
        baseViewHolder.setText(R.id.tv_msg, SysNotifyUtils.getClickableContentFromHtml(dataBean.content, new SysNotifyUtils.OnContentClickListener() {
            @Override
            public void onClickContent(String url) {
                if ("vip".equalsIgnoreCase(url)) {
                    VipGoodsListActivity.go(getContext());
                }
            }
        }));

    }

    private String formatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }
}
