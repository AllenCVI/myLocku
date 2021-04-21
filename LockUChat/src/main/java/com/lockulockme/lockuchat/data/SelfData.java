package com.lockulockme.lockuchat.data;

import com.lockulockme.lockuchat.bean.rsp.User;

public interface SelfData {
    /**
     *
     * @return 自己的User对象信息
     */
    User getSelfUser();

    /**
     *
     * @return 1是vip，0不是，-1还没获取
     */
    int getSelfIsVip4Sync();
}
