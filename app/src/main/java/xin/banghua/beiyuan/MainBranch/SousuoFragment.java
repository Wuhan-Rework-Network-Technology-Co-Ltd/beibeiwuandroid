package xin.banghua.beiyuan.MainBranch;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.navigation.Navigation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SousuoFragment extends Fragment {

    private static final String TAG = "SousuoFragment";

    //昵称，年龄，地区
    EditText userNickname_et,userAge_et,userRegion_et,direct_et;
    //性别，标签
    RadioGroup userGender_rg,userProperty_rg;
    RadioButton male_rb,female_rb,zProperty_rb,bProperty_rb,dProperty_rb;

    //
    String logtype,userAccount,userPassword,userNickname,userAge,userRegion,userGender,userProperty,userPortrait;
    Button direct_btn,condition_btn;


    TextView viptime_tv;
    public SousuoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sousuo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userRegion_et = view.findViewById(R.id.userRegion);
        userProperty_rg = view.findViewById(R.id.userProperty);//直接搜索
        direct_et = view.findViewById(R.id.direct_et);
        direct_btn = view.findViewById(R.id.direct_btn);
        //条件搜索
        userAge_et = view.findViewById(R.id.userAge);
        userGender_rg = view.findViewById(R.id.userGender);
        condition_btn = view.findViewById(R.id.submit_btn);
        viptime_tv = view.findViewById(R.id.viptime_tv);
        zProperty_rb = view.findViewById(R.id.zProperty);
        bProperty_rb = view.findViewById(R.id.bProperty);
        dProperty_rb = view.findViewById(R.id.dProperty);
        //vip
        getVipinfo("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat");
        //初始化导航按钮
        initNavigateButton(view);

        //监听提交按钮
        submitValue(view);
    }
    //三个按钮初始化
    private void initNavigateButton(View view){
        Button tuijian = view.findViewById(R.id.tuijian_btn);
        Button fujin = view.findViewById(R.id.fujin_btn);
        //Button sousuo = view.findViewById(R.id.sousuo_btn);

        tuijian.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.sousuo_tuijian_action));
        fujin.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.sousuo_fujin_action));
    }

    //搜索提交的传值
    private void submitValue(final View view){



        //会直接运行getDirectBundle，所以没法动态传值
        //direct_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.sousuo_result_action,getDirectBundle()));
        //condition_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.sousuo_result_action,getConditionBundle(view)));
        SearchView searchView = view.findViewById(R.id.direct_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("type","direct");
                bundle.putString("nameOrPhone",query);
                Navigation.findNavController(view).navigate(R.id.sousuo_result_action, bundle);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        direct_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 直接搜索"+getDirectBundle().getString("nameOrPhone"));
                Navigation.findNavController(v).navigate(R.id.sousuo_result_action, getDirectBundle());
            }
        });
        condition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 条件搜索");
                Navigation.findNavController(v).navigate(R.id.sousuo_result_action, getConditionBundle(view));
            }
        });
    }

    //获取值作为监听参数
    private Bundle getDirectBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("type","direct");
        bundle.putString("nameOrPhone",direct_et.getText().toString());
        return bundle;
    }
    //获取值作为监听参数
    private Bundle getConditionBundle(View view){
        Bundle bundle = new Bundle();
        bundle.putString("type","condition");
        bundle.putString("userAge",userAge_et.getText().toString());
        bundle.putString("userRegion",userRegion_et.getText().toString());
        userGender = ((RadioButton) view.findViewById(userGender_rg.getCheckedRadioButtonId())).getText().toString();
        userProperty = ((RadioButton) view.findViewById(userProperty_rg.getCheckedRadioButtonId())).getText().toString();
        bundle.putString("userGender",userGender);
        bundle.putString("userProperty",userProperty);
        return bundle;
    }



    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据
            switch (msg.what){
                case 1:

                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        viptime_tv.setText(msg.obj.toString());
                        if (msg.obj.toString().equals("会员已到期")){
                            zProperty_rb.setEnabled(false);
                            bProperty_rb.setEnabled(false);
                            dProperty_rb.setEnabled(false);
                            userRegion_et.setEnabled(false);
                        }

                    break;
            }
        }
    };
    //TODO okhttp获取用户信息
    public void getVipinfo(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(getActivity().getApplicationContext());
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("id", myid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
