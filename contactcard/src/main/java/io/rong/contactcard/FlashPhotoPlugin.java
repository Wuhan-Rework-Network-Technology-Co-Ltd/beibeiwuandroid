package io.rong.contactcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.rong.contactcard.activities.FlashPhotoPreviewActivity;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
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

import static android.app.Activity.RESULT_OK;

public class FlashPhotoPlugin implements IPluginModule {

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
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.rc_ext_plugin_image_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "闪图";
    }

    @Override
    public void onClick(final Fragment fragment, RongExtension rongExtension) {
        mContext = fragment.getActivity();


        uniqueID = ((int)(Math.random()*100))+"x"+(new Date().getTime());
        //示例获取 会话类型、targetId、Context,此处可根据产品需求自定义逻辑，如:开启新的 Activity 等。
        conversationType = rongExtension.getConversationType();
        targetId = rongExtension.getTargetId();
//        ImageMessage imageMessage = ImageMessage.obtain(RealPathFromUriUtils.getDrawableFromDrawableRes(fragment.getActivity(),R.drawable.flashphoto), RealPathFromUriUtils.getDrawableFromDrawableRes(fragment.getActivity(),R.drawable.flashphoto),false);
//        imageMessage.setExtra("zzzzzzzzzzzs");
//        Message message = Message.obtain(targetId, conversationType,imageMessage);
//        RongIM.getInstance().sendImageMessage(message, null, null, new RongIMClient.SendImageMessageCallback() {
//            @Override
//            public void onAttached(Message message) {
//            }
//            @Override
//            public void onSuccess(Message message) {
//                message.setExtra("pppppp");
//                message.setObjectName("flashphoto");
//                Log.d("闪照","发送成功"+message.getContent().toString()+"|"+message.toString());
//                Toast.makeText(fragment.getActivity(), "消息发送成功, 示例获取 Context", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProgress(Message message, int i) {
//            }
//
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//            }
//        });
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, !mImgConfig.allowOnlineImages);
//        photoPickerIntent.setType("image/*");
        //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        Intent i = new Intent(Intent.ACTION_PICK);
        //i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, i);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{i2});
        rongExtension.startActivityForPluginResult(chooserIntent, 123, this);
    }


    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        Log.d(TAG, "进入了8888888onActivityResult");

        if (i == 123 && i1 == RESULT_OK) {
            String mPath = "";
            if (intent.getData() != null) {
                Log.d(TAG, "相册获取的图片地址" + intent.getData().toString());
                mPath = RealPathFromUriUtils.getRealPathFromUri(mContext, intent.getData(), "image");
            }else {
                Log.d(TAG, "相机获取的图片地址"+intent.getExtras().get("data"));
                mPath = RealPathFromUriUtils.getRealPathFromBitmap(mContext,(Bitmap)intent.getExtras().get("data"));
            }
                photoPath = mPath;
                Log.d(TAG,"图片路径"+photoPath);

                //
            Intent intent1 = new Intent(mContext, FlashPhotoPreviewActivity.class);
            intent1.putExtra("uniqueID",uniqueID);
            intent1.putExtra("targetId",targetId);
            intent1.putExtra("photoPath",photoPath);
            intent1.putExtra("conversationType",conversationType);
            mContext.startActivity(intent1);



                //发送消息
                //示例获取 会话类型、targetId、Context,此处可根据产品需求自定义逻辑，如:开启新的 Activity 等。
//                textMessage = TextMessage.obtain("<点击查看5秒闪图>");
//                textMessage.setExtra(uniqueID);
//
//                message = Message.obtain(targetId, conversationType, textMessage);
//                message.setExtra("ok");
//                RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
//                    @Override
//                    public void onAttached(Message message) {
//                    }
//                    @Override
//                    public void onSuccess(Message message) {
//                        senderuserid = message.getSenderUserId();
//                        targetid = message.getTargetId();
//                        senttime = message.getSentTime()+"";
//                        Log.d(TAG,"发送成功"+message.getContent().toString()+"|"+message.toString());
//                        //保存到服务器
//                        saveFlashPhoto("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=saveflashphoto&m=socialchat");
//                    }
//                    @Override
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    }
//                });
            }
    }



//    @SuppressLint("HandlerLeak")
//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 1:
//                    Log.d(TAG, "闪图上传完成");
//                    break;
//            }
//
//        }
//    };
//    //TODO 注册 form形式的post
//    public void saveFlashPhoto(final String url){
//        new Thread(new Runnable() {
//            @Override
//            public void run(){
//                //获取文件名
//                Log.d("进入run","run");
//                File tempFile =new File(photoPath.trim());
//                String fileName = tempFile.getName();
//                //开始网络传输
//                OkHttpClient client = new OkHttpClient();
//                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("uniqueid", uniqueID)
//                        .addFormDataPart("senderuserid", senderuserid)
//                        .addFormDataPart("targetid", targetid)
//                        .addFormDataPart("senttime", senttime)
//                        .addFormDataPart("photourl",fileName,RequestBody.create(new File(photoPath),MEDIA_TYPE_PNG))
//                        .build();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(requestBody)
//                        .build();
//                Log.d("进入try","try");
//                try (Response response = client.newCall(request).execute()) {
//                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                    //Log.d("form形式的post",response.body().string());
//                    //格式：{"error":"0","info":"登陆成功"}
//                    android.os.Message message=handler.obtainMessage();
//                    message.what=1;
//                    message.obj=response.body().string();
//                    Log.d("闪图上传", message.obj.toString());
//                    handler.sendMessageDelayed(message,10);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}
