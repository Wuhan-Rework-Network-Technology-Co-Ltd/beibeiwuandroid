package xin.banghua.beiyuan.MainBranch;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.UserInfoSliderAdapter;
import xin.banghua.beiyuan.GlobalDialogSingle;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.util.GpsUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class FujinFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final String TAG = "TuijianFragment";
    private String selectedGender = "all";
    JSONArray sliderJsonArray;
    private View mView;
    private Integer pageindex = 1;
    private SliderLayout mDemoSlider;
    UserInfoSliderAdapter adapter;
    PullLoadMoreRecyclerView recyclerView;
    //button
    TableRow seewho_tablerow;
    Button seewho_btn, seeall_btn, seefemale_btn, seemale_btn;
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
    private ArrayList<String> mUserSVIP = new ArrayList<>();
    private ArrayList<String> mAllowLocation = new ArrayList<>();

    //vars用来存放快捷键后的值
    private ArrayList<String> mUserID1 = new ArrayList<>();
    private ArrayList<String> mUserPortrait1 = new ArrayList<>();
    private ArrayList<String> mUserNickName1 = new ArrayList<>();
    private ArrayList<String> mUserAge1 = new ArrayList<>();
    private ArrayList<String> mUserGender1 = new ArrayList<>();
    private ArrayList<String> mUserProperty1 = new ArrayList<>();
    private ArrayList<String> mUserLocation1 = new ArrayList<>();
    private ArrayList<String> mUserRegion1 = new ArrayList<>();
    private ArrayList<String> mUserVIP1 = new ArrayList<>();
    private ArrayList<String> mAllowLocation1 = new ArrayList<>();

    public FujinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_fujin, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!(GpsUtil.isOPen(getContext()))){
            openGPS();
        }
        //使用okhttp获取推荐的幻灯片
        getDataSlide(getString(R.string.fujin_url));

        initNavigateButton(view);

        recyclerView = view.findViewById(R.id.tuijian_RecyclerView);
        //快捷按钮
        seewho_tablerow = mView.findViewById(R.id.seewho_tablerow);
        seewho_btn = view.findViewById(R.id.seewho_btn);
        seewho_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seewho_tablerow.getVisibility() == View.VISIBLE) {
                    seewho_tablerow.setVisibility(View.GONE);
                } else {
                    seewho_tablerow.setVisibility(View.VISIBLE);
                }
            }
        });
        seeall_btn = view.findViewById(R.id.seeall_btn);
        seeall_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "all";
                pageindex = 1;
                //显示全部
                mUserID.clear();
                mUserPortrait.clear();
                mUserNickName.clear();
                mUserAge.clear();
                mUserGender.clear();
                mUserProperty.clear();
                mUserLocation.clear();
                mUserRegion.clear();
                mUserVIP.clear();
                mAllowLocation.clear();
                getDataUserinfo(getString(R.string.fujin_url), pageindex + "");
            }
        });
        seefemale_btn = view.findViewById(R.id.seefemale_btn);
        seefemale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "女";
                pageindex = 1;

                mUserID.clear();
                mUserPortrait.clear();
                mUserNickName.clear();
                mUserAge.clear();
                mUserGender.clear();
                mUserProperty.clear();
                mUserLocation.clear();
                mUserRegion.clear();
                mUserVIP.clear();
                mAllowLocation.clear();
                getDataUserinfo(getString(R.string.fujin_url), pageindex + "");
            }
        });
        seemale_btn = view.findViewById(R.id.seemale_btn);
        seemale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "男";
                pageindex = 1;

                mUserID.clear();
                mUserPortrait.clear();
                mUserNickName.clear();
                mUserAge.clear();
                mUserGender.clear();
                mUserProperty.clear();
                mUserLocation.clear();
                mUserRegion.clear();
                mUserVIP.clear();
                mAllowLocation.clear();
                getDataUserinfo(getString(R.string.fujin_url), pageindex + "");
            }
        });

        locationCheck();
    }

    //三个按钮初始化
    private void initNavigateButton(View view) {
        Button tuijian = view.findViewById(R.id.tuijian_btn);
        //Button fujin = view.findViewById(R.id.fujin_btn);
        Button sousuo = view.findViewById(R.id.sousuo_btn);


        tuijian.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.fujin_tuijian_action));
        sousuo.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.fujin_sousuo_action));

    }

    //TODO 幻灯片相关
    private void initSlider(View view, JSONArray jsonArray) throws JSONException {
//        mDemoSlider = view.findViewById(R.id.tuijian_slider);
//
//        HashMap<String,String> url_maps = new HashMap<String, String>();
//        if (jsonArray.length()>0){
//            for (int i=0;i<jsonArray.length();i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                url_maps.put(jsonObject.getString("slidename"), jsonObject.getString("slidepicture"));
//            }
//        }
//
//        for(String name : url_maps.keySet()){
//            TextSliderView textSliderView = new TextSliderView(getActivity());
//            // initialize a SliderLayout
//            textSliderView
//                    .description(name)
//                    .image(url_maps.get(name))
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra",name);
//
//            mDemoSlider.addSlider(textSliderView);
//        }
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setDuration(4000);
//        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Log.d(TAG, "onSliderClick: clicked");

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageScrolled: scrolled");

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected: selected");

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged: changed");

    }


    //TODO 用户列表
    private void initUserInfo(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initUserInfo: preparing userinfo");

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mUserID.add(jsonObject.getString("id"));
                mUserPortrait.add(jsonObject.getString("portrait"));
                mUserNickName.add(jsonObject.getString("nickname"));
                mUserAge.add(jsonObject.getString("age"));
                mUserGender.add(jsonObject.getString("gender"));
                mUserProperty.add(jsonObject.getString("property"));
                mUserLocation.add(jsonObject.getString("location"));
                mUserRegion.add(jsonObject.getString("region"));
                mUserVIP.add(jsonObject.getString("vip"));
                mUserSVIP.add(jsonObject.getString("svip"));
                mAllowLocation.add(jsonObject.getString("allowlocation"));
            }
        }

        if (pageindex > 1) {
            //第二页以上，只加载刷新，不新建recyclerView
            adapter.swapData(mUserID, mUserPortrait, mUserNickName, mUserAge, mUserGender, mUserProperty, mUserLocation, mUserRegion, mUserVIP,mUserSVIP, mAllowLocation);
        } else {
            //初次加载
            initRecyclerView(view, mUserID, mUserPortrait, mUserNickName, mUserAge, mUserGender, mUserProperty, mUserLocation, mUserRegion, mUserVIP,mUserSVIP, mAllowLocation);
        }
    }

    private void initRecyclerView(View view, ArrayList<String> mUserID, ArrayList<String> mUserPortrait, ArrayList<String> mUserNickName, ArrayList<String> mUserAge, ArrayList<String> mUserGender, ArrayList<String> mUserProperty, ArrayList<String> mUserLocation, ArrayList<String> mUserRegion, ArrayList<String> mUserVIP,ArrayList<String> mUserSVIP, ArrayList<String> mAllowLocation) {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        adapter = new UserInfoSliderAdapter(view.getContext(), sliderJsonArray, mUserID, mUserPortrait, mUserNickName, mUserAge, mUserGender, mUserProperty, mUserLocation, mUserRegion, mUserVIP, mUserSVIP,mAllowLocation);
        recyclerView.setAdapter(adapter);
        recyclerView.setLinearLayout();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

                Log.d(TAG, "onRefresh: start");
                recyclerView.postDelayed(()->recyclerView.setPullLoadMoreCompleted(),1000);
            }

            @Override
            public void onLoadMore() {
//                if (pageindex == 1&& ConstantValue.myId==null){
//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity())
//                            .setTitle("登录以查看更多内容！")
//                            .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent = new Intent(getActivity(), SigninActivity.class);
//                                    getActivity().startActivity(intent);
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            });
//                    builder.create().show();
//                }else {
//                    pageindex = pageindex + 1;
//                    getDataUserinfo(getString(R.string.fujin_url), pageindex + "");
//                }
                pageindex = pageindex + 1;
                getDataUserinfo(getString(R.string.fujin_url), pageindex + "");
                Log.d(TAG, "附近页码：" + pageindex);
                recyclerView.postDelayed(()->recyclerView.setPullLoadMoreCompleted(),1000);
            }
        });
    }


    //TODO okhttp获取用户信息
    public void getDataUserinfo(final String url, final String pageindex) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedHelper sh = new SharedHelper(getActivity());
                Map<String, String> locationInfo = sh.readLocation();
                String myid = sh.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getUserInfo")
                        .add("myid", myid)
                        .add("latitude", locationInfo.get("latitude"))
                        .add("longitude", locationInfo.get("longitude"))
                        .add("pageindex", pageindex)
                        .add("selectedGender", selectedGender)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    message.what = 1;
                    Log.d(TAG, "run: Userinfo发送的值" + message.obj.toString());
                    handler.sendMessageDelayed(message, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //TODO okhttp获取幻灯片
    public void getDataSlide(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getSlide")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Message message = handler.obtainMessage();
                    message.obj = response.body().string();
                    message.what = 2;
                    Log.d(TAG, "run: Slide发送的值" + message.obj.toString());
                    handler.sendMessageDelayed(message, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what) {
                case 1:
                    try {
                        String resultJson1 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 用户数据接收的值" + msg.obj.toString());

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initUserInfo(mView, jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        String resultJson2 = msg.obj.toString();
                        Log.d(TAG, "handleMessage: 幻灯片接收的值" + msg.obj.toString());
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        sliderJsonArray = jsonArray;
                        getDataUserinfo(getString(R.string.fujin_url), "1");
                        //initSlider(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23421) {
            //TODO something;
            Log.d("开启权限", "定位权限返回");
        }else if(requestCode == 887){
            locationCheck();
        }
    }

    public void locationCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) || !checkPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(getActivity(), "当前无权限，请授权", Toast.LENGTH_SHORT);
                new GlobalDialogSingle(getActivity(), "", "当前未获取定位权限权限", "去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        getActivity().startActivityForResult(intent, 23421);
                        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
            }
        }
    }

    private boolean checkPermissionGranted(String permission) {
        return getActivity().checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }


    //gps
    private void openGPS() {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("提示")
                .setMessage("检测到您还没有开启GPS定位")
                .setNegativeButton("取消", null)
                .setPositiveButton("开启定位", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 887);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}
