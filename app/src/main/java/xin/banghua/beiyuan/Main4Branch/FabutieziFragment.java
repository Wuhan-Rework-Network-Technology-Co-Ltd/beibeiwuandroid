package xin.banghua.beiyuan.Main4Branch;


import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;
import static io.agora.chatroom.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.agora.chatroom.Common;
import io.agora.chatroom.activity.ChatRoomActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.utils.MD5Tool;


public class FabutieziFragment extends Fragment {
    private static final String TAG = "FabutieziFragment";

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


    int imageView_state;

    View mView;

    @Override
    public void onPause() {
        super.onPause();

    }

    private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();


    }
    public FabutieziFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_fabutiezi, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView back_btn = view.findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.fabutiezi_luntan_action));

        CheckPermission.verifyPermissionCameraAndStorage(getActivity());

        title_et = view.findViewById(R.id.title_et);
        content_et = view.findViewById(R.id.content_et);
        bankuai_rg = view.findViewById(R.id.bankuai_rg);
        zipai_rb = view.findViewById(R.id.zipai_rb);
        zhenshi_rb = view.findViewById(R.id.zhenshi_rb);
        qinggan_rb = view.findViewById(R.id.qinggan_rb);
        daquan_rb = view.findViewById(R.id.daquan_rb);
        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);
        release_btn = view.findViewById(R.id.release_btn);
        switch_comment = view.findViewById(R.id.switch_comment);
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

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(getActivity());

                new AlertDialog.Builder(mContext)
                        .setTitle(io.agora.chatroom.R.string.ktv_room_change_music_title)
                        .setMessage(io.agora.chatroom.R.string.ktv_room_change_music_msg)
                        .setNegativeButton("图片", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageSelector.builder()
                                        .onlyImage(true)
                                        .useCamera(true) // 设置是否使用拍照
                                        .setSingle(true)  //设置是否单选
                                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                                        .start(getActivity(), IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                                imageView_state = 1;
                            }
                        })
                        .setPositiveButton("视频", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(getActivity());

                ImageSelector.builder()
                        .onlyImage(true)
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(true)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .start(getActivity(), IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                imageView_state = 2;
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission.verifyPermissionCameraAndStorage(getActivity());

                ImageSelector.builder()
                        .onlyImage(true)
                        .useCamera(true) // 设置是否使用拍照
                        .setSingle(true)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .start(getActivity(), IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
                imageView_state = 3;
            }
        });

        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                posttitle = title_et.getText().toString();
                posttext = content_et.getText().toString();
                platename = ((RadioButton)mView.findViewById(bankuai_rg.getCheckedRadioButtonId())).getText().toString();

                release_btn.setClickable(false);
                postFabutiezi("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=fabutiezi&m=socialchat");

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 动态图片地址");
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
//            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
//            //Your Code
//            ListIterator<String> listIterator = mPaths.listIterator();
//            while (listIterator.hasNext()){
//                String mPath = listIterator.next();
//
//                Log.d("path", mPath);
//                switch (imageView_state){
//                    case 1:
//                        imageView1.setImageURI(Uri.parse(mPath));
//                        postpicture1 = mPath;
//                        imageView2.setVisibility(View.VISIBLE);
//                        Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture1);
//                        break;
//                    case 2:
//                        imageView2.setImageURI(Uri.parse(mPath));
//                        postpicture2 = mPath;
//                        imageView3.setVisibility(View.VISIBLE);
//                        Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture2);
//                        break;
//                    case 3:
//                        imageView3.setImageURI(Uri.parse(mPath));
//                        postpicture3 = mPath;
//                        Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture3);
//                        break;
//                }
//
//            }
//        }

        switch (requestCode) {
            //从图片选择器回来
            case IMAGE_SELECTOR_REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(getActivity(), "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+image);
                        String mPath = image;
//                        portrait.setImageURI(Uri.fromFile(new File(image)));

                        switch (imageView_state){
                            case 1:
                                imageView1.setImageURI(Uri.parse(mPath));
                                postpicture1 = mPath;
                                imageView2.setVisibility(View.VISIBLE);
                                Log.d(TAG, "onActivityResult: 动态图片地址"+postpicture1);
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

            if (msg.arg1==1) {
                Log.d("跳转", "Navigation");

            }

        }
    };

    //TODO 注册 form形式的post
    public void postFabutiezi(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                //获取文件名
                Log.d("进入run","run");
                String fileName = "postpicture.png";
                SharedHelper shuserinfo = SharedHelper.getInstance(App.getApplication());
                String myid = shuserinfo.readUserInfo().get("userID");
                //开始网络传输
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                multipartBody.setType(MultipartBody.FORM);
                multipartBody.addFormDataPart("sign", MD5Tool.getSign(myid));
                multipartBody.addFormDataPart("authid", myid);
                multipartBody.addFormDataPart("posttitle", posttitle);
                multipartBody.addFormDataPart("posttext", posttext);
                multipartBody.addFormDataPart("platename", platename);
                multipartBody.addFormDataPart("comment_forbid", comment_forbid);
                if (!postpicture1.isEmpty())multipartBody.addFormDataPart("postpicture1",fileName,RequestBody.create(new File(postpicture1),MEDIA_TYPE_PNG));
                if (!postpicture2.isEmpty())multipartBody.addFormDataPart("postpicture2",fileName,RequestBody.create(new File(postpicture2),MEDIA_TYPE_PNG));
                if (!postpicture3.isEmpty())multipartBody.addFormDataPart("postpicture3",fileName,RequestBody.create(new File(postpicture3),MEDIA_TYPE_PNG));
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
                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                    });
                    //格式：{"error":"0","info":"登陆成功"}
                    Message message=handler.obtainMessage();
                    message.arg1=1;
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        runOnUiThread(()-> {
            if (platename.equals("招募令")){
                Navigation.findNavController(mView).navigate(R.id.fabutiezi_luntan_action);
                Log.d(TAG, "postFabutiezi: 招募令"+Common.myUserInfoList.getId());
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, xin.banghua.beiyuan.Common.userInfoList.getId());
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, "处CP");
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                startActivity(intent);
            }else {
                Navigation.findNavController(mView).navigate(R.id.fabutiezi_luntan_action);
            }
        });
    }
}
