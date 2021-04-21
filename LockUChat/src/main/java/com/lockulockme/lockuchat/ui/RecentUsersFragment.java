package com.lockulockme.lockuchat.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.adapter.RecentUsersAdapter;
import com.lockulockme.lockuchat.bean.BlockEvent;
import com.lockulockme.lockuchat.bean.RecentUser;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.common.EventHelper;
import com.lockulockme.lockuchat.databinding.FgRecentBinding;
import com.lockulockme.lockuchat.http.HttpReqCacheClear;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.utils.BadgerHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.lockulockme.lockuchat.utils.im.RecentUsersHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentUsersFragment extends Fragment {
    private FgRecentBinding binding;
    private RecentUsersAdapter recentUsersAdapter;
    private boolean isRegister=false;
    private ConfirmPop confirmPop;
    private RecentUserCallBack callBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.getInstance().clear(this.toString());
        HttpReqCacheClear.clearHttp(this.toString());
        registerMsgObservers(false);
    }


    public void sendReportAndBlockEvent(String stringId) {
        boolean isHasUser=false;
        if (recentUsersAdapter != null && recentUsersAdapter.getData().size() > 0) {
            MsgService msgService = NIMClient.getService(MsgService.class);
            for (int i = 0; i < recentUsersAdapter.getData().size(); i++) {
                RecentUser recentUser = recentUsersAdapter.getData().get(i);
                if (recentUser.user.stringId.equals(stringId)) {
                    isHasUser=true;
                    RecentContact recent = recentUser.recentContact;
                    String sessionId = recent.getContactId();
                    SessionTypeEnum sessionType = recent == null ? null : recent.getSessionType();
                    msgService.deleteRecentContact(recent);
                    msgService.clearChattingHistory(sessionId, sessionType, false);
                    recentUsersAdapter.getData().remove(recentUser);
                    recentUsersAdapter.notifyDataSetChanged();
                    FriendService service = NIMClient.getService(FriendService.class);
                    service.addToBlackList(sessionId);
                }
            }
        }
        if (isHasUser)
        EventHelper.getInstance().updateApp2MeBlockEventLiveData(new BlockEvent(stringId));
    }

    public void deleteRecent(String userId) {
        if (recentUsersAdapter != null && recentUsersAdapter.getItemCount() > 0) {
            for (RecentUser datum : recentUsersAdapter.getData()) {
                if (userId.equals(datum.user.stringId)) {

                    final MsgService msgService = NIMClient.getService(MsgService.class);
                    final String sessionId = datum.recentContact == null ? null : datum.recentContact.getContactId();
                    final SessionTypeEnum sessionType = datum.recentContact == null ? null : datum.recentContact.getSessionType();
                    msgService.deleteRecentContact(datum.recentContact);
                    msgService.clearChattingHistory(sessionId, sessionType, false);

                    recentUsersAdapter.getData().remove(datum);
                    refreshMsgAdapter(true);
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FgRecentBinding.inflate(inflater, container, false);
        if (recentUsersAdapter == null) {
            recentUsersAdapter = new RecentUsersAdapter();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rv.setLayoutManager(layoutManager);
        binding.rv.setAdapter(recentUsersAdapter);
        recentUsersAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                RecentUser recentUser=recentUsersAdapter.getItem(position);
//                if (isBlacked(recentUser.user.accid)){
//                    deleteRecent(recentUser.user.stringId);
//                    ToastUtils.toastShow(R.string.user_is_blacked);
//                    return;
//                }

                ChatActivity.startMe(getContext(),recentUser.user);
            }
        });

        recentUsersAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                showDeletePop(recentUsersAdapter.getItem(position));
                return false;
            }
        });

        binding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadedMsg=false;
                loadData();
            }
        });

        return binding.getRoot();
    }

    private void showDeletePop(RecentUser recentUser){
        final MsgService msgService = NIMClient.getService(MsgService.class);
        final String sessionId = recentUser.recentContact == null ? null : recentUser.recentContact.getContactId();
        final SessionTypeEnum sessionType = recentUser.recentContact == null ? null :  recentUser.recentContact.getSessionType();
        if (confirmPop==null){
            confirmPop = new ConfirmPop(getActivity());
        }
        confirmPop.setTitle(getResources().getString(R.string.del_chat))
                .setOnBtnListener(new ConfirmPop.OnBtnListener() {
            @Override
            public void onConfirmClick(View v) {
                msgService.deleteRecentContact(recentUser.recentContact);
                msgService.clearChattingHistory(sessionId, sessionType, false);
                recentUsersAdapter.getData().remove(recentUser);
                refreshMsgAdapter(true);
            }
            @Override
            public void onCancelClick(View v) {
            }
        });
        confirmPop.show(binding.getRoot());

    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
        if (!isRegister){
            isRegister=true;
            registerMsgObservers(true);
        }
    }

    private boolean isBlacked(String imId){
        boolean black = NIMClient.getService(FriendService.class).isInBlackList(imId);
        return black;
    }

    boolean isLoadedMsg;
    private void loadData(){
        if (isLoadedMsg) {
            return;
        }

        NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
            @Override
            public void onEvent(Boolean aBoolean) {
                NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(this, false);
//
                if (isLoadedMsg) {
                    return;
                }

                // 查询最近联系人列表数据
                RecentUsersHelper.getInstance().getRecentUsers(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recentContacts, Throwable throwable) {
                        if (code != ResponseCode.RES_SUCCESS || recentContacts == null) {
                            binding.srlRefresh.setRefreshing(false);
                            return;
                        }
                        initMsg(recentContacts);
                    }
                });
            }
        }, true);

    }

    void initMsg(List<RecentContact> recentContacts){
        List<String> ids=new ArrayList<>();
        for (RecentContact recentContact : recentContacts) {
            LogHelper.e("recentContact",""+recentContact.getContent());
            ids.add(recentContact.getContactId());
        }
        UserBeanCacheUtils.getInstance().getNetworkUsers(RecentUsersFragment.this.toString(), ids, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                List<RecentUser> list = new ArrayList<>();
                for (RecentContact recent : recentContacts) {
                    User user = getUser(recent.getContactId(), userList);
                    if (user != null){
                        RecentUser recentUser = new RecentUser();
                        recentUser.recentContact = recent;
                        recentUser.user = user;
                        list.add(recentUser);
                    }
                }

                isLoadedMsg = true;
                if (isAdded()) {
                    onRecentUsersLoaded(list);
                }
                binding.srlRefresh.setRefreshing(false);
            }

            @Override
            public void onGetFailed() {
                binding.srlRefresh.setRefreshing(false);
            }
        });

    }

    private User getUser(String imId, List<User> response) {
        for (User user : response) {
            if (user.accid.equals(imId)) {
                return user;
            }
        }
        return null;
    }
    private void onRecentUsersLoaded(List<RecentUser> list) {
        sortRecentUsers(list);
        recentUsersAdapter.setNewInstance(list);
        updateBadgerNum(true,list);
        updateNoMsgUI();
    }

    private void updateNoMsgUI(){
        binding.ivNoMsg.setVisibility(isLoadedMsg&&recentUsersAdapter.getItemCount()==0?View.VISIBLE:View.GONE);
    }

    private void updateBadgerNum(boolean unreadIsChange, List<RecentUser> list) {
        if (unreadIsChange) {
            int unreadNum = 0;
            for (RecentUser r : list) {
                unreadNum += r.recentContact.getUnreadCount();
            }
            IMLoginHelper.getInstance().setUnreadNum(unreadNum);
            BadgerHelper.updateBadgerNum(unreadNum);
        }
    }

    /******************************** 排序器*********************************/
    private void sortRecentUsers(List<RecentUser> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comparator);
    }

    private static final Comparator<RecentUser> comparator = (recent1, recent2) -> {
        long time = recent1.recentContact.getTime() - recent2.recentContact.getTime();
        return time == 0 ? 0 : (time > 0 ? -1 : 1);
    };
    /******************************** 排序器*********************************/





    private void registerMsgObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineObserver, register);
        if (register)
        IMLoginHelper.getInstance().observeOnlineStatus(this, new androidx.lifecycle.Observer<StatusCode>() {
            @Override
            public void onChanged(StatusCode code) {
                if (!code.wontAutoLogin()) {
                    if (code == StatusCode.NET_BROKEN) {
                        binding.tvStatusDesc.setVisibility(View.VISIBLE);
                        binding.tvStatusDesc.setText(R.string.internet_is_no_good);
                    } else if (code == StatusCode.UNLOGIN) {
                        binding.tvStatusDesc.setVisibility(View.VISIBLE);
                        binding.tvStatusDesc.setText(R.string.nologin_in);
                    } else if (code == StatusCode.CONNECTING) {
                        binding.tvStatusDesc.setVisibility(View.VISIBLE);
                        binding.tvStatusDesc.setText(R.string.connecting);
                    } else if (code == StatusCode.LOGINING) {
                        binding.tvStatusDesc.setVisibility(View.VISIBLE);
                        binding.tvStatusDesc.setText(R.string.logining);
                    } else {
                        binding.tvStatusDesc.setVisibility(View.GONE);
                        loadData();
                    }
                }
            }
        });
        if (register)
        EventHelper.getInstance().observeMe2MeBlockEvent(this.toString(),blockEvent -> {
            if (callBack!=null) callBack.onBlock(blockEvent);
        });
    }
    Observer<StatusCode> onlineObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                if (callBack!=null) callBack.onLoginout();
            }
        }
    };
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {

        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            onRecentUserChanged(recentContacts);
        }
    };

    private void onRecentUserChanged(final List<RecentContact> recentContacts) {
        List<RecentContact> cacheRecentContacts = new ArrayList<>();
        for (RecentContact r : recentContacts) {
            cacheRecentContacts.add(r);
        }
        List<String> ids = new ArrayList<>();
        for (RecentContact recent : cacheRecentContacts) {
            ids.add(recent.getContactId());
        }
        UserBeanCacheUtils.getInstance().getNetworkUsers(RecentUsersFragment.this.toString(), ids, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                int index;
                for (RecentContact r : recentContacts) {
                    index = -1;
                    for (int i = 0; i < recentUsersAdapter.getItemCount(); i++) {
                        if (r.getContactId().equals(recentUsersAdapter.getItem(i).recentContact.getContactId()) && r.getSessionType() == (recentUsersAdapter.getItem(i).recentContact
                                .getSessionType())) {
                            index = i;
                            break;
                        }
                    }
                    if (index >= 0) {
                        recentUsersAdapter.getData().remove(index);
                    }
                }


                List<RecentUser> list = new ArrayList<>();
                for (RecentContact recent : cacheRecentContacts) {
                    User user = getUser(recent.getContactId(), userList);
                    if (user!=null){
                        RecentUser msgBoxBean = new RecentUser();
                        msgBoxBean.recentContact = recent;
                        msgBoxBean.user = user;
                        list.add(msgBoxBean);
                    }
                }
                recentUsersAdapter.getData().addAll(list);
                refreshMsgAdapter(true);
            }

            @Override
            public void onGetFailed() {
                binding.srlRefresh.setRefreshing(false);
            }
        });
    }

    private void refreshMsgAdapter(boolean b) {
        List<RecentUser> list=recentUsersAdapter.getData();
        sortRecentUsers(list);
        recentUsersAdapter.notifyDataSetChanged();
        updateBadgerNum(b,list);
        updateNoMsgUI();
    }


    final Observer<IMMessage> statusObserver = new Observer<IMMessage>() {

        @Override
        public void onEvent(IMMessage message) {
            if (message == null) {
                return;
            }
            String sessionId = message.getSessionId();
            SessionTypeEnum sessionType = message.getSessionType();
            int index = getRecentUserPos(sessionId, sessionType);
            if (index >= 0 && index < recentUsersAdapter.getItemCount()) {
                RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(sessionId, sessionType);
                if (recentContact != null){
                    RecentUser recentUser = recentUsersAdapter.getItem(index);
                    recentUser.recentContact = recentContact;
                    recentUsersAdapter.setData(index, recentUser);
                }else {
                    recentUsersAdapter.removeAt(index);
                }
            }
        }
    };

    private int getRecentUserPos(String sessionId, SessionTypeEnum sessionType) {
        for (int i = 0; i < recentUsersAdapter.getItemCount(); i++) {
            RecentUser recentUser = recentUsersAdapter.getItem(i);
            RecentContact item = recentUser.recentContact;
            if (TextUtils.equals(item.getContactId(), sessionId) && item.getSessionType() == sessionType) {
                return i;
            }
        }
        return -1;
    }

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {

        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentUser recentUser : recentUsersAdapter.getData()) {
                    RecentContact item = recentUser.recentContact;
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) &&
                            item.getSessionType() == recentContact.getSessionType()) {
                        recentUsersAdapter.getData().remove(recentUser);
                        refreshMsgAdapter(true);
                        break;
                    }
                }
            } else {
                recentUsersAdapter.getData().clear();
                refreshMsgAdapter(true);
            }
        }
    };

    public interface RecentUserCallBack{
        void onBlock(BlockEvent blockEvent);
        void onLoginout();
    }

    public void setCallBack(RecentUserCallBack callBack) {
        this.callBack = callBack;
    }


}
