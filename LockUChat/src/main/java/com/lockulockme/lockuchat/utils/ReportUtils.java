package com.lockulockme.lockuchat.utils;


import com.lockulockme.lockuchat.http.NetDataUtils;

public class ReportUtils {

    public static void report(String page, String subType, int code, String desc) {
        NetDataUtils.getInstance().getNetData().reportException(page, subType, code, desc);
    }
}
