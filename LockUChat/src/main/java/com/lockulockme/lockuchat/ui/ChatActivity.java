package com.lockulockme.lockuchat.ui;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.AVChatJump;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallType;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.adapter.ChatAdapter;
import com.lockulockme.lockuchat.adapter.chat.QAAnswerAdapter;
import com.lockulockme.lockuchat.attach.QAAnswerMsgAttachment;
import com.lockulockme.lockuchat.attach.QAMsgAttachment;
import com.lockulockme.lockuchat.attach.StrategyAVMsgAttachment;
import com.lockulockme.lockuchat.bean.QAAnswer;
import com.lockulockme.lockuchat.bean.VipStatus;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.common.EventHelper;
import com.lockulockme.lockuchat.common.Extra;
import com.lockulockme.lockuchat.common.ExtraUtils;
import com.lockulockme.lockuchat.common.VipStatusListener;
import com.lockulockme.lockuchat.databinding.AcChatBinding;
import com.lockulockme.lockuchat.http.UserBeanCacheUtils;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.utils.AudioMsgPlayer;
import com.lockulockme.lockuchat.utils.ImageHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.MatisseHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.TransformTextMsgUtils;
import com.lockulockme.lockuchat.utils.im.IMMsgHelper;
import com.lockulockme.lockuchat.utils.im.MsgSendController;
import com.lockulockme.lockuchat.utils.permission.OnPermissionsListener;
import com.lockulockme.lockuchat.utils.permission.PermissionsUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NetCallAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends BaseActivity implements MsgSendController {
    private static final String TAG = "ChatActivity";
    private static final int PICTURE_REQUEST_CODE = 10001;
    private AcChatBinding binding;


    private static final int ENTERING = 101;
    private static final int ENTERED = 102;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ENTERING:
                    binding.tvEntering.setVisibility(View.VISIBLE);
                    binding.llNavTitle.setVisibility(View.GONE);
                    break;
                case ENTERED:
                    binding.tvEntering.setVisibility(View.GONE);
                    binding.llNavTitle.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private BottomHelper bottomHelper;
    public User sendUser;
    private String sendAccount;
    private static final String SEND_USER = "send_user";
    private static final int LOAD_MSG_COUNT = 20;
    public ChatAdapter chatAdapter;
    public SessionTypeEnum sessionType = SessionTypeEnum.P2P;
    boolean firstLoad = true;
    private LinearLayoutManager layoutManager;

    private List<OnChatTransformListener> onChatTransformListenerList = new ArrayList<>();

    private ChatOnPermissionsListener pictureChatOnPermissionsListener;
    private ChatOnPermissionsListener videoChatOnPermissionsListener;
    private ChatOnPermissionsListener audioChatOnPermissionsListener;
    private ChatOnPermissionsListener recordAudioChatOnPermissionsListener;

    public static void startMe(Context context, Object user) {
        Gson gson = new Gson();
        String sendUserJson = gson.toJson(user);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(SEND_USER, sendUserJson);
        context.startActivity(intent);
    }

    public static void startMe(Context context, String sessionId) {
        List<String> ids = Arrays.asList(sessionId);

        UserBeanCacheUtils.getInstance().getCacheUsers(new Object(), ids, new UserBeanCacheUtils.OnGetUsersLitener() {
            @Override
            public void onGetUsers(List<User> userList) {
                if (userList != null && userList.size() > 0) {
                    startMe(context, userList.get(0));
                }
            }

            @Override
            public void onGetFailed() {
            }
        });

    }

    //    public static void startMe(Context context, User sendUser) {
    //        Intent intent = new Intent(context, ChatActivity.class);
    //        intent.putExtra(SEND_USER, sendUser);
    //        context.startActivity(intent);
    //    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AcChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handIntent();
        initUI();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        layoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(null, sendUser);
        chatAdapter.setMsgSendController(this);
        binding.rvChat.setAdapter(chatAdapter);
        binding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMsgFromLocal();
            }
        });
        chatAdapter.addChildClickViewIds(R.id.iv_head_left, R.id.iv_msg_alert, R.id.content_view);
        chatAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_head_left) {
                    GoNeedUIUtils.getInstance().getGoNeedUI().goUserInfo(ChatActivity.this, sendUser.stringId);
                } else if (view.getId() == R.id.iv_msg_alert) {
                    onAlertClick(chatAdapter.getItem(position));
                } else if (view.getId() == R.id.content_view) {
                    IMMessage message = chatAdapter.getItem(position);
                    if (message.getAttachment() instanceof NetCallAttachment) {
                        NetCallAttachment attachment = (NetCallAttachment) message.getAttachment();
                        if (attachment.getType() == ChannelType.AUDIO.getValue()) {
                            startChatCall(CallType.AUDIO);
                        } else {
                            startChatCall(CallType.VIDEO);
                        }
                    } else if (message.getAttachment() instanceof StrategyAVMsgAttachment) {
                        StrategyAVMsgAttachment attachment = (StrategyAVMsgAttachment) message.getAttachment();
                        if (attachment.getCallType() == StrategyAVMsgAttachment.AUDIO_CALL_TYPE) {
                            startChatCall(CallType.AUDIO);
                        } else {
                            startChatCall(CallType.VIDEO);
                        }
                    }
                }
            }
        });

        binding.layoutEdit.etMsgContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.layoutEdit.etMsgContent.getText().toString().length() > 0) {
                    sendEntering();
                }
            }
        });

        bottomHelper = new BottomHelper(binding, this, handler, sendUser);


        if (sendUser.myLevelData != null && sendUser.myLevelData.myLevel > 0) {
            binding.ivLevel.setVisibility(View.VISIBLE);
            ImageHelper.intoIV4Circle(binding.ivLevel, sendUser.myLevelData.LevelIcon, R.mipmap.level_icon_place_holder, R.mipmap.level_icon_place_holder);
        } else {
            binding.ivLevel.setVisibility(View.GONE);
        }
        loadMsgFromLocal();
        observeEvent();

        receiveMsgListener(true);

        pictureChatOnPermissionsListener = new ChatOnPermissionsListener(ChatOnPermissionsListener.CHAT_PICTURE, this);
        videoChatOnPermissionsListener = new ChatOnPermissionsListener(ChatOnPermissionsListener.CHAT_VIDEO, this);
        audioChatOnPermissionsListener = new ChatOnPermissionsListener(ChatOnPermissionsListener.CHAT_AUDIO, this);
        recordAudioChatOnPermissionsListener = new ChatOnPermissionsListener(ChatOnPermissionsListener.CHAT_RECORD_AUDIO, this);

    }

    public void onAlertClick(IMMessage message) {
        if (message.getDirect() == MsgDirectionEnum.Out) {
            if (message.getStatus() == MsgStatusEnum.fail) {
                reSendMsg(message); // 重发
            } else {
                if (message.getAttachment() instanceof FileAttachment) {

                } else {
                    reSendMsg(message);
                }
            }
        }
    }

    private void reSendMsg(IMMessage message) {
        IMMessage oldMsg = null;
        for (IMMessage datum : chatAdapter.getData()) {
            if (TextUtils.equals(datum.getUuid(), message.getUuid())) {
                oldMsg = datum;
            }
        }
        if (oldMsg != null) {
            oldMsg.setStatus(MsgStatusEnum.sending);
            chatAdapter.remove(oldMsg);
            onSendMsg(oldMsg);
        }
        sendIMMsg(message, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setReadedMsg();
        if (bottomHelper != null)
            bottomHelper.onResume();
    }


    private void observeEvent() {
        VipStatusListener.getInstance().observeVipStatus(this, new androidx.lifecycle.Observer<VipStatus>() {
            @Override
            public void onChanged(VipStatus vipStatus) {
                VipDiamondsHelper.getInstance().resetVipStatus();
                VipDiamondsHelper.getInstance().getVipStatus(ChatActivity.this.toString(), new VipDiamondsHelper.OnVipStatusListener() {
                    @Override
                    public void onSuccess(int status) {
                        bottomHelper.initVipUI(status);
                    }

                    @Override
                    public void onFailed() {
                        bottomHelper.initVipUI(0);
                    }
                });
            }
        });
        EventHelper.getInstance().observeMe2MeBlockEvent(this.toString(), blockEvent -> {
            if (blockEvent.userStringId.equals(sendUser.stringId)) {
//                    删除会话数据
                finish();
            }
        });
        EventHelper.getInstance().observeApp2MeBlockEvent(this.toString(), blockEvent -> {
            if (blockEvent.userStringId.equals(sendUser.stringId)) {
//                    删除会话数据
                finish();
            }
        });

        EventHelper.getInstance().observeStrategyMsg(this.toString(), message -> {
            int index = 0;
            for (IMMessage datum : chatAdapter.getData()) {
                if (datum.getUuid().equals(message.getUuid())) {
                    datum.setStatus(MsgStatusEnum.read);
                    chatAdapter.notifyItemChanged(index);
                    break;
                }
                index++;
            }
        });
    }

    private void initUI() {
        binding.tvNavTitle.setText(sendUser.nick);
        ImageHelper.intoIV4Circle(binding.ivCountry, sendUser.countryUrl, -1, -1);
    }

    public void onStart() {
        super.onStart();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.ivVideoAnim, "scaleX", 0.67f, 1f, 0.67f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(binding.ivVideoAnim, "scaleY", 0.67f, 1f, 0.67f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1500);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.playTogether(objectAnimator, objectAnimator2);
        animatorSet.start();
    }

    public void onStop() {
        super.onStop();
        binding.ivVideoAnim.clearAnimation();
    }

    Observer<List<IMMessage>> msgComeInObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            LogHelper.e(TAG, "incomingMessageObserver");
            if (messages != null && !messages.isEmpty()) {
                onMsgComeIn(messages);
            }
        }
    };

    public void onMsgComeIn(List<IMMessage> messages) {
        boolean lastMsgIsVisible = lastMsgIsVisible();
        boolean isNotify = false;
        for (IMMessage message : messages) {
            if (message.getMsgType().getValue() == MsgTypeEnum.custom.getValue() && message.getAttachment() instanceof StrategyAVMsgAttachment) {
                StrategyAVMsgAttachment msgAttachment = (StrategyAVMsgAttachment) message.getAttachment();
                if (msgAttachment.isDelayShow()) {
                    message.setStatus(MsgStatusEnum.draft);
                }
            }
            if (isMeMessage(message)) {
                chatAdapter.getData().add(message);
                isNotify = true;
            }
        }
        if (isNotify) {
            sortMsgOrder(chatAdapter.getData());
            chatAdapter.notifyDataSetChanged();
            lastMessageShow();
        }
        IMMessage lastMsg = messages.get(messages.size() - 1);
        if (isMeMessage(lastMsg)) {
            if (lastMsgIsVisible) {
                scrollToBottom();
            }
        }
    }


    private void sortMsgOrder(List<IMMessage> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static final Comparator<IMMessage> comp = new Comparator<IMMessage>() {

        @Override
        public int compare(IMMessage o1, IMMessage o2) {
            long time = o1.getTime() - o2.getTime();
            return time == 0 ? 0 : (time < 0 ? -1 : 1);
        }
    };

    private boolean lastMsgIsVisible() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.rvChat.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisiblePosition >= chatAdapter.getItemCount() - 1;
    }

    private final Observer<IMMessage> msgStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (isMeMessage(message)) {
                LogHelper.e(TAG, String.format("content11: %s, callbackExt: %s", message.getContent(), message.getCallbackExtension()));
                onMsgStatusChanged(message);
            }
        }
    };

    private void onMsgStatusChanged(IMMessage message) {
        int index = getMsgInAdapter(message.getUuid());
        if (index < 0) {
            if (message.getDirect() == MsgDirectionEnum.Out) {
                addMessageIfBigFirstMsg(message);
            }
        } else if (index >= 0 && index < chatAdapter.getItemCount()) {
            updateMessage(message, index);
        }
        lastMessageShow();
    }


    private int getMsgInAdapter(String uuid) {
        List<IMMessage> items = chatAdapter.getData();
        for (int i = 0; i < items.size(); i++) {
            IMMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    private void addMessageIfBigFirstMsg(IMMessage message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<IMMessage> items = chatAdapter.getData();
                boolean insert = items.size() == 0 || message.getTime() > items.get(0).getTime();
                if (insert) {
                    int index = 0;
                    for (index = 0; index < items.size(); index++) {
                        IMMessage item = items.get(index);
                        if (message.getTime() < item.getTime()) {
                            break;
                        }
                    }
                    chatAdapter.addData(index, message);

                    chatAdapter.notifyItemChanged(index);
                }
            }
        });

    }

    private void updateMessage(IMMessage message, int index) {
        chatAdapter.setData(index, message);
    }

    private boolean isMeMessage(IMMessage message) {
        return message.getSessionId() != null
                && message.getSessionId().equals(sendAccount);
    }

    public void receiveMsgListener(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(msgComeInObserver, register);//接受观察
        service.observeMsgStatus(msgStatusObserver, register);
        service.observeCustomNotification(customNotificationObserver, register);
    }

    private void handIntent() {
        sendUser = new Gson().fromJson(getIntent().getStringExtra(SEND_USER), User.class);
        sendAccount = sendUser.accid;
        if (TextUtils.isEmpty(sendAccount)) {
            throw new ClassCastException("UserBean is Error");
        }

    }

    @Override
    public void onBackNavClick(View view) {
        bottomHelper.hideKeyboard();
        super.onBackNavClick(view);
    }

    public void onChatNavClick(View view) {
        if (view.getId() == R.id.iv_phone) {
            PermissionsUtils.getInstance().getPermissions().request(this, audioChatOnPermissionsListener, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        } else if (view.getId() == R.id.ripple_background) {
            PermissionsUtils.getInstance().getPermissions().request(this, videoChatOnPermissionsListener, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (view.getId() == R.id.iv_gift) {
            bottomHelper.onBtnClick(view);
        } else if (view.getId() == R.id.iv_more) {
            ChatOptionActivity.startMe(this, sendUser);
            //            NERTCEndActivity.start(this);
        }
    }

    private static class ChatOnPermissionsListener implements OnPermissionsListener {
        private WeakReference<ChatActivity> weakReference;
        public static final int CHAT_PICTURE = 1;
        public static final int CHAT_VIDEO = 2;
        public static final int CHAT_AUDIO = 3;
        public static final int CHAT_RECORD_AUDIO = 4;
        private int type;

        public ChatOnPermissionsListener(int type, ChatActivity activity) {
            this.type = type;
            this.weakReference = new WeakReference<>(activity);
        }

        public void clear() {
            if (weakReference != null) {
                weakReference.clear();
            }
        }

        @Override
        public void onResult(boolean granted) {
            ChatActivity activity = null;
            if (weakReference != null) {
                activity = weakReference.get();
            }
            if (activity == null) {
                return;
            }
            switch (type) {
                case CHAT_PICTURE:
                    if (granted) {
                        MatisseHelper.getLocalImage(activity);
                    } else {
                        ToastUtils.toastShow(R.string.open_permission_to_use);
                    }

                    break;
                case CHAT_VIDEO:
                    if (granted) {
                        activity.startChatCall(CallType.VIDEO);
                    } else {
                        ToastUtils.toastShow(R.string.open_permission_to_chat);
                    }
                    break;
                case CHAT_AUDIO:
                    if (granted) {
                        activity.startChatCall(CallType.AUDIO);
                    } else {
                        ToastUtils.toastShow(R.string.open_permission_to_chat);
                    }
                    break;
                case CHAT_RECORD_AUDIO:
                    if (granted) {
                        activity.bottomHelper.switchVoiceKeyboard();
                    } else {
                        ToastUtils.toastShow(R.string.open_permission_to_chat);
                    }
                    break;
            }

        }
    }

    public void onBtnClick(View view) {
        if (view.getId() == R.id.btn_send) {
            sendTextMsg(binding.layoutEdit.etMsgContent.getText().toString().trim());
        } else if (view.getId() == R.id.ll_picture) {
            PermissionsUtils.getInstance().getPermissions().request(this, pictureChatOnPermissionsListener, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (view.getId() == R.id.tv_novip) {
            GoNeedUIUtils.getInstance().goVipPage(this);
        } else if (view.getId() == R.id.iv_record_voice) {
            PermissionsUtils.getInstance().getPermissions().request(this, recordAudioChatOnPermissionsListener,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            bottomHelper.onBtnClick(view);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            List<File> fileList = MatisseHelper.getFile(selectList, getContentResolver());
            if (fileList != null && fileList.size() > 0)
                sendPictureMsg(fileList.get(0));
        }
    }

    public void sendVoiceMsg(File audioFile, long audioLength) {
        IMMessage message = MessageBuilder.createAudioMessage(sendAccount, sessionType, audioFile, audioLength);
        sendMsg(message);
    }

    public void sendPictureMsg(File file) {
        IMMessage message = MessageBuilder.createImageMessage(sendAccount, sessionType, file);
        sendMsg(message);
    }

    private static class OnChatTransformListener implements TransformTextMsgUtils.OnTransformListener {
        WeakReference<ChatActivity> weakReference;
        IMMessage message;

        public OnChatTransformListener(ChatActivity activity, IMMessage message) {
            weakReference = new WeakReference<>(activity);
            this.message = message;
        }

        public void clear() {
            weakReference.clear();
            weakReference = null;
        }

        @Override
        public void onSuccess(String result) {
            if (weakReference != null) {
                ChatActivity activity = weakReference.get();
                if (activity != null) {
                    Map<String, Object> extensions = message.getRemoteExtension();
                    if (extensions == null) {
                        extensions = new HashMap<>();
                    }
                    extensions.put(Extra.TRANSFORM_KEY, result);
                    message.setRemoteExtension(extensions);

                    activity.sendMsg(message);
                    activity.clearTransformListener(this);
                }
            }


        }

        @Override
        public void onFailed(String msg) {
            if (weakReference != null) {
                ChatActivity activity = weakReference.get();
                if (activity != null) {
                    activity.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.sendMsg(message);
                        }
                    }, 200);
                    activity.clearTransformListener(this);
                }
            }


        }
    }

    private void clearTransformListener(OnChatTransformListener onChatTransformListener) {
        onChatTransformListenerList.remove(onChatTransformListener);
        onChatTransformListener.clear();
    }

    public void sendTextMsg(String text) {
        if (TextUtils.isEmpty(text)) {
            binding.layoutEdit.etMsgContent.setText("");
            return;
        }
        binding.layoutEdit.etMsgContent.setText("");
        IMMessage message = MessageBuilder.createTextMessage(sendAccount, sessionType, text);
        onSendMsg(message);

        OnChatTransformListener onChatTransformListener = new OnChatTransformListener(this, message);
        onChatTransformListenerList.add(onChatTransformListener);
        TransformTextMsgUtils.getInstance().transformTextMsg(sendUser.ctry, text, onChatTransformListener);
    }

    private void onSendMsg(IMMessage message) {
        chatAdapter.addData(message);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
            }
        }, 200);

    }

    private void loadMsgFromLocal() {
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(getAnchorMsg(), QueryDirectionEnum.QUERY_OLD, LOAD_MSG_COUNT, true)
                .setCallback(loadMsgCallBack);
    }

    private IMMessage getAnchorMsg() {
        if (chatAdapter.getItemCount() == 0) {
            return MessageBuilder.createEmptyMessage(sendAccount, sessionType, 0);
        } else {
            return chatAdapter.getItem(0);
        }
    }


    private final RequestCallback<List<IMMessage>> loadMsgCallBack = new RequestCallbackWrapper<List<IMMessage>>() {
        @Override
        public void onResult(int code, List<IMMessage> messages, Throwable exception) {
            if (code != ResponseCode.RES_SUCCESS || exception != null) {
                finishLoadMoreUI();
                return;
            }

            if (messages != null) {
                onMessageLoaded(messages);
            }
        }
    };

    private void finishLoadMoreUI() {
        binding.srlRefresh.setRefreshing(false);
    }

    private void onMessageLoaded(final List<IMMessage> messages) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                finishLoadMoreUI();
                if (messages == null) {
                    return;
                }

                // 在第一次加载的过程中又收到了新消息，做一下去重
                if (firstLoad && chatAdapter.getItemCount() > 0) {
                    for (IMMessage message : messages) {
                        int removeIndex = 0;
                        for (IMMessage item : chatAdapter.getData()) {
                            if (item.isTheSame(message)) {
                                chatAdapter.removeAt(removeIndex);
                                break;
                            }
                            removeIndex++;
                        }
                    }
                }

                chatAdapter.addData(0, messages);

                if (firstLoad) {
                    scrollToBottom();
                }
                firstLoad = false;
                lastMessageShow();
            }
        });

    }

    public void scrollToBottom() {
        binding.rvChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        //        layoutManager.scrollToPositionWithOffset(chatMsgAdapter.getItemCount() - 1, Integer.MIN_VALUE);
    }


    @Override
    public void sendMsg(IMMessage message) {
        ExtraUtils.msgAddExtension(message);

        onSendMsg(message);

        sendIMMsg(message, false);
        //        sendLocalIMMsg(message);
    }

    /**
     * //此处加入接口判断 如果可以发送调用以下方法 onSendIMMsg ，否则调用 sendLocalIMMsg(message)
     *
     * @param message
     * @param resend
     */
    private void sendIMMsg(IMMessage message, boolean resend) {
        onSendIMMsg(message, resend);
    }

    private void onSendIMMsg(IMMessage message, boolean resend) {
        NIMClient.getService(MsgService.class).sendMessage(message, resend);
    }

    /**
     * 保存消息到本地
     *
     * @param message
     */
    private void sendLocalIMMsg(IMMessage message) {

        List<String> uuids = new ArrayList<>();
        uuids.add(message.getUuid());
        NIMClient.getService(MsgService.class).queryMessageListByUuid(uuids).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> list, Throwable throwable) {
                message.setStatus(MsgStatusEnum.fail);
                if (code != ResponseCode.RES_SUCCESS || list == null || list.size() == 0) {
                    NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, false, System.currentTimeMillis());
                }
                int pos = getMsgInAdapter(message.getUuid());
                if (pos >= 0 && pos < chatAdapter.getItemCount()) {
                    chatAdapter.notifyItemChanged(pos);
                } else {
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventHelper.getInstance().clear(this.toString());
        AudioMsgPlayer.getInstance().stop();
        handler.removeCallbacksAndMessages(null);
        receiveMsgListener(false);
        if (onChatTransformListenerList != null && onChatTransformListenerList.size() > 0) {
            for (OnChatTransformListener onChatTransformListener : onChatTransformListenerList) {
                if (onChatTransformListener != null) {
                    onChatTransformListener.clear();
                }
            }
        }

    }

    private void setReadedMsg() {
        NIMClient.getService(MsgService.class).setChattingAccount(sendAccount, SessionTypeEnum.P2P);
    }

    public void startChatCall(final CallType callType) {
        if (ChatAnswerUtils.getInstance().isAnsweringOrCanChat()) {
            return;
        }
        VipDiamondsHelper.getInstance().getVipStatus(ChatActivity.this.toString(), new VipDiamondsHelper.OnVipStatusListener() {
            @Override
            public void onSuccess(int status) {
                if (status == 1) {
                    VipDiamondsHelper.getInstance().diamondsIsEnough(ChatActivity.this.toString(), sendUser.stringId,
                            callType == CallType.VIDEO ? VipDiamondsHelper.videoChat : VipDiamondsHelper.voiceChat,
                            new VipDiamondsHelper.OnDiamondsEnoughListener() {
                                @Override
                                public void onSuccess(boolean isEnough) {
                                    if (isEnough) {
                                        AVChatJump.goAudioVideoCall(ChatActivity.this, callType, sendUser);
                                    } else {
                                        GoNeedUIUtils.getInstance().goDiamondsPage(ChatActivity.this);
                                    }
                                }

                                @Override
                                public void onFailed() {
                                    GoNeedUIUtils.getInstance().goDiamondsPage(ChatActivity.this);
                                }
                            });
                } else {
                    GoNeedUIUtils.getInstance().goVipPage(ChatActivity.this);
                }
            }

            @Override
            public void onFailed() {
                ToastUtils.toastShow(R.string.get_vip_failed);
            }

        });


    }


    private long enteringTime;
    private static final String KEY_ENTERING_ID = "id";
    private static final String KEY_ENTERING_SHOWTIME = "showTime";
    private static final String KEY_ENTERING_DATA = "data";
    private static final String KEY_ENTERING_ID_VALUE = "1001";
    private static final int KEY_ENTERING_SHOWTIME_DEFAULT = 3;//默认显示2秒

    private void sendEntering() {
        if (System.currentTimeMillis() - enteringTime > 2000L) {
            enteringTime = System.currentTimeMillis();
            CustomNotification command = new CustomNotification();
            command.setSessionId(sendAccount);
            command.setSessionType(sessionType);
            CustomNotificationConfig config = new CustomNotificationConfig();
            config.enablePush = false;
            config.enableUnreadCount = false;
            command.setConfig(config);
            command.setSendToOnlineUserOnly(true);

            JSONObject contentJson = new JSONObject();
            try {
                contentJson.put(KEY_ENTERING_ID, KEY_ENTERING_ID_VALUE);
                JSONObject dataJson = new JSONObject();
                dataJson.put(KEY_ENTERING_SHOWTIME, KEY_ENTERING_SHOWTIME_DEFAULT);
                contentJson.put(KEY_ENTERING_DATA, dataJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            command.setContent(contentJson.toString());

            NIMClient.getService(MsgService.class).sendCustomNotification(command);
        }
    }


    /**
     * 命令消息接收观察者
     */
    private Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!sendAccount.equals(message.getSessionId()) || message.getSessionType() != SessionTypeEnum.P2P) {
                return;
            }
            showEnterMessage(message);
        }
    };

    protected void showEnterMessage(CustomNotification message) {
        if (getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
            return;
        }
        String content = message.getContent();
        try {
            JSONObject contentJson = new JSONObject(content);
            String id = contentJson.getString(KEY_ENTERING_ID);
            if (TextUtils.equals(id,KEY_ENTERING_ID_VALUE)) {
                JSONObject dataJson = contentJson.getJSONObject(KEY_ENTERING_DATA);
                int showTime = dataJson.getInt(KEY_ENTERING_SHOWTIME);
                handler.removeMessages(ENTERING);
                handler.removeMessages(ENTERED);
                handler.sendEmptyMessage(ENTERING);
                long showLong = showTime * 1000;
                if (showLong == 0) {
                    showLong = 2000;
                }
                handler.sendEmptyMessageDelayed(ENTERED, showLong);
            }
        } catch (Exception ignored) {

        }
    }

    private void showQAMessage(IMMessage imMessage) {
        binding.rootQa.setVisibility(View.VISIBLE);
        QAMsgAttachment attachment = (QAMsgAttachment) imMessage.getAttachment();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvAnswer.setLayoutManager(layoutManager);
        final QAAnswerAdapter qaAnswerAdapter = new QAAnswerAdapter(attachment.getAnswers());
        binding.rvAnswer.setAdapter(qaAnswerAdapter);

        binding.tvTitle.setText(attachment.getQuestion());

        qaAnswerAdapter.setOnItemClickListener((adapter, view, position) -> {
            hideQAMessage();
            QAAnswerMsgAttachment answerMsgAttachment = new QAAnswerMsgAttachment();
            QAAnswer answer = qaAnswerAdapter.getItem(position);
            answerMsgAttachment.setAnswer(answer.answer);
            answerMsgAttachment.setIds(answer.ids);
            IMMessage qaIMMessage = MessageBuilder.createCustomMessage(imMessage.getSessionId(), SessionTypeEnum.P2P, answerMsgAttachment);
            IMMsgHelper.storeRemoteExtension(qaIMMessage);
            Map<String, Object> extensions = qaIMMessage.getRemoteExtension();
            extensions.put("qaAnswer", "1");
            qaIMMessage.setRemoteExtension(extensions);
            sendMsg(qaIMMessage);
        });
    }

    private void hideQAMessage() {
        binding.rootQa.setVisibility(View.GONE);
    }

    /**
     * 分别在：初始化历史消息、收到新消息、发送消息成功的时候调用了
     * 需要在改变聊天适配器条数的时候调用
     */
    private void lastMessageShow() {
        if (chatAdapter.getData().size() <= 0) {
            return;
        }
        IMMessage imMessage = chatAdapter.getItem(chatAdapter.getData().size() - 1);
        MsgAttachment attachment = imMessage.getAttachment();
        if (attachment instanceof QAMsgAttachment) {
            showQAMessage(imMessage);
            binding.layoutNovip.getRoot().setVisibility(View.GONE);
            binding.layoutWaitForReplay.getRoot().setVisibility(View.GONE);
            return;
        }

        if (attachment instanceof QAAnswerMsgAttachment && imMessage.getStatus().getValue() == MsgStatusEnum.success.getValue()){
            binding.layoutNovip.getRoot().setVisibility(View.GONE);
            binding.layoutWaitForReplay.getRoot().setVisibility(View.VISIBLE);
            hideQAMessage();
            if (bottomHelper.mVipStatus == 0){
                ToastUtils.toastShow(R.string.reply_success);
            }
            return;
        }else if (attachment instanceof QAAnswerMsgAttachment && imMessage.getStatus().getValue() == MsgStatusEnum.fail.getValue()){
            binding.layoutNovip.getRoot().setVisibility(View.GONE);
            binding.layoutWaitForReplay.getRoot().setVisibility(View.GONE);
            hideQAMessage();
            return;
        }

        hideQAMessage();
        binding.layoutNovip.getRoot().setVisibility(View.VISIBLE);
        binding.layoutWaitForReplay.getRoot().setVisibility(View.GONE);

    }
}
