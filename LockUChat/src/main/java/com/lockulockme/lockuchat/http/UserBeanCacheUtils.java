package com.lockulockme.lockuchat.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;
import com.lockulockme.lockuchat.bean.rsp.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserBeanCacheUtils {
    private ConcurrentHashMap<String, User> userCacheMap = new ConcurrentHashMap<>();
    private Gson gson = new Gson();

    private static class InstanceHolder {
        private static UserBeanCacheUtils INSTANCE = new UserBeanCacheUtils();
    }
    private User getUserCache(String key){
        User userBean = userCacheMap.get(key);
        if (userBean == null){
            return null;
        }
        String userStr = gson.toJson(userBean);
        User newUserBean = gson.fromJson(userStr,User.class);
        return newUserBean;
    }

    private void putCacheUser(String key, User userBean){
        String userStr = gson.toJson(userBean);
        User newUserBean = gson.fromJson(userStr,User.class);
        userCacheMap.put(key,newUserBean);
    }
    HashMap<Object, HashMap<Object, OnGetUsersLitener>> userBeanCacheUtils = new HashMap<>();


    public static UserBeanCacheUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void getCacheUsers(Object tag, List<String> ids, OnGetUsersLitener onBackUserBeans) {
        List<String> queryIds = new ArrayList<>();
        List<User> oldUserBeans = new ArrayList<>();
        for (String id : ids) {
            if (userCacheMap.containsKey(id) && userCacheMap.get(id) != null) {
                oldUserBeans.add(getUserCache(id));
            } else {
                queryIds.add(id);
            }
        }
        if (queryIds.size()==0){
            onBackUserBeans.onGetUsers(oldUserBeans);
            return;
        }

        Object object = new Object();

        HashMap<Object, OnGetUsersLitener>  onGetVipListenerHashMap = userBeanCacheUtils.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap<>();
            userBeanCacheUtils.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onBackUserBeans);

        NetDataUtils.getInstance().getNetData().batchGetUserDetail(queryIds, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getListData(response, User.class);
                if (baseBean.code != 0) {
                    return;
                }
                List<User> userList= (List<User>) baseBean.data;
                if (userList!=null&&userList.size()>0){
                    for (User userBean : userList) {
                        putCacheUser(userBean.stringId,userBean);
                    }
                    oldUserBeans.addAll(userList);
                    if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                        userBeanCacheUtils.get(tag).get(object).onGetUsers(oldUserBeans);
                    }
                }else {
                    if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                        userBeanCacheUtils.get(tag).get(object).onGetFailed();
                    }
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                    userBeanCacheUtils.get(tag).get(object).onGetFailed();
                }

            }
        });
    }

    public void clearVipListeners(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            HashMap<Object, OnGetUsersLitener> hashMap = userBeanCacheUtils.get(tag);
            if (hashMap != null) {
                hashMap.clear();
            }
            userBeanCacheUtils.remove(tag);
        }
    }

    public void getNetworkUsers(Object tag, List<String> ids, OnGetUsersLitener onBackUserBeans) {
        Object object = new Object();

        HashMap<Object, OnGetUsersLitener>  onGetVipListenerHashMap = userBeanCacheUtils.get(tag);
        if (onGetVipListenerHashMap == null) {
            onGetVipListenerHashMap = new HashMap<>();
            userBeanCacheUtils.put(tag, onGetVipListenerHashMap);
        }
        onGetVipListenerHashMap.put(object, onBackUserBeans);
        NetDataUtils.getInstance().getNetData().batchGetUserDetail(ids, new NetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                BaseBean baseBean = GsonUtils.getInstance().getListData(response, User.class);
                if (baseBean.code != 0) {
                    return;
                }
                List<User> userList= (List<User>) baseBean.data;
                if (userList!=null&&userList.size()>0){
                    for (User userBean : userList) {
                        putCacheUser(userBean.stringId,userBean);
                    }
                    if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                        userBeanCacheUtils.get(tag).get(object).onGetUsers(userList);
                    }
                }else {
                    if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                        userBeanCacheUtils.get(tag).get(object).onGetFailed();
                    }
                }
            }

            @Override
            public void onFailed(String msg, int code) {
                if (userBeanCacheUtils != null && userBeanCacheUtils.get(tag) != null && userBeanCacheUtils.get(tag).get(object) != null) {
                    userBeanCacheUtils.get(tag).get(object).onGetFailed();
                }
            }
        });
    }


    public interface OnGetUsersLitener {
        void onGetUsers(List<User> users);
        void onGetFailed();
    }
}
