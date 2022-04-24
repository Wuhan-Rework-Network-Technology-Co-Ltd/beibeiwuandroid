package xin.banghua.beiyuan.OkHttp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class OkHttpHelper {
    Response response = null;
    public String resultJson;
    public Context mContext;
    public OkHttpHelper(Context yContext){
        mContext = yContext;
    }

    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resultJson = msg.obj.toString();
            Toast.makeText(mContext,resultJson,Toast.LENGTH_LONG);
        }
    };

    //TODO 同步的GET
    public void getDatasync(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = OkHttpInstance.getInstance();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象

                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        Log.d("kwwl","res=="+response.body().string());

                        Message message=handler.obtainMessage();
                        message.obj=resultJson;
                        handler.sendMessage(message);
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //TODO 异步的GET
    public void getDataAsync(String url) {
        OkHttpClient client = OkHttpInstance.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    Log.d("kwwl","获取数据成功了");
                    Log.d("kwwl","response.code()=="+response.code());
                    //Log.d("kwwl","response.body().string()=="+response.body().string());
                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    Log.d("objffffff",message.obj.toString());
                    handler.sendMessageDelayed(message,1000);
                }
            }
        });
    }




    //TODO 登录 form形式的post
    public void getDataForm(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("acid", "4")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Log.d("form形式的post",response.body().string());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    //TODO 登录 form形式的post
    public void postSignIn(final String url, final String userAccount, final String userPassword){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userAccount", userAccount)
                        .add("userPassword",userPassword)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();

                    Log.d("登录信息",message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
