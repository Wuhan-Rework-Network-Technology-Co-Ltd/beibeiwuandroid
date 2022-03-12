package io.agora.chatroom;

import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

import io.agora.chatroom.model.Constant;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpInstance {
    private static final String TAG = "OkHttpInstance";
    private static OkHttpClient uniqueInstance = new OkHttpClient();
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    private OkHttpInstance(){}
    public static OkHttpClient getInstance(){
        uniqueInstance.newBuilder().proxy(Proxy.NO_PROXY);
        return uniqueInstance;
    }
    /**
     * RTM获取token
     * @param okHttpResponseCallBack
     */
    public static void getRTMToken(OkHttpResponseCallBack okHttpResponseCallBack) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("uid", Constant.sUserId+"")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/otherinterface/agora/sample/RtmTokenBuilderSampleXiaobei.php")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String responseString = response.body().string();
                        Log.d(TAG, "run: getRTMToken"+responseString);
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
     * RTC获取token
     * @param channelName
     * @param okHttpResponseCallBack
     */
    public static void getRTCToken(String channelName,OkHttpResponseCallBack okHttpResponseCallBack) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("uid", Constant.sUserId+"")
                            .add("channelName", channelName)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/otherinterface/agora/sample/RtcTokenBuilderSampleXiaobei.php")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String responseString = response.body().string();
                        Log.d(TAG, "run: getRTCToken"+responseString);
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
     * 房间设置
     * @param room_name
     * @param room_cover
     * @param room_bg
     * @param okHttpResponseCallBack
     */
    public static void roomSet(String room_name,String room_cover,String room_bg, OkHttpResponseCallBack okHttpResponseCallBack){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                    multipartBody.setType(MultipartBody.FORM);
                    multipartBody.addFormDataPart("uid", Constant.sUserId+"");
                    if (!TextUtils.isEmpty(room_name)){
                        multipartBody.addFormDataPart("room_name", room_name);
                    }
                    if (!TextUtils.isEmpty(room_cover)) {
                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                        File tempFile = new File(room_cover.trim());
                        String fileName = tempFile.getName();
                        multipartBody.addFormDataPart("room_cover", fileName, RequestBody.create(new File(room_cover), MEDIA_TYPE_PNG));
                    }
                    if (!TextUtils.isEmpty(room_bg)) {
                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                        File tempFile = new File(room_bg.trim());
                        String fileName = tempFile.getName();
                        multipartBody.addFormDataPart("room_bg", fileName, RequestBody.create(new File(room_bg), MEDIA_TYPE_PNG));
                    }

                    RequestBody requestBody = multipartBody.build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=roomSet&m=socialchat")
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        Log.d(TAG, "run: 房间设置");
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
     * 创建房间
     * @param audioroomtype
     * @param okHttpResponseCallBack
     */
    public static void createRoom(String audioroomtype,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myID", Constant.sUserId+"")
                        .add("audioroomtype", audioroomtype)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=createRoom&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 创建房间"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 获取电影
     * @param filter_search
     */
    public static void getFilmTopic(int pageindex,String filter_search,String film_type,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("pageindex", pageindex+"")
                        .add("filter_search", filter_search)
                        .add("film_type", film_type)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=getFilmTopic&m=moyuan")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 获取电影"+ filter_search + "|" + film_type + "|" + resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 同步电影信息
     * @param film_info
     */
    public static void updateChannelFilmInfo(String film_info){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("film_info", film_info)
                        .add("channel_id", Constant.sUserId+"")
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=updateChannelFilmInfo&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, " 同步电影信息"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 同步电影信息
     * @param channel_id
     * @param okHttpResponseCallBack
     */
    public static void getChannelFilmInfo(String channel_id,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("channel_id", channel_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=getChannelFilmInfo&m=rediscache")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 获取电影信息"+ resultString);
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
                            .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=getUserAttributesXiaobei&m=rediscache")
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

                            runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
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


    /**
     * 发送弹幕
     * @param filmid
     * @param barrage_text
     * @param progress
     * @param vip  0不是，1是vip，2是svip
     */
    public static void sendBarrage(String filmid,String barrage_text,int progress,String vip){
        try {
            new Thread(new Runnable() {
                @Override
                public void run(){
                    OkHttpClient client = OkHttpInstance.getInstance();
                    RequestBody formBody = new FormBody.Builder()
                            .add("authid", Constant.sUserId+"")
                            .add("filmid", filmid)
                            .add("barrage_text", barrage_text)
                            .add("progress", progress+"")
                            .add("vip", vip)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=sendBarrage&m=socialchat")
                            .post(formBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
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
     * 获取弹幕
     * @param filmid
     * @param pageIndex
     * @param okHttpResponseCallBack
     */
    public static void getBarrageList(String filmid,int pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("filmid", filmid)
                        .add("pageIndex", pageIndex+"")
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getBarrageList&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 获取弹幕"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    /**
     * 获取推荐房间
     * @param okHttpResponseCallBack
     */
    public static void getSystemRoom(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSystemRoom&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 获取推荐房间"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    /**
     * 缓存房间人数
     * @param channel_id
     * @param channel_mem
     */
    public static void setChannelMemXiaobei(String channel_id,String channel_mem){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("channel_id", channel_id)
                        .add("channel_mem", channel_mem)
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=setChannelMemXiaobei&m=rediscache")
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




    /**
     * 获取ktv音乐列表
     * getKtvMusicList
     * @param key_word
     * @param pageIndex
     * @param okHttpResponseCallBack
     */
    public static void getKtvMusicList(String key_word,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("key_word", key_word)
                        .add("pageIndex", pageIndex)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getKtvMusicList&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 音乐列表"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 获取房间被点歌曲
     *
     * @param roomId
     * @param okHttpResponseCallBack
     */
    public static void getSongList(String roomId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("roomId", roomId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSongList&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 获取房间被点歌曲"+ roomId +resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 点播一首歌曲
     *
     * @param roomId
     * @param musicId
     */
    public static void addSong(String roomId,String musicId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", Constant.sUserId+"")
                        .add("roomId", roomId)
                        .add("musicId", musicId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=addSong&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 点播一首歌曲"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 删除一首歌曲
     *
     * @param songId
     */
    public static void deleteSong(String songId,String roomId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("songId", songId)
                        .add("roomId", roomId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteSong&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, " 删除一首歌曲"+ songId + resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 删除用户歌曲
     *
     * @param userId
     * @param okHttpResponseCallBack
     */
    public static void deleteSongUser(String userId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", userId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteSongUser&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, "删除用户歌曲"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 清空房间歌曲
     * @param okHttpResponseCallBack
     */
    public static void deleteSongRoom(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", Constant.sUserId+"")
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteSongRoom&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, "清空房间歌曲"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 从点歌id获取音乐
     *
     * @param roomId
     * @param songId
     * @param okHttpResponseCallBack
     */
    public static void getSongFromSongId(String roomId, String songId, OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("roomId", roomId)
                        .add("songId", songId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSongFromSongId&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    Log.d(TAG, "从音乐id获取音乐"+ resultString);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 是否点歌
     */
    public static void isOpenOrderSong(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=isOpenOrderSong&m=rediscache")
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
    }



    /**
     * 礼物
     */
    public static void getGiftList(OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getGiftList&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "run: 礼物列表"+resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 送礼物
     * @param giftId
     * @param receiverId
     * @param okHttpResponseCallBack
     */
    public static void sendGift(String giftId,String receiverId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", Constant.sUserId+"")
                        .add("giftId", giftId)
                        .add("receiverId",  receiverId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=sendGift&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "送礼物"+ resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 微信虚拟币下单
     * @param coinId
     */
    public static void buyCoinWechat(String coinId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", Constant.sUserId+"")
                        .add("coinId", coinId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=buyCoinWechat&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "微信虚拟币下单"+ resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 支付宝虚拟币下单
     * @param coinId
     */
    public static void buyCoinAlipay(String coinId,OkHttpResponseCallBack okHttpResponseCallBack){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", Constant.sUserId+"")
                        .add("coinId", coinId)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=buyCoinAlipay&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String resultString = response.body().string();
                    Log.d(TAG, "支付宝虚拟币下单"+ resultString);
                    runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TODO 撤销招募令
    public static void deleteRecruitment(OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", Constant.sUserId+"")
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteRecruitment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:撤销招募令" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    //TODO 开始播放
    public static void playOnce(String post_id,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", Constant.sUserId+"")
                        .add("post_id", post_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=playOnce&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:开始播放" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 播放超过5秒
    public static void moreThanFiveSeconds(String post_id,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", Constant.sUserId+"")
                        .add("post_id", post_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=moreThanFiveSeconds&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:播放超过5秒" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TODO 播放完成
    public static void playCompleted(String post_id,String play_time,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", Constant.sUserId+"")
                        .add("post_id", post_id)
                        .add("play_time", play_time)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=playCompleted&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:播放完成" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //TODO 播放时长
    public static void playTime(String post_id,String play_time,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("authid", Constant.sUserId+"")
                        .add("post_id", post_id)
                        .add("play_time", play_time)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=playTime&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "run:播放时长" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
