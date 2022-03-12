package xin.banghua.beiyuan.utils;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Common;

public class OkHttpInstance {
    private static final String TAG = "OkHttpInstance";
    private static OkHttpClient uniqueInstance = new OkHttpClient();
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    private OkHttpInstance(){}
    public static OkHttpClient getInstance(){
        return uniqueInstance;
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
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=isShowADNew&m=socialchat")
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
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=SvipTry&m=socialchat")
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
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=SvipTry&m=socialchat")
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
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getUserAttributes&m=socialchat")
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
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteRecruitment&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=UpdateOnline&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getFollowList&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=follow&m=socialchat")
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
                                    .add("me", Common.myID)
                                    .add("you", you)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=unfollow&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getFollowUser&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getFansUser&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=Friendsapplynewnew&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=agreefriendnew&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=withdraw&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSlide&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getStore&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=buyStoreGoods&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getMyGoods&m=socialchat")
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
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=equipGoods&m=socialchat")
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
}
