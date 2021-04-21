package com.lockulockme.locku.base;

public class UriConstant {


    public static boolean isDebug = true;

//    public static String BASE_URL = isDebug ? "https://test-api.lockulockme.com" : "https://api.lockulockme.com";
    public static final String DEV_URL = "https://test-api.lockulockme.com";
    public static final String PRO_URL = "https://api.lockulockme.com";

    public static String getBaseUrl() {
        if (isDebug) {
            return DEV_URL;
        }
        return PRO_URL;
    }
    //bindFacebook
    public static final String BIND_FACEBOOK = "/kt3FUiWxmnQyG4Mwu1U3SA==/THY4fKlWUTriqvGR-NF5raDK1NqGpb3PxqbHmz99GP4=";
    //bindGoogle
    public static final String BIND_GOOGLE = "/kt3FUiWxmnQyG4Mwu1U3SA==/THY4fKlWUTriqvGR-NF5rSViNGZnZrG4CMFYyaTe7FY=";
    //一键注册
    public static final String ONE_CLICK_REGISTER = "/kt3FUiWxmnQyG4Mwu1U3SA==/Evdx7yzmxqK42E2w43okRQ==";
    //setpwb
    public static final String SET_PWB = "/kt3FUiWxmnQyG4Mwu1U3SA==/CZBc9QdwPC_X-rQswrQeSg5Gz1zpKQvS5ii8IwW-I_Y=";
    //登陆
    public static final String LOGIN_ACCOUNT = "/kt3FUiWxmnQyG4Mwu1U3SA==/5RuFxbUDQUSPd1PUX6psCg==";
    //完善信息
    public static final String COMPLETE_INFO = "/kt3FUiWxmnQyG4Mwu1U3SA==/AEA7ohe2chGOwCjeZG5s0A==";
    //获取首页列表
    public static final String INDEX_LIST = "/Ykv_YK9otI97cvnOu6CEcQ==/d5u5FHnBAKTHMePFvFrf00iyxmnr1e-OUwbbAIpqjKY=";
    //获取我的等级
    public static final String GET_MY_LEVEL ="/FKOApHFVeWpWcvT3tR0HIw==/MD0wU0CSk55CdXDZaQg-oA==";

    //获取挽留信息
    public static final String RETENTION_INFO = "/kt3FUiWxmnQyG4Mwu1U3SA==/fZrV7q-I2Paifg_ptGl23oluMjwg9VmbxWn2kNwc3SI=";

    //是否提示绑定
    public static final String PROMPT_BIND = "/kt3FUiWxmnQyG4Mwu1U3SA==/looshk8CQPI3Np-onT2NmvbWmhqtnWp7sm7p4stH8ZI=";
    //女性排行榜
    public static final String FEMALE_RANK = "/FKOApHFVeWpWcvT3tR0HIw==/wv48DFDVcupUvtuGp2uAPtuy4SrIpv2o5N41FV4NUaU=";
    //男性排行榜
    public static final String MALE_RANK = "/FKOApHFVeWpWcvT3tR0HIw==/7spXIoJG58-QEUwuN31nPQ==";
    //她的信息
    public static final String SHE_INFO = "/Ykv_YK9otI97cvnOu6CEcQ==/YCJ5wFija9QpPYdQ1mvNGQ==";
    //获取朋友圈相册
    public static final String OTHER_IMAGES = "/Ykv_YK9otI97cvnOu6CEcQ==/F29lKo7jcvuPm0LaOH75Ihdq1u4MQPxo_qW8MIEyxQU=";
    //获取朋友圈短视频
    public static final String OTHER_VIDEOS = "/Ykv_YK9otI97cvnOu6CEcQ==/AAZDEpeomt4q6z4RKyFEWu7tqdcxV5T2whycGfVlfWs=";
    //语音视频聊天价格
    public static final String VOICE_VIDEO_CHAT_PRICE = "/FKOApHFVeWpWcvT3tR0HIw==/YmbrO-v_VPBDm6W1KnO_dTgc4fTPKZdadj4RrVnHzUClSQy24rnEm_gYxB1EvPne";
    //hello
    public static final String SAY_HELLO = "/Ykv_YK9otI97cvnOu6CEcQ==/Rtg5O3Wm8saUsdl93cjNjA==";
    //vip
    public static final String VIP_STATE = "/FKOApHFVeWpWcvT3tR0HIw==/Is3pDaLHIgEPh7AtXdwsfGaSGH7XODmnYzR1pCVLkXE=";
    //国家list
    public static final String COUNTRY_LIST = "/kt3FUiWxmnQyG4Mwu1U3SA==/s5Lx09Wjoqp4b4SelcD_MEiyxmnr1e-OUwbbAIpqjKY=";
    //举报
    public static final String REPORT_USER = "/e6tQ5bHUTJWxjKr6yKOrig==/z-5j6G6kTrCBM5utT8y6NA==";

    //unbind 第三方解绑
    public static final String UNBIND = "/kt3FUiWxmnQyG4Mwu1U3SA==/CZ66cKyqAcTOfBH4t1hWroufQ1sRNyCv3j__VYRnAs4=";

    //我的资料
    public static final String MY_DETAILS = "/kt3FUiWxmnQyG4Mwu1U3SA==/YCJ5wFija9QpPYdQ1mvNGQ==";
    //钻石数量
    public static final String MY_STONE_NUM = "/FKOApHFVeWpWcvT3tR0HIw==/vw-4493aJx8K7yvUBzHX89uy4SrIpv2o5N41FV4NUaU=";
    //修改昵称 个性签名等
    public static final String MODIFY_MY_INFO = "/kt3FUiWxmnQyG4Mwu1U3SA==/0ZdU7NWe4i8EMP37Oe74GV82Pk9BRhYw6NVV7TwHOUc=";
    //头像
    public static final String UPLOAD_PORTRAIT = "/kt3FUiWxmnQyG4Mwu1U3SA==/mjFE_xf6duzl8-UrxS7mf9uy4SrIpv2o5N41FV4NUaU=";
    //上传相册
    public static final String UPLOAD_ALBUMS = "/Ykv_YK9otI97cvnOu6CEcQ==/Lxo-MjtZeOfJq2pnLdQ9xvh8Ig5zomU7dzm7eSSRPkQ=";
    //商品列表
    public static final String PRODUCT_LIST = "/FKOApHFVeWpWcvT3tR0HIw==/0UUfUjm9g-vO6AGePwv24Q==";
    //PAY TYPE
    public static final String PAY_CENTER_LIST = "/FKOApHFVeWpWcvT3tR0HIw==/OOd6whSTczK7jUu-G0qjVA==";
    //UPDATE ORDER
    @Deprecated
    public static final String UPDATE_ORDER_STATUS = "/FKOApHFVeWpWcvT3tR0HIw==/UUh5GJu-G4og7P-A7V0dPNuy4SrIpv2o5N41FV4NUaU=";
    //Create order
    public static final String CREATE_PAY_ORDER = "/FKOApHFVeWpWcvT3tR0HIw==/4syOTMz6c8LjKodD3f5Bn9uy4SrIpv2o5N41FV4NUaU=";
    //Create order2
    public static final String GET_PAY_URL = "/FKOApHFVeWpWcvT3tR0HIw==/FPwDI9awXUOlF5Clm2Jl_KqLIIxS0ruKF_BnHyix0RY=";
    //coda
    public static final String INDIA_CODA = "/FKOApHFVeWpWcvT3tR0HIw==/Mr3i0cibWGf-RvQPA7C0Mxtb92kks7-XPSdPd1GJpVE=";
    //payerMax
    public static final String PAYER_MAX_URL = "/FKOApHFVeWpWcvT3tR0HIw==/7Atvlnse4JTttekBGTUJbzer-SJWv1m1ufQet_9TrVg=";
    //album list
    public static final String ALBUM_LIST = "/Ykv_YK9otI97cvnOu6CEcQ==/47CV6yIa1DgpTZyyiZIn4GvTb2oIzYoTWncj8QOgWZw=";
    //lookme
    public static final String LOOK_ME_LIST = "/Ykv_YK9otI97cvnOu6CEcQ==/lisGjfolYDrhS8OQdDvqTIvQXw2sJd86wVE7nbs8Q5o=";
    //delete image
    public static final String DELETE_IMAGE = "/Ykv_YK9otI97cvnOu6CEcQ==/NwGpIRabWmis6tmaP11gVA8VYG82FFtyOC2lnetlqJk=";
    public static final String MODIFY_PASSWORD = "/kt3FUiWxmnQyG4Mwu1U3SA==/MC12ob_tV-RW6Po9r3LYdY1uMHUdE1XXFnXcPGV1Dgc=";
    //批量查询用户信息以及在线状态
    public static final String BATCH_GET_DETAIL = "/Ykv_YK9otI97cvnOu6CEcQ==/CubFE8Li67R2xgaoXjAfY1sgemrfaEQSfiFFSjkURhw=";
    //unreadMsg
    public static final String GET_UNREAD_MSG = "/7N5Y5BPddf8OEpih_V4dMA==/6zhAiowH97jU98sO-Ddv0Q==";
    //获取系统消息列表
    public static final String NOTIFICATION_MSG = "/7N5Y5BPddf8OEpih_V4dMA==/_wBVxKNNoDYfx7owWuOPRtuy4SrIpv2o5N41FV4NUaU=";
    //钻石是否足够
    public static final String ENOUGH = "/FKOApHFVeWpWcvT3tR0HIw==/sQxSRbpm4kLC5SlwHH9IPA==";
    //随机获取视频
    public static final String VIDEO_LIST = "/Ykv_YK9otI97cvnOu6CEcQ==/Huv9AUU0CgekskCZ6UmFyJ-4M9gVvZy2tjdk2-5A_38=";
    //获得礼物列表
    public static final String GIFT_LIST = "/FKOApHFVeWpWcvT3tR0HIw==/Y5yW8idGV6V4bDMz02cGgg==";
    //获取主播下rankList
    public static final String MACY_LIST = "/FKOApHFVeWpWcvT3tR0HIw==/0CZLQPNhplWw9T2TniTQkULiXWn9hKyvS7sWM-0lUvE=";
    //获取主播礼物列表
    public static final String SHE_GIFT_LIST = "/FKOApHFVeWpWcvT3tR0HIw==/PRZlGTg9wap8RS2kwvkgxQ==";
    //拉黑用户
    public static final String BLOCK_USER = "/e6tQ5bHUTJWxjKr6yKOrig==/TCB0RAhHLatgQPkt-rg-mg==";
    //不喜欢视频
    public static final String NO_LIKE_VIDEO = "/e6tQ5bHUTJWxjKr6yKOrig==/GOj4rQjMiXbqNrNtFO4_Kg==";
    //发送礼物
    public static final String SEND_GIFT = "/FKOApHFVeWpWcvT3tR0HIw==/bnjJQp4X4GXN7Kcjyk2FXg==";
    //消费钻石
    public static final String REDUCE_DIAMONDS = "/FKOApHFVeWpWcvT3tR0HIw==/v-1HGB7yr01Jgw-bnOpIGA==";
    //获取通话token
    public static final String GET_NIM_TOKEN = "/kt3FUiWxmnQyG4Mwu1U3SA==/rVVLNU10YJIRymScGSr5knLF6V-XeXABDMEX6ER_ZZ8=";
    //facebook
    public static final String FACEBOOK_LOGIN = "/kt3FUiWxmnQyG4Mwu1U3SA==/Ryn1bnoDKVUacxw2FwoZBjMDRJxtX4aiBa1-SaxOkII=";
    //google登陆
    public static final String GOOGLE_LOGIN = "/kt3FUiWxmnQyG4Mwu1U3SA==/AUKqPEtYBS_OrlzBCCUx2aL7Wl9iduw47LDfbUyZneI=";
    //心跳
    public static final String UPDATE_ONLINE = "/BGXJagz2X6MuvEN_CAbXYQ==/MQooN-ECLZLcnP8zrP6CRw==";
    //轮播图
    public static final String VIP_BANNER_IMAGES = "/FKOApHFVeWpWcvT3tR0HIw==/Z1bQIwHEvyqQ67zqIH4lQ-iQqXgeP4MXX4KCPaqbbQs=";
    //    是否允许接听电话
    public static final String ALLOW_CALL = "/FKOApHFVeWpWcvT3tR0HIw==/F4Lz2flHQimxVtt_PeE0otuy4SrIpv2o5N41FV4NUaU=";
    //判断视频策略信用户能否接听
    public static final String CHECK_USER_ACCEPT_VIDEO = "/Ykv_YK9otI97cvnOu6CEcQ==/zqffyQLxv46VmTBpCunG3A==";
    //添加用户视频策略信的接听次数
    public static final String ADD_VIDEO_STRATEGY_NUM = "/Ykv_YK9otI97cvnOu6CEcQ==/t7T-bLDDtXhOX-G_SwZDcNuy4SrIpv2o5N41FV4NUaU=";
    //更新谷歌支付
    public static final String UPDATE_GOOGLE_ORDER = "/FKOApHFVeWpWcvT3tR0HIw==/LzCgelq9H1kFbqoop318Y8x3GtbApskgIKmQ5D1T41A=";
    //关注
    public static final String ATTENTION = "/XgJpTn-S-RhLZHzOvBDdag==/R7DHFB4DBJlBOeGKYXjiNw==";
    //取消关注
    public static final String UN_ATTENTION = "/XgJpTn-S-RhLZHzOvBDdag==/O7q9jUHJrRkvyvNsrn_Dtw==";
    //获取关注数量
    public static final String ATTENTION_NUM = "/XgJpTn-S-RhLZHzOvBDdag==/cBNgH6QPAqQNLU1OVgGDpw==";
    //获取关系列表
    public static final String ATTENTION_LIST = "/XgJpTn-S-RhLZHzOvBDdag==/i9BfDawl3zrBUTuduzxDmg==";
    //评论标签
    public static final String RATE_TAG = "/4uUp6bSQBeOnu5tqxlKRJw==/nhv4Gvbu8k4e6RLxn8b0xA==";
    //评价
    public static final String RATE_USER = "/4uUp6bSQBeOnu5tqxlKRJw==/4uUp6bSQBeOnu5tqxlKRJw==";
    //获取主播评论标签
    public static final String SHE_TAGS = "/4uUp6bSQBeOnu5tqxlKRJw==/3R49CcZbiP7AjniSjQCIIA==";
    //是否给主播评分过
    public static final String CHECK_USER_RATE = "/4uUp6bSQBeOnu5tqxlKRJw==/JcT5mDRgEdPABUYi5f6uGA==";
    //获取绑定的第三方
    public static final String GET_BIND = "/kt3FUiWxmnQyG4Mwu1U3SA==/e75ANTlRfvsdTtMMwbKz-X9IS4ITy6CIcNtwXOGBNsY=";
    //获取interceptDialog label
    public static final String INTERCEPT_LABEL = "/FKOApHFVeWpWcvT3tR0HIw==/phW074hC1Z1587Q_oV_TOBs7-BgiNPmth7FBywcVzaI=";
    //首次注册的引导弹框
    public static final String FIRST_GUIDE = "/kt3FUiWxmnQyG4Mwu1U3SA==/lnU2i_2cDuWWx5HiV6_DqxRpLNRall0G6FeGnsezt8Y=";
    //上报
    public static final String REPORT_EXCEPTION = "/vSwybA5I-XKvRElOaLWuMA==/FedYGqpD3WaEnwWrne7iUw==";

    //上传两个channelId
    public static final String UP_CHANNELID = "/Ykv_YK9otI97cvnOu6CEcQ==/Iug5Wmpym9EPfBiTcxP5KMyLRe9G9qTRdtNxMUGXPPg=";
}
