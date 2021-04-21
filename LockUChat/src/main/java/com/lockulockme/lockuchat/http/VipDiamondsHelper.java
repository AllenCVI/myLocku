package com.lockulockme.lockuchat.http;

import android.text.TextUtils;

import com.lockulockme.lockuchat.bean.rsp.AllowCallRsp;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.DiamondsEnoughRsp;
import com.lockulockme.lockuchat.bean.rsp.GetVipStatusRsp;
import com.lockulockme.lockuchat.bean.rsp.UserStrategyPermissionRsp;
import com.lockulockme.lockuchat.bean.rst.DiamondEnoughRst;
import com.lockulockme.lockuchat.bean.rst.ReduceDiamondsRst;
import com.lockulockme.lockuchat.common.VipStatusListener;
import com.lockulockme.lockuchat.router.GoNeedUIUtils;

import java.util.HashMap;

public class VipDiamondsHelper {
    private int vipStatus = -1;
    private long lastGetVipTime;

    private static class InstanceHelper {
        private static final VipDiamondsHelper INSTANCE = new VipDiamondsHelper();
    }

    public static VipDiamondsHelper getInstance() {
        return VipDiamondsHelper.InstanceHelper.INSTANCE;
    }

    HashMap<Object, HashMap<Object, OnVipStatusListener>> onVipStatusListenerMap = new HashMap<>();
    HashMap<Object, HashMap<Object, OnDiamondsEnoughListener>> diamondListenerHashMap = new HashMap<>();
    HashMap<Object, HashMap<Object, OnReduceListener>> reduceListenerHashMap = new HashMap<>();
    HashMap<Object, HashMap<Object, OnAllowCallListener>> allowListenerHashMap = new HashMap<>();

    public void resetVipStatus() {
        vipStatus = -1;
    }

    public void getVipStatus(Object tag, OnVipStatusListener onVipStatusListener) {
        Object object = new Object();
        HashMap<Object, OnVipStatusListener> onGetVipListenerHashMap = onVipStatusListenerMap.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap<>();
            onVipStatusListenerMap.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onVipStatusListener);
        if (vipStatus == -1 || System.currentTimeMillis() - lastGetVipTime > 5 * 60 * 1000) {
            NetDataUtils.getInstance().getNetData().getSelfIsVip(new NetDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(String response) {
                    BaseBean baseBean = GsonUtils.getInstance().getData(response, GetVipStatusRsp.class);
                    if (baseBean.code != 0) {
                        vipStatus = -1;
                        if (onVipStatusListenerMap != null && onVipStatusListenerMap.get(tag) != null && onVipStatusListenerMap.get(tag).get(object) != null) {
                            onVipStatusListenerMap.get(tag).get(object).onFailed();
                        }
                        return;
                    }
                    GetVipStatusRsp getVipStatus = (GetVipStatusRsp) baseBean.data;
                    vipStatus = getVipStatus.isMember ? 1 : 0;
                    if (onVipStatusListenerMap != null && onVipStatusListenerMap.get(tag) != null && onVipStatusListenerMap.get(tag).get(object) != null) {
                        onVipStatusListenerMap.get(tag).get(object).onSuccess(vipStatus);
                    }
                    lastGetVipTime = System.currentTimeMillis();
                }

                @Override
                public void onFailed(String msg, int code) {
                    vipStatus = -1;
                    if (onVipStatusListenerMap != null && onVipStatusListenerMap.get(tag) != null && onVipStatusListenerMap.get(tag).get(object) != null) {
                        onVipStatusListenerMap.get(tag).get(object).onFailed();
                    }
                }
            });
        } else {
            if (onVipStatusListenerMap != null && onVipStatusListenerMap.get(tag) != null && onVipStatusListenerMap.get(tag).get(object) != null) {
                onVipStatusListenerMap.get(tag).get(object).onSuccess(vipStatus);
            }
        }

    }

    public interface OnVipStatusListener {
        void onSuccess(int status);

        void onFailed();
    }

    public interface OnDiamondsEnoughListener {
        void onSuccess(boolean isEnough);

        void onFailed();
    }

    public interface OnReduceListener {
        void onSuccess();

        void onFailed(int code);
    }

    public interface OnAllowCallListener {
        void onSuccess(AllowCallRsp allowCallRsp);

        void onFailed();
    }

    public interface onUserPermission {
        void onSuccess(UserStrategyPermissionRsp rsp);

        void onFailed(int code);
    }

    public interface onUserRateListener {
        void onSuccess(boolean isRate);

        void onFailed(int code);
    }

    public void diamondsIsEnough4AudioChat(Object tag, String stringId, OnDiamondsEnoughListener onDiamondsEnoughListener) {
        Object object = new Object();
        HashMap<Object, OnDiamondsEnoughListener> onDiamondsEnoughListenerHashMap = diamondListenerHashMap.get(tag);
        if (onDiamondsEnoughListenerHashMap == null) {
            onDiamondsEnoughListenerHashMap = new HashMap<>();
            diamondListenerHashMap.put(tag, onDiamondsEnoughListenerHashMap);
        }
        onDiamondsEnoughListenerHashMap.put(object, onDiamondsEnoughListener);
        DiamondEnoughRst diamondEnough = new DiamondEnoughRst(voiceChat, stringId);
        NetDataUtils.getInstance().getNetData().getDiamondsIsEnough(diamondEnough, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, DiamondsEnoughRsp.class);
                if (baseBean.code != 0) {
                    if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                        diamondListenerHashMap.get(tag).get(object).onFailed();
                    }
                    return;
                }
                DiamondsEnoughRsp diamondsEnoughRsp = (DiamondsEnoughRsp) baseBean.data;
                if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                    diamondListenerHashMap.get(tag).get(object).onSuccess(diamondsEnoughRsp.enough);
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                    diamondListenerHashMap.get(tag).get(object).onFailed();
                }
            }
        });
    }

    public void diamondsIsEnough(Object tag, String stringId, String callType, OnDiamondsEnoughListener onDiamondsEnoughListener) {
        if (callType.equals(videoChat)) {
            diamondsIsEnough4VideoChat(tag, stringId, onDiamondsEnoughListener);
        } else {
            diamondsIsEnough4AudioChat(tag, stringId, onDiamondsEnoughListener);
        }
    }

    public static final String videoChat = "videoChat";
    public static final String voiceChat = "voiceChat";

    public void diamondsIsEnough4VideoChat(Object tag, String stringId, OnDiamondsEnoughListener onDiamondsEnoughListener) {
        Object object = new Object();
        HashMap<Object, OnDiamondsEnoughListener> onDiamondsEnoughListenerHashMap = diamondListenerHashMap.get(tag);
        if (onDiamondsEnoughListenerHashMap == null) {
            onDiamondsEnoughListenerHashMap = new HashMap<>();
            diamondListenerHashMap.put(tag, onDiamondsEnoughListenerHashMap);
        }
        onDiamondsEnoughListenerHashMap.put(object, onDiamondsEnoughListener);
        DiamondEnoughRst diamondEnough = new DiamondEnoughRst(videoChat, stringId);
        NetDataUtils.getInstance().getNetData().getDiamondsIsEnough(diamondEnough, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, DiamondsEnoughRsp.class);
                if (baseBean.code != 0) {
                    if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                        diamondListenerHashMap.get(tag).get(object).onFailed();
                    }
                    return;
                }
                DiamondsEnoughRsp diamondsEnoughRsp = (DiamondsEnoughRsp) baseBean.data;
                if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                    diamondListenerHashMap.get(tag).get(object).onSuccess(diamondsEnoughRsp.enough);
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (diamondListenerHashMap != null && diamondListenerHashMap.get(tag) != null && diamondListenerHashMap.get(tag).get(object) != null) {
                    diamondListenerHashMap.get(tag).get(object).onFailed();
                }
            }
        });
    }

    public void ruduceDiamonds4VoiceChat(Object tag, String targetImId, String channelId, OnReduceListener onReduceListener) {
        Object object = new Object();
        HashMap<Object, OnReduceListener> onGetVipListenerHashMap = reduceListenerHashMap.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap<>();
            reduceListenerHashMap.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onReduceListener);
        ReduceDiamondsRst reduceDiamondsRst = new ReduceDiamondsRst(voiceChat, targetImId, channelId);
        NetDataUtils.getInstance().getNetData().reduceDiamonds(reduceDiamondsRst, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getBaseBean(response);
                if (baseBean.code != 0) {
                    GoNeedUIUtils.getInstance().getGoNeedUI().notifyVIPExpired();
                    VipDiamondsHelper.getInstance().resetVipStatus();
                    VipStatusListener.getInstance().notifyVipStatusChange();
                    if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                        reduceListenerHashMap.get(tag).get(object).onFailed(baseBean.code);
                    }
                    return;
                }
                if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                    reduceListenerHashMap.get(tag).get(object).onSuccess();
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                GoNeedUIUtils.getInstance().getGoNeedUI().notifyVIPExpired();
                VipDiamondsHelper.getInstance().resetVipStatus();
                VipStatusListener.getInstance().notifyVipStatusChange();
                if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                    reduceListenerHashMap.get(tag).get(object).onFailed(code);
                }
            }
        });
    }

    public void ruduceDiamonds4VideoChat(Object tag, String targetImId, String channelId, OnReduceListener onReduceListener) {
        Object object = new Object();
        HashMap<Object, OnReduceListener> onGetVipListenerHashMap = reduceListenerHashMap.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap<>();
            reduceListenerHashMap.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onReduceListener);
        ReduceDiamondsRst reduceDiamondsRst = new ReduceDiamondsRst(videoChat, targetImId, channelId);
        NetDataUtils.getInstance().getNetData().reduceDiamonds(reduceDiamondsRst, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getBaseBean(response);
                if (baseBean.code != 0) {
                    GoNeedUIUtils.getInstance().getGoNeedUI().notifyVIPExpired();
                    VipDiamondsHelper.getInstance().resetVipStatus();
                    VipStatusListener.getInstance().notifyVipStatusChange();
                    if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                        reduceListenerHashMap.get(tag).get(object).onFailed(baseBean.code);
                    }
                    return;
                }
                if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                    reduceListenerHashMap.get(tag).get(object).onSuccess();
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                GoNeedUIUtils.getInstance().getGoNeedUI().notifyVIPExpired();
                VipDiamondsHelper.getInstance().resetVipStatus();
                VipStatusListener.getInstance().notifyVipStatusChange();
                if (reduceListenerHashMap != null && reduceListenerHashMap.get(tag) != null && reduceListenerHashMap.get(tag).get(object) != null) {
                    reduceListenerHashMap.get(tag).get(object).onFailed(code);
                }
            }
        });
    }

    public void allowCall4Video(Object tag, String targetImId, OnAllowCallListener onAllowCallListener) {
        Object object = new Object();
        HashMap<Object, OnAllowCallListener> onGetVipListenerHashMap = allowListenerHashMap.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap();
            allowListenerHashMap.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onAllowCallListener);
        ReduceDiamondsRst reduceDiamondsRst = new ReduceDiamondsRst(videoChat, targetImId);
        NetDataUtils.getInstance().getNetData().allowCall(reduceDiamondsRst, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, AllowCallRsp.class);
                if (baseBean.code != 0) {
                    if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                        allowListenerHashMap.get(tag).get(object).onFailed();
                    }
                    return;
                }
                AllowCallRsp allowCallRsp = (AllowCallRsp) baseBean.data;
                if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                    allowListenerHashMap.get(tag).get(object).onSuccess(allowCallRsp);
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                    allowListenerHashMap.get(tag).get(object).onFailed();
                }
            }
        });
    }

    public void allowCall4Audio(Object tag, String targetImId, OnAllowCallListener onAllowCallListener) {
        Object object = new Object();
        HashMap<Object, OnAllowCallListener> onGetVipListenerHashMap = allowListenerHashMap.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap();
            allowListenerHashMap.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onAllowCallListener);
        ReduceDiamondsRst reduceDiamondsRst = new ReduceDiamondsRst(voiceChat, targetImId);
        NetDataUtils.getInstance().getNetData().allowCall(reduceDiamondsRst, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, AllowCallRsp.class);
                if (baseBean.code != 0) {
                    if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                        allowListenerHashMap.get(tag).get(object).onFailed();
                    }
                    return;
                }
                AllowCallRsp allowCallRsp = (AllowCallRsp) baseBean.data;
                if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                    allowListenerHashMap.get(tag).get(object).onSuccess(allowCallRsp);
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (allowListenerHashMap != null && allowListenerHashMap.get(tag) != null && allowListenerHashMap.get(tag).get(object) != null) {
                    allowListenerHashMap.get(tag).get(object).onFailed();
                }
            }
        });
    }

    public void userAcceptVideoStrategy(Object tag, onUserPermission listener) {
        NetDataUtils.getInstance().getNetData().userAcceptStrategy(tag, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, UserStrategyPermissionRsp.class);
                if (baseBean.code != 0) {
                    listener.onFailed(baseBean.code);
                    return;
                }
                listener.onSuccess((UserStrategyPermissionRsp) baseBean.data);
            }

            @Override
            public void onFailed(String msg, int code) {
                listener.onFailed(code);
            }
        });
    }

    public void userAddVideoStrategyNum(Object tag, OnReduceListener listener) {
        NetDataUtils.getInstance().getNetData().userAddStrategyNum(tag, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getData(response, Object.class);
                if (baseBean.code != 0) {
                    listener.onFailed(baseBean.code);
                    return;
                }
                listener.onSuccess();
            }

            @Override
            public void onFailed(String msg, int code) {
                listener.onFailed(code);
            }
        });
    }

    public void checkUserRate(Object tag, String imId, onUserRateListener listener) {
        NetDataUtils.getInstance().getNetData().hasRate(tag, imId, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getBaseBean(response);
                if (baseBean.code != 0) {
                    listener.onFailed(baseBean.code);
                    return;
                }
                listener.onSuccess((boolean) baseBean.data);
            }

            @Override
            public void onFailed(String msg, int code) {
                listener.onFailed(code);
            }
        });
    }

    public void clearListener(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            HashMap<Object, OnVipStatusListener> hashMap = onVipStatusListenerMap.get(tag);
            if (hashMap != null) {
                hashMap.clear();
            }

            onVipStatusListenerMap.remove(tag);
            HashMap<Object, OnDiamondsEnoughListener> diamondsEnoughListenerHashMap = diamondListenerHashMap.get(tag);
            if (diamondsEnoughListenerHashMap != null) {
                diamondsEnoughListenerHashMap.clear();
            }
            diamondListenerHashMap.remove(tag);

            HashMap<Object, OnAllowCallListener> allowHashMap = allowListenerHashMap.get(tag);
            if (allowHashMap != null) {
                allowHashMap.clear();
            }
            allowListenerHashMap.remove(tag);

            HashMap<Object, OnReduceListener> reduceHashMap = reduceListenerHashMap.get(tag);
            if (reduceHashMap != null) {
                reduceHashMap.clear();
            }
            reduceListenerHashMap.remove(tag);
        }
    }
}
