package com.lockulockme.locku.im;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.lockulockme.locku.R;
import com.lockulockme.locku.base.UriConstant;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.module.ui.activity.CompleteInformationActivity;
import com.lockulockme.locku.module.ui.activity.CurrentDiamondActivity;
import com.lockulockme.locku.module.ui.activity.H5Activity;
import com.lockulockme.locku.module.ui.activity.LoginActivity;
import com.lockulockme.locku.module.ui.activity.LoginIndexActivity;
import com.lockulockme.locku.module.ui.activity.PayCenterActivity;
import com.lockulockme.locku.module.ui.activity.RegisterActivity;
import com.lockulockme.locku.module.ui.activity.SetHeaderImgActivity;
import com.lockulockme.locku.module.ui.activity.SplashActivity;
import com.lockulockme.locku.base.utils.AccountManager;
import com.lockulockme.locku.module.ui.activity.VipGoodsListActivity;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.service.CallServiceInstance;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCAudioCallActivity;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCVideoCallActivity;
import com.lockulockme.lockuchat.attach.AskGiftsAttachment;
import com.lockulockme.lockuchat.attach.NotifyMsgAttachment;
import com.lockulockme.lockuchat.ui.FloatAnsweringWindow;
import com.lockulockme.lockuchat.ui.FloatNotificationManager;
import com.lockulockme.lockuchat.common.InitFloatWindow;
import com.lockulockme.lockuchat.common.LockUMixPushMessageHandler;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.http.NetDataUtils;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;
import com.lockulockme.lockuchat.ui.ChatActivity;
import com.lockulockme.lockuchat.ui.LockUIMUserInfoProvider;
import com.lockulockme.lockuchat.ui.NotifyMsgManager;
import com.lockulockme.lockuchat.ui.VideoCustomCallActivity;
import com.lockulockme.lockuchat.utils.ContextHelper;
import com.lockulockme.lockuchat.utils.LogHelper;
import com.lockulockme.lockuchat.utils.ResouseUtils;
import com.lockulockme.lockuchat.utils.ScreenInfo;
import com.lockulockme.lockuchat.utils.im.IMLoginHelper;
import com.lockulockme.lockuchat.utils.im.MsgDigest;
import com.lockulockme.lockuchat.utils.im.SelfAttachmentUtils;
import com.lockulockme.lockuchat.utils.permission.PermissionsUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.NotificationFoldStyle;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.NotificationExtraTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.util.NIMUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class IMInitHelper {
    private IMInitHelper(){
    }
    private static class InstanceHelper{
        private static final IMInitHelper INSTANCE = new IMInitHelper();
    }
    public static IMInitHelper getInstance(){
        return IMInitHelper.InstanceHelper.INSTANCE;
    }
    
    private static final String PROD_KEY = "a8c3512fe0c29421767783f99d694a6f";
    private static final String TEST_KEY = "223ba2656f3f1f04be0193885cdba1a1";
//    private Context context;
    public static String getKey() {
        if (UriConstant.isDebug) {
            return TEST_KEY;
        }
        return PROD_KEY;
    }
    public void init(Application context){
//        this.context=context;
        ContextHelper.getInstance().init(context);
        NetDataUtils.getInstance().setNetData(new NetDataImpl());
        SelfDataUtils.getInstance().setSelfData(new SelfDataImpl());
        GoNeedUIUtils.getInstance().setGoNeedUI(new GoNeedUIImpl());
        PermissionsUtils.getInstance().setPermissions(new PermissionsImpl());
        context.registerActivityLifecycleCallbacks(FloatAnsweringWindow.getInstance());
        FloatAnsweringWindow.getInstance().setApplication(context);
        FloatAnsweringWindow.getInstance().addFilterActivity(SplashActivity.class, LoginIndexActivity.class,LoginActivity.class,RegisterActivity.class);
        InitFloatWindow.initFloatWindow(context);
        NotifyMsgManager.getInstance().init(context,SplashActivity.class);
        SDKOptions sdkOptions = getSDKOptions(context);
        sdkOptions.appKey = getKey();
        LogUtil.LogE("IMInitHelper", getKey());
        NIMClient.init(context, getLoginInfo(), sdkOptions);
        boolean isPrint = context.getResources().getBoolean(R.bool.print);
        LogHelper.isLog=isPrint;
        // 以下逻辑只在主进程初始化时执行
        if (NIMUtil.isMainProcess(context)) {
            // 注册自定义推送消息处理，这个是可选项
            NIMPushClient.registerMixPushMessageHandler(new LockUMixPushMessageHandler());

            // 初始化消息提醒
            NIMClient.toggleNotification(true);
            // 云信sdk相关业务初始化
            registerIMMessageFilter();
            registerLocaleReceiver(context,true);
            IMLoginHelper.getInstance().init();//初始化
            SelfAttachmentUtils.registerSelfAttachmentParser();

            context.registerActivityLifecycleCallbacks(FloatNotificationManager.getInstance());
            context.registerActivityLifecycleCallbacks(IMLoginHelper.getInstance());
            context.registerActivityLifecycleCallbacks(CallServiceInstance.getInstance());
            FloatNotificationManager.getInstance().init(context);
            FloatNotificationManager.getInstance().setSplashActivity(SplashActivity.class);
            FloatNotificationManager.getInstance().setFilterActivity(Arrays.asList(ChatActivity.class, VipGoodsListActivity.class,
                    CurrentDiamondActivity.class, PayCenterActivity.class, H5Activity.class,
                    VideoCustomCallActivity.class, NERTCVideoCallActivity.class, NERTCAudioCallActivity.class,
                    LoginIndexActivity.class,LoginActivity.class,RegisterActivity.class, CompleteInformationActivity.class,
                    SetHeaderImgActivity.class));
            IMLoginHelper.getInstance().setFilterActivity(Arrays.asList(VipGoodsListActivity.class,
                    CurrentDiamondActivity.class, PayCenterActivity.class, H5Activity.class,
                    VideoCustomCallActivity.class, NERTCVideoCallActivity.class, NERTCAudioCallActivity.class,
                    LoginIndexActivity.class,LoginActivity.class,RegisterActivity.class, CompleteInformationActivity.class,
                    SetHeaderImgActivity.class));

            CallServiceInstance.getInstance().setFilterActivity(Arrays.asList(VipGoodsListActivity.class,
                    CurrentDiamondActivity.class, PayCenterActivity.class, H5Activity.class,
                    VideoCustomCallActivity.class, NERTCVideoCallActivity.class, NERTCAudioCallActivity.class,
                    LoginIndexActivity.class,LoginActivity.class,RegisterActivity.class, CompleteInformationActivity.class,
                    SetHeaderImgActivity.class));
        }


    }
    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private static void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(message -> {
            MsgAttachment attachment = message.getAttachment();
            if (attachment != null){

                if (attachment instanceof NotifyMsgAttachment){
                    NotifyMsgAttachment systemMsgAttachment = (NotifyMsgAttachment) message.getAttachment();
                    if (NotifyMsgAttachment.SHOW_TYPE_NO_SHOW.equals(systemMsgAttachment.getIsShow())){
                        NotifyMsgManager.getInstance().showNotification(message);
                        return true;
                    }
                } else if (attachment instanceof AskGiftsAttachment){
                    boolean isFromServer = false;
                    Map<String, Object> map = message.getRemoteExtension();
                    if (map != null && map.containsKey("fromType")) {
                        String fromType = (String) map.get("fromType");
                        isFromServer = "2".equalsIgnoreCase(fromType);
                    }
                    if (isFromServer){
                        return true;
                    }
                }


            }
            return false;
        });
    }

    private static void registerLocaleReceiver(Context context,boolean register) {
        if (register) {
            updateLocale(context);
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            context.registerReceiver(localeReceiver, filter);
        } else {
            context.unregisterReceiver(localeReceiver);
        }
    }

    private static void updateLocale(Context context) {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = context.getString(R.string.notification_status_multi_msg_incoming);
        strings.status_bar_image_message = context.getString(R.string.notification_status_image_msg);
        strings.status_bar_audio_message = context.getString(R.string.notification_status_audio_msg);
        strings.status_bar_custom_message = context.getString(R.string.notification_status_custom_msg);
        strings.status_bar_file_message = context.getString(R.string.notification_status_file_msg);
        strings.status_bar_location_message = context.getString(R.string.notification_status_location_msg);
        strings.status_bar_notification_message = context.getString(R.string.notification_status_notification_msg);
        strings.status_bar_ticker_text = context.getString(R.string.notification_status_ticker_text);
        strings.status_bar_unsupported_message = context.getString(R.string.notification_status_unsupported_msg);
        strings.status_bar_video_message = context.getString(R.string.notification_status_video_msg);
        strings.status_bar_hidden_message_content = context.getString(R.string.notification_status_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    private static BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale(context);
            }
        }
    };


    public static LoginInfo getLoginInfo() {
        String account = null;
        if (AccountManager.getInstance().getCurrentUser()!=null){
            account=AccountManager.getInstance().getCurrentUser().nimId;
        }
        String token = AccountManager.getInstance().getNimToken();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            IMLoginHelper.getInstance().setAccount(account);
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }
    public static SDKOptions getSDKOptions(Context context) {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options);
        // 配置 APP 保存图片/语音/文件/log等数据的目录
        options.sdkStorageRootPath = getAppCacheDir(context) + "/locku"; // 可以不设置，那么将采用默认路径
        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;
        // 配置附件缩略图的尺寸大小
        options.thumbnailSize = getImageMaxEdge();
        // 通知栏显示用户昵称和头像
        options.userInfoProvider = new LockUIMUserInfoProvider(context);
        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;
        // 在线多端同步未读数
        options.sessionReadAck = true;
        // 动图的缩略图直接下载原图
        options.animatedImageThumbnailEnabled = true;
        // 采用异步加载SDK
        options.asyncInitSDK = true;
        // 是否是弱IM场景
        options.reducedIM = false;
        // 是否检查manifest 配置，调试阶段打开，调试通过之后请关掉
        options.checkManifestConfig = false;
        // 是否启用群消息已读功能，默认关闭
        options.enableTeamMsgAck = true;
        // 打开消息撤回未读数-1的开关
        options.shouldConsiderRevokedMessageUnreadCount = true;
        // 云信私有化配置项
//        configServerAddress(options, context);
        options.mixPushConfig = buildMixPushConfig();
        options.loginCustomTag = "登录自定义字段";
        options.useXLog = false;
        // 会话置顶是否漫游
        options.notifyStickTopSession = true;

        return options;
    }


    private static void initStatusBarNotificationConfig(SDKOptions options) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig userConfig = loadStatusBarNotificationConfig();
        options.statusBarNotificationConfig = userConfig;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private static StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = SplashActivity.class;
        config.notificationSmallIconId = R.mipmap.app_icon;
        config.notificationColor = ResouseUtils.getResouseColor(R.color.c_16143c);
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.lockulockme.locku/raw/msg";
        config.notificationFolded = false;
        //        config.notificationFolded = false;
        config.notificationFoldStyle = NotificationFoldStyle.EXPAND;
        config.downTimeEnableNotification = true;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 是否APP ICON显示未读数红点(Android O有效)
        config.showBadge = true;
        config.notificationExtraType = NotificationExtraTypeEnum.MESSAGE;

        IMLoginHelper.setNotificationConfig(config);
        return config;
    }


    private static MixPushConfig buildMixPushConfig() {
        // 第三方推送配置
        MixPushConfig config = new MixPushConfig();

        // fcm 推送，适用于海外用户，不使用fcm请不要配置
        config.fcmCertificateName = "locku_fcm";

        return config;
    }

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenInfo.getInstance().screenWidth);
    }

    public static String getAppCacheDir(Context context) {
        String storageRootPath = null;
        try {
            if (context.getExternalCacheDir() != null) {
                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            storageRootPath = Environment.getExternalStorageDirectory() + "/" + context.getPackageName();
        }
        return storageRootPath;
    }



    private static final MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {

        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return MsgDigest.getMsgDigest(message);
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeRevokeMsgTip(String revokeAccount, IMMessage item) {
            return "";
        }

    };
}
