package com.lockulockme.locku.zlocknine.module.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lockulockme.locku.R;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.ReportRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheDetailsRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheGiftRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheImageRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheMacyRequestBean;
import com.lockulockme.locku.base.beans.requestbean.VideoPriceRequestBean;
import com.lockulockme.locku.base.beans.responsebean.AudioAndVideoPriceResponseBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SayHelloResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheGiftBean;
import com.lockulockme.locku.base.beans.responsebean.SheGiftsResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheImageResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheMacyResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheVideoResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.eventbus.BlockEvent;
import com.lockulockme.locku.base.eventbus.SayHiEvent;
import com.lockulockme.locku.base.eventbus.VipStatusRefreshEvent;
import com.lockulockme.locku.databinding.AcSheDetailBinding;
import com.lockulockme.locku.databinding.LayoutRechargeDiamondDialogBinding;
import com.lockulockme.locku.databinding.LayoutSayhiFiveChancesDialogBinding;
import com.lockulockme.locku.databinding.LayoutSwitchUserBinding;
import com.lockulockme.locku.databinding.PopSheDetailBinding;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.zlocknine.base.utils.EnoughManager;
import com.lockulockme.locku.zlocknine.base.utils.GlideUtils;
import com.lockulockme.locku.zlocknine.base.utils.OkGoUtils;
import com.lockulockme.locku.zlocknine.base.utils.VipManager;
import com.lockulockme.locku.zlocknine.common.BaseActivity;
import com.lockulockme.locku.zlocknine.common.PopupWindowBuilder;
import com.lockulockme.locku.zlocknine.module.ui.adapter.SheGiftAdapter;
import com.lockulockme.locku.zlocknine.module.ui.adapter.ShePicturesAdapter;
import com.lockulockme.locku.zlocknine.module.ui.adapter.SheVideosAdapter;
import com.lockulockme.lockuchat.aavg2.AVChatJump;
import com.lockulockme.lockuchat.common.SayHiUtils;
import com.lockulockme.lockuchat.ui.ChatActivity;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.netease.nimlib.sdk.RequestCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SheDetailActivity extends BaseActivity<AcSheDetailBinding> {


    static final String ID_KEY = "intent_key_id";
    private String id;
    private UserInfo userInfo;
    private ShePicturesAdapter imageAdapter;
    private SheVideosAdapter videosAdapter;
//    public boolean isVip = false;
    private List<TextView> rankTvList;
    private List<ImageView> rankIvList;
    private SheGiftAdapter mGiftAdapter;

    public static void StartMe(Context mContext, String idStr) {
        Intent intent = new Intent(mContext, SheDetailActivity.class);
        intent.putExtra(ID_KEY, idStr);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcSheDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        id = getIntent().getStringExtra(ID_KEY);
        initView();
        initHeader();
        loadUserData(id);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVipRecharge(VipStatusRefreshEvent event) {
        loadUserData(id);
    }


    private void initView() {
        rankIvList = new ArrayList<>();
        rankTvList = new ArrayList<>();
        rankIvList.add(binding.ivFirst);
        rankIvList.add(binding.ivSecond);
        rankIvList.add(binding.ivThird);
        rankTvList.add(binding.tvFirst);
        rankTvList.add(binding.tvSecond);
        rankTvList.add(binding.tvThird);
        binding.rvImage.setLayoutManager(new GridLayoutManager(mContext, 3));
        imageAdapter = new ShePicturesAdapter(null);
        binding.rvImage.setAdapter(imageAdapter);
        binding.rvVideo.setLayoutManager(new GridLayoutManager(mContext, 3));
        videosAdapter = new SheVideosAdapter(null);
        binding.rvVideo.setAdapter(videosAdapter);

        binding.rvGift.setLayoutManager(new GridLayoutManager(mContext, 2));
        mGiftAdapter = new SheGiftAdapter();
        binding.rvGift.setAdapter(mGiftAdapter);

        videosAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PlayActivity.StartMe(mContext, videosAdapter.getItem(position).videoUrl, videosAdapter.getItem(position).videoIcon);
            }
        });

        imageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                List<String> list = new ArrayList<>();
                List<SheImageResponseBean> data = imageAdapter.getData();
                if (VipManager.getInstance().getVipStateFromCache() != null && VipManager.getInstance().getVipStateFromCache().isVip) {
                    for (SheImageResponseBean item : data) {
                        list.add(item.imgUrl);
                    }
                    ImagePreviewActivity.StartMe(mContext, list, position);
                } else {
                    if (position == 0) {
                        list.add(data.get(position).imgUrl);
                        ImagePreviewActivity.StartMe(mContext, list, position);
                    } else {
                        VipGoodsListActivity.go(SheDetailActivity.this);
                    }
                }
            }
        });
        binding.layoutCommonEmpty.rltCommonEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserData(id);
            }
        });
        binding.layoutCommonNetworkError.rltCommonNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserData(id);
            }
        });
    }

    private void initHeader() {
        binding.head.ivRight.setImageResource(R.mipmap.she_details_msg);
        binding.head.ivRight.setVisibility(View.VISIBLE);
        binding.head.ivRight.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        binding.head.ivRight.setOnClickListener(v -> {
            showPop();
        });
        setNavTitle("");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_hi:
                userHello(id);
                break;
            case R.id.ll_call:
                startAudioOrVideoCall(EnoughManager.VOICE_TYPE, id, userInfo);
                break;
            case R.id.ll_video:
                startAudioOrVideoCall(EnoughManager.VIDEO_TYPE, id, userInfo);
                break;
            case R.id.iv_msg:
                ChatActivity.startMe(SheDetailActivity.this, userInfo);
                break;

        }
    }


    private void userHello(String id) {
        VipManager.getInstance().getVipState(this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                sendHi(id);
            }

            @Override
            public void onVipFailed() {
                ToastUtils.toastShow(R.string.failed_to_get_vip_status);
            }
        });
    }

    private void sendHi(String id) {
        OkGoUtils.getInstance().userSayHello(this, new SheDetailsRequestBean(id), new NewJsonCallback<SayHelloResponseBean>() {
            @Override
            public void onSuc(SayHelloResponseBean response, String msg) {
                ToastUtils.toastShow(R.string.hello_success);
                binding.ivHi.setVisibility(View.GONE);
                binding.ivMsg.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new SayHiEvent(id));
                SayHiUtils.sendHiMsg(userInfo.nimId, getString(R.string.hi), userInfo.country, false, new RequestCallback<Void>() {
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

    private void blockUser(String id) {
        showLoading();
        OkGoUtils.getInstance().blockUser(this, new SheDetailsRequestBean(id), new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {
                hideLoading();
                EventBus.getDefault().post(new BlockEvent(id));
                finish();
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, Void response) {
                super.onE(httpCode, apiCode, msg, response);
                hideLoading();
            }
        });
    }

    private void loadUserData(String id) {
        binding.layoutCommonLoading.rltProgress.setVisibility(View.VISIBLE);
        binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.GONE);
        binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
        VipManager.getInstance().getVipState(this, new VipManager.OnVipListener() {
            @Override
            public void onVipSuccess(VipResponseBean vipResp) {
                loadRealUserData(id);
            }

            @Override
            public void onVipFailed() {
                binding.layoutCommonLoading.rltProgress.setVisibility(View.GONE);
                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loadRealUserData(String id) {
        OkGoUtils.getInstance().getOtherInfo(this, new SheDetailsRequestBean(id), new NewJsonCallback<UserInfo>() {
            @Override
            public void onSuc(UserInfo response, String msg) {
                fillUserData(response);
            }

            @Override
            public void onE(int httpCode, int apiCode, String msg, UserInfo response) {
                binding.layoutCommonLoading.rltProgress.setVisibility(View.GONE);
                binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
                binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.VISIBLE);
            }
        });
        OkGoUtils.getInstance().getSheImages(this, new SheImageRequestBean(id, 1, 50), new NewJsonCallback<List<SheImageResponseBean>>() {
            @Override
            public void onSuc(List<SheImageResponseBean> response, String msg) {
                fillImages(response);
            }
        });

        OkGoUtils.getInstance().getSheVideos(this, new SheImageRequestBean(id, 1, 50), new NewJsonCallback<List<SheVideoResponseBean>>() {
            @Override
            public void onSuc(List<SheVideoResponseBean> response, String msg) {
                fillVideos(response);
            }
        });

        OkGoUtils.getInstance().getShePrices(this, new VideoPriceRequestBean(id), new NewJsonCallback<List<AudioAndVideoPriceResponseBean>>() {
            @Override
            public void onSuc(List<AudioAndVideoPriceResponseBean> response, String msg) {
                fillPriceData(response);
            }
        });

        OkGoUtils.getInstance().getMacyList(this, new SheMacyRequestBean(id), new NewJsonCallback<List<SheMacyResponseBean>>() {
            @Override
            public void onSuc(List<SheMacyResponseBean> response, String msg) {
                fillRankData(response);
            }
        });

        OkGoUtils.getInstance().getSheGiftList(this, new SheGiftRequestBean(id), new NewJsonCallback<SheGiftsResponseBean>() {
            @Override
            public void onSuc(SheGiftsResponseBean response, String msg) {
                fillGiftData(response);
            }
        });
    }

    private void fillGiftData(SheGiftsResponseBean response) {
        if (response.detail == null) return;
        binding.tvGiftReceive.setText(String.format(getString(R.string.she_details_gift_second_title), response.giftTypeNumber + ""));
        if (response.detail.size() > 4) {
            List<SheGiftBean> giftShortList = response.detail.subList(0, 4);
            mGiftAdapter.setNewInstance(giftShortList);
            binding.llGiftMore.setVisibility(View.VISIBLE);
            binding.llGiftMore.setOnClickListener(v -> {
                mGiftAdapter.setNewInstance(response.detail);
                binding.llGiftMore.setVisibility(View.GONE);
            });
        } else {
            mGiftAdapter.setNewInstance(response.detail);
            binding.llGiftMore.setVisibility(View.GONE);
        }
        binding.llGift.setVisibility(response.detail.size() == 0 ? View.GONE : View.VISIBLE);

    }

    private void fillRankData(List<SheMacyResponseBean> list) {
        if (list.size() >= 3) {
            for (int i = 0; i < rankTvList.size(); i++) {
                rankTvList.get(i).setText(list.get(i).nickname);
                if ("1".equals(list.get(i).gender)) {
                    GlideUtils.loadCircleImg(mContext, list.get(i).icon, rankIvList.get(i), R.mipmap.ic_male_portrait_placeholder);
                } else {
                    GlideUtils.loadCircleImg(mContext, list.get(i).icon, rankIvList.get(i), R.mipmap.ic_female_portrait_placeholder);
                }
            }
            binding.llRank.setVisibility(View.VISIBLE);
        } else {
            binding.llRank.setVisibility(View.GONE);
        }
    }


    private void fillPriceData(List<AudioAndVideoPriceResponseBean> response) {
        for (AudioAndVideoPriceResponseBean res : response) {
            if ("voiceChat".equalsIgnoreCase(res.priceType)) {
                binding.tvAudioPrice.setText(res.costStr + "");
            } else if ("videoChat".equalsIgnoreCase(res.priceType)) {
                binding.tvVideoPrice.setText(res.costStr + "");
            }
        }
        binding.llMore.setVisibility(View.VISIBLE);
    }

    private void fillVideos(List<SheVideoResponseBean> response) {
        if (response == null) return;
        if (response.size() > 3) {
            List<SheVideoResponseBean> shortList = response.subList(0, 3);
            videosAdapter.setNewInstance(shortList);
            binding.llVideoMore.setVisibility(View.VISIBLE);
            binding.llVideoMore.setOnClickListener(v -> {
                videosAdapter.setNewInstance(response);
                binding.llVideoMore.setVisibility(View.GONE);
            });
        } else {
            videosAdapter.setNewInstance(response);
            binding.llVideoMore.setVisibility(View.GONE);
        }
        binding.llShortVideo.setVisibility(response.size() == 0 ? View.GONE : View.VISIBLE);
    }

    private void fillImages(List<SheImageResponseBean> response) {
        if (response == null) return;
        if (response.size() > 3) {
            List<SheImageResponseBean> shortList = response.subList(0, 3);
            imageAdapter.setNewInstance(shortList);
            binding.llAlbumMore.setVisibility(View.VISIBLE);
            binding.llAlbumMore.setOnClickListener(v -> {
                imageAdapter.setNewInstance(response);
                binding.llAlbumMore.setVisibility(View.GONE);
            });
        } else {
            imageAdapter.setNewInstance(response);
            binding.llAlbumMore.setVisibility(View.GONE);
        }
        binding.llAlbum.setVisibility(response.size() == 0 ? View.GONE : View.VISIBLE);
    }

    private void fillUserData(UserInfo response) {
        userInfo = response;
        GlideUtils.loadCircleImg(mContext, response.countryUr, binding.ivCountry, R.mipmap.country_placeholder);
        GlideUtils.loadImage(mContext, response.avatar, binding.ivImg);
        binding.tvName.setText(response.name);
        binding.tvAge.setText(response.age + "");
        binding.tvSign.setText(response.userSign);
        binding.ivGender.setImageResource(response.userGender.equals("2") ? R.mipmap.gender_female : R.mipmap.gender_male);
        if (response.hello) {
            binding.ivMsg.setVisibility(View.VISIBLE);
            binding.ivHi.setVisibility(View.GONE);
        } else {
            binding.ivMsg.setVisibility(View.GONE);
            binding.ivHi.setVisibility(View.VISIBLE);
        }
        if ("3".equals(userInfo.userType)) {
            binding.llVideo.setVisibility(View.GONE);
        } else {
            binding.llVideo.setVisibility(View.VISIBLE);
        }
        if (userInfo.online) {
            if (userInfo.busy) {
                binding.tvBusy.setVisibility(View.VISIBLE);
                binding.tvOnline.setVisibility(View.GONE);
                binding.tvOffline.setVisibility(View.GONE);
            } else {
                binding.tvBusy.setVisibility(View.GONE);
                binding.tvOnline.setVisibility(View.VISIBLE);
                binding.tvOffline.setVisibility(View.GONE);
            }
        } else {
            binding.tvBusy.setVisibility(View.GONE);
            binding.tvOnline.setVisibility(View.GONE);
            binding.tvOffline.setVisibility(View.VISIBLE);
        }

        binding.layoutCommonLoading.rltProgress.setVisibility(View.GONE);
        binding.layoutCommonEmpty.rltCommonEmpty.setVisibility(View.GONE);
        binding.layoutCommonNetworkError.rltCommonNetworkError.setVisibility(View.GONE);
    }

    public void showPop() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(this)
                .setContentView(R.layout.pop_she_detail)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        PopSheDetailBinding sheDetailBinding = PopSheDetailBinding.bind(popBuilder.getRootView());
        popBuilder.show(binding.getRoot(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        sheDetailBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBuilder.dismiss();
            }
        });

        sheDetailBinding.tvBlock.setOnClickListener(v -> {
            showBlockUserPop(v);
            popBuilder.dismiss();
        });

        sheDetailBinding.tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportActivity.StartMe(mContext, id, ReportRequestBean.PERSONAL_TYPE);
                popBuilder.dismiss();
            }
        });
    }

    public void showBlockUserPop(View view) {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(this)
                .setContentView(R.layout.layout_switch_user)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .build();
        final LayoutSwitchUserBinding popRateBing = LayoutSwitchUserBinding.bind(popBuilder.getRootView());
        popBuilder.show(binding.getRoot(), Gravity.CENTER, 0, 0);
        popRateBing.tvContent.setText(getResources().getString(R.string.block_content));
        popRateBing.tvSwitchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBuilder.dismiss();
            }
        });
        popRateBing.tvSwitchConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockUser(id);
                popBuilder.dismiss();
            }
        });

    }

    private void startAudioOrVideoCall(String type, String id, UserInfo userInfo) {
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
                ToastUtils.toastShow(R.string.failed_to_get_vip_status);
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

    public void diamondIsNotEnoughIntercept() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(this)
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
                CurrentDiamondActivity.go(SheDetailActivity.this);
            }
        });
    }

    public void fiveChancesVipIntercept() {
        final PopupWindowBuilder popBuilder = PopupWindowBuilder.newBuilder(this)
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
                VipGoodsListActivity.go(SheDetailActivity.this);
            }
        });
    }

}
