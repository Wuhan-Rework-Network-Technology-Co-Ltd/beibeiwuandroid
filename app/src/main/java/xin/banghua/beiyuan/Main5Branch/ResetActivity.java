package xin.banghua.beiyuan.Main5Branch;

import static com.donkingliang.imageselector.ImageSelectorActivity.IMAGE_SELECTOR_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main5Activity;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.CityAdapter;
import xin.banghua.beiyuan.Signin.ProvinceAdapter;
import xin.banghua.beiyuan.bean.AddrBean;
import xin.banghua.beiyuan.utils.MD5Tool;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class ResetActivity extends AppCompatActivity {

    private static final String TAG = "ResetActivity";
    //年龄选择
    //地区选择
    Spinner spProvince, spCity;
    private AddrBean addrBean;
    private ProvinceAdapter adpProvince;
    private CityAdapter adpCity;
    private List<AddrBean.ProvinceBean.CityBean> cityBeanList;
    private AddrBean.ProvinceBean provinceBean;
    //var
    Button submit_btn,verificationCode_btn;
    EditText value_et,verificationCode_et;
    TextView title_tv;
    TextView current_address;
    CircleImageView portrait;
    RadioGroup userGender;
    RadioGroup userProperty;
    String smscode;
    String userAcountString,verificationCodeString;;
    Integer countDown = 60;

    String title,userPortrait,value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        title = getIntent().getStringExtra("title");

        ImageView back_btn = findViewById(R.id.iv_back_left);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        current_address = findViewById(R.id.current_address);
        current_address.setVisibility(View.GONE);

        getDataPersonage("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=personage&m=socialchat");


        if (title.equals("手机绑定")){
            verificationCode_btn = findViewById(R.id.verificationCode_btn);
            verificationCode_et = findViewById(R.id.verificationCode_et);
            verificationCode_et.setVisibility(View.VISIBLE);
            verificationCode_btn.setVisibility(View.VISIBLE);
            verificationCode_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAcountString = value_et.getText().toString();
                    if (userAcountString.length()!=11){
                        Toast.makeText(ResetActivity.this, "请输入手机号", Toast.LENGTH_LONG).show();
                        return;
                    }
                    sendCode("https://www.banghua.xin/sms_beibeiwu.php",userAcountString);
                    countDown();
                }
            });
        }

    }

    //TODO okhttp验证信息
    public void verificationCode(final String verificationCodeString){
        OkHttpInstance.smsVerify(userAcountString, verificationCodeString, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if(responseString.equals("true")){
                    submitValue("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=reset&m=socialchat","手机绑定");
                }else {
                    Toast.makeText(ResetActivity.this, "验证码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void initView() {
        spCity = findViewById(R.id.spinner_city);
        spProvince = findViewById(R.id.spinner_province);


        adpProvince = new ProvinceAdapter(this);
        adpCity = new CityAdapter(this);
    }
    private void loadData() {
        try {
            InputStream inputStream = getApplicationContext().getAssets().open("addr_china.json");

            addrBean = new GsonBuilder().create().fromJson(new InputStreamReader(inputStream), AddrBean.class);

            spProvince.setAdapter(adpProvince);
            adpProvince.setProvinceBeanList(addrBean.getProvinceList());

            spCity.setAdapter(adpCity);
            adpCity.setCityBeanList(addrBean.getProvinceList().get(0).getCitylist());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void register() {
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spCity.setVisibility(View.VISIBLE);
                cityBeanList = addrBean.getProvinceList().get(position).getCitylist();
                provinceBean = addrBean.getProvinceList().get(position);
                adpCity.setCityBeanList(addrBean.getProvinceList().get(position).getCitylist());
                //选择省份后，城市默认第一个
                String selected_province = provinceBean.getProvince();
                String selected_city = cityBeanList.get(0).getCityName();
                value_et.setText(selected_province+"-"+selected_city);
                spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //选择城市后
                        String selected_province = provinceBean.getProvince();
                        String selected_city = cityBeanList.get(position).getCityName();
                        value_et.setText(selected_province+"-"+selected_city);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //TODO okhttp获取用户信息
    public void getDataPersonage(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getBaseContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("userid", myid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=3;
                    Log.d(TAG, "run: getDataPersonage"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private final int  RequestCode = 101;
    public void initPersonage(JSONObject jsonObject) throws JSONException {




        submit_btn = findViewById(R.id.submit_btn);
        value_et = findViewById(R.id.value_et);
        value_et.setHint(title);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(title);
        portrait = findViewById(R.id.portrait);
        userGender = findViewById(R.id.userGender);
        userProperty = findViewById(R.id.userProperty);


        if (title.equals("头像设置")){
            SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
            String myportrait = shuserinfo.readUserInfo().get("userPortrait");
            value_et.setVisibility(View.GONE);
            portrait.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(myportrait)){
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.picture_icon_placeholder)
                        .into(portrait);
            }else {
                Glide.with(App.getApplication())
                        .asBitmap()
                        .load(Common.getOssResourceUrl(myportrait))
                        .into(portrait);
            }

        }else if (title.equals("昵称设置")){
            value_et.setVisibility(View.VISIBLE);
            value_et.setText(jsonObject.getString("nickname"));
        }else if (title.equals("年龄设置")){
            value_et.setVisibility(View.GONE);
            value_et.setText(jsonObject.getString("age"));
            //年龄选择器
            Spinner spinner_age = findViewById(R.id.spinner_age);
            spinner_age.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> adapter_age = ArrayAdapter.createFromResource(this,R.array.age,android.R.layout.simple_spinner_item);
            adapter_age.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_age.setAdapter(adapter_age);
            spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected_age = parent.getItemAtPosition(position).toString();
                    value_et.setText(selected_age);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinner_age.setSelection(adapter_age.getPosition(jsonObject.getString("age")));
        }else if (title.equals("性别设置")){
            value_et.setVisibility(View.GONE);
            userGender.setVisibility(View.VISIBLE);
            if (jsonObject.getString("gender").equals("男")){
                userGender.check(R.id.male);
            }else {
                userGender.check(R.id.female);
            }
        }else if (title.equals("属性设置")){
            value_et.setVisibility(View.GONE);
            userProperty.setVisibility(View.VISIBLE);
            if (jsonObject.getString("property").equals("双")){
                userProperty.check(R.id.dProperty);
            }else if(jsonObject.getString("property").equals("Z")){
                userProperty.check(R.id.zProperty);
            }else if(jsonObject.getString("property").equals("B")){
                userProperty.check(R.id.bProperty);
            }
        }else if (title.equals("地区设置")){
            value_et.setVisibility(View.GONE);
            current_address.setVisibility(View.VISIBLE);
            current_address.setText("当前地址："+jsonObject.getString("region"));
            spCity = findViewById(R.id.spinner_city);
            spProvince = findViewById(R.id.spinner_province);
            spCity.setVisibility(View.VISIBLE);
            spProvince.setVisibility(View.VISIBLE);
            initView();
            loadData();
            register();
        }else if (title.equals("签名设置")){
            value_et.setVisibility(View.VISIBLE);
            value_et.setText(jsonObject.getString("signature"));
        }


        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ImagePicker.Builder(ResetActivity.this)
//                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
//                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
//                        .directory(ImagePicker.Directory.DEFAULT)
//                        .extension(ImagePicker.Extension.PNG)
//                        .scale(600, 600)
//                        .allowMultipleImages(false)
//                        .enableDebuggingMode(true)
//                        .build();

                CheckPermission.verifyPermissionCameraAndStorage(ResetActivity.this);

                ImageSelector.builder()
                        .useCamera(true) // 设置是否使用拍照
                        .setCrop(true)  // 设置是否使用图片剪切功能。
                        .setSingle(true)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .start(ResetActivity.this, IMAGE_SELECTOR_REQUEST_CODE); // 打开相册
            }
        });



        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (title){
                    case "头像设置":
                        setUserPortrait("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=reset&m=socialchat");
                        break;
                    case "性别设置":
                        submitValue("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=reset&m=socialchat","性别");
                        break;
                    case "属性设置":
                        submitValue("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=reset&m=socialchat","属性");
                        break;
                    case "意见反馈":
                        submitValue("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=feedback&m=socialchat","意见");
                        break;
                    case "手机绑定":
                        verificationCode(verificationCode_et.getText().toString());
                        break;
                    default:
                        submitValue("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=reset&m=socialchat","其他");
                        break;
                }
                Toast.makeText(v.getContext(), "提交成功", Toast.LENGTH_LONG).show();

            }
        });
    }
    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(ResetActivity.this, Main5Activity.class);
            switch (msg.what){
                case 1:
                    if (title.equals("昵称设置")){
                        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userNickName", value);
                        editor.commit();

                        Common.userInfoList.setNickname(value);
                        //融云更新缓存
                        SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                        String myid = shuserinfo.readUserInfo().get("userID");
                        String mynickname = shuserinfo.readUserInfo().get("userNickName");
                        String myportrait = shuserinfo.readUserInfo().get("userPortrait");
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(myid, mynickname, Uri.parse(Common.getOssResourceUrl(myportrait))));
                    }

                    startActivity(intent);
                    break;
                case 2:
                    SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    Log.d(TAG, "handleMessage: 修改头像"+msg.obj.toString());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userPortrait", msg.obj.toString());
                    editor.commit();
                    Common.userInfoList.setPortrait(msg.obj.toString());
                    //融云更新缓存
                    SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                    String myid = shuserinfo.readUserInfo().get("userID");
                    String mynickname = shuserinfo.readUserInfo().get("userNickName");
                    String myportrait = shuserinfo.readUserInfo().get("userPortrait");
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(myid, mynickname, Uri.parse(Common.getOssResourceUrl(myportrait))));

                    startActivity(intent);
                    break;
                case 3:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        JSONObject jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        initPersonage(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    if (msg.arg1==0){
                        verificationCode_btn.setText("点击获取验证码");
                        verificationCode_btn.setEnabled(true);
                        countDown = 60;
                    }else {
                        verificationCode_btn.setText(msg.arg1+"");
                        verificationCode_btn.setEnabled(false);
                    }
                    break;
                case 5:
                    Toast.makeText(ResetActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                    smscode = msg.obj.toString();
                    break;
            }
        }
    };
    //TODO okhttp设置手机，密码，邮箱,昵称,年龄，地区。
    public void submitValue(final String url, final String type){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                String mynickname = shuserinfo.readUserInfo().get("userNickName");
                String myportrait = shuserinfo.readUserInfo().get("userPortrait");
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody;
                switch (type){
                    case "性别":
                        value = ((RadioButton) findViewById(userGender.getCheckedRadioButtonId())).getText().toString();
                         formBody = new FormBody.Builder()
                                 .add("sign", MD5Tool.getSign(myid))
                                 .add("type",title)
                                 .add("userID", myid)
                                 .add("value",value)
                                 .build();
                    break;
                    case "属性":
                        value = ((RadioButton) findViewById(userProperty.getCheckedRadioButtonId())).getText().toString();
                         formBody = new FormBody.Builder()
                                 .add("sign", MD5Tool.getSign(myid))
                                 .add("type",title)
                                 .add("userID", myid)
                                 .add("value",value)
                                 .build();
                        break;
                    case "意见":
                        value = value_et.getText().toString();
                        formBody = new FormBody.Builder()
                                .add("sign", MD5Tool.getSign(myid))
                                .add("type",title)
                                .add("userID", myid)
                                .add("nickname", mynickname)
                                .add("portrait", myportrait)
                                .add("content",value)
                                .build();
                        break;
                    default:
                        value = value_et.getText().toString();
                         formBody = new FormBody.Builder()
                                 .add("sign", MD5Tool.getSign(myid))
                                 .add("type",title)
                                 .add("userID", myid)
                                 .add("value",value)
                                 .build();
                        break;
                }

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.what=1;

                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //TODO 注册 form形式的post
    public void setUserPortrait(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                //获取文件名
                Log.d("进入run","run");
                File tempFile =new File(userPortrait.trim());
                String fileName = tempFile.getName();
                //用户id
                SharedHelper shuserinfo = new SharedHelper(getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");
                //开始网络传输
                OkHttpClient client = OkHttpInstance.getInstance();
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("sign", MD5Tool.getSign(myid))
                        .addFormDataPart("type", title)
                        .addFormDataPart("userID", myid)
                        .addFormDataPart("userPortrait",fileName,RequestBody.create(new File(userPortrait),MEDIA_TYPE_PNG))
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
                    Message message=handler.obtainMessage();
                    message.what=2;
                    message.obj=response.body().string();
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
//            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
//            //Your Code
//            ListIterator<String> listIterator = mPaths.listIterator();
//            while (listIterator.hasNext()){
//                String mPath = listIterator.next();
//                Log.d("来自选单的单选地址", mPath);
//                portrait.setImageURI(Uri.parse(mPath));
//                userPortrait = mPath;
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
                        Toast.makeText(this, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+image);
                        String mPath = image;
//                        portrait.setImageURI(Uri.fromFile(new File(image)));

                        portrait.setImageURI(Uri.parse(mPath));
                        userPortrait = mPath;
                    }
                }
                break;
        }
    }

    //TODO okhttp获取用户信息
    public void sendCode(final String url,final String phoneNumber){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("phoneNumber", phoneNumber)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=(Integer.parseInt(response.body().string())-18)+"";
                    message.what=5;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

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
                        message.what=4;
                        handler.sendMessageDelayed(message,10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
