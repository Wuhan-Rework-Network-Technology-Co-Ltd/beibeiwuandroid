package xin.banghua.beiyuan.RongYunExtension;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;

public class FlashPhotoActivity extends AppCompatActivity {
    Integer countDown = 5;
    TextView countdown_textview;
    ImageView flashphoto_imageview;

    String uniqueid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_photo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        countdown_textview = findViewById(R.id.countdown_textview);
        flashphoto_imageview = findViewById(R.id.flashphoto_imageview);


        uniqueid = getIntent().getStringExtra("uniqueid");

        getFlashPhoto("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getflashphoto&m=socialchat");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (msg.arg1==0){
                        Toast.makeText(getApplicationContext(), "图片已销毁", Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        countdown_textview.setText(msg.arg1+"");
                    }
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        Log.d("TAG", "获取闪图地址"+jsonObject.get("photourl"));
                        if (jsonObject.get("photostatus").equals("0")){
                            Glide.with(FlashPhotoActivity.this)
                                    .asBitmap()
                                    .load(jsonObject.get("photourl"))
                                    .into(flashphoto_imageview);
                            countDown();
                        }else {
                            Toast.makeText(getApplicationContext(), "图片已销毁", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    //TODO 倒计时
    public void countDown(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (countDown != 0) {
                    countDown--;
                    try {
                        Thread.sleep(1000);
                        Message message=handler.obtainMessage();
                        message.arg1=countDown;
                        message.what=1;
                        handler.sendMessageDelayed(message,10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


    //TODO okhttp获取用户信息
    public void getFlashPhoto(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("uniqueid", uniqueid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=2;
                    Log.d("TAG", "获取闪图信息"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
