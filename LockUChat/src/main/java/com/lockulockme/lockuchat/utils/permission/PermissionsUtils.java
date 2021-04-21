package com.lockulockme.lockuchat.utils.permission;


public class PermissionsUtils {
    private Permissions permissions;
    private static class InstanceHelper{
        private static PermissionsUtils INSTANCE = new PermissionsUtils();
    }
    public static PermissionsUtils getInstance(){
        return PermissionsUtils.InstanceHelper.INSTANCE;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}
