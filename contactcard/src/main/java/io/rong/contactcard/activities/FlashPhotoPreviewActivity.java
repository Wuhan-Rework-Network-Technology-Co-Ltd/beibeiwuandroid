package io.rong.contactcard.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.rong.contactcard.R;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlashPhotoPreviewActivity extends RongBaseNoActionbarActivity {
    Button cancel_btn,confirm_btn;
    ImageView flashphoto_preview_img;

    String TAG ="闪图";
    Conversation.ConversationType conversationType;
    String targetId;




    Context mContext;

    TextMessage textMessage;
    Message message;

    String uniqueID;
    String senderuserid;
    String targetid;
    String senttime;
    String photoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_photo_preview);

        cancel_btn = findViewById(R.id.cancel_btn);
        confirm_btn = findViewById(R.id.confirm_btn);
        flashphoto_preview_img = findViewById(R.id.flashphoto_preview_img);

        Intent intent = getIntent();
        targetId = intent.getStringExtra("targetId");
        uniqueID = intent.getStringExtra("uniqueID");
        photoPath = intent.getStringExtra("photoPath");
        conversationType = (Conversation.ConversationType)intent.getSerializableExtra("conversationType");

        flashphoto_preview_img.setImageBitmap(getLoacalBitmap(photoPath));

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"发送闪图"+targetId+"|"+uniqueID+"|"+photoPath);


                textMessage = TextMessage.obtain("<点击查看5秒闪图>");
                textMessage.setExtra(uniqueID);


                message = Message.obtain(targetId, conversationType, textMessage);
                message.setExtra("ok");
                RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }
                    @Override
                    public void onSuccess(Message message) {
                        senderuserid = message.getSenderUserId();
                        targetid = message.getTargetId();
                        senttime = message.getSentTime()+"";
                        Log.d(TAG,"闪图消息发送成功"+message.getContent().toString()+"|"+message.toString());
                        //保存到服务器
                        saveFlashPhoto("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=saveflashphoto&m=socialchat");
                    }
                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                });

                finish();
            }
        });

    }
    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.d(TAG, "闪图上传完成");
                    break;
            }

        }
    };
    //TODO 注册 form形式的post
    public void saveFlashPhoto(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                //获取文件名
                Log.d("进入run","run");
                File tempFile =new File(photoPath.trim());
                String fileName = tempFile.getName();
                //开始网络传输
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uniqueid", uniqueID)
                        .addFormDataPart("senderuserid", senderuserid)
                        .addFormDataPart("targetid", targetid)
                        .addFormDataPart("senttime", senttime)
                        .addFormDataPart("photourl",fileName,RequestBody.create(new File(photoPath),MEDIA_TYPE_PNG))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Log.d("进入try","try");
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    //格式：{"error":"0","info":"登陆成功"}
                    android.os.Message message=handler.obtainMessage();
                    message.what=1;
                    message.obj=response.body().string();
                    Log.d("闪图上传", message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}