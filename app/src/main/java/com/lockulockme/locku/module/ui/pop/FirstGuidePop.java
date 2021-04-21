package com.lockulockme.locku.module.ui.pop;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.FirstGuideResponseBean;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.databinding.ItemGuideUserBinding;

import org.jetbrains.annotations.NotNull;

public class FirstGuidePop {

    private final Activity activity;
    private LinearLayout linearLayout;
    private PopupWindow popupWindow;
    private TextView tvTitle, tvDeputy, tvGo;
    private RecyclerView recyclerView;
    private GuideAdapter mAdapter;

    public FirstGuidePop(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        View view = View.inflate(activity, R.layout.pop_register_guide, null);
        linearLayout = view.findViewById(R.id.ll_content);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDeputy = view.findViewById(R.id.tv_deputy);
        tvGo = view.findViewById(R.id.tv_go);
        recyclerView = view.findViewById(R.id.rv_guide);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        mAdapter = new GuideAdapter();
        recyclerView.setAdapter(mAdapter);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        linearLayout.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
        tvGo.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
    }

    public FirstGuidePop setBean(FirstGuideResponseBean responseBean) {
        tvTitle.setText(responseBean.title);
        tvDeputy.setText(responseBean.content);
        mAdapter.setNewInstance(responseBean.users);
        return this;
    }

    public void showDialog() {
        if (popupWindow == null || popupWindow.isShowing() || activity == null || activity.isDestroyed()) {
            return;
        }
        popupWindow.update();
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    class GuideAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {

        public GuideAdapter() {
            super(R.layout.item_guide_user);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, UserInfo userInfo) {
            ItemGuideUserBinding bind = ItemGuideUserBinding.bind(baseViewHolder.itemView);
            if (activity != null && !activity.isDestroyed()) {
                GlideUtils.loadCircleImg(activity, userInfo.smallAvatar, bind.ivAvatar, R.mipmap.icon_placeholder_user);
            }
        }
    }
}
