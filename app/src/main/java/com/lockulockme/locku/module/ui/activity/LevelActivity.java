package com.lockulockme.locku.module.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.responsebean.LevelConfig;
import com.lockulockme.locku.base.beans.responsebean.MyLevelRe;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.GlideUtils;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.common.BaseActivity;
import com.lockulockme.locku.databinding.AcMyLevelBinding;
import com.lockulockme.locku.module.ui.adapter.LevelAdapter;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class LevelActivity extends BaseActivity<AcMyLevelBinding> {

    private LevelAdapter mLevelAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, LevelActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcMyLevelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initData();
    }


    private void initView() {
        setNavTitle(getString(R.string.my_level));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.rvLevelTable.setLayoutManager(linearLayoutManager);
        mLevelAdapter = new LevelAdapter();
        binding.rvLevelTable.setAdapter(mLevelAdapter);

    }

    private void initData() {
        getMyLevelData();
    }

    private void getMyLevelData() {
        showLoading();
        OkGoUtils.getInstance().getMyLevel(LevelActivity.this, new NewJsonCallback<MyLevelRe>() {
            @Override
            public void onSuc(MyLevelRe response, String msg) {
                hideLoading();
                if (response==null){
                    return;
                }
                fillLevelData(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, MyLevelRe response) {
                hideLoading();
                ToastUtils.toastShow(R.string.network_not_available);
            }
        });
    }

    private void fillLevelData(MyLevelRe response) {
        binding.flAvatar.setVisibility(View.VISIBLE);
        binding.ivLevel.setVisibility(View.VISIBLE);
        binding.arcProgress.setVisibility(View.VISIBLE);

        UserInfo userBean = AccountManager.getInstance().getCurrentUser();
        if (userBean!=null) {
            if ("1".equals(userBean.userGender)) {
                GlideUtils.loadCircleImg(this, userBean.smallAvatar, binding.ivAvatar, R.mipmap.ic_male_portrait_placeholder);
            } else {
                GlideUtils.loadCircleImg(this, userBean.smallAvatar, binding.ivAvatar, R.mipmap.ic_female_portrait_placeholder);
            }
        }

        binding.arcProgress.setProgress(response.mLevelPercentage);
        binding.tvUpLevel.setText(getResources().getString(R.string.Lv)+response.mLevel);
        binding.tvNextLevel.setText(getResources().getString(R.string.Lv)+(response.mLevel+1));

        GlideUtils.loadCircleImg(this,response.mLevelIcon,binding.ivLevel,R.mipmap.level_icon_place_holder);
        if (response.mLevelTab==null||response.mLevelTab.size()==0){
            return;
        }
        response.mLevelTab.add(0,new LevelConfig());
        mLevelAdapter.setNewInstance(response.mLevelTab);
    }

    @Override
    public void showLoading() {
        binding.rlProgress.rltProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.rlProgress.rltProgress.setVisibility(View.GONE);
    }


}
