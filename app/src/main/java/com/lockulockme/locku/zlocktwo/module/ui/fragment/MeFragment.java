package com.lockulockme.locku.zlocktwo.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.MyStoneResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.eventbus.AvatarModifiedEvent;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.databinding.FgMeBinding;
import com.lockulockme.locku.zlocktwo.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocktwo.base.utils.AccountManager;
import com.lockulockme.locku.zlocktwo.base.utils.GlideUtils;
import com.lockulockme.locku.zlocktwo.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocktwo.base.utils.VipManager;
import com.lockulockme.locku.zlocktwo.common.BaseFragment;
import com.lockulockme.locku.zlocktwo.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.MainActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.ModifyActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.MySpaceActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.SeeMeActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.SettingsActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.SystemMsgActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.UploadAvatarActivity;
import com.lockulockme.locku.zlocktwo.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MeFragment extends BaseFragment<FgMeBinding> implements View.OnClickListener {

//    UserInfo userBean = AccountManager.getInstance().getCurrentUser();

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgMeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    protected void initEvent() {
        EventBus.getDefault().register(this);
    }

    protected void unregisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rltMineTopInfo.setOnClickListener(this);
        binding.framelayoutPortrait.setOnClickListener(this);
        binding.rltMeDiamond.setOnClickListener(this);
        binding.rltMeMembershipService.setOnClickListener(this);
        binding.rltMeMySpace.setOnClickListener(this);
        binding.rltMeRecentVisitors.setOnClickListener(this);
        binding.rltMeEditInfo.setOnClickListener(this);
        binding.rltMeSetting.setOnClickListener(this);
        binding.ivMsgBack.setOnClickListener(this);
        binding.rlMsg.setOnClickListener(this);
        binding.llUnread.setOnClickListener(this);
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        if ("1".equals(userBean.userGender)) {
            GlideUtils.loadCircleImg(getContext(), userBean.avatar, binding.ivMyPortrait, R.mipmap.ic_male_portrait_placeholder);
        } else {
            GlideUtils.loadCircleImg(getContext(), userBean.avatar, binding.ivMyPortrait, R.mipmap.ic_female_portrait_placeholder);
        }
        binding.tvMeName.setText(userBean.name);


        IMLoginHelper.getInstance().observeUnreadMsgNum(this, unreadNum -> {
            setUnreadMsg(unreadNum);
        });
    }

    private void loadMyStone() {

    }


    @Override
    public void onFragmentShow() {
        super.onFragmentShow();
        loadMyStone();
        getUnreadMsg();
    }

    @Override
    public void onStart() {
        super.onStart();
        initUserState();
    }

    private void initUserState() {
        VipManager.getInstance().getVipState(null, new VipManager.OnVipListener() {

            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                if (vipResp.isVip) {
                    binding.rltVipStatus.setVisibility(View.VISIBLE);
                    binding.tvMeVipDeadline.setText(getVipDeadLine(vipResp.underTime));
                } else {
                    binding.rltVipStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onVipFailed() {
                binding.rltVipStatus.setVisibility(View.GONE);
            }
        });
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        binding.tvMeName.setText(userBean.name);

    }

    private String getVipDeadLine(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date utcDate = new Date(time);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(utcDate.getTime());
    }

    private void getUnreadMsg() {

    }

    private void setUnreadMsg(int msgNum) {
        if (msgNum > 0) {
            binding.llUnread.setVisibility(View.VISIBLE);
            binding.tvUnreadContent.setText(String.format(getString(R.string.new_msg_click_to_read), msgNum + ""));
        } else {
            binding.llUnread.setVisibility(View.GONE);
        }
    }

    private void setUnreadSystemMsg(int msgNum) {
        if (msgNum > 0) {
            binding.tvUnread.setVisibility(View.VISIBLE);
        } else {
            binding.tvUnread.setVisibility(View.INVISIBLE);
        }
    }

    public void onClick(View view) {
        if (binding.rltMineTopInfo == view) {
            ModifyActivity.go(getActivity());
        } else if (binding.framelayoutPortrait == view) {
            UploadAvatarActivity.go(getActivity());
        } else if (binding.rltMeMembershipService == view) {
            VipGoodsListActivity.go(getActivity());
        } else if (binding.rltMeDiamond == view) {
            CurrentDiamondActivity.go(getActivity());
        } else if (binding.rltMeMySpace == view) {
            MySpaceActivity.go(getActivity());
        } else if (binding.rltMeRecentVisitors == view) {
            SeeMeActivity.go(getActivity());
        } else if (binding.rltMeEditInfo == view) {
            ModifyActivity.go(getActivity());
        } else if (binding.rltMeSetting == view) {
            SettingsActivity.go(getActivity());
        } else if (binding.ivMsgBack == view) {
            binding.llUnread.setVisibility(View.GONE);
        } else if (binding.rlMsg == view) {
            SystemMsgActivity.StartMe(mContext);
        } else if (binding.llUnread == view) {
            ((MainActivity) mContext).setMsgCurrent();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void avatarUpdateEvent(AvatarModifiedEvent event) {
        GlideUtils.loadCircleImg(getContext(), event.filePath, binding.ivMyPortrait);
    }

}
