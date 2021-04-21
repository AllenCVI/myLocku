package com.lockulockme.lockuchat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.bean.BlockEvent;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.bean.rst.ReportUserRst;
import com.lockulockme.lockuchat.common.EventHelper;
import com.lockulockme.lockuchat.databinding.AcChatOptionBinding;
import com.lockulockme.lockuchat.http.GsonUtils;
import com.lockulockme.lockuchat.http.NetDataListener;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class ChatOptionActivity extends BaseActivity {

    private AcChatOptionBinding binding;
    private static final String USER="user";
    private User reportUser;

    public static void startMe(Context context, Object user){
        Gson gson = new Gson();
        String sendUserJson = gson.toJson(user);
        Intent intent = new Intent(context, ChatOptionActivity.class);
        intent.putExtra(USER, sendUserJson);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcChatOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reportUser = new Gson().fromJson(getIntent().getStringExtra(USER),User.class);

        ImageHelper.intoIV4Circle(binding.ivHead,reportUser.smallUserIcon, R.mipmap.icon_placeholder_user,R.mipmap.icon_placeholder_user);
        binding.tvNickname.setText(reportUser.nick);

        binding.switchBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    blockUser();
                }
            }
        });
        binding.llReport.setOnClickListener(v -> GoNeedUIUtils.getInstance().getGoNeedUI().goReport4ChatActivity(that,reportUser.stringId));
        EventHelper.getInstance().observeApp2MeBlockEvent(this.toString(),blockEvent -> {
            if (blockEvent.userStringId.equals(reportUser.stringId)){
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventHelper.getInstance().clear(this.toString());
    }

    private void blockUser(){
        NetDataUtils.getInstance().getNetData().blockUser(new ReportUserRst(reportUser.stringId),new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                LogHelper.e("recent", "response="+response);
                BaseBean baseBean= GsonUtils.getInstance().getBaseBean(response);
                if (baseBean.code!=0){
                    ToastUtils.toastShow(R.string.block_fail);
                    return;
                }
                ToastUtils.toastShow(R.string.block_suc);
                EventHelper.getInstance().updateMe2MeBlockEventLiveData(new BlockEvent(reportUser.stringId));
                finish();
            }

            @Override
            public void onFailed(String msg, int code) {
                ToastUtils.toastShow(R.string.internet_is_not_can_use);
            }
        },this);
    }
}
