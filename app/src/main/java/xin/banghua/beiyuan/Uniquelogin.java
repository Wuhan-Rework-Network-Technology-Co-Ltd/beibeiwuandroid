package xin.banghua.beiyuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class Uniquelogin {

    private static final String TAG = "Uniquelogin";

    Context context;
    Handler handler;
    public Uniquelogin(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public String saveToken(){
        String uniquelogintoken = System.currentTimeMillis()+"";
        SharedPreferences sp = context.getSharedPreferences("uniquelogintoken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uniquelogintoken", uniquelogintoken);
        editor.commit();
        Log.d(TAG, "run: uniquelogintoken"+uniquelogintoken);
        return uniquelogintoken;
    }


    public void uniqueNotification(){
        try {
            //自定义打开的界面
//            Intent intent = new Intent(context, SigninActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //intent里面可以携带参数
//            intent.putExtra("key","来自通知");
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 9
//                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            //1,获取NotificationManager实例
//            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationChannel notificationChannel = new NotificationChannel("10","强制退出通知" ,NotificationManager.IMPORTANCE_DEFAULT);
//            nm.createNotificationChannel(notificationChannel);
//            //2,创建Notification实例
//            Notification.Builder builder = new Notification.Builder(context,notificationChannel.getId());
//            builder.setAutoCancel(true)  //设置点击通知后自动取消通知
//                    .setContentTitle("登出")  //通知标题
//                    .setContentText("您的账号已被禁用或在其他设备登录，强制退出")  //通知第二行的内容
//                    .setContentIntent(pendingIntent)  //点击通知后，发送指定的PendingIntent
//                    .setSmallIcon(R.drawable.logo);  //通知图标，必须设置否则通知不显示
//            Notification build = builder.build();
//            //3,发送通知
//            nm.notify(10,build);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO okhttp对比Uniquetoken
    public void compareUniqueLoginToken(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(context.getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                SharedPreferences sp = context.getSharedPreferences("uniquelogintoken", Context.MODE_PRIVATE);
                String token = sp.getString("uniquelogintoken", "");

                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("token", token)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=10;
                    Log.d(TAG, "run: 对比Uniquetoken"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
