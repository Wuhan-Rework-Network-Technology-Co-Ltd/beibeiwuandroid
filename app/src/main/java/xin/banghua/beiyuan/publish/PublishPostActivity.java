package xin.banghua.beiyuan.publish;

import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;
import static xyz.doikki.videoplayer.player.VideoView.STATE_PREPARED;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.Common;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.util.CheckPermission;
import io.agora.chatroom.util.MD5Tool;
import io.agora.chatroom.util.NiceZoomImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main4Activity;
import xin.banghua.beiyuan.custom_ui.music_selector.SingletonMusicPlayer;
import xin.banghua.beiyuan.topic.TopicDialog;
import xin.banghua.beiyuan.topic.TopicList;
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


    public List<String> pictures = new ArrayList<>();



    @BindView(R2.id.recyclerview)
    RecyclerView recyclerview;

    PictureAdapter pictureAdapter;

    public List<TopicList> topicLists = new ArrayList<>();
    @BindView(R2.id.add_topic)
    Button add_topic;


    public LinearLayout topic_layout;


    public String music;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_recruitment);

        ButterKnife.bind(this);

        SingletonMusicPlayer.getInstance().music = null;
        SingletonMusicPlayer.getInstance().reset();

        mContext = this;

        if(Common.filterString.length==0){
            OkHttpInstance.getFilterWords(responseString -> {
                Common.filterString = responseString.split(",");
            });
        }

        zoom_layout = findViewById(R.id.zoom_layout);
        videoView = findViewById(R.id.player);
        topic_layout = findViewById(R.id.topic_layout);

        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CheckPermission.verifyPermissionCameraAndStorage(mContext);


        pictureAdapter = new PictureAdapter(pictures);
        recyclerview.setAdapter(pictureAdapter);
        recyclerview.setLayoutManager(new GridLayoutManager(mContext,3));

        title_et = findViewById(R.id.title_et);
        title_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (String element: Common.filterString) {
                    if (s.toString().contains(element)){
                        title_et.setText(s.toString().replace(element, "*"));
                        Log.d(TAG, "onEditorAction: 过滤了"+s.toString());
                    }
                }
            }
        });
        content_et = findViewById(R.id.content_et);
        content_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (String element: Common.filterString) {
                    if (s.toString().contains(element)){
                        content_et.setText(s.toString().replace(element, "*"));
                        Log.d(TAG, "onEditorAction: 过滤了"+s.toString());
                    }
                }
            }
        });
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
                postFabutiezi("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=fabutiezinewnew&m=socialchat");

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

        TopicDialog topicDialog = new TopicDialog(this);
        add_topic.setOnClickListener(v -> {
            topicDialog.initShow(this);
        });


        if (getIntent().getStringExtra("music")!=null){
            music = getIntent().getStringExtra("music");
        }
        if (getIntent().getStringExtra("picture")!=null){
            pictures.add(getIntent().getStringExtra("picture"));
            pictureAdapter.setPictures(pictures);
            videoView.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
        }
        if (getIntent().getStringExtra("postvideo")!=null){
            postvideo = getIntent().getStringExtra("postvideo");

            release_btn.setClickable(true);

            videoView.setUrl(postvideo); //设置视频地址
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

            videoView.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            imageView3.setVisibility(View.GONE);

            recyclerview.setVisibility(View.GONE);
        }


        if (getIntent().getBooleanExtra("isAlbum",false)){
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
    }


    public void addTopicView(){

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

                                recyclerview.setVisibility(View.GONE);
                                break;
                            case 1:
                                pictures.add(image);
                                pictureAdapter.setPictures(pictures);
                                videoView.setVisibility(View.GONE);
                                recyclerview.setVisibility(View.VISIBLE);
//                                imageView1.setImageURI(Uri.parse(mPath));
//                                postpicture1 = mPath;
//                                imageView2.setVisibility(View.VISIBLE);
//                                Log.d(TAG, "onActivityResult1: 动态图片地址"+postpicture1);
//                                break;
//                            case 2:
//                                imageView2.setImageURI(Uri.parse(mPath));
//                                postpicture2 = mPath;
//                                imageView3.setVisibility(View.VISIBLE);
//                                Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture2);
//                                break;
//                            case 3:
//                                imageView3.setImageURI(Uri.parse(mPath));
//                                postpicture3 = mPath;
//                                Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture3);
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
                String myid = PublishPostActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("userID", "");
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

                if (music != null){
                    multipartBody.addFormDataPart("music", music);
                }
                if (topicLists.size()>0){
                    Log.d(TAG, "run: 帖子话题"+JSON.toJSONString(topicLists));
                    multipartBody.addFormDataPart("topicLists", JSON.toJSONString(topicLists));
                }
                if (!postvideo.isEmpty()) {
                    Log.d(TAG, "run: 111视频长度"+videoDuration);
                    multipartBody.addFormDataPart("width", videoView.getVideoSize()[0]+"");
                    multipartBody.addFormDataPart("height", videoView.getVideoSize()[1]+"");
                    multipartBody.addFormDataPart("postvideo","postvideo."+Common.lastName(new File(postvideo)),RequestBody.create(new File(postvideo),MEDIA_TYPE_VIDEO));
                    multipartBody.addFormDataPart("duration", videoDuration+"");
                }

                if(pictures.size()>0){
                    for (int i=0;i<pictures.size();i++){
                        multipartBody.addFormDataPart("postpicture"+i,"postpicture"+i+"."+Common.lastName(new File(pictures.get(i))), RequestBody.create(new File(pictures.get(i)),MEDIA_TYPE_PNG));
                    }
                }

//                if (!postpicture1.isEmpty())multipartBody.addFormDataPart("postpicture1","postpicture1."+Common.lastName(new File(postpicture1)), RequestBody.create(new File(postpicture1),MEDIA_TYPE_PNG));
//                if (!postpicture2.isEmpty())multipartBody.addFormDataPart("postpicture2","postpicture2."+Common.lastName(new File(postpicture2)),RequestBody.create(new File(postpicture2),MEDIA_TYPE_PNG));
//                if (!postpicture3.isEmpty())multipartBody.addFormDataPart("postpicture3","postpicture3."+Common.lastName(new File(postpicture3)),RequestBody.create(new File(postpicture3),MEDIA_TYPE_PNG));
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

                    if (!result.equals("发布成功，等待审核！（经验+1，积分+10）")){
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
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
                //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                startActivity(intent);
            }else {
                Intent intent = new Intent(mContext, Main4Activity.class);
                startActivity(intent);
            }
        });
    }




    public class PictureAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<String> pictures = new ArrayList<>();

        public PictureAdapter(List<String> pictures) {
            this.pictures = pictures;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item0,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setPictures(List<String> pictures){
            this.pictures = pictures;
            notifyDataSetChanged();
        }
        public void swapData(List<String> pictures){
            int oldSize = this.pictures.size();
            int newSize = pictures.size();
            this.pictures = pictures;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");
            int currentPosition = position;
            if (this.pictures.size() == position){
                holder.delete_img.setVisibility(View.GONE);
                Glide.with(mContext).load(R.drawable.plus).into(holder.picture_img);
                holder.picture_img.setOnClickListener(v -> {

                    CheckPermission.verifyPermissionCameraAndStorage(mContext);

                    int num = 9 - pictures.size();

                    if (imageView_state==1){
                        ImageSelector.builder()
                                .onlyImage(true)
                                .useCamera(true) // 设置是否使用拍照
                                .setSingle(false)  //设置是否单选
                                .setMaxSelectCount(num)
                                .setViewImage(true) //是否点击放大图片查看,，默认为true
                                .start(mContext, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                    }else {
                        new AlertDialog.Builder(mContext)
                                .setTitle("图片or视频")
                                .setMessage("选择发布图片或视频动态")
                                .setNegativeButton("图片", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ImageSelector.builder()
                                                .onlyImage(true)
                                                .useCamera(true) // 设置是否使用拍照
                                                .setSingle(false)  //设置是否单选
                                                .setMaxSelectCount(num)
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
            }else {
                String currentItem = this.pictures.get(position);
                Glide.with(mContext).load(currentItem).into(holder.picture_img);
                holder.delete_img.setVisibility(View.VISIBLE);
                holder.delete_img.setOnClickListener(v -> {
                    this.pictures.remove(currentPosition);
                    notifyDataSetChanged();
                });
                holder.picture_img.setClickable(false);
            }
        }

        @Override
        public int getItemCount() {
            if (pictures.size()==9){
                return this.pictures.size();
            }else {
                return this.pictures.size() + 1;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        NiceZoomImageView picture_img;
        ImageView delete_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture_img = itemView.findViewById(R.id.picture_img);
            delete_img = itemView.findViewById(R.id.delete_img);
        }
    }
}