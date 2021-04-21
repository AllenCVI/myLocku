package com.lockulockme.lockuchat.utils;


import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgerHelper {
    private static boolean support = true;

    public static void updateBadgerNum(final int unreadCount) {
        if (!support) {
            return;
        }
        int badgerCount = unreadCount;
        if (badgerCount < 0) {
            badgerCount = 0;
        } else if (badgerCount > 99) {
            badgerCount = 99;
        }
        boolean res = ShortcutBadger.applyCount(ContextHelper.getInstance().getContext(), badgerCount);
        if (!res) {
            support = false;
        }
    }
}
