package com.lockulockme.locku.module.ui.fragment;

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
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.AttentionListRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheDetailsRequestBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SayHelloResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.eventbus.AttentionEvent;
import com.lockulockme.locku.base.eventbus.SayHiEvent;
import com.lockulockme.locku.base.utils.EnoughManager;
import com.lockulockme.locku.base.utils.OkGoUtils;
import com.lockulockme.locku.base.utils.VipManager;
import com.lockulockme.locku.common.BaseFragment;
import com.lockulockme.locku.common.PopupWindowBuilder;
import com.lockulockme.locku.databinding.EmptyAttentionBinding;
import com.lockulockme.locku.databinding.FgAttentionBinding;
import com.lockulockme.locku.databinding.LayoutRechargeDiamondDialogBinding;
import com.lockulockme.locku.databinding.LayoutSayhiFiveChancesDialogBinding;
import com.lockulockme.locku.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.locku.module.ui.adapter.AttentionAdapter;
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

public class AttentionFragment extends BaseFragment<FgAttentionBinding> implements OnRefreshLoadMoreListener {

    private int page = 1;
    private final int size = 30;
    private AttentionAdapter mAdapter;
    private final int REFRESH_TYPE = 1;
    private final int LOADMORE_TYPE = 2;


    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgAttentionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHiEvent(SayHiEvent event) {
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (mAdapter.getData().get(i).stringId.equals(event.userStringId)) {
                    mAdapter.getItem(i).hello = true;
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAttentionEvent(AttentionEvent event) {
        page = 1;
        loadFollow(page, size, REFRESH_TYPE);
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        binding.rvAttention.setLayoutManager(new GridLayoutManager(mContext, 2));
        mAdapter = new AttentionAdapter();
        binding.rvAttention.setAdapter(mAdapter);
        View empty = View.inflate(mContext, R.layout.empty_attention, null);
        EmptyAttentionBinding bind = EmptyAttentionBinding.bind(empty);
        bind.tvTitle.setText(R.string.no_follow);
        mAdapter.setEmptyView(empty);
        mAdapter.setAdapterAnimation(new AlphaInAnimation());
        binding.refreshLayout.setOnRefreshLoadMoreListener(this);
        binding.refreshLayout.autoRefresh();
        binding.llNetwork.rltCommonNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.refreshLayout.autoRefresh();
                binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
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


    private void sendMsgFormType(String type, String id, UserInfo userInfo) {
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

    private void isEnough(String type, String id, UserInfo userInfo) {
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

    private void loadFollow(int pg, int limit, int type) {
        OkGoUtils.getInstance().getAttentionList(this, new AttentionListRequestBean(pg, limit, MineAttentionFragment.ATTENTION_TYPE), new NewJsonCallback<List<UserInfo>>() {
            @Override
            public void onSuc(List<UserInfo> response, String msg) {
                if (response != null && response.size() > 0) {
                    page++;
                }

                if (type == REFRESH_TYPE) {
                    binding.refreshLayout.finishRefresh();
                    mAdapter.setNewInstance(response);
                    binding.llNetwork.rltCommonNetworkError.setVisibility(View.GONE);
                } else {
                    mAdapter.addData(response);
                    binding.refreshLayout.finishLoadMore();
                }
                binding.rvAttention.setVisibility(View.VISIBLE);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<UserInfo> response) {
                super.onE(httpCode, apiCode, msg, response);
                if (type == REFRESH_TYPE) {
                    binding.refreshLayout.finishRefresh(false);
                    binding.rvAttention.setVisibility(View.GONE);
                    binding.llNetwork.rltCommonNetworkError.setVisibility(View.VISIBLE);
                } else {
                    binding.refreshLayout.finishLoadMore(false);
                }
            }

        });
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadFollow(page, size, LOADMORE_TYPE);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        loadFollow(page, size, REFRESH_TYPE);
    }
}
