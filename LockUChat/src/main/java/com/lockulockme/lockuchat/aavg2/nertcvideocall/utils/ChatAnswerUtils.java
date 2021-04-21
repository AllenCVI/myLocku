package com.lockulockme.lockuchat.aavg2.nertcvideocall.utils;


import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.utils.ToastUtils;

public class ChatAnswerUtils {
    private boolean isAnswering=false;
    private ChatAnswerUtils() {
    }

    private static class InstanceHolder {
        private static ChatAnswerUtils INSTANCE = new ChatAnswerUtils();
    }

    public static ChatAnswerUtils getInstance() {
        return ChatAnswerUtils.InstanceHolder.INSTANCE;
    }

    public boolean isAnsweringOrCanChat() {
        if (isAnswering) {
            ToastUtils.toastShow(R.string.answer_ing);
            return isAnswering;
        }
        return false;
    }

    public boolean isAnswering() {
        return isAnswering;
    }

    public void setAnswering(boolean answering) {
        isAnswering = answering;
    }

}
