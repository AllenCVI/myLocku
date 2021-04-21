package com.lockulockme.locku.module.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.AttentionListRequestBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.AttentionEvent;
import com.lockulockme.locku.base.utils.AttentionHelper;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.BaseFragment;
import com.lockulockme.locku.databinding.EmptyAttentionBinding;
import com.lockulockme.locku.databinding.FgMineAttentionBinding;
import com.lockulockme.locku.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.module.ui.adapter.MineAttentionAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MineAttentionFragment extends BaseFragment<FgMineAttentionBinding> implements Filterable {

    public static MineAttentionFragment getInstance(String type) {
        MineAttentionFragment followFragment = new MineAttentionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_KEY, type);
        followFragment.setArguments(bundle);
        return followFragment;
    }

    private String mType;
    static final String TYPE_KEY = "type_key";
    private MineAttentionAdapter mAdapter;
    public static final String FRIEND_TYPE = "friends";
    public static final String ATTENTION_TYPE = "followers";
    public static final String FANS_TYPE = "fans";
    private AttentionFilter mFilter;
    private List<UserInfo> mUserList;

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgMineAttentionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAttentionEvent(AttentionEvent event) {
        if (mAdapter == null || mUserList == null) {
            return;
        }
        for (UserInfo item : mUserList) {
            if (event.userStringId.equals(item.stringId)) {
                item.followed = event.eventType == AttentionEvent.ATTENTION_TYPE;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        initViews();
    }

    private void initViews() {
        mType = getArguments().getString(TYPE_KEY);
        mFilter = new AttentionFilter();
        binding.llSearch.setVisibility(mType.equals(FANS_TYPE) ? View.GONE : View.VISIBLE);
        binding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MineAttentionAdapter();
        binding.rv.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.tv_follow, R.id.tv_unfollow);
        View empty = View.inflate(mContext, R.layout.empty_attention, null);
        EmptyAttentionBinding bind = EmptyAttentionBinding.bind(empty);
        bind.tvTitle.setText(getTypeEmptyStr(mType));
        mAdapter.setEmptyView(empty);
        initListener();
        loadAttentionData();
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tv_follow) {
                attentionUser(mAdapter.getItem(position));
            } else if (view.getId() == R.id.tv_unfollow) {
                unAttentionUser(mAdapter.getItem(position));
            }
        });

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            SheDetailActivity.StartMe(getActivity(), mAdapter.getItem(position).stringId);
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFilter.setOnFilterResult(new AttentionFilter.onFilterResult() {
            @Override
            public void onResult(List<UserInfo> list) {
                mAdapter.setNewInstance(list);
            }
        });
        binding.llNetwork.getRoot().setOnClickListener(v -> {
            loadAttentionData();
        });
    }

    private void unAttentionUser(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        showLoading();
        AttentionHelper.getInstance().unAttentionUser(this, userInfo.stringId, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                userInfo.followed = false;
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                ToastUtils.toastShow(R.string.unfollow_suc);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                super.onE(httpCode, apiCode, msg, response);
                hideLoading();
                ToastUtils.toastShow(R.string.unfollow_error);
            }
        });
    }

    private void attentionUser(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        showLoading();
        AttentionHelper.getInstance().attentionUser(this, userInfo.stringId, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                userInfo.followed = true;
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                ToastUtils.toastShow(R.string.follow_suc);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                super.onE(httpCode, apiCode, msg, response);
                hideLoading();
                ToastUtils.toastShow(R.string.follow_error);
            }
        });
    }

    private int getTypeEmptyStr(String type) {
        if (FRIEND_TYPE.equals(type)) {
            return R.string.no_friends;
        } else if (ATTENTION_TYPE.equals(type)) {
            return R.string.no_follow;
        } else if (FANS_TYPE.equals(type)) {
            return R.string.no_fans;
        }
        return R.string.no_follow;
    }


    private void loadAttentionData() {
        binding.llLoad.getRoot().setVisibility(View.VISIBLE);
        AttentionListRequestBean bean = new AttentionListRequestBean(1, Integer.MAX_VALUE, mType);
        OkGoUtils.getInstance().getAttentionList(this, bean, new NewJsonCallback<List<UserInfo>>() {
            @Override
            public void onSuc(List<UserInfo> response, String msg) {
                binding.llLoad.getRoot().setVisibility(View.GONE);
                binding.llNetwork.getRoot().setVisibility(View.GONE);
                binding.rv.setVisibility(View.VISIBLE);
                if (response == null) {
                    return;
                }
                mUserList = response;
                mAdapter.setNewInstance(mUserList);
                mFilter.setList(mUserList);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<UserInfo> response) {
                super.onE(httpCode, apiCode, msg, response);
                binding.llLoad.getRoot().setVisibility(View.GONE);
                binding.llNetwork.getRoot().setVisibility(View.VISIBLE);
                binding.rv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

}
