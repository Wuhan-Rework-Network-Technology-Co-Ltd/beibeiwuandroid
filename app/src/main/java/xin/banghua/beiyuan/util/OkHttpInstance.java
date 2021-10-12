package xin.banghua.beiyuan.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static xin.banghua.beiyuan.util.ThreadUtils.runOnUiThread;

public class OkHttpInstance {
    private static final String TAG = "OkHttpInstance";
    private static OkHttpClient uniqueInstance = new OkHttpClient();
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    private OkHttpInstance(){}
    public static OkHttpClient getInstance(){
        return uniqueInstance;
    }


    //TODO RTM获取token
    public static void exhaustSvipTry() {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", ConstantValue.myId)
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
                        .add("id", ConstantValue.myId)
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
}
