package com.lockulockme.lockuchat.aavg2;

import android.app.Activity;

import com.google.gson.Gson;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.CallType;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.aavg2.ui.model.UserModel;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCAudioCallActivity;
import com.lockulockme.lockuchat.aavg2.ui.ui.NERTCVideoCallActivity;
import com.lockulockme.lockuchat.bean.rsp.User;


public class AVChatJump {
    public static void goAudioChat(Activity activity, Object user) {
        Gson gson=new Gson();
        String userJson=gson.toJson(user);
        User userBean=gson.fromJson(userJson,User.class);
        goAudioVideoCall(activity, CallType.AUDIO, userBean);
    }

    public static void goVideoChat(Activity activity, Object user) {
        Gson gson=new Gson();
        String userJson=gson.toJson(user);
        User userBean=gson.fromJson(userJson,User.class);
        goAudioVideoCall(activity, CallType.VIDEO, userBean);
    }
    public static void goAudioChat(Activity activity, User userBean) {
        goAudioVideoCall(activity, CallType.AUDIO, userBean);
    }

    public static void goVideoChat(Activity activity, User userBean) {
        goAudioVideoCall(activity, CallType.VIDEO, userBean);
    }

    public static void goAudioVideoCall(Activity activity, CallType avChatType, User userBean) {
        if (ChatAnswerUtils.getInstance().isAnsweringOrCanChat() || userBean == null){
            return;
        }
        UserModel userModel = new UserModel();
        userModel.imAccid = userBean.accid;
        userModel.nickname = userBean.nick;
        userModel.avatar = userBean.userIcon;
        userModel.countryFlagUrl = userBean.countryUrl;
        if (avChatType == CallType.AUDIO) {
            NERTCAudioCallActivity.startCallOther(activity, userModel);
        } else {
            NERTCVideoCallActivity.startCallOther(activity, userModel);
        }
    }

}
