package xin.banghua.beiyuan.MainBranch;


import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import androidx.navigation.Navigation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.UserInfoAdapter;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.ParseJSON.ParseJSONObject;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SousuoresultFragment extends Fragment {

    private static final String TAG = "SousuoresultFragment";

    private View mView;
    private Integer pageindex = 1;
    UserInfoAdapter adapter;
    private SliderLayout mDemoSlider;
    //vars
    private ArrayList<String> mUserID = new ArrayList<>();
    private ArrayList<String> mUserPortrait = new ArrayList<>();
    private ArrayList<String> mUserNickName = new ArrayList<>();
    private ArrayList<String> mUserAge = new ArrayList<>();
    private ArrayList<String> mUserGender = new ArrayList<>();
    private ArrayList<String> mUserProperty = new ArrayList<>();
    private ArrayList<String> mUserLocation = new ArrayList<>();
    private ArrayList<String> mUserRegion = new ArrayList<>();
    private ArrayList<String> mUserVIP = new ArrayList<>();
    private ArrayList<String> mAllowLocation = new ArrayList<>();

    public SousuoresultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_sousuoresult, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: 获取的参数"+getArguments().getString("type")+";"+getArguments().getString("userProperty")+";"+getArguments().getString("userGender")+";"+getArguments().getString("userRegion")+";"+getArguments().getString("userAge"));

        //初始化导航按钮
        initNavigateButton(view);

        if (getArguments().getString("type")=="direct") {
            //获取用户信息
            getDataUserinfo(getString(R.string.directsousuo_url),getArguments(),"1");
        }else if(getArguments().getString("type")=="condition"){
            getDataUserinfo(getString(R.string.conditionsousuo_url),getArguments(),"1");
        }
    }

    //三个按钮初始化
    private void initNavigateButton(View view){
        Button tuijian = view.findViewById(R.id.tuijian_btn);
        Button fujin = view.findViewById(R.id.fujin_btn);
        Button sousuo = view.findViewById(R.id.sousuo_btn);

        tuijian.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.result_tuijian_action));
        fujin.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.result_fujin_action));
        sousuo.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.result_sousuo_action));

        Log.d(TAG, "initNavigateButton: 进入SousuoresultFragment");
    }


    //TODO 用户列表
    private void initUserInfo(View view, JSONArray getJsonArray,JSONObject getJsonObject) throws JSONException {
        Log.d(TAG, "initUserInfo: preparing userinfo");

        if (getJsonArray!=null){
            for (int i=0;i<getJsonArray.length();i++){
                JSONObject jsonObject = getJsonArray.getJSONObject(i);
                mUserID.add(jsonObject.getString("id"));
                mUserPortrait.add(jsonObject.getString("portrait"));
                mUserNickName.add(jsonObject.getString("nickname"));
                mUserAge.add(jsonObject.getString("age"));
                mUserGender.add(jsonObject.getString("gender"));
                mUserProperty.add(jsonObject.getString("property"));
                mUserLocation.add(jsonObject.getString("location"));
                mUserRegion.add(jsonObject.getString("region"));
                mUserVIP.add(jsonObject.getString("vip"));
                mAllowLocation.add(jsonObject.getString("allowlocation"));
            }
        }else {
            mUserID.add(getJsonObject.getString("id"));
            mUserPortrait.add(getJsonObject.getString("portrait"));
            mUserNickName.add(getJsonObject.getString("nickname"));
            mUserAge.add(getJsonObject.getString("age"));
            mUserGender.add(getJsonObject.getString("gender"));
            mUserProperty.add(getJsonObject.getString("property"));
            mUserLocation.add(getJsonObject.getString("location"));
            mUserRegion.add(getJsonObject.getString("region"));
            mUserVIP.add(getJsonObject.getString("vip"));
            mAllowLocation.add(getJsonObject.getString("allowlocation"));
        }

        if (pageindex>1){
            //第二页以上，只加载刷新，不新建recyclerView
            adapter.swapData(mUserID,mUserPortrait,mUserNickName,mUserAge,mUserGender,mUserProperty,mUserLocation,mUserRegion,mUserVIP,mAllowLocation);
        }else {
            //初次加载
            initRecyclerView(view);
        }

    }

    private void initRecyclerView(View view){
        Log.d(TAG, "initRecyclerView: init recyclerview");

        final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.tuijian_RecyclerView);
        adapter = new UserInfoAdapter(view.getContext(),mUserID,mUserPortrait,mUserNickName,mUserAge,mUserGender,mUserProperty,mUserLocation,mUserRegion,mUserVIP,mAllowLocation);
        recyclerView.setAdapter(adapter);
        recyclerView.setLinearLayout();;
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "onRefresh: start");
                recyclerView.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {
                pageindex = pageindex+1;
                if (getArguments().getString("type")=="direct") {
                    getDataUserinfo(getString(R.string.directsousuo_url),getArguments(),pageindex+"");
                }else {
                    getDataUserinfo(getString(R.string.conditionsousuo_url),getArguments(),pageindex+"");
                }
                Log.d(TAG, "搜索页码："+pageindex);
                recyclerView.setPullLoadMoreCompleted();
            }
        });
    }


    //TODO okhttp获取用户信息
    public void getDataUserinfo(final String url,Bundle bundle,final String pageindex){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper sh = new SharedHelper(getActivity());
                Map<String,String> locationInfo = sh.readLocation();

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = null;
                if (getArguments().getString("type")=="direct") {
                    Log.d(TAG, "run: 进入direct post");
                    formBody = new FormBody.Builder()
                            .add("type", "getUserInfo")
                            .add("nameorphone",getArguments().getString("nameOrPhone"))
                            .add("latitude",locationInfo.get("latitude"))
                            .add("longitude",locationInfo.get("longitude"))
                            .add("pageindex",pageindex)
                            .build();
                }else if(getArguments().getString("type")=="condition"){
                    Log.d(TAG, "run: 进入condition post");
                    formBody = new FormBody.Builder()
                            .add("type", "getUserInfo")
                            .add("userAge",getArguments().getString("userAge"))
                            .add("userRegion",getArguments().getString("userRegion"))
                            .add("userGender",getArguments().getString("userGender"))
                            .add("userProperty",getArguments().getString("userProperty"))
                            .add("latitude",locationInfo.get("latitude"))
                            .add("longitude",locationInfo.get("longitude"))
                            .add("pageindex",pageindex)
                            .build();
                }


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

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据
            switch (msg.what){
                case 1:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值"+msg.obj.toString());

                        Object typeObject = new JSONTokener(resultJson1).nextValue();
                        JSONArray jsonArray = null;
                        JSONObject jsonObject = null;
                        if (typeObject instanceof org.json.JSONArray) {
                            Log.d(TAG, "handleMessage: JSONArray");
                            jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        } else if (typeObject instanceof org.json.JSONObject) {
                            Log.d(TAG, "handleMessage: JSONObject");
                            jsonObject = new ParseJSONObject(msg.obj.toString()).getParseJSON();
                        }
                        if (jsonArray==null&&jsonObject==null){
                            Toast.makeText(mView.getContext(),"没有搜索结果",Toast.LENGTH_LONG);
                        }else{
                            initUserInfo(mView,jsonArray,jsonObject);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    
}
