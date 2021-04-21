package com.lockulockme.lockuchat.utils.permission;

import androidx.fragment.app.FragmentActivity;

public interface Permissions {
    void request(FragmentActivity activity, OnPermissionsListener onPermissionsListener, String... permissions);
}
