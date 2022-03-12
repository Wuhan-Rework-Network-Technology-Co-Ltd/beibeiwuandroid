package io.agora.chatroom.activity;

import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PREPARED;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.agora.chatroom.Common;
import io.agora.chatroom.R;
import io.agora.chatroom.util.CheckPermission;
import io.agora.chatroom.util.MD5Tool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.player.VideoView;

public class PublishPostActivity extends AppCompatActivity {



    private static final String TAG = "PublishRecruitmentActivity";

    //var
    EditText title_et,content_et;
    RadioGroup bankuai_rg;
    RadioButton zipai_rb,zhenshi_rb,qinggan_rb,daquan_rb,dongtai_rb,zhaomuling_rb;
    ImageView imageView1,imageView2,imageView3;
    Button release_btn;
    Switch switch_comment;
    String comment_forbid = "0";

    String posttitle = "";
    String posttext = "";
    String postpicture1 = "";
    String postpicture2 = "";
    String postpicture3 = "";
    String platename = "";
    String postvideo = "";

    ConstraintLayout zoom_layout;

    int imageView_state;

    View mView;

    private AppCompatActivity mContext;

    VideoView videoView;
    int videoHeight = 0;
    int videoWidth = 0;
    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!videoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
    Long videoDuration = 0l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_recruitment);

        mContext = this;

        zoom_layout = findViewById(R.id.zoom_layout);
        videoView = findViewById(R.id.player);

        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CheckPermission.verifyPermissionCameraAndStorage(mContext);




        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        bankuai_rg = findViewById(R.id.bankuai_rg);
        zipai_rb = findViewById(R.id.zipai_rb);
        zhenshi_rb = findViewById(R.id.zhenshi_rb);
        qinggan_rb = findViewById(R.id.qinggan_rb);
        daquan_rb = findViewById(R.id.daquan_rb);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        release_btn = findViewById(R.id.release_btn);
        switch_comment = findViewById(R.id.switch_comment);
        switch_comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    comment_forbid = "0";
                }else {
                    comment_forbid = "1";
                }
            }
        });

        imageView1.setImageResource(R.drawable.plus);
        imageView2.setImageResource(R.drawable.plus);
        imageView3.setImageResource(R.drawable.plus);


        videoView.setOnClickListener(v -> {
            ImageSelector.builder()
                    .onlyVideo(true)
                    .useCamera(true) // 设置是否使用拍照
                    .setSingle(true)  //设置是否单选
                    .setViewImage(true) //是否点击放大图片查看,，默认为true
                    .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
            imageView_state = 0;
        });
        videoView.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == STATE_PREPARED){
                    videoDuration = videoView.getDuration();
                    Log.d(TAG, "onPlayStateChanged: 视频长度"+videoView.getDuration());
                    if (videoView.getDuration()>(60*10*1000)){
                        Toast.makeText(mContext,"视频长度不得大于10分钟，请重新选择!",Toast.LENGTH_LONG).show();
                        release_btn.setClickable(false);
                    }
                }
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(mContext);

                new AlertDialog.Builder(mContext)
                        .setTitle("图片or视频")
                        .setMessage("选择发布图片或视频动态")
                        .setNegativeButton("图片", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageSelector.builder()
                                        .onlyImage(true)
                                        .useCamera(true) // 设置是否使用拍照
                                        .setSingle(true)  //设置是否单选
                                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                                        .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                                imageView_state = 1;
                            }
                        })
                        .setPositiveButton("视频", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageSelector.builder()
                                        .onlyVideo(true)
                                        .useCamera(true) // 设置是否使用拍照
                                        .setSingle(true)  //设置是否单选
                                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                                        .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                                imageView_state = 0;
                            }
                        })
                        .show();


            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(mContext);

                ImageSelector.builder()
                        .onlyImage(true)
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(true)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                imageView_state = 2;
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(mContext);

                ImageSelector.builder()
                        .onlyImage(true)
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(true)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                imageView_state = 3;
            }
        });

        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                posttitle = title_et.getText().toString();
                posttext = content_et.getText().toString();
                platename = ((RadioButton)findViewById(bankuai_rg.getCheckedRadioButtonId())).getText().toString();

                release_btn.setClickable(false);
                postFabutiezi("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=fabutiezi&m=socialchat");

            }
        });

        dongtai_rb = findViewById(R.id.dongtai_rb);
        zhaomuling_rb = findViewById(R.id.zhaomuling_rb);
        if (TextUtils.isEmpty(getIntent().getStringExtra("recruitment"))){
            dongtai_rb.setVisibility(View.VISIBLE);
            bankuai_rg.check(dongtai_rb.getId());
        }else {
            dongtai_rb.setVisibility(View.GONE);
            bankuai_rg.check(zhaomuling_rb.getId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 动态图片地址");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //从图片选择器回来
            case IMAGE_SELECTOR_REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(mContext, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+imageView_state+image);
                        String mPath = image;
//                        portrait.setImageURI(Uri.fromFile(new File(image)));

                        switch (imageView_state){
                            case 0:
                                release_btn.setClickable(true);

                                videoView.setUrl(mPath); //设置视频地址
                                StandardVideoController controller = new StandardVideoController(this);
                                controller.addDefaultControlComponent("标题", false, new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });
                                videoView.setVideoController(controller); //设置控制器
                                videoView.start(); //开始播放，不调用则不自动播放


                                postvideo = mPath;

                                videoView.setVisibility(View.VISIBLE);
                                imageView1.setVisibility(View.GONE);
                                imageView2.setVisibility(View.GONE);
                                imageView3.setVisibility(View.GONE);
                                break;
                            case 1:
                                imageView1.setImageURI(Uri.parse(mPath));
                                postpicture1 = mPath;
                                imageView2.setVisibility(View.VISIBLE);
                                Log.d(TAG, "onActivityResult1: 动态图片地址"+postpicture1);
                                break;
                            case 2:
                                imageView2.setImageURI(Uri.parse(mPath));
                                postpicture2 = mPath;
                                imageView3.setVisibility(View.VISIBLE);
                                Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture2);
                                break;
                            case 3:
                                imageView3.setImageURI(Uri.parse(mPath));
                                postpicture3 = mPath;
                                Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture3);
                                break;
                        }
                    }
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("进入handler", "handler");
            //Toast.makeText(mContext, "已发布成功，等待审核", Toast.LENGTH_LONG).show();
            if (msg.arg1==1) {
                Log.d("跳转", "Navigation");

            }

        }
    };

    //TODO 注册 form形式的post
    public void postFabutiezi(final String url){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_toast_custom,
                null);
//        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
//        tv_msg.setText("正在审核，请稍等！");
//        Toast toast = new Toast(PublishPostActivity.this);
//        toast.setGravity(Gravity.TOP, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(view);
//        toast.show();
        Toast.makeText(PublishPostActivity.this,"已提交审核，请稍等！",Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run(){
                //获取文件名
                Log.d("进入run","run");
                String fileName = "postpicture.png";
                String videoName = "post.mp4";
                String myid = Common.myUserInfoList.getId();
                //开始网络传输
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                MediaType MEDIA_TYPE_VIDEO = MediaType.parse("video/*");
                MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                multipartBody.setType(MultipartBody.FORM);
                multipartBody.addFormDataPart("sign", MD5Tool.getSign(myid));
                multipartBody.addFormDataPart("authid", myid);
                multipartBody.addFormDataPart("posttitle", posttitle);
                multipartBody.addFormDataPart("posttext", posttext);
                multipartBody.addFormDataPart("platename", platename);
                multipartBody.addFormDataPart("comment_forbid", comment_forbid);

                if (!postvideo.isEmpty())
                {
                    Log.d(TAG, "run: 111视频长度"+videoDuration);
                    multipartBody.addFormDataPart("width", videoView.getVideoSize()[0]+"");
                    multipartBody.addFormDataPart("height", videoView.getVideoSize()[1]+"");
                    multipartBody.addFormDataPart("postvideo","postvideo."+Common.lastName(new File(postvideo)),RequestBody.create(new File(postvideo),MEDIA_TYPE_VIDEO));
                    multipartBody.addFormDataPart("duration", videoDuration+"");
                }
                if (!postpicture1.isEmpty())multipartBody.addFormDataPart("postpicture1","postpicture1."+Common.lastName(new File(postpicture1)), RequestBody.create(new File(postpicture1),MEDIA_TYPE_PNG));
                if (!postpicture2.isEmpty())multipartBody.addFormDataPart("postpicture2","postpicture2."+Common.lastName(new File(postpicture2)),RequestBody.create(new File(postpicture2),MEDIA_TYPE_PNG));
                if (!postpicture3.isEmpty())multipartBody.addFormDataPart("postpicture3","postpicture3."+Common.lastName(new File(postpicture3)),RequestBody.create(new File(postpicture3),MEDIA_TYPE_PNG));
                RequestBody requestBody = multipartBody.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Log.d("进入try","try");
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String result = response.body().string();
                    runOnUiThread(() -> {
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.view_toast_custom,
                                null);
                        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
                        tv_msg.setText(result);
                        Toast toast = new Toast(PublishPostActivity.this);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(view);
                        toast.show();
                    });

                    if (!result.equals("发布成功，等待审核！")){
                        release_btn.setClickable(true);
                        return;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        runOnUiThread(()-> {
            //Toast.makeText(mContext, "已发布成功，等待审核", Toast.LENGTH_LONG).show();
            if (platename.equals("招募令")){
                finish();
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, Common.myUserInfoList.getId());
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, "处CP");
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                startActivity(intent);
            }else {
                onBackPressed();
            }
        });
    }
}