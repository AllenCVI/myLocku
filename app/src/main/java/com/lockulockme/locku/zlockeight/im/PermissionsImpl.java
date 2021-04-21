package com.lockulockme.locku.zlockeight.im;


import androidx.fragment.app.FragmentActivity;

import com.lockulockme.lockuchat.utils.permission.OnPermissionsListener;
import com.lockulockme.lockuchat.utils.permission.Permissions;
import com.tbruyelle.rxpermissions3.RxPermissions;

public class PermissionsImpl implements Permissions {
    @Override
    public void request(FragmentActivity activity, OnPermissionsListener onPermissionsListener, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions
                .request(permissions)
                .subscribe(granted -> {
                    onPermissionsListener.onResult(granted);
                });
    }
}
