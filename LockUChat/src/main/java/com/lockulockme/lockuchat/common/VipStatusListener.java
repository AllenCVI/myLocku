package com.lockulockme.lockuchat.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.lockulockme.lockuchat.bean.VipStatus;


public class VipStatusListener {
    private SingleLiveData<VipStatus> vipStatusLiveData=new SingleLiveData<>();
    private static class InstanceHelper{
        private static VipStatusListener INSTANCE = new VipStatusListener();
    }
    public static VipStatusListener getInstance(){
        return VipStatusListener.InstanceHelper.INSTANCE;
    }

    public void notifyVipStatusChange(){
        setVipStatus(new VipStatus());
    }

    private void setVipStatus(VipStatus vipStatus){
        vipStatusLiveData.setValue(vipStatus);
    }

    public void observeVipStatus(@NonNull LifecycleOwner owner, @NonNull Observer<VipStatus> observer){
        vipStatusLiveData.observe(owner,observer);
    }
}
