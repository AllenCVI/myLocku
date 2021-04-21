package com.lockulockme.locku.zlockfive.common;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.base.utils.ProgressDialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Description: BaseFragment
 */
public abstract class BaseFragment<BD extends ViewBinding> extends Fragment {

    private final String TAG = getClass().getSimpleName();
    protected Context mContext;
    protected View rootView;
    private boolean isViewPrepared; // 标识fragment视图已经初始化完毕
    private boolean hasFetchData; // 标识已经触发过懒加载数据
    private boolean isLoad = false;

    private boolean isShowing = false; //为添加onFragmentHide、onFragmentShow的控制变量，代表fragment是否正在显示
    protected boolean visibleToUser = true; //为添加onFragmentHide、onFragmentShow的控制变量
    private boolean needShow = false; //为添加onFragmentHide、onFragmentShow的控制变量
    private String tag = ""; //为添加onFragmentHide、onFragmentShow的控制变量
    public BD binding;

    private ProgressDialogUtils progressDialogUtils;


    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        LogUtil.LogE(TAG, "------>onAttach");
        if (mContext != null) {
            this.mContext = mContext;
        } else {
            this.mContext = getActivity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.LogE(TAG, "------>onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.LogE(TAG, "------>onCreateView");
        ViewGroup parent = (ViewGroup) getView();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        if (rootView == null) {
            rootView = getRootView(inflater,container);
        }
        initView(inflater);
        progressDialogUtils = new ProgressDialogUtils(mContext);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.LogE(TAG, "------>onActivityCreated");
        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.LogE(TAG, "------>onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.LogE(TAG, "------>onResume");
        if (!isShowing && visibleToUser) {
            if (isViewPrepared) {
                isShowing = true;
                onFragmentShow();
            } else {
                needShow = true;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.LogE(TAG, "------>onViewCreated");
        isViewPrepared = true;
        lazyFetchDataIfPrepared();
        if (needShow) {
            isShowing = true;
            onFragmentShow();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.LogE(TAG, "------>onPause");
        if (isShowing && visibleToUser) {
            isShowing = false;
            onFragmentHide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.LogE(TAG, "------>onStop");
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
        LogUtil.LogE(TAG, "------>onDestroyView okgo取消请求" + this.getClass().getSimpleName());
        // view被销毁后，将可以重新触发数据懒加载，因为在viewpager下，fragment不会再次新建并走onCreate的生命周期流程，将从onCreateView开始
        hasFetchData = false;
        isViewPrepared = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.LogE(TAG, "------>onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.LogE(TAG, "------>onDetach");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.v(TAG, getClass().getName() + "------>isVisibleToUser = " + isVisibleToUser);
        if (isVisibleToUser) {
            lazyFetchDataIfPrepared();
        }
        visibleToUser = isVisibleToUser;
        if (visibleToUser) {
            if (!isShowing) {
                if (isViewPrepared) {
                    isShowing = true;
                    onFragmentShow();
                } else {
                    needShow = true;
                }
            }
        } else {
            //fragmentHasShow防止 viewpager缓存机制引起的 onFragmentHide 被回调多次
            if (isShowing) {
                isShowing = false;
                onFragmentHide();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            visibleToUser = false;
            if (isShowing) {
                isShowing = false;
                onFragmentHide();
                if (getChildFragmentManager().getFragments().size() > 0) {
                    for (Fragment fragment : getAllChildFragment()) {
                        if (fragment instanceof BaseFragment) {
                            if (((BaseFragment) fragment).visibleToUser) {
                                ((BaseFragment) fragment).isShowing = false;
                                ((BaseFragment) fragment).visibleToUser = false;
                                ((BaseFragment) fragment).setTargetTag("targetChild");
                                ((BaseFragment) fragment).onFragmentHide();
                            }
                        }
                    }
                }
            }
        } else {
            visibleToUser = true;
            if (!isShowing) {
                isShowing = true;
                onFragmentShow();
                if (getChildFragmentManager().getFragments().size() > 0) {
                    for (Fragment fragment : getAllChildFragment()) {
                        if (fragment instanceof BaseFragment) {
                            if (((BaseFragment) fragment).getTargetTag().equals("targetChild")) {
                                ((BaseFragment) fragment).isShowing = true;
                                ((BaseFragment) fragment).visibleToUser = true;
                                ((BaseFragment) fragment).onFragmentShow();
                                ((BaseFragment) fragment).setTargetTag("");
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Fragment> getAllChildFragment() {
        List<Fragment> result = new ArrayList<>();
        collectFragments(this, result);
        return result;
    }

    private void collectFragments(Fragment fragment, List<Fragment> result) {
        if (fragment.getChildFragmentManager().getFragments().size() == 0) {
            return;
        }

        for (Fragment item : fragment.getChildFragmentManager().getFragments()) {
            result.add(item);
            collectFragments(item, result);
        }
    }

    private void setTargetTag(String tag) {
        this.tag = tag;
    }

    private String getTargetTag() {
        return this.tag;
    }

    private void lazyFetchDataIfPrepared() {
        // 用户可见fragment && 没有加载过数据 && 视图已经准备完毕
        if (getUserVisibleHint() && !hasFetchData && isViewPrepared && !isLoad) {
            hasFetchData = true;
            lazyFetchData();
            isLoad = true;
        }

    }

    /**
     * 懒加载的方式获取数据，仅在满足fragment可见和视图已经准备好的时候调用一次
     */
    protected void lazyFetchData() {
        Log.v(TAG, getClass().getName() + "------>lazyFetchData");
    }

    public void onFragmentHide() {
        Log.v(TAG, getClass().getName() + "------>onFragmentHide");
    }

    public void onFragmentShow() {
        Log.v(TAG, getClass().getName() + "------>onFragmentShow");
    }

    public String getName() {
        return BaseFragment.class.getName();
    }

    protected abstract View getRootView(LayoutInflater inflater, ViewGroup container);

    protected void initView(LayoutInflater inflater) {
    }

    protected void initEvent() {
    }


    public void showLoading() {
        if (progressDialogUtils != null) {
            progressDialogUtils.showCancelDialog();
        }
    }

    public void hideLoading() {
        if (progressDialogUtils != null) {
            progressDialogUtils.dismissDialog();
        }
    }

}
