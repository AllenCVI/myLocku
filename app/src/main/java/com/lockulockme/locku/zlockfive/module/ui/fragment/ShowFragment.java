package com.lockulockme.locku.zlockfive.module.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.DislikeVideoRequestBean;
import com.lockulockme.locku.base.beans.requestbean.PageRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ReportRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheDetailsRequestBean;
import com.lockulockme.locku.base.beans.responsebean.AudioAndVideoPriceResponseBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SayHelloResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VideoResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.eventbus.BlockEvent;
import com.lockulockme.locku.base.eventbus.ReportEvent;
import com.lockulockme.locku.base.eventbus.SayHiEvent;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.databinding.FgShowBinding;
import com.lockulockme.locku.databinding.ItemVideoBinding;
import com.lockulockme.locku.databinding.LayoutRechargeDiamondDialogBinding;
import com.lockulockme.locku.databinding.LayoutSayhiFiveChancesDialogBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlockfive.base.utils.EnoughManager;
import com.lockulockme.locku.zlockfive.base.utils.GlideUtils;
import com.lockulockme.locku.zlockfive.base.utils.OkGoUtils;
import com.lockulockme.locku.zlockfive.base.utils.VipManager;
import com.lockulockme.locku.zlockfive.common.BaseFragment;
import com.lockulockme.locku.zlockfive.common.PopupWindowBuilder;
import com.lockulockme.locku.zlockfive.module.custom.MyJzvdStd;
import com.lockulockme.locku.zlockfive.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.zlockfive.module.ui.activity.ReportActivity;
import com.lockulockme.locku.zlockfive.module.ui.activity.SheDetailActivity;
import com.lockulockme.locku.zlockfive.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.locku.zlockfive.module.ui.adapter.ViewPagerLayoutManager;
import com.lockulockme.lockuchat.aavg2.AVChatJump;
import com.lockulockme.lockuchat.common.SayHiUtils;
import com.lockulockme.lockuchat.ui.ChatActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.jzvd.Jzvd;

public class ShowFragment extends BaseFragment<FgShowBinding> {

    private String countryStr;
    private int page = 1;
    private final int size = 15;
    private final int REFRESH_TYPE = 1;
    private final int LOAD_TYPE = 2;
    private int mCurrentPosition = 0;
    private ViewPagerLayoutManager mViewPagerLayoutManager;
    private VideoAdapter videoAdapter;
    private final String TAG = "ShowFragment";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final boolean isFirst = true;

    public void updateCountryStr(String str) {
        countryStr = str;
        binding.refreshLayout.autoRefresh(isFirst ? 500 : 0);
    }

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container) {
        binding = FgShowBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        EventBus.getDefault().register(this);
    }

    /**
     * 举报通知 举报和拉黑两个event 举报不需要删除数据
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReportEvent(ReportEvent event) {
        if (event.type == ReportEvent.SHOW_TYPE) {
            binding.viewpager.setUserInputEnabled(false);
            binding.viewpager.beginFakeDrag();
            binding.viewpager.fakeDragBy(-getScreenHeight(mContext) / 3);
            binding.viewpager.endFakeDrag();
            binding.viewpager.setUserInputEnabled(true);
        }

    }

    /**
     * 接受其他页面过来的拉黑通知 此页面没有拉黑
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBlockEvent(BlockEvent event) {
        if (videoAdapter != null && videoAdapter.getData().size() > 0) {
            for (VideoResponseBean datum : videoAdapter.getData()) {
                if (datum.user.stringId.equals(event.userStringId)) {
                    videoAdapter.remove(datum);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHiEvent(SayHiEvent event) {
        LogUtil.LogD("SayHiEvent", "ShowFragment getHiEvent");
        if (videoAdapter != null && videoAdapter.getData().size() > 0) {
            for (int i = 0; i < videoAdapter.getData().size() - 1; i++) {
                if (videoAdapter.getData().get(i).user.stringId.equals(event.userStringId)) {
                    videoAdapter.getItem(i).user.hello = true;
                    videoAdapter.getViewByPosition(i, R.id.iv_hi).setVisibility(View.GONE);
                    videoAdapter.getViewByPosition(i, R.id.iv_msg).setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void deleteVideo(int videoId) {
        Log.d("ShowFragment", "deleteVideo: videoId:" + videoId);
        int size = videoAdapter.getData().size();
        for (int i = 0; i < size - 1; i++) {
            if (videoAdapter.getData().get(i).videoId == videoId) {
                int finalI = i;
                videoAdapter.getData().remove(videoAdapter.getItem(finalI));
                videoAdapter.notifyItemRemoved(finalI);
                binding.viewpager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoPlayVideo(binding.viewpager.getCurrentItem());
                    }
                }, 50);
                break;
            }
        }
    }

    public int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }


    @Override
    public void onFragmentShow() {
        super.onFragmentShow();
        Jzvd.goOnPlayOnResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        Jzvd.goOnPlayOnPause();
        if (handler.hasCallbacks(playFirstRunnable))
            handler.removeCallbacks(playFirstRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        Jzvd.goOnPlayOnPause();
        if (handler.hasCallbacks(playFirstRunnable))
            handler.removeCallbacks(playFirstRunnable);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();
        Jzvd.goOnPlayOnPause();
        if (handler.hasCallbacks(playFirstRunnable))
            handler.removeCallbacks(playFirstRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void lazyFetchData() {
        super.lazyFetchData();
        initView();
        binding.refreshLayout.autoRefresh();
    }

    private void initView() {
        initViewpager();
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadVideoList(page, REFRESH_TYPE);
            }
        });
    }

    private void initViewpager() {
        videoAdapter = new VideoAdapter();
        binding.viewpager.setAdapter(videoAdapter);
        binding.viewpager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mCurrentPosition == position) {
                    return;
                }
                mCurrentPosition = position;
                autoPlayVideo(position);
                LogUtil.LogD(TAG, "onPageSelected   position:" + position + "  + mCurrentPosition:" + mCurrentPosition);
            }
        });


        videoAdapter.addChildClickViewIds(R.id.iv_avatar, R.id.ll_call, R.id.ll_video, R.id.iv_hi, R.id.iv_msg);
        videoAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_avatar) {
                    SheDetailActivity.StartMe(mContext, videoAdapter.getItem(position).user.stringId);
                } else if (view.getId() == R.id.ll_call) {
                    sendMsgFormType(EnoughManager.VOICE_TYPE, videoAdapter.getItem(position).user.stringId, videoAdapter.getItem(position).user);
                } else if (view.getId() == R.id.ll_video) {
                    sendMsgFormType(EnoughManager.VIDEO_TYPE, videoAdapter.getItem(position).user.stringId, videoAdapter.getItem(position).user);
                } else if (view.getId() == R.id.iv_hi) {
                    userHello(videoAdapter.getItem(position).user.stringId, position);
                } else if (view.getId() == R.id.iv_msg) {
                    ChatActivity.startMe(getActivity(), videoAdapter.getItem(position));
                }
            }
        });
        videoAdapter.setActionClickListener(new ActionClickListener() {
            @Override
            public void onReportClick(VideoResponseBean bean, int position) {
                ReportActivity.StartMe(mContext, bean.user.stringId, ReportRequestBean.VIDEO_TYPE);
            }

            @Override
            public void onNotInterested(VideoResponseBean bean, int position) {
                noLikeVideo(bean.videoId);
            }
        });
    }


    private void autoPlayVideo(int position) {
        if (binding.viewpager == null || binding.viewpager.getChildAt(0) == null) {
            return;
        }
        MyJzvdStd player = (MyJzvdStd) videoAdapter.getViewByPosition(position, R.id.jzvd);
        if (player != null) {
            player.startPreloading();
        }

        if (videoAdapter.getItemCount() - position <= 6) {
            loadVideoList(page, LOAD_TYPE);
        }
    }


    private void sendHi(String id, int position) {
        OkGoUtils.getInstance().userSayHello(this, new SheDetailsRequestBean(id), new NewJsonCallback<SayHelloResponseBean>() {
            @Override
            public void onSuc(SayHelloResponseBean response, String msg) {
                ToastUtils.toastShow(R.string.hello_success);
                EventBus.getDefault().post(new SayHiEvent(id));
                SayHiUtils.sendHiMsg(videoAdapter.getItem(position).user.nimId, getString(R.string.hi), videoAdapter.getItem(position).user.country, false, new RequestCallback<Void>() {
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

    private void noLikeVideo(int videoId) {
        OkGoUtils.getInstance().noLikeVideo(this, new DislikeVideoRequestBean(videoId), new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                deleteVideo(videoId);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                ToastUtils.toastShow(R.string.no_interested_error);
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

    private final Runnable playFirstRunnable = new Runnable() {
        @Override
        public void run() {
            autoPlayVideo(0);
        }
    };


    private void loadVideoList(int pg, int type) {
        OkGoUtils.getInstance().getVideoList(this, new PageRequestBean(pg, size), new NewJsonCallback<List<VideoResponseBean>>() {
            @Override
            public void onSuc(List<VideoResponseBean> response, String msg) {
                page++;
                finishLoad(type);
                if (type == REFRESH_TYPE) {
                    videoAdapter.setNewInstance(response);
                    binding.viewpager.postDelayed(playFirstRunnable, 200);
                } else if (type == LOAD_TYPE) {
                    videoAdapter.addData(response);
                }
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, List<VideoResponseBean> response) {
                super.onE(httpCode, apiCode, msg, response);
                finishLoad(type);
                ToastUtils.toastShow(R.string.load_video_failed);
            }
        });
    }

    private void finishLoad(int type) {
        if (type == REFRESH_TYPE) {
            binding.refreshLayout.finishRefresh();
        } else if (type == LOAD_TYPE) {
            binding.refreshLayout.finishLoadMore();
        }
    }


    class VideoAdapter extends BaseQuickAdapter<VideoResponseBean, BaseViewHolder> {


        private ActionClickListener actionClickListener;


        public VideoAdapter() {
            super(R.layout.item_video);
        }

        public void setActionClickListener(ActionClickListener actionClickListener) {
            this.actionClickListener = actionClickListener;
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, VideoResponseBean item) {

            ItemVideoBinding binding = ItemVideoBinding.bind(baseViewHolder.itemView);
            MyJzvdStd myJzvdStd = baseViewHolder.getView(R.id.jzvd);
            myJzvdStd.setUpVideo(item.videoUrl, item.coverUrl);
            myJzvdStd.startButton.setImageResource(R.mipmap.start_video);
            myJzvdStd.posterImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.ivHi.setVisibility(item.user.hello ? View.GONE : View.VISIBLE);
            binding.ivMsg.setVisibility(item.user.hello ? View.VISIBLE : View.GONE);
            binding.tvName.setText(item.user.name);
            binding.tvAge.setText(item.user.age + "");
            if ("3".equals(item.user.userType)) {
                binding.llVideo.setVisibility(View.GONE);
            } else {
                binding.llVideo.setVisibility(View.VISIBLE);
            }

            if (item.user.online) {
                if (item.user.busy) {
                    binding.tvBusy.setVisibility(View.VISIBLE);
                    binding.tvOnline.setVisibility(View.GONE);
                } else {
                    binding.tvBusy.setVisibility(View.GONE);
                    binding.tvOnline.setVisibility(View.VISIBLE);
                }
            } else {
                binding.tvBusy.setVisibility(View.GONE);
                binding.tvOnline.setVisibility(View.GONE);
            }
            binding.rlReport.setOnClickListener(v -> {
                binding.rlReport.setVisibility(View.GONE);
            });
            binding.ivMore.setOnClickListener(v -> {
                binding.rlReport.setVisibility(View.VISIBLE);
            });
            binding.tvReport.setOnClickListener(v -> {
                binding.rlReport.setVisibility(View.GONE);
                if (actionClickListener != null) {
                    actionClickListener.onReportClick(item, baseViewHolder.getAdapterPosition());
                }
            });
            binding.tvNotInterested.setOnClickListener(v -> {
                binding.rlReport.setVisibility(View.GONE);
                if (actionClickListener != null) {
                    actionClickListener.onNotInterested(item, baseViewHolder.getAdapterPosition());
                }
            });
            binding.rlReport.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        binding.rlReport.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
            binding.tvCancel.setOnClickListener(v -> {
                binding.rlReport.setVisibility(View.GONE);
            });
            binding.ivGender.setImageResource("2".equals(item.user.userGender) ? R.mipmap.gender_female : R.mipmap.gender_male);
            GlideUtils.loadCircleImg(getContext(), item.user.countryUr, binding.ivCountry, R.mipmap.country_placeholder);
            GlideUtils.loadCircleImg(getContext(), item.user.avatar, binding.ivAvatar, R.mipmap.ic_me_default_portrait);
            GlideUtils.loadImage(getContext(), item.coverUrl, myJzvdStd.posterImageView, -1);
            List<AudioAndVideoPriceResponseBean> priceItems = item.priceItems;
            if (priceItems != null) {
                for (int i = 0; i < priceItems.size(); i++) {
                    if ("voiceChat".equalsIgnoreCase(priceItems.get(i).priceType)) {
                        binding.tvAudioPrice.setText(priceItems.get(i).costStr + "");
                    } else if ("videoChat".equalsIgnoreCase(priceItems.get(i).priceType)) {
                        binding.tvVideoPrice.setText(priceItems.get(i).costStr + "");
                    }
                }
            }
        }
    }

    public interface ActionClickListener {
        void onReportClick(VideoResponseBean bean, int position);

        void onNotInterested(VideoResponseBean bean, int position);
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
