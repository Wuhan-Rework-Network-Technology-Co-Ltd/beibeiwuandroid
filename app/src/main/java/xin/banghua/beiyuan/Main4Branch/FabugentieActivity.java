package xin.banghua.beiyuan.Main4Branch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main5Branch.SomeonesluntanActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class FabugentieActivity extends AppCompatActivity {

    private static final String TAG = "FabugentieActivity";
    String id;
    String plateid;
    String platename;
    String authid;
    String authnickname;
    String authportrait;
    String posttip;
    String posttitle;
    String posttext;
    String[] postpicture;
    String like;
    String favorite;
    String time;
    //var
    EditText content_et;
    ImageView imageView1,imageView2,imageView3;
    Button release_btn;

    int imageView_state;

    String followtext = "";
    String followpicture1 = "";
    String followpicture2 = "";
    String followpicture3 = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fabugentie);
        id = getIntent().getStringExtra("postid");
        plateid = getIntent().getStringExtra("plateid");
        platename = getIntent().getStringExtra("platename");
        authid = getIntent().getStringExtra("authid");
        authnickname = getIntent().getStringExtra("authnickname");
        authportrait = getIntent().getStringExtra("authportrait");
        posttip = getIntent().getStringExtra("posttip");
        posttitle = getIntent().getStringExtra("posttitle");
        posttext = getIntent().getStringExtra("posttext");
        postpicture = getIntent().getStringArrayExtra("postpicture");
        like = getIntent().getStringExtra("like");
        favorite = getIntent().getStringExtra("favorite");
        time = getIntent().getStringExtra("time");
        Log.d(TAG, "onCreate: postid"+id);

        content_et = findViewById(R.id.content_et);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        release_btn = findViewById(R.id.release_btn);

        imageView1.setImageResource(R.drawable.plus);
        imageView2.setImageResource(R.drawable.plus);
        imageView3.setImageResource(R.drawable.plus);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(FabugentieActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
                imageView_state = 1;
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(FabugentieActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
                imageView_state = 2;
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(FabugentieActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
                imageView_state = 3;
            }
        });

        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                followtext = content_et.getText().toString();

                postFabugentie("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=fabugentie&m=socialchat");

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 动态图片地址");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            //Your Code
            ListIterator<String> listIterator = mPaths.listIterator();
            while (listIterator.hasNext()){
                String mPath = listIterator.next();

                Log.d("path", mPath);
                switch (imageView_state){
                    case 1:
                        imageView1.setImageURI(Uri.parse(mPath));
                        followpicture1 = mPath;
                        imageView2.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onActivityResult: 动态图片地址"+followpicture1);
                        break;
                    case 2:
                        imageView2.setImageURI(Uri.parse(mPath));
                        followpicture2 = mPath;
                        imageView3.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onActivityResult: 动态图片地址"+followpicture2);
                        break;
                    case 3:
                        imageView3.setImageURI(Uri.parse(mPath));
                        followpicture3 = mPath;
                        Log.d(TAG, "onActivityResult: 动态图片地址"+followpicture3);
                        break;
                }

            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("进入handler", "handler");
            if (msg.arg1==1) {
                Log.d("跳转", "Navigation");
//                Intent intent = new Intent(FabugentieActivity.this,PostListActivity.class);
//                intent.putExtra("postid",id);
//                intent.putExtra("plateid",plateid);
//                intent.putExtra("platename",platename);
//                intent.putExtra("authid",authid);
//                intent.putExtra("authnickname",authnickname);
//                intent.putExtra("authportrait",authportrait);
//                intent.putExtra("posttip",posttip);
//                intent.putExtra("posttitle",posttitle);
//                intent.putExtra("posttext",posttext);
//                intent.putExtra("postpicture",postpicture);
//                intent.putExtra("like",like);
//                intent.putExtra("favorite",favorite);
//                intent.putExtra("time",time);
//                startActivity(intent);
                //改为跳转个人帖子
                Intent intent = new Intent(FabugentieActivity.this, SomeonesluntanActivity.class);
                startActivity(intent);
            }

        }
    };

    //TODO 注册 form形式的post
    public void postFabugentie(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                //获取文件名
                Log.d("进入run","run");
                String fileName = "followpicture.png";
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                //开始网络传输
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                multipartBody.setType(MultipartBody.FORM);
                multipartBody.addFormDataPart("authid", myid);
                multipartBody.addFormDataPart("postid",id);
                multipartBody.addFormDataPart("followtext", followtext);
                if (!followpicture1.isEmpty())multipartBody.addFormDataPart("followpicture1",fileName, RequestBody.create(new File(followpicture1),MEDIA_TYPE_PNG));
                if (!followpicture2.isEmpty())multipartBody.addFormDataPart("followpicture2",fileName,RequestBody.create(new File(followpicture2),MEDIA_TYPE_PNG));
                if (!followpicture3.isEmpty())multipartBody.addFormDataPart("followpicture3",fileName,RequestBody.create(new File(followpicture3),MEDIA_TYPE_PNG));
                RequestBody requestBody = multipartBody.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Log.d("进入try","try");
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Log.d("form形式的post",response.body().string());
                    //格式：{"error":"0","info":"登陆成功"}
                    Message message=handler.obtainMessage();
                    message.arg1=1;
                    Log.d("帖子","");
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
