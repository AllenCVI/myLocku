package com.lockulockme.locku.zlockfour.module.ui.fragment;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.AlphaInAnimation;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.requestbean.IndexRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheDetailsRequestBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.beans.responsebean.IndexUserResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SayHelloResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.eventbus.BlockEvent;
import com.lockulockme.locku.base.eventbus.SayHiEvent;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.databinding.FgHotBinding;
import com.lockulockme.locku.databinding.LayoutRechargeDiamondDialogBinding;
import com.lockulockme.locku.databinding.LayoutSayhiFiveChancesDialogBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfour.base.utils.EnoughManager;
import com.lockulockme.locku.zlockfour.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfour.base.utils.VipManager;
import com.lockulockme.locku.zlockfour.common.BaseFragment;
import com.lockulockme.locku.zlockfour.common.PopupWindowBuilder;
import com.lockulockme.locku.zlockfour.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.zlockfour.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.zlockfour.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.locku.zlockfour.module.ui.adapter.HotAdapter;
import com.lockulockme.lockuchat.aavg2.AVChatJump;
import com.lockulockme.lockuchat.common.SayHiUtils;
import com.lockulockme.lockuchat.ui.ChatActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class HotFragment extends BaseFragment<FgHotBinding> {

    private final int REFRESH_TYPE = 1;
    private final int LOADMORE_TYPE = 2;
    private int page = 1;
    private final int limit = 30;
    private HotAdapter mAdapter;
    private String countryStr = "";
    private boolean isFirst = true;

    public void updateCountryStr(String str) {
        countryStr = str;
        if (binding != null) {
            binding.refreshLayout.autoRefresh(isFirst ? 500 : 0);
            isFirst = false;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBlockEvent(BlockEvent event) {
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            for (IndexUserResponseBean datum : mAdapter.getData()) {
                if (datum.stringId.equals(event.userStringId)) {
                    mAdapter.remove(datum);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHiEvent(SayHiEvent event) {
        LogUtil.LogD("SayHiEvent", "HotFragment getHiEvent");
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (mAdapter.getData().get(i).stringId.equals(event.userStringId)) {
                    mAdapter.getItem(i).hello = true;
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgHotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        initAdapter();
        initListener();
        binding.layoutCommonEmpty.rltCommonEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.refreshLayout.autoRefresh();
                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.GONE);
            }
        });
        binding.layoutCommonNetworkError.rltCommonNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.refreshLayout.autoRefresh();
                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.GONE);
            }
        });
        binding.refreshLayout.autoRefresh();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    private void initListener() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadIndexData(page, limit, countryStr, LOADMORE_TYPE);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadIndexData(page, limit, countryStr, REFRESH_TYPE);
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SheDetailActivity.StartMe(mContext, mAdapter.getItem(position).stringId);
            }
        });
        mAdapter.addChildClickViewIds(R.id.iv_hi, R.id.iv_msg, R.id.iv_audio, R.id.iv_video);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_hi) {
                    userHello(mAdapter.getItem(position).stringId, position);
                } else if (view.getId() == R.id.iv_msg) {
                    ChatActivity.startMe(getActivity(), mAdapter.getItem(position));
                } else if (view.getId() == R.id.iv_audio) {
                    sendMsgFormType(EnoughManager.VOICE_TYPE, mAdapter.getItem(position).stringId, mAdapter.getItem(position));
                } else if (view.getId() == R.id.iv_video) {
                    sendMsgFormType(EnoughManager.VIDEO_TYPE, mAdapter.getItem(position).stringId, mAdapter.getItem(position));
                }
            }
        });
    }

    private void sendMsgFormType(String type, String id, IndexUserResponseBean userInfo) {
        VipManager.getInstance().getVipState(this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                if (vipResp.isVip) {
                    isEnough(type, id, userInfo);
                } else {
                    VipGoodsListActivity.go(mContext);
                }
            }

            @Override
            public void onVipFailed() {

            }
        });
    }

    private void isEnough(String type, String id, IndexUserResponseBean userInfo) {
        EnoughManager.getInstance().isEnough(type, id, this, new EnoughManager.EnoughListener() {
            @Override
            public void onEnoughSuc(EnoughResponseBean bean) {
                if (bean.enough) {
                    if (type == EnoughManager.VIDEO_TYPE) {
                        AVChatJump.goVideoChat((Activity) mContext, userInfo);
                    } else if (type == EnoughManager.VOICE_TYPE) {
                        AVChatJump.goAudioChat((Activity) mContext, userInfo);
                    }
                } else {
                    diamondIsNotEnoughIntercept();
                }
            }

            @Override
            public void onEnoughFailed() {
                ToastUtils.toastShow(R.string.diamond_is_or_not_sufficient_failed);
            }
        });
    }


    private void initAdapter() {
        binding.rvHot.setLayoutManager(new GridLayoutManager(mContext, 2));
        mAdapter = new HotAdapter(null);
        binding.rvHot.setAdapter(mAdapter);
        mAdapter.setAdapterAnimation(new AlphaInAnimation());

    }

    private void sendHi(String id, int position) {
        OkGoUtils.getInstance().userSayHello(this, new SheDetailsRequestBean(id), new NewJsonCallback<SayHelloResponseBean>() {
            @Override
            public void onSuc(SayHelloResponseBean response, String msg) {
                ToastUtils.toastShow(R.string.hello_success);
                EventBus.getDefault().post(new SayHiEvent(id));
                SayHiUtils.sendHiMsg(mAdapter.getItem(position).nimId, getString(R.string.hi), mAdapter.getItem(position).country, false, new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }

                    @Override
                    public void onFailed(int i) {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, SayHelloResponseBean response) {
                if (apiCode == 14) {
                    fiveChancesVipIntercept();
                } else if (apiCode == 27) {

                } else {
                    ToastUtils.toastShow(R.string.hello_failed);
                }
            }
        });
    }

    private void userHello(String id, int position) {
        VipManager.getInstance().getVipState(this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                sendHi(id, position);
            }

            @Override
            public void onVipFailed() {
                ToastUtils.toastShow(R.string.hello_failed);
            }
        });
    }


    private void loadIndexData(int pg, int limit, String country, int type) {
        VipManager.getInstance().getVipState(null, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                OkGoUtils.getInstance().getIndexList(this, new IndexRequestBean(pg, limit, country), new NewJsonCallback<List<IndexUserResponseBean>>() {
                    @Override
                    public void onSuc(List<IndexUserResponseBean> response, String msg) {
                        page++;
                        if (type == REFRESH_TYPE) {
                            binding.refreshLayout.finishRefresh();
                            mAdapter.setNewInstance(response);
                            if (mAdapter.getData() != null && mAdapter.getData().size() == 0) {
                                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.GONE);
                                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mAdapter.addData(response);
                            binding.refreshLayout.finishLoadMore();
                        }

                    }

                    @Override
                    public void onE(int httpCode, int apiCode, String msg, List<IndexUserResponseBean> response) {
                        super.onE(httpCode, apiCode, msg, response);
                        if (type == REFRESH_TYPE) {
                            binding.refreshLayout.finishRefresh(false);
                        } else {
                            binding.refreshLayout.finishLoadMore(false);
                        }
                        binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.VISIBLE);
                        binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onVipFailed() {
                if (type == REFRESH_TYPE) {
                    binding.refreshLayout.finishRefresh(false);
                } else {
                    binding.refreshLayout.finishLoadMore(false);
                }
                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.VISIBLE);
                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
            }
        });

    }

    public void fiveChancesVipIntercept() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(getActivity())
                .setContentView(R.layout.layout_sayhi_five_chances_dialog)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        final LayoutSayhiFiveChancesDialogBinding popRateBing = LayoutSayhiFiveChancesDialogBinding.bind(popBuilder.getRootView());
        popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
        popRateBing.tvRechargeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.tvRechargeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
                VipGoodsListActivity.go(getActivity());
            }
        });
    }

    public void diamondIsNotEnoughIntercept() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(getActivity())
                .setContentView(R.layout.layout_recharge_diamond_dialog)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        final LayoutRechargeDiamondDialogBinding popRateBing = LayoutRechargeDiamondDialogBinding.bind(popBuilder.getRootView());
        popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
        popRateBing.tvRechargeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.tvRechargeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
                CurrentDiamondActivity.go(getActivity());
            }
        });
    }
}
