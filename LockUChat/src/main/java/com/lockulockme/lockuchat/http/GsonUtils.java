package com.lockulockme.lockuchat.http;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.lockulockme.lockuchat.bean.rsp.BaseBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class GsonUtils {

    private final Gson gson;

    private GsonUtils(){
        gson = new Gson();
    }

    private static class InstanceHelper{
        private static GsonUtils INSTANCE = new GsonUtils();
    }
    public static GsonUtils getInstance(){
        return GsonUtils.InstanceHelper.INSTANCE;
    }

    public <T> BaseBean getListData(String response,Class clazz){
        BaseBean baseBean=getBaseBean(response);
        Type type = new ParameterizedTypeImpl(clazz);
        List<LinkedTreeMap<String,Object>> treeMaps= (List<LinkedTreeMap<String, Object>>) baseBean.data;
        String listStr=gson.toJson(treeMaps);
        List<T> list = gson.fromJson(listStr, type);
        baseBean.data=list;
        return baseBean;
    }

    public <T> BaseBean getData(String response,Class<T> clazz){
        BaseBean baseBean=getBaseBean(response);
        LinkedTreeMap<String,Object> treeMap= (LinkedTreeMap<String, Object>) baseBean.data;
        String beanStr=gson.toJson(treeMap);
        T t = gson.fromJson(beanStr,clazz);
        baseBean.data=t;
        return baseBean;
    }

    public BaseBean getBaseBean(String response){
        BaseBean baseBean=gson.fromJson(response , BaseBean.class);
        return baseBean;
    }


    private  class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
