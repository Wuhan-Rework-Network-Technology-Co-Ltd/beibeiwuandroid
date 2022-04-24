package xin.banghua.beiyuan.utils;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main3Branch.RongyunConnect;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class OkHttpInstance {
    private static final String TAG = "OkHttpInstance";
    private static OkHttpClient.Builder uniqueInstance = new OkHttpClient.Builder();
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    private OkHttpInstance(){}
    public static OkHttpClient getInstance(){
        uniqueInstance.proxy(Proxy.NO_PROXY);
        return uniqueInstance.build();
    }
    /**
     * 广告开关
     * @param okHttpResponseCallBack
     */
    public static void isShowAD(OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=isShowADNew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "广告开关" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO RTM获取token
    public static void exhaustSvipTry() {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", Common.myID)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=SvipTry&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 更新vip时长
    public static void updateVipTime(OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", Common.myID)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=SvipTry&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:更新vip时长" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TOOD 获取用户属性
    public static void getUserAttributes(String userId,@Nullable OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("userId", userId)
                            .add("latitude", Common.latitude)
                            .add("longitude", Common.longitude)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getUserAttributes&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        if (okHttpResponseCallBack!=null){
                            String resultString = response.body().string();
                            Log.d(TAG, "run:获取用户属性" +Common.latitude+"|"+Common.longitude);
                            if (resultString.equals("false")){
                                return;
                            }

                            ThreadUtils.runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TOOD 获取用户属性
    public static void getUserAttributesMe(String userId,@Nullable OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("userId", userId)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getUserAttributesMe&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        if (okHttpResponseCallBack!=null){
                            String resultString = response.body().string();
                            Log.d(TAG, "run:获取用户属性" +userId+ resultString);
                            if (resultString.equals("false")){
                                return;
                            }

                            ThreadUtils.runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO 撤销招募令
    public static void deleteRecruitment(OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", Common.myID)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=deleteRecruitment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:更新vip时长" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 每5分钟更新在线状态
     * @param okHttpResponseCallBack
     */
    public static void updateOnline(OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", Common.userInfoList.getId())
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=UpdateOnline&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取关注列表
     * @param okHttpResponseCallBack
     */
    public static void getFollowList(String id,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", id)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getFollowList&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取关注列表"+Common.myID+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取关注列表
     * @param okHttpResponseCallBack
     */
    public static void follow(String you,OkHttpResponseCallBack okHttpResponseCallBack){
        for (int j = 0;j < Common.followList.size();j++){
            Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.get(j));
            if (Common.followList.get(j).getUserId().equals(you)){
                return;
            }
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("me", Common.myID)
                            .add("you", you)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=follow&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Common.userInfoList.setFollow((Integer.parseInt(Common.userInfoList.getFollow())+1)+"");
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取关注列表
     * @param okHttpResponseCallBack
     */
    public static void unfollow(String you,OkHttpResponseCallBack okHttpResponseCallBack){
        for (int j = 0;j < Common.followList.size();j++){
            Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.get(j));
            if (Common.followList.get(j).getUserId().equals(you)){
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run(){
                            OkHttpClient client = OkHttpInstance.getInstance();
                            RequestBody formBody = new FormBody.Builder()
                                    .add("me", Common.userInfoList.getId())
                                    .add("you", you)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=unfollow&m=socialchat")
                                    .post(formBody)
                                    .build();

                            try (Response response = client.newCall(request).execute()) {
                                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                                String resultString = response.body().string();
                                Common.userInfoList.setFollow((Integer.parseInt(Common.userInfoList.getFollow())-1)+"");
                                for (int i = 0;i < Common.followList.size();i++){
                                    if (Common.followList.get(i).getUserId().equals(you)){
                                        Common.followList.remove(i);
                                    }
                                }
                                runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除粉丝
     * @param okHttpResponseCallBack
     */
    public static void deleteFans(String you,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("me", you)
                            .add("you", Common.userInfoList.getId())
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=unfollow&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Common.userInfoList.setFans((Integer.parseInt(Common.userInfoList.getFans())-1)+"");
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户关注的人
     * @param okHttpResponseCallBack
     */
    public static void getFollowUser(String id,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", id)
                            .add("pageIndex",pageIndex)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getFollowUser&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取用户关注的人"+Common.myID+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户粉丝的人
     * @param okHttpResponseCallBack
     */
    public static void getFansUser(String id,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", id)
                            .add("pageIndex",pageIndex)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getFansUser&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取用户粉丝的人"+Common.myID+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取好友申请列表
     * @param okHttpResponseCallBack
     */
    public static void friendsapplynewnew(String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", Common.userInfoList.getId())
                            .add("pageIndex",pageIndex)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=Friendsapplynewnewnew&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取好友申请列表"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同意好友
     * @param okHttpResponseCallBack
     */
    public static void agreefriendnew(String yourid,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", Common.userInfoList.getId())
                            .add("yourid",yourid)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=agreefriendnew&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 同意好友申请"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提现
     * @param okHttpResponseCallBack
     */
    public static void withdraw(String alilogonid,String aliname,String transamount,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("authid", Common.userInfoList.getId())
                            .add("alilogonid",alilogonid)
                            .add("aliname", aliname)
                            .add("transamount", transamount)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=withdraw&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 提现"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 幻灯片
     * @param okHttpResponseCallBack
     */
    public static void getSlide(String slidesort,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("slidesort", slidesort)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getSlide&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 幻灯片"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 商店
     * @param okHttpResponseCallBack
     */
    public static void getStore(String type,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("type", type)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getStore&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 商店"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 购买商品
     * @param okHttpResponseCallBack
     */
    public static void buyStoreGoods(String goods_id,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("authid", Common.userInfoList.getId())
                            .add("goods_id", goods_id)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=buyStoreGoods&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 购买商品"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已购买商品
     * @param okHttpResponseCallBack
     */
    public static void getMyGoods(OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("authid", Common.userInfoList.getId())
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getMyGoods&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取已购买商品"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 装备商品
     * @param okHttpResponseCallBack
     */
    public static void equipGoods(String goods_id,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("authid", Common.userInfoList.getId())
                            .add("goods_id",goods_id)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=equipGoods&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 装备商品"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 验证码
     * @param phoneNumber
     * @param okHttpResponseCallBack
     */
    public static void sendCode(final String phoneNumber, OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("phoneNumber", phoneNumber)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://www.banghua.xin/sms_beibeiwu.php")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 验证码");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证码登录
     * @param userAccount
     * @param okHttpResponseCallBack
     */
    public static void smsLogin(final String userAccount, OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("userAccount", userAccount)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=SigninOneKeyLogin&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 验证码登录");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除朋友数量
     *
     * @param yourid                 yourid
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void deleteFriendNumber(String myid,String yourid,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", myid)
                            .add("yourid", yourid)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=DeleteFriendsapply&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 删除朋友数量");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除朋友
     *
     * @param yourid                 yourid
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void deleteFriend(final String yourid,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid", Common.userInfoList.getId())
                            .add("yourid", yourid)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=deletefriendnew&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 删除朋友");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据postlist
     *
     * @param subNav                 子导航
     * @param pageindex              pageindex
     * @param filter_gender          过滤器性别
     * @param filter_search          过滤搜索
     * @param filter_property        过滤器属性
     * @param filter_region          过滤器区域
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getDataPostlist(String subNav,String pageindex,String filter_gender,String filter_search,String filter_property,String filter_region,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    SharedHelper sh = new SharedHelper(App.getApplication());
                    String myid = sh.readUserInfo().get("userID");

                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("type", "getPostlist")
                            .add("myid", myid)
                            .add("platename", subNav)
                            .add("pageindex",pageindex)
                            .add("filter_gender", filter_gender)
                            .add("filter_search", filter_search)
                            .add("filter_property", filter_property)
                            .add("filter_region", filter_region)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=luntannewnewnew&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 获取数据postlist");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 举报评论
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void tipoffComment(final String commentID,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("commentID", commentID)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=tipoffComment&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 举报评论");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 绑定手机
     * @param userAccount
     * @param okHttpResponseCallBack
     */
    public static void bindPhone(final String userAccount,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("myid",Common.userInfoList.getId())
                            .add("userAccount", userAccount)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=bindPhone&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 绑定手机");
                        String responseString = response.body().string();
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //TODO 登录 注册融云
    public static void postRongyunUserRegister(UserInfoList userInfoList, OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                Log.d("融云注册信息","进入融云注册");
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userID", userInfoList.getId())
                        .add("userNickName",userInfoList.getNickname())
                        .add("userPortrait",userInfoList.getPortrait())
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/otherinterface/rongyun/RongCloudNew/example/User/userregister.php")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    //Log.d(TAG, "run: 融云注册信息返回"+response.toString());
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultJson1 = response.body().string();
                    try {
                        JSONObject object1 = new JSONObject(resultJson1);
                        Log.d("融云token获取",object1.getString("code"));
                        if (object1.getString("code").equals("200")){

                            //保存融云token
                            Log.d("融云token",object1.getString("token"));
                            SharedHelper sh = new SharedHelper(App.getApplication());
                            sh.saveRongtoken(object1.getString("token"));

                            //链接融云
                            RongyunConnect rongyunConnect = new RongyunConnect();
                            rongyunConnect.connect(object1.getString("token"));
                        }else {
                            OkHttpInstance.postRongyunUserRegister(userInfoList,responseString -> {

                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 得到主题文章
     *
     * @param topic                  主题
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getTopicPost(final String topic,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("topic", topic)
                            .add("pageIndex", pageIndex)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getTopicPost&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String responseString = response.body().string();
                        Log.d(TAG, "run: 得到主题文章"+topic+responseString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(responseString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 音乐列表
     * @param okHttpResponseCallBack
     */
    public static void getOnlineMusicList(String pageindex,String query,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("pageindex",pageindex)
                            .add("query",query)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=GetOnlineMusic&m=moyuan")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 音乐列表获取的值"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得脚本
     *
     * @param pageIndex              页面索引
     * @param type                   类型
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getScript(String pageIndex,String type,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("pageIndex",pageIndex)
                            .add("type",type)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getScript&m=socialchat")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获得脚本"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 小贝比赛
     */
    public static void xiaobeiMatch(String type,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id",Common.userInfoList.getId())
                            .add("type",type)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=xiaobeiMatch&m=rediscache")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 小贝比赛"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除小贝比赛
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void removeXiaobeiMatch(String type,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id",Common.userInfoList.getId())
                            .add("type",type)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=removeXiaobeiMatch&m=rediscache")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 删除小贝比赛"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 重置匹配
     *
     * @param value                  价值
     */
    public static void resetMatch(String value){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id",Common.userInfoList.getId())
                            .add("value",value)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=resetMatch&m=socialchat")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 重置匹配"+resultString);
                        //runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 得到视频
     *
     * @param tag                    标签
     * @param pageIndex              页面索引
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getVideo(String tag,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("tag",tag)
                            .add("pageIndex",pageIndex)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getVideo&m=socialchat")
                            .post(formBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 得到视频"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取真人认证token
     * @param okHttpResponseCallBack
     */
    public static void getRPVerifyToken(String metaInfo,String certNo,String certName,OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("metaInfo", metaInfo)
                            .add("certNo", certNo)
                            .add("certName", certName)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/otherinterface/aliyun/InitFaceVerify.php")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 获取真人认证token"+resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 远程请求真人认证信息
     */
    public static void saveRPVerifyToken(String certifyId,String cert_name,String cert_no){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id",Common.userInfoList.getId())
                            .add("cert_name", cert_name)
                            .add("cert_no", cert_no)
                            .add("certifyId", certifyId)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/otherinterface/aliyun/DescribeFaceVerify.php")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String resultString = response.body().string();
                        Log.d(TAG, "run: 远程请求真人认证信息"+resultString);
                        if (!resultString.equals("认证失败")){
                            Common.userInfoList.setRp_verify_time(resultString);
                        }
//                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //TODO okhttp点赞
    public static void like(String postid,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("postid", postid)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=luntanlike&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: okhttp点赞"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 匹配用户上网
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getOnlineMatchUser(String id,String gender,String property,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id",id)
                        .add("gender",gender)
                        .add("property",property)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getOnlineMatchUser&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 匹配用户上网"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 匹配用户上网
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getOnlineMatchUserOne(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id",Common.userInfoList.getId())
                        .add("gender",Common.userInfoList.getGender())
                        .add("property",Common.userInfoList.getProperty())
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getOnlineMatchUserOneNew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 匹配用户上网"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 上网匹配num
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getOnlineMatchNum(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getOnlineMatchNum&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 上网匹配num"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 上网比赛时间
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getOnlineMatchTimes(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id",Common.userInfoList.getId())
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=getOnlineMatchTimes&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 上网比赛时间"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 个人动态
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getDataPostlist(String authid,String pageindex,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", authid)
                        .add("pageindex",pageindex)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=someonesluntannew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 个人动态"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 得到朋友数字
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void getFriendNumber(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", Common.userInfoList.getId())
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=friendsnumber&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 个人动态"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 加入朋友
     *
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void addfriend(String userId,String yourwords,String giftLists,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", userId)
                        .add("yourid", Common.userInfoList.getId())
                        .add("yournickname", Common.userInfoList.getNickname())
                        .add("yourportrait", Common.userInfoList.getPortrait())
                        .add("yourwords",yourwords)
                        .add("gift_string", giftLists)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=addfriend&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String resultString = response.body().string();
                    Log.d(TAG, "run: 加入朋友"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * addblacklist
     *
     * @param yourid                 yourid
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void addblacklist(String yourid,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", Common.userInfoList.getId())
                        .add("yourid", yourid)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=addblacklist&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    //拉黑了好友，则刷新
                    try {
                        xin.banghua.beiyuan.Common.newFriendOrDeleteFriend = true;
                        xin.banghua.beiyuan.Common.friendListMap.remove(yourid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String resultString = response.body().string();
                    Log.d(TAG, "run: 加入朋友"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * iffriendnew
     *
     * @param myid                   myid
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void iffriendnew(String myid,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", Common.userInfoList.getId())
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=iffriendnew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: iffriendnew"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 手机短信验证
     *
     * @param phone                  电话
     * @param code                   代码
     * @param okHttpResponseCallBack 好http响应回电话
     */
    public static void smsVerify(String phone,String code,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("phone", phone)
                        .add("code", code)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=smsVerify&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: smsVerify"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void tuijiannew(String pageindex,String selectedGender,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getUserInfo")
                        //.add("myid", Common.myID)
                        .add("pageindex", pageindex)
                        .add("selectedGender", selectedGender)
                        .add("latitude",Common.latitude)
                        .add("longitude",Common.longitude)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=tuijiannew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: tuijiannew"+pageindex+selectedGender);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void fujinnew(String pageindex,String selectedGender,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getUserInfo")
                        //.add("myid", Common.myID)
                        .add("pageindex", pageindex)
                        .add("selectedGender", selectedGender)
                        .add("latitude",Common.latitude)
                        .add("longitude",Common.longitude)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=fujinnew&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: fujinnew"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void wealthAndGlamour(String type,String pageindex,String selectedGender,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", type)
                        .add("pageindex", pageindex)
                        .add("selectedGender", selectedGender)
                        .add("latitude",Common.latitude)
                        .add("longitude",Common.longitude)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=wealthAndGlamour&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: wealthAndGlamour"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
