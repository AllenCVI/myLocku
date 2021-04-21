package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.RatingTagRequestBean;
import com.lockulockme.locku.base.beans.requestbean.RatingUserRequestBean;
import com.lockulockme.locku.base.beans.responsebean.RateTagResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.UtilDpOrPx;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcRatingBinding;
import com.lockulockme.locku.module.ui.adapter.RatingStarAdapter;
import com.lockulockme.locku.module.ui.adapter.RatingTagAdapter;
import com.lockulockme.locku.module.ui.adapter.SpaceItemDecoration;
import com.lockulockme.locku.module.ui.adapter.TagManager;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends BaseActivity<AcRatingBinding> {


    private RatingStarAdapter mRateStarAdapter;
    private RatingTagAdapter mTagAdapter;
    private List<String> mRateStarList;
    private String mImId;
    private int mType;
    public static final String TYPE_KEY = "intent_key_rate_type";
    public static final String ID_KEY = "intent_key_id";
    private boolean mLoadUser = false;
    private boolean mLoadTag = false;
    private boolean mLoadRating = false;
    private User mUser;
    private List<RateTagResponseBean> mTagsList;

    public static void start(Context context, String imId, int type) {
        Intent intent = new Intent(context, RatingActivity.class);
        intent.putExtra(ID_KEY, imId);
        intent.putExtra(TYPE_KEY, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mType = getIntent().getIntExtra(TYPE_KEY, 1);
        mImId = getIntent().getStringExtra(ID_KEY);
        initView();
        loadData();
    }

    private void loadData() {
        binding.progress.setVisibility(View.VISIBLE);
        loadUser();
    }

    private void loadUser() {
        if (TextUtils.isEmpty(mImId)) {
            return;
        }
        if (mLoadUser) {
            return;
        }
        mLoadUser = true;
        List<String> userAccount = new ArrayList<>();
        userAccount.add(mImId);
        UserBeanCacheUtils.getInstance().getNetworkUsers(this.toString(), userAccount, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                mLoadUser = false;
                if (RatingActivity.this != null && !RatingActivity.this.isDestroyed()) {
                    if (userList != null && userList.size() > 0) {
                        mUser = userList.get(0);
                        initUserData(mUser);
                        loadRateTags();
                    } else {
                        hideProgressError();
                    }
                }
            }

            @Override
            public void onGetFailed() {
                mLoadUser = false;
                hideProgressError();
            }
        });
    }

    private void loadRateTags() {
        if (mLoadTag) {
            return;
        }
        mLoadTag = true;
        OkGoUtils.getInstance().getRateTags(this, new RatingTagRequestBean(mType), new NewJsonCallback<List<RateTagResponseBean>>() {
            @Override
            public void onSuc(List<RateTagResponseBean> response, String msg) {
                mLoadTag = false;
                if (response != null) {
                    hideProgress();
                    mTagsList = response;
                } else {
                    hideProgressError();
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<RateTagResponseBean> response) {
                super.onE(httpCode, apiCode, msg, response);
                mLoadTag = false;
                hideProgressError();
            }
        });
    }

    private void hideProgress() {
        binding.progress.setVisibility(View.GONE);
        binding.llContent.setVisibility(View.VISIBLE);
        binding.llNetwork.setVisibility(View.GONE);
    }

    private void hideProgressError() {
        binding.llNetwork.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
        binding.llContent.setVisibility(View.GONE);
    }

    private void initUserData(User userBean) {
        if (userBean == null)
            return;
        GlideUtils.loadCircleImg(mContext, userBean.userIcon, binding.ivAvatar, R.mipmap.icon_placeholder_user);
        binding.tvName.setText(userBean.nick);
    }


    private void initView() {
        mRateStarList = new ArrayList<>();
        mRateStarList.add("1");
        mRateStarList.add("1");
        mRateStarList.add("1");
        mRateStarList.add("1");
        mRateStarList.add("1");

        binding.rvStar.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        mRateStarAdapter = new RatingStarAdapter();
        binding.rvStar.setAdapter(mRateStarAdapter);
        mRateStarAdapter.setNewInstance(mRateStarList);

        binding.rvTag.setLayoutManager(new TagManager());
        binding.rvTag.addItemDecoration(new SpaceItemDecoration(UtilDpOrPx.dip2px(mContext, 8)));
        mTagAdapter = new RatingTagAdapter();
        binding.rvTag.setAdapter(mTagAdapter);

        mRateStarAdapter.setOnItemClickListener((adapter, view, position) -> {
            mRateStarAdapter.setSelectItem(position);
            if (mTagsList == null) return;
            int selectItem = mRateStarAdapter.getSelectItem() + 1;
            if (selectItem <= 3) {
                List<RateTagResponseBean> list = new ArrayList<>();
                for (RateTagResponseBean rateTagBean : mTagsList) {
                    if (!rateTagBean.positive) {
                        list.add(rateTagBean);
                    }
                }
                mTagAdapter.setNewInstance(list);
            } else if (selectItem > 3) {
                List<RateTagResponseBean> list = new ArrayList<>();
                for (RateTagResponseBean rateTagBean : mTagsList) {
                    if (rateTagBean.positive) {
                        list.add(rateTagBean);
                    }
                }
                mTagAdapter.setNewInstance(list);
            }
        });

        mTagAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                checkItem(position);
            }
        });

        binding.tvSave.setOnClickListener(v -> {
            SaveToRate();
        });

        binding.llNetwork.setOnClickListener(v -> {
            loadData();
        });

    }

    private void SaveToRate() {
        if (mLoadRating) {
            return;
        }
        if (mRateStarAdapter.getSelectItem() == -1) {
            ToastUtils.toastShow(R.string.select_rate_level);
            return;
        }
        if (!isCheckLabel()) {
            ToastUtils.toastShow(R.string.select_rate_tag);
            return;
        }
        mLoadRating = true;
        binding.progress.setVisibility(View.VISIBLE);
        int i = mRateStarAdapter.getSelectItem() + 1;
        OkGoUtils.getInstance().ratingUser(RatingActivity.this, new RatingUserRequestBean(mUser.stringId, i, "", getTags()), new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                mLoadRating = false;
                binding.progress.setVisibility(View.GONE);
                ToastUtils.toastShow(R.string.eva_suc);
                finish();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                super.onE(httpCode, apiCode, msg, response);
                mLoadRating = false;
                binding.progress.setVisibility(View.GONE);
                ToastUtils.toastShow(R.string.eva_error);
            }
        });
    }

    private boolean isCheckLabel() {
        if (mTagAdapter.getData() != null && mTagAdapter.getData() != null) {
            for (RateTagResponseBean datum : mTagAdapter.getData()) {
                if (datum.check) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getNumber() {
        if (mTagAdapter == null || mTagAdapter.getData() == null) {
            return false;
        }
        int i = 0;
        for (RateTagResponseBean datum : mTagAdapter.getData()) {
            if (datum.check) {
                i += 1;
            }
        }
        return i < 3;
    }

    private void checkItem(int position) {
        if (mTagAdapter == null || mTagAdapter.getData() == null || position >= mTagAdapter.getData().size()) {
            return;
        }
        if (!mTagAdapter.getItem(position).check) {
            if (getNumber()) {
                mTagAdapter.getItem(position).check = !mTagAdapter.getItem(position).check;
                mTagAdapter.notifyDataSetChanged();
            } else {
                ToastUtils.toastShow(R.string.choose_max_three);
            }
        } else {
            mTagAdapter.getItem(position).check = !mTagAdapter.getItem(position).check;
            mTagAdapter.notifyDataSetChanged();
        }
    }


    private List<Integer> getTags() {
        ArrayList<Integer> list = new ArrayList<>();
        if (mTagAdapter != null && mTagAdapter.getData() != null) {
            for (RateTagResponseBean datum : mTagAdapter.getData()) {
                if (datum.check) {
                    list.add(datum.id);
                }
            }
        }
        return list;
    }


}
