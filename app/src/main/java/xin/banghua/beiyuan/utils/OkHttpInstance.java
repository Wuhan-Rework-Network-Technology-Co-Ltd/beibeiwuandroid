package xin.banghua.beiyuan.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

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
}
