package com.lockulockme.locku.zlockeight.base.utils;

import com.google.gson.Gson;
import com.lockulockme.locku.base.UriConstant;
import com.lockulockme.locku.base.beans.BaseEntity;
import com.lockulockme.locku.base.beans.UserInfo;
import com.lockulockme.locku.base.beans.requestbean.AttentionListRequestBean;
import com.lockulockme.locku.base.beans.requestbean.AttentionRequestBean;
import com.lockulockme.locku.base.beans.requestbean.CompleteInfoRequest;
import com.lockulockme.locku.base.beans.requestbean.FacebookRequestBean;
import com.lockulockme.locku.base.beans.requestbean.GoogleSignRequest;
import com.lockulockme.locku.base.beans.requestbean.HasRatingRequestBean;
import com.lockulockme.locku.base.beans.requestbean.IndexRequestBean;
import com.lockulockme.locku.base.beans.requestbean.LoginRequestBean;
import com.lockulockme.locku.base.beans.requestbean.PageRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ProductRequestBean;
import com.lockulockme.locku.base.beans.requestbean.RatingTagRequestBean;
import com.lockulockme.locku.base.beans.requestbean.RatingUserRequestBean;
import com.lockulockme.locku.base.beans.requestbean.RegisterRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ReportExceptionRequestBean;
import com.lockulockme.locku.base.beans.requestbean.ReportRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SetPwbRequest;
import com.lockulockme.locku.base.beans.requestbean.SheDetailsRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheImageRequestBean;
import com.lockulockme.locku.base.beans.requestbean.SheTagRequestBean;
import com.lockulockme.locku.base.beans.requestbean.UnbindRequest;
import com.lockulockme.locku.base.beans.requestbean.VideoPriceRequestBean;
import com.lockulockme.locku.base.beans.requestbean.VipInterceptRequestBean;
import com.lockulockme.locku.base.beans.responsebean.AttentionNumResponseBean;
import com.lockulockme.locku.base.beans.responsebean.AudioAndVideoPriceResponseBean;
import com.lockulockme.locku.base.beans.responsebean.BindData;
import com.lockulockme.locku.base.beans.responsebean.CountryResponseBean;
import com.lockulockme.locku.base.beans.responsebean.CreatePayOrderResponseBean;
import com.lockulockme.locku.base.beans.responsebean.EnoughResponseBean;
import com.lockulockme.locku.base.beans.responsebean.FirstGuideResponseBean;
import com.lockulockme.locku.base.beans.responsebean.GetUrlV2ResponseBean;
import com.lockulockme.locku.base.beans.responsebean.IndexUserResponseBean;
import com.lockulockme.locku.base.beans.responsebean.LoginResponseBean;
import com.lockulockme.locku.base.beans.responsebean.LookMeResponseBean;
import com.lockulockme.locku.base.beans.responsebean.MyAlbumResponseBean;
import com.lockulockme.locku.base.beans.responsebean.MyLevelRe;
import com.lockulockme.locku.base.beans.responsebean.MyStoneResponseBean;
import com.lockulockme.locku.base.beans.responsebean.PayCenterResponseBean;
import com.lockulockme.locku.base.beans.responsebean.ProductResponseBean;
import com.lockulockme.locku.base.beans.responsebean.RankItemResponseBean;
import com.lockulockme.locku.base.beans.responsebean.RateTagResponseBean;
import com.lockulockme.locku.base.beans.responsebean.RetentionBean;
import com.lockulockme.locku.base.beans.responsebean.SayHelloResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheGiftsResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheImageResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheMacyResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheTagResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SheVideoResponseBean;
import com.lockulockme.locku.base.beans.responsebean.SystemMsgResponseBean;
import com.lockulockme.locku.base.beans.responsebean.UpdatePayOrderResponseBean;
import com.lockulockme.locku.base.beans.responsebean.UploadPortraitResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VideoResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipBannerImageResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipInterceptResponseBean;
import com.lockulockme.locku.base.beans.responsebean.VipResponseBean;
import com.lockulockme.locku.base.callback.NewJsonCallback;
import com.lockulockme.locku.base.callback.StringCall;
import com.lockulockme.locku.base.netbase.LockUOkGo;
import com.lockulockme.locku.base.utils.LogUtil;
import com.lockulockme.locku.module.ui.fragment.ChartsChildFragment;
import com.lockulockme.lockuchat.bean.rst.UpChannelId;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkGoUtils {
    static OkGoUtils okGoUtils;
    private final Gson gson;

    public OkGoUtils() {
        gson = new Gson();
    }

    public static OkGoUtils getInstance() {
        if (okGoUtils == null) {
            okGoUtils = new OkGoUtils();
        }
        return okGoUtils;
    }

    public <T> void postObject(String url, Object tag, Object o, com.lockulockme.locku.base.callback.NewJsonCallback<T> callback) {
        url = UriConstant.getBaseUrl() + url;
        String s = gson.toJson(o);
        LogUtil.LogE("register--------------", s);
        LogUtil.LogE("OkGo", s);
        LockUOkGo.getInstance().post(tag,s,url,callback);
    }


    public void postObject(String url, Object tag, Object o, StringCall callback) {
        url = UriConstant.getBaseUrl() + url;
        String s = gson.toJson(o);
        LogUtil.LogE("register--------------", s);
        LogUtil.LogE("OkGo", s);
        LockUOkGo.getInstance().postStr(tag,s,url,callback);
    }

    //TODO  重复请求限制
    public void postObject(String url, Object tag, Object o,StringCall callback, int retryNum) {
        url = UriConstant.getBaseUrl() + url;
        String s = gson.toJson(o);
        LogUtil.LogE("register--------------", s);
        LogUtil.LogE("OkGo", s);
        LockUOkGo.getInstance().postStr(tag,s,url,callback);
    }

    public <T> void postFile(String url, Object tag, String fileName, File file, com.lockulockme.locku.base.callback.NewJsonCallback<T> callback) {
        url = UriConstant.getBaseUrl() + url;
        LockUOkGo.getInstance().postFile(tag,url,file,null,fileName,callback);
    }


    public void reportUser(Object tag, ReportRequestBean req, File file, com.lockulockme.locku.base.callback.NewJsonCallback callback) {
        String url = UriConstant.getBaseUrl() + UriConstant.REPORT_USER;
        String fileName = "certificateFile";
        Map<String, String> map = new HashMap<>();
        map.put("userStringId", req.userId);
        map.put("content", req.content);
        map.put("position", req.position);
        map.put("type", req.type);
        LockUOkGo.getInstance().postFile(tag,url,file,map,fileName,callback);
    }

    public void getData() {
        postObject("", "", new BaseEntity(), new com.lockulockme.locku.base.callback.NewJsonCallback<BaseEntity>() {

            @Override
            public void onSuc(BaseEntity response, String msg) {

            }
        });
    }

    public void onRegister(Object tag, RegisterRequestBean registerReq, com.lockulockme.locku.base.callback.NewJsonCallback<LoginResponseBean> callback) {
        postObject(UriConstant.ONE_CLICK_REGISTER, tag, registerReq, callback);
    }

    public void setPassword(Object tag, SetPwbRequest setPwbRequest, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.SET_PWB, tag, setPwbRequest, callback);
    }


    public void onLogin(Object tag, LoginRequestBean loginReq, com.lockulockme.locku.base.callback.NewJsonCallback<LoginResponseBean> callback) {
        postObject(UriConstant.LOGIN_ACCOUNT, tag, loginReq, callback);
    }

    public void linkFacebook(Object tag, FacebookRequestBean facebookRequestBean, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.BIND_FACEBOOK, tag, facebookRequestBean, callback);
    }

    public void linkGoogle(Object tag, GoogleSignRequest googleSignRequest, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.BIND_GOOGLE, tag, googleSignRequest, callback);
    }


    public void completeInfo(Object tag, CompleteInfoRequest completeInfoRequest, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.COMPLETE_INFO, tag, completeInfoRequest, callback);
    }

    public void getIndexList(Object tag, IndexRequestBean indexReq, com.lockulockme.locku.base.callback.NewJsonCallback<List<IndexUserResponseBean>> callback) {
        postObject(UriConstant.INDEX_LIST, tag, indexReq, callback);
    }

    public void getRankList(Object tag, PageRequestBean pageReq, int type, com.lockulockme.locku.base.callback.NewJsonCallback<List<RankItemResponseBean>> callback) {
        if (type == ChartsChildFragment.MALE_TYPE) {
            postObject(UriConstant.MALE_RANK, tag, pageReq, callback);
        } else {
            postObject(UriConstant.FEMALE_RANK, tag, pageReq, callback);
        }
    }

    public void getOtherInfo(Object tag, SheDetailsRequestBean req, com.lockulockme.locku.base.callback.NewJsonCallback<UserInfo> callback) {
        postObject(UriConstant.SHE_INFO, tag, req, callback);
    }

    public void getSheImages(Object tag, SheImageRequestBean req, com.lockulockme.locku.base.callback.NewJsonCallback<List<SheImageResponseBean>> callback) {
        postObject(UriConstant.OTHER_IMAGES, tag, req, callback);
    }

    public void getSheVideos(Object tag, SheImageRequestBean req, com.lockulockme.locku.base.callback.NewJsonCallback<List<SheVideoResponseBean>> callback) {
        postObject(UriConstant.OTHER_VIDEOS, tag, req, callback);
    }

    public void getShePrices(Object tag, VideoPriceRequestBean req, com.lockulockme.locku.base.callback.NewJsonCallback<List<AudioAndVideoPriceResponseBean>> callback) {
        postObject(UriConstant.VOICE_VIDEO_CHAT_PRICE, tag, req, callback);
    }

    public void userSayHello(Object tag, SheDetailsRequestBean req, com.lockulockme.locku.base.callback.NewJsonCallback<SayHelloResponseBean> callback) {
        postObject(UriConstant.SAY_HELLO, tag, req, callback);
    }

    public void getVipState(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<VipResponseBean> callback) {
        postObject(UriConstant.VIP_STATE, tag, new Object(), callback);
    }

    public void promptBind(Object tab, com.lockulockme.locku.base.callback.NewJsonCallback<Boolean> callback) {
        postObject(UriConstant.PROMPT_BIND, tab, new Object(), callback);
    }

    public void getRetentionInfo(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<RetentionBean> callback){
        postObject(UriConstant.RETENTION_INFO,tag,new Object(),callback);
    }

    public void getMyDetail(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<UserInfo> callback) {
        postObject(UriConstant.MY_DETAILS, tag, new Object(), callback);
    }

    public void unbind(Object tag, UnbindRequest unbindRequest, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.UNBIND, tag, unbindRequest, callback);
    }

    public void getMyStone(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<MyStoneResponseBean> callback) {
        postObject(UriConstant.MY_STONE_NUM, tag, new Object(), callback);
        //        postObject(UriConstant.VIP_STATE, tag, new Object(), callback);
    }

    public void getCountry(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<List<CountryResponseBean>> callback) {
        postObject(UriConstant.COUNTRY_LIST, tag, new Object(), callback);
    }



    public void modifyPersonalInfo(Object tag, UserInfo req, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.MODIFY_MY_INFO, tag, req, callback);
    }

    public void uploadPortrait(Object tag, String filename, File file, com.lockulockme.locku.base.callback.NewJsonCallback<UploadPortraitResponseBean> callback) {
        postFile(UriConstant.UPLOAD_PORTRAIT, tag, filename, file, callback);
    }


    public void uploadAlbums(Object tag, String filename, File file, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postFile(UriConstant.UPLOAD_ALBUMS, tag, filename, file, callback);
    }

    public void getProductList(Object tag, ProductRequestBean productReq, com.lockulockme.locku.base.callback.NewJsonCallback<List<ProductResponseBean>> callback) {
        postObject(UriConstant.PRODUCT_LIST, tag, productReq, callback);
    }

    public void getPayCenterList(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<List<PayCenterResponseBean>> callback) {
        postObject(UriConstant.PAY_CENTER_LIST, tag, object, callback);
    }

    public void createPayOrder(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<CreatePayOrderResponseBean> callback) {
        postObject(UriConstant.CREATE_PAY_ORDER, tag, object, callback);
    }

    public void updatePayOrder(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<UpdatePayOrderResponseBean> callback) {
        postObject(UriConstant.UPDATE_GOOGLE_ORDER, tag, object, callback);
    }

    public void getMyAlbums(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<List<MyAlbumResponseBean>> callback) {
        postObject(UriConstant.ALBUM_LIST, tag, object, callback);
    }

    public void getCodaPayUrl(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<String> callback) {
        postObject(UriConstant.INDIA_CODA, tag, object, callback);
    }

    public void getPayerMaxUrl(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<String> callback) {
        postObject(UriConstant.PAYER_MAX_URL, tag, object, callback);
    }

    public void getPayUrlV2(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<GetUrlV2ResponseBean> callback) {
        postObject(UriConstant.GET_PAY_URL, tag, object, callback);
    }

    public void getLookmeList(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<List<LookMeResponseBean>> callback) {
        postObject(UriConstant.LOOK_ME_LIST, tag, object, callback);
    }

    public void deleteImage(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.DELETE_IMAGE, tag, object, callback);
    }

    public void modifyPassword(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.MODIFY_PASSWORD, tag, object, callback);
    }

    public void getUnreadMsg(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<Integer> callback) {
        postObject(UriConstant.GET_UNREAD_MSG, tag, new Object(), callback);
    }

    public void getMyLevel(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<MyLevelRe> callback) {
        postObject(UriConstant.GET_MY_LEVEL, tag, new Object(), callback);
    }

    public void getNotificationMsg(Object tag, Object obj, com.lockulockme.locku.base.callback.NewJsonCallback<SystemMsgResponseBean> callback) {
        postObject(UriConstant.NOTIFICATION_MSG, tag, obj, callback);
    }

    public void isEnough(Object tag, Object obj, com.lockulockme.locku.base.callback.NewJsonCallback<EnoughResponseBean> callback) {
        postObject(UriConstant.ENOUGH, tag, obj, callback);
    }

    public void getVideoList(Object tag, Object obj, com.lockulockme.locku.base.callback.NewJsonCallback<List<VideoResponseBean>> callback) {
        postObject(UriConstant.VIDEO_LIST, tag, obj, callback);
    }

    public void getRecentUsers(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.BATCH_GET_DETAIL, tag, object, callback);
    }

    public void getGiftList(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.GIFT_LIST, tag, object, callback);
    }

    public void getMacyList(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<List<SheMacyResponseBean>> callback) {
        postObject(UriConstant.MACY_LIST, tag, object, callback);
    }

    public void getSheGiftList(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<SheGiftsResponseBean> callback) {
        postObject(UriConstant.SHE_GIFT_LIST, tag, object, callback);
    }

    public void blockUser(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.BLOCK_USER, tag, object, callback);
    }

    public void noLikeVideo(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.NO_LIKE_VIDEO, tag, object, callback);
    }

    public void getDiamondsNum(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.MY_STONE_NUM, tag, object, callback);
    }

    public void sendGift(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.SEND_GIFT, tag, object, callback);
    }

    public void getVipStatus(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.VIP_STATE, tag, object, callback);
    }

    public void diamondsIsEnough(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.ENOUGH, tag, object, callback);
    }

    public void reduceDiamonds(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.REDUCE_DIAMONDS, tag, object, callback, 0);
    }

    public void getNimToken(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.GET_NIM_TOKEN, tag, object, callback);
    }

    public void blockUser4String(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.BLOCK_USER, tag, object, callback);
    }

    public void allowCall(Object tag, Object object, StringCall callback) {
        postObject(UriConstant.ALLOW_CALL, tag, object, callback);
    }

    public void facebookLogin(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<LoginResponseBean> callback) {
        postObject(UriConstant.FACEBOOK_LOGIN, tag, object, callback);
    }

    public void googleLogin(Object tag, GoogleSignRequest googleSignRequest, com.lockulockme.locku.base.callback.NewJsonCallback<LoginResponseBean> callback) {
        postObject(UriConstant.GOOGLE_LOGIN, tag, googleSignRequest, callback);
    }

    public void getBind(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<List<BindData>> callback) {
        postObject(UriConstant.GET_BIND, tag, new Object(), callback);
    }

    public void updateOnline(Object object, StringCall callback) {
        postObject(UriConstant.UPDATE_ONLINE, null, object, callback);
    }

    public void getVipImages(Object tag, Object object, com.lockulockme.locku.base.callback.NewJsonCallback<List<VipBannerImageResponseBean>> callback) {
        postObject(UriConstant.VIP_BANNER_IMAGES, tag, object, callback);
    }

    public void userAcceptStrategy(Object tag, StringCall callback) {
        postObject(UriConstant.CHECK_USER_ACCEPT_VIDEO, tag, new Object(), callback);
    }

    public void userAddStrategyNum(Object tag, StringCall callback) {
        postObject(UriConstant.ADD_VIDEO_STRATEGY_NUM, tag, new Object(), callback);
    }

    public void attention(Object tag, AttentionRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.ATTENTION, tag, requestBean, callback);
    }

    public void unAttention(Object tag, AttentionRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.UN_ATTENTION, tag, requestBean, callback);
    }

    public void getAttentionNum(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<AttentionNumResponseBean> callback) {
        postObject(UriConstant.ATTENTION_NUM, tag, new Object(), callback);
    }

    public void getAttentionList(Object tag, AttentionListRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<List<UserInfo>> callback) {
        postObject(UriConstant.ATTENTION_LIST, tag, requestBean, callback);
    }

    public void getRateTags(Object tag, RatingTagRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<List<RateTagResponseBean>> callback) {
        postObject(UriConstant.RATE_TAG, tag, requestBean, callback);
    }

    public void ratingUser(Object tag, RatingUserRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<Void> callback) {
        postObject(UriConstant.RATE_USER, tag, requestBean, callback);
    }

    public void getUserTags(Object tag, SheTagRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<SheTagResponseBean> callback) {
        postObject(UriConstant.SHE_TAGS, tag, requestBean, callback);
    }

    public void checkUserRate(Object tag, HasRatingRequestBean requestBean, StringCall callback) {
        postObject(UriConstant.CHECK_USER_RATE, tag, requestBean, callback);
    }

    public void getInterceptLabel(Object tag, VipInterceptRequestBean requestBean, com.lockulockme.locku.base.callback.NewJsonCallback<VipInterceptResponseBean> callback) {
        postObject(UriConstant.INTERCEPT_LABEL, tag, requestBean, callback);
    }

    public void getFirstGuideDialog(Object tag, com.lockulockme.locku.base.callback.NewJsonCallback<FirstGuideResponseBean> callback) {
        postObject(UriConstant.FIRST_GUIDE, tag, new Object(), callback);
    }

    public void reportException(Object tag, ReportExceptionRequestBean req) {
        postObject(UriConstant.REPORT_EXCEPTION, tag, req, new NewJsonCallback<Void>() {
            @Override
            public void onSuc(Void response, String msg) {

            }
        });
    }

    public void upChannelId(Object tag, UpChannelId upChannelId, StringCall callback) {
        postObject(UriConstant.UP_CHANNELID, tag, upChannelId, callback);
    }
}
