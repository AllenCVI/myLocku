package com.lockulockme.locku.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.AttentionNumResponseBean;
import com.lockulockme.locku.base.beans.responsebean.MyLevelRe;
import com.lockulockme.locku.base.beans.responsebean.MyStoneResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.AvatarModifiedEvent;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseFragment;
import com.lockulockme.locku.databinding.FgMeBinding;
import com.lockulockme.locku.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.module.ui.activity.LevelActivity;
import com.lockulockme.locku.module.ui.activity.MainActivity;
import com.lockulockme.locku.module.ui.activity.MineAttentionActivity;
import com.lockulockme.locku.module.ui.activity.ModifyActivity;
import com.lockulockme.locku.module.ui.activity.MySpaceActivity;
import com.lockulockme.locku.module.ui.activity.SeeMeActivity;
import com.lockulockme.locku.module.ui.activity.SettingsActivity;
import com.lockulockme.locku.module.ui.activity.SystemMsgActivity;
import com.lockulockme.locku.module.ui.activity.UploadAvatarActivity;
import com.lockulockme.locku.module.ui.activity.VipGoodsListActivity;
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rltMineTopInfo.setOnClickListener(this);
        binding.framelayoutPortrait.setOnClickListener(this);
        binding.rltMeDiamond.setOnClickListener(this);
        binding.rltMeMembershipService.setOnClickListener(this);
        binding.rltMeMyLevel.setOnClickListener(this);
        binding.rltMeMySpace.setOnClickListener(this);
        binding.rltMeRecentVisitors.setOnClickListener(this);
        binding.rltMeEditInfo.setOnClickListener(this);
        binding.rltMeSetting.setOnClickListener(this);
        binding.ivMsgBack.setOnClickListener(this);
        binding.rlMsg.setOnClickListener(this);
        binding.llUnread.setOnClickListener(this);
        binding.llFriend.setOnClickListener(this);
        binding.llFollow.setOnClickListener(this);
        binding.llFans.setOnClickListener(this);
        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        if ("1".equals(userBean.userGender)) {
            GlideUtils.loadCircleImg(MeFragment.this, userBean.smallAvatar, binding.ivMyPortrait, R.mipmap.ic_male_portrait_placeholder);
        } else {
            GlideUtils.loadCircleImg(MeFragment.this, userBean.smallAvatar, binding.ivMyPortrait, R.mipmap.ic_female_portrait_placeholder);
        }
        binding.tvUserId.setText(userBean.account);
        binding.tvMeName.setText(userBean.name);
        OkGoUtils.getInstance().getMyDetail(MeFragment.this, new NewJsonCallback<UserInfo>() {
            @Override
            public void onSuc(UserInfo response, String msg) {
                AccountManager.getInstance().putUser(response);
                binding.tvMeName.setText(response.name);
                if ("1".equals(response.userGender)) {
                    GlideUtils.loadCircleImg(MeFragment.this, response.avatar, binding.ivMyPortrait, R.mipmap.ic_male_portrait_placeholder);
                } else {
                    GlideUtils.loadCircleImg(MeFragment.this, response.avatar, binding.ivMyPortrait, R.mipmap.ic_female_portrait_placeholder);
                }
            }

            public void onE(int httpCode, int apiCode, String msg, UserInfo response) {
                LogUtil.LogE("meFragment", httpCode + "," + apiCode);
            }
        });

        IMLoginHelper.getInstance().observeUnreadMsgNum(this, unreadNum -> {
            setUnreadMsg(unreadNum);
        });
    }

    private void loadMyStone() {
        OkGoUtils.getInstance().getMyStone(MeFragment.this, new NewJsonCallback<MyStoneResponseBean>() {
            @Override
            public void onSuc(MyStoneResponseBean response, String msg) {
                binding.tvMyDiamond.setText(response.stoneNum + "");
            }
        });
    }


    @Override
    public void onFragmentShow() {
        super.onFragmentShow();
        loadMyStone();
        getUnreadMsg();
        getMyLevel();
        getAttentionNum();
        initUserState();
    }

    private void getMyLevel() {
        OkGoUtils.getInstance().getMyLevel(MeFragment.this, new NewJsonCallback<MyLevelRe>() {
            @Override
            public void onSuc(MyLevelRe response, String msg) {
                if (response==null){
                    return;
                }
                if (response.mLevel>0) {
                    binding.ivLevel.setVisibility(View.VISIBLE);
                    GlideUtils.loadCircleImg(MeFragment.this, response.mLevelIcon, binding.ivLevel, R.mipmap.level_icon_place_holder);
                }else {
                    binding.ivLevel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getAttentionNum() {
        OkGoUtils.getInstance().getAttentionNum(MeFragment.this, new NewJsonCallback<AttentionNumResponseBean>() {
            @Override
            public void onSuc(AttentionNumResponseBean response, String msg) {
                binding.tvFriend.setText(String.valueOf(response.buddyNumber));
                binding.tvFollow.setText(String.valueOf(response.attentionNumber));
                binding.tvFans.setText(String.valueOf(response.fansNumber));
            }
        });
    }

    private void initUserState() {
        VipManager.getInstance().getVipState(this, new VipManager.OnVipListener() {

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
        OkGoUtils.getInstance().getUnreadMsg(MeFragment.this, new NewJsonCallback<Integer>() {
            @Override
            public void onSuc(Integer response, String msg) {
                setUnreadSystemMsg(response);
            }
        });
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
//            RatingActivity.start(getContext(), "", 1);
        } else if (binding.ivMsgBack == view) {
            binding.llUnread.setVisibility(View.GONE);
        } else if (binding.rlMsg == view) {
            SystemMsgActivity.StartMe(mContext);
        } else if (binding.llUnread == view) {
            ((MainActivity) mContext).setMsgCurrent();
        } else if (binding.llFriend == view) {
            MineAttentionActivity.start(getContext(), MineAttentionActivity.MUT_INDEX);
        } else if (binding.llFollow == view) {
            MineAttentionActivity.start(getContext(), MineAttentionActivity.FOLLOW_INDEX);
        } else if (binding.llFans == view) {
            MineAttentionActivity.start(getContext(), MineAttentionActivity.FANS_INDEX);
        }else if (binding.rltMeMyLevel == view){
            LevelActivity.start(mContext);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void avatarUpdateEvent(AvatarModifiedEvent event) {
        GlideUtils.loadCircleImg(MeFragment.this, event.filePath, binding.ivMyPortrait);
    }

}
