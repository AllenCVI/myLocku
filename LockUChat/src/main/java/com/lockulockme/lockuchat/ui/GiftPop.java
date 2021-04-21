package com.lockulockme.lockuchat.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.adapter.GiftPageAdapter;
import com.lockulockme.lockuchat.adapter.GiftPointAdapter;
import com.lockulockme.lockuchat.attach.GiftMsgAttachment;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.GiftBeanRsp;
import com.lockulockme.lockuchat.bean.rsp.MyDiamondsNumRsp;
import com.lockulockme.lockuchat.bean.rsp.SendGiftRsp;
import com.lockulockme.lockuchat.bean.rst.SendGiftRst;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.databinding.PopGiftBinding;
import com.lockulockme.lockuchat.http.GsonUtils;
import com.lockulockme.lockuchat.http.NetDataListener;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.im.MsgSendController;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

public class GiftPop extends PopupWindow {
    Activity mContext;
    private final PopGiftBinding binding;
    private GiftPageAdapter pageAdapter;
    private GiftPointAdapter pointAdapter;
    private SessionTypeEnum sessionType;
    private String sendAccount;
    private User sendUser;
    private MsgSendController msgSendController;

    public GiftPop(Activity context, MsgSendController msgSendController, SessionTypeEnum sessionType, User sendUser) {
        super(context);
        this.mContext = context;
        this.msgSendController = msgSendController;
        this.sessionType = sessionType;
        this.sendUser = sendUser;
        this.sendAccount = sendUser.accid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = PopGiftBinding.inflate(inflater);
        setContentView(binding.getRoot());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.gift_style);
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 判断是不是点击了外部
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                //不是点击外部
                return false;
            }
        });
        initView();
        initData();
    }

    private void initData() {
        getGiftList();
        getDiamondsNum();
    }

    private void getGiftList() {
        NetDataUtils.getInstance().getNetData().getGiftList(new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getListData(response, GiftBeanRsp.class);
                binding.pbLoading.setVisibility(View.GONE);
                if (baseBean.code != 0) {
                    return;
                }
                List<GiftBeanRsp> giftBeans = (List<GiftBeanRsp>) baseBean.data;


                List<List<GiftBeanRsp>> lists = new ArrayList<>();
                for (int i = 0; i < giftBeans.size(); i++) {
                    if (i % 8 == 0) {
                        List<GiftBeanRsp> itemList = new ArrayList<>();
                        for (int j = i; j < i + 8; j++) {
                            if (j < giftBeans.size())
                                itemList.add(giftBeans.get(j));
                        }
                        lists.add(itemList);
                    }
                }

                pointAdapter.setNewInstance(lists);
                pageAdapter.setNewInstance(lists);
            }

            @Override
            public void onFailed(String msg, int code) {
                binding.pbLoading.setVisibility(View.GONE);
            }
        },mContext);
    }

    public void getDiamondsNum() {
        NetDataUtils.getInstance().getNetData().getDiamondsNum(new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, MyDiamondsNumRsp.class);
                if (baseBean.code != 0) {
                    return;
                }

                MyDiamondsNumRsp diamondsNum = (MyDiamondsNumRsp) baseBean.data;

                binding.tvDiamonds.setText(String.valueOf(diamondsNum.stone));
            }

            @Override
            public void onFailed(String msg, int code) {

            }
        },mContext);
    }

    private void sendGift() {
        if (selectGift == null) return;
        SendGiftRst sendGift = new SendGiftRst();
        sendGift.anchorStringId = sendUser.stringId;
        sendGift.giftId = selectGift.id;
        NetDataUtils.getInstance().getNetData().sendGift(sendGift, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, SendGiftRsp.class);
                if (baseBean.code != 0) {
                    if (baseBean.code == 9) {
                        GoNeedUIUtils.getInstance().goDiamondsPage(mContext);
                    }else {
                        ToastUtils.toastShow(R.string.send_failed);
                    }
                    return;
                }

                SendGiftRsp sendGiftRsp = (SendGiftRsp) baseBean.data;
                GiftMsgAttachment attachment = new GiftMsgAttachment(sendGiftRsp.giftUrl, sendGiftRsp.meGiftName,sendGiftRsp.id,
                        sendGiftRsp.diamond,sendGiftRsp.meGiftDesc,sendGiftRsp.score,sendGiftRsp.sheGiftName,sendGiftRsp.sheGiftDesc);
                IMMessage message = MessageBuilder.createCustomMessage(sendAccount, sessionType, attachment);
                msgSendController.sendMsg(message);
                dismiss();

                getDiamondsNum();
            }

            @Override
            public void onFailed(String msg, int code) {
                ToastUtils.toastShow(R.string.send_failed);
            }
        },mContext);
    }


    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        pointAdapter = new GiftPointAdapter();
        binding.recyclerView.setAdapter(pointAdapter);
        pageAdapter = new GiftPageAdapter();
        pageAdapter.setOnGiftSelect(onGiftSelect);
        binding.viewPager.setAdapter(pageAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pointAdapter.setPos(position);
                pointAdapter.notifyDataSetChanged();
            }
        });

        binding.tvGive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectGift();
            }
        });

        binding.tvRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDiamondsPage();
            }
        });
    }

    private void sendSelectGift() {
        if (selectGift==null){
            ToastUtils.toastShow(R.string.please_select_gift);
            return;
        }

        sendGift();
    }

    private void toVipPage() {
        GoNeedUIUtils.getInstance().goVipPage(mContext);
    }
    private void toDiamondsPage() {
        GoNeedUIUtils.getInstance().goDiamondsPage(mContext);
    }


    public void show(View parent) {
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public interface OnGiftSelect {
        void onSelect(GiftBeanRsp giftBean);
    }

    private GiftBeanRsp selectGift;

    GiftPop.OnGiftSelect onGiftSelect = new GiftPop.OnGiftSelect() {
        @Override
        public void onSelect(GiftBeanRsp giftBean) {
            selectGift = giftBean;
        }
    };

}
