package xin.banghua.beiyuan.Main4Branch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import androidx.navigation.Navigation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

import static android.app.Activity.RESULT_OK;


public class FabutieziFragment extends Fragment {
    private static final String TAG = "FabutieziFragment";

    //var
    EditText title_et,content_et;
    RadioGroup bankuai_rg;
    RadioButton zipai_rb,zhenshi_rb,qinggan_rb,daquan_rb;
    ImageView imageView1,imageView2,imageView3;
    Button release_btn;

    String posttitle = "";
    String posttext = "";
    String postpicture1 = "";
    String postpicture2 = "";
    String postpicture3 = "";
    String platename = "";

    int imageView_state;

    View mView;

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

        imageView1.setImageResource(R.drawable.plus);
        imageView2.setImageResource(R.drawable.plus);
        imageView3.setImageResource(R.drawable.plus);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(getActivity())
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
                new ImagePicker.Builder(getActivity())
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
                new ImagePicker.Builder(getActivity())
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

                posttitle = title_et.getText().toString();
                posttext = content_et.getText().toString();
                platename = ((RadioButton)mView.findViewById(bankuai_rg.getCheckedRadioButtonId())).getText().toString();

                release_btn.setClickable(false);
                postFabutiezi("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=fabutiezi&m=socialchat");

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
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("进入handler", "handler");
            Toast.makeText(mContext, "已发布成功，等待审核", Toast.LENGTH_LONG).show();
            if (msg.arg1==1) {
                Log.d("跳转", "Navigation");
                Navigation.findNavController(mView).navigate(R.id.fabutiezi_luntan_action);
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
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                //开始网络传输
                OkHttpClient client = new OkHttpClient();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                MultipartBody.Builder multipartBody = new MultipartBody.Builder();
                multipartBody.setType(MultipartBody.FORM);
                multipartBody.addFormDataPart("authid", myid);
                multipartBody.addFormDataPart("posttitle", posttitle);
                multipartBody.addFormDataPart("posttext", posttext);
                multipartBody.addFormDataPart("platename", platename);
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
