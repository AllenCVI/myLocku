package com.lockulockme.locku.zlocknine.module.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.eventbus.BlockEvent;
import com.lockulockme.locku.databinding.FgMsgListBinding;
import com.lockulockme.locku.zlocknine.base.utils.AccountManager;
import com.lockulockme.locku.zlocknine.common.BaseFragment;
import com.lockulockme.locku.zlocknine.common.LoginoutManager;
import com.lockulockme.lockuchat.ui.RecentUsersFragment;
import com.lockulockme.lockuchat.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MsgListFragment extends BaseFragment<FgMsgListBinding> {

    private RecentUsersFragment fragment;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBlockEvent(BlockEvent event) {
        if (fragment == null)
            return;
        fragment.sendReportAndBlockEvent(event.userStringId);
    }

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgMsgListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addRecentUsersFragment();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    private void addRecentUsersFragment() {
        if (fragment == null){
            fragment = new RecentUsersFragment();
            fragment.setCallBack(new RecentUsersFragment.RecentUserCallBack() {
                @Override
                public void onBlock(com.lockulockme.lockuchat.bean.BlockEvent blockEvent) {
                    EventBus.getDefault().post(new BlockEvent(blockEvent.userStringId));
                }

                @Override
                public void onLoginout() {
                    LogHelper.e("observeHangUpEvent","addRecentUsersFragment------");
                    AccountManager.getInstance().putPwd("");
                    LoginoutManager.loginout(getActivity());
                }
            });
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_msg_container, fragment).commit();

    }

}
