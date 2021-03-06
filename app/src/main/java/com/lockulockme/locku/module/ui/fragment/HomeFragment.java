package com.lockulockme.locku.module.ui.fragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.responsebean.CountryResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseFragment;
import com.lockulockme.locku.databinding.FgHomeBinding;
import com.lockulockme.locku.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.locku.module.ui.adapter.ContentNormalPagerAdapter;
import com.lockulockme.locku.module.ui.adapter.CountryAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment<FgHomeBinding> {

    private CountryAdapter mCountryAdapter;
    private final String ALL_STR = "ALL";
    private final String ALL_VALUE = "";
    private String countryStr;
    private List<Fragment> fragments;
    private final int HOT_TYPE = 1;
    private final int SHOW_TYPE = 2;
    private final int FOLLOW_TYPE = 3;
    private final int[] types = {HOT_TYPE, SHOW_TYPE, FOLLOW_TYPE};
    private ShowFragment showFragment;

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        initViewPager();
        initAdapter();
        initDrawerLayout();
        initListener();
        loadCountry();
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(v -> {
            loadCountry();
        });
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();
        if (fragments != null && binding.viewpager != null) {
            if (binding.viewpager.getCurrentItem() == 1) {
                showFragment.onFragmentHide();
            }
        }
    }


    @Override
    public void onFragmentShow() {
        super.onFragmentShow();
        if (mCountryAdapter != null && mCountryAdapter.getData().size() == 0) {
            loadCountry();
        }
        if (fragments != null && binding.viewpager != null) {
            if (binding.viewpager.getCurrentItem() == 1) {
                showFragment.onFragmentShow();
            }
        }
    }


    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new HotFragment());
        showFragment = new ShowFragment();
        fragments.add(showFragment);
        fragments.add(new AttentionFragment());
        binding.viewpager.setAdapter(new ContentNormalPagerAdapter(getChildFragmentManager(), fragments, null));
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectViewpager(types[position]);
                selectFragmentOnPauseOrResume(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.viewpager.setOffscreenPageLimit(2);
    }

    private void selectFragmentOnPauseOrResume(int position) {
        if (types[position] == HOT_TYPE || types[position] == FOLLOW_TYPE) {
            showFragment.onFragmentHide();
        } else {
            showFragment.onFragmentShow();
        }
    }

    private void selectText(int type) {
        if (type == HOT_TYPE) {
            binding.tvHot.setTextSize(23);
            binding.tvShow.setTextSize(20);
            binding.tvFollow.setTextSize(20);
            binding.tvHot.setTextColor(Color.WHITE);
            binding.tvShow.setTextColor(Color.parseColor("#80ffffff"));
            binding.tvFollow.setTextColor(Color.parseColor("#80ffffff"));
        } else if (type == SHOW_TYPE) {
            binding.tvHot.setTextSize(20);
            binding.tvFollow.setTextSize(20);
            binding.tvShow.setTextSize(23);
            binding.tvHot.setTextColor(Color.parseColor("#80ffffff"));
            binding.tvFollow.setTextColor(Color.parseColor("#80ffffff"));
            binding.tvShow.setTextColor(Color.WHITE);
        } else if (type == FOLLOW_TYPE) {
            binding.tvHot.setTextSize(20);
            binding.tvShow.setTextSize(20);
            binding.tvFollow.setTextSize(23);
            binding.tvHot.setTextColor(Color.parseColor("#80ffffff"));
            binding.tvShow.setTextColor(Color.parseColor("#80ffffff"));
            binding.tvFollow.setTextColor(Color.WHITE);
        }
    }

    private void initAdapter() {
        binding.rvCountry.setLayoutManager(new LinearLayoutManager(mContext));
        mCountryAdapter = new CountryAdapter(null);
        binding.rvCountry.setAdapter(mCountryAdapter);
    }


    private void initListener() {
        binding.ivFilter.setOnClickListener(v -> {
            openDrawer();
        });

        mCountryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                openDrawer();
                VipManager.getInstance().getVipState(HomeFragment.this, new VipManager.OnVipListener() {
                    @Override
                    public void onVipSuccess(VipResponseBean vipResp) {
                        countrySelectItem(vipResp.isVip, position);
                    }

                    @Override
                    public void onVipFailed() {
                        countrySelectItem(false, position);
                    }
                });

            }
        });

        binding.tvHot.setOnClickListener(v -> {
            setSelectViewpager(HOT_TYPE);
        });
        binding.tvShow.setOnClickListener(v -> {
            setSelectViewpager(SHOW_TYPE);
        });
        binding.tvFollow.setOnClickListener(v -> {
            setSelectViewpager(FOLLOW_TYPE);
        });
    }

    public void initDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), binding.drawerlayout, null, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        binding.drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.drawerlayout.addDrawerListener(drawerToggle);
        binding.rightLayout.setOnClickListener(v -> {

        });
        drawerToggle.syncState();
    }

    private void setSelectViewpager(int type) {
        if (type == HOT_TYPE) {
            binding.viewpager.setCurrentItem(0);
            binding.ivFilter.setVisibility(View.VISIBLE);
        } else if (type == SHOW_TYPE) {
            binding.ivFilter.setVisibility(View.INVISIBLE);
            binding.viewpager.setCurrentItem(1);
        } else if (type == FOLLOW_TYPE) {
            binding.ivFilter.setVisibility(View.INVISIBLE);
            binding.viewpager.setCurrentItem(2);
        }
        selectText(type);
    }

    public void openDrawer() {
        if (binding.drawerlayout.isDrawerOpen(binding.rightLayout)) {
            binding.drawerlayout.closeDrawer(binding.rightLayout);
        } else {
            binding.drawerlayout.openDrawer(binding.rightLayout);
        }
    }


    private void loadCountry() {
        showLoading();
        OkGoUtils.getInstance().getCountry(HomeFragment.this, new NewJsonCallback<List<CountryResponseBean>>() {
            @Override
            public void onSuc(List<CountryResponseBean> response, String msg) {
                hideLoading();
                convertVipData(response);
                binding.llContent.setVisibility(View.VISIBLE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<CountryResponseBean> response) {
                super.onE(httpCode, apiCode, msg, response);
                hideLoading();
                binding.llContent.setVisibility(View.GONE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void convertVipData(List<CountryResponseBean> response) {
        CountryResponseBean bean = new CountryResponseBean();
        bean.type = CountryResponseBean.ALL_TYPE;
        bean.label = getString(R.string.all);
        bean.valueStr = ALL_VALUE;
        VipManager.getInstance().getVipState(HomeFragment.this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                checkFragmentStr(vipResp.isVip, bean, response);
                binding.llContent.setVisibility(View.VISIBLE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
            }

            @Override
            public void onVipFailed() {
                binding.llContent.setVisibility(View.GONE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });

    }

    private void checkFragmentStr(boolean isVip, CountryResponseBean bean, List<CountryResponseBean> response) {
        response.add(0, bean);
        mCountryAdapter.setNewInstance(response);
        if (isVip) {
            mCountryAdapter.setCheckStr(ALL_VALUE);
            countryStr = mCountryAdapter.getCheckStr();
            updateFragmentCountry(0, countryStr);
        } else {
            for (CountryResponseBean countryResponseBean : response) {
                if (countryResponseBean.valueStr.equals(AccountManager.getInstance().getCurrentUser().country)) {
                    mCountryAdapter.setCheckStr(countryResponseBean.valueStr);
                    break;
                }
            }
            countryStr = mCountryAdapter.getCheckStr();
            updateFragmentCountry(0, countryStr);
        }

    }

    private void checkVipStr(boolean isVip) {
        if (mCountryAdapter == null || mCountryAdapter.getData().size() == 0)
            return;

        if (isVip) {
            mCountryAdapter.setCheckStr(ALL_VALUE);
            countryStr = mCountryAdapter.getCheckStr();
            updateFragmentCountry(0, countryStr);
        } else {
            for (CountryResponseBean countryResponseBean : mCountryAdapter.getData()) {
                if (countryResponseBean.valueStr.equals(AccountManager.getInstance().getCurrentUser().country)) {
                    mCountryAdapter.setCheckStr(countryResponseBean.valueStr);
                    break;
                }
            }
            countryStr = mCountryAdapter.getCheckStr();
            updateFragmentCountry(0, countryStr);
        }
    }

    private void countrySelectItem(boolean isVip, int position) {
        if (isVip) {
            mCountryAdapter.setCheckStr(mCountryAdapter.getItem(position).valueStr);
            countryStr = mCountryAdapter.getCheckStr();
            updateFragmentCountry(binding.viewpager.getCurrentItem(), countryStr);
        } else {
            if (!AccountManager.getInstance().getCurrentUser().country.equals(mCountryAdapter.getItem(position).valueStr)) {
                VipGoodsListActivity.go(getContext());
                return;
            } else {
                mCountryAdapter.setCheckStr(mCountryAdapter.getItem(position).valueStr);
                countryStr = mCountryAdapter.getCheckStr();
                updateFragmentCountry(binding.viewpager.getCurrentItem(), countryStr);
            }
        }

    }

    private void updateFragmentCountry(int position, String str) {
        if (types[position] == HOT_TYPE) {
            HotFragment fragment = (HotFragment) fragments.get(position);
            fragment.updateCountryStr(str);
        } else if (types[position] == SHOW_TYPE) {
            ShowFragment fragment = (ShowFragment) fragments.get(position);
            fragment.updateCountryStr(str);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVipRecharge(VipStatusRefreshEvent event) {
        VipManager.getInstance().getVipState(HomeFragment.this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                binding.llContent.setVisibility(View.VISIBLE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                checkVipStr(vipResp.isVip);
            }

            @Override
            public void onVipFailed() {
                binding.llContent.setVisibility(View.GONE);
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });
    }

}
