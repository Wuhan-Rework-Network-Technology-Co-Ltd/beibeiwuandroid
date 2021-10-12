package xin.banghua.beiyuan.Main4Branch;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Adapter.LuntanSliderAdapter;
import xin.banghua.beiyuan.MarqueeTextView;
import xin.banghua.beiyuan.MarqueeTextViewClickListener;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.CityAdapter;
import xin.banghua.beiyuan.Signin.ProvinceAdapter;
import xin.banghua.beiyuan.bean.AddrBean;


/**
 * A simple {@link Fragment} subclass.
 */
public class LuntanFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    private static final String TAG = "LuntanFragment";
    JSONArray sliderJsonArray;
    //公告
    String[] strs;
    MarqueeTextView marqueeTv;
    //幻灯
    private SliderLayout mDemoSlider;
    //帖子列表
    private List<LuntanList> luntanLists = new ArrayList<>();
    private LuntanSliderAdapter adapter;
    //页码
    private Integer pageindex = 1;
    //二级菜单
    ToggleButton toggleButton1,toggleButton2,toggleButton3,toggleButton4,toggleButton5;

    private View mView;

    private String subtitle;

    Spinner spProvince, spCity;
    private AddrBean addrBean;
    private ProvinceAdapter adpProvince;
    private CityAdapter adpCity;
    private List<AddrBean.ProvinceBean.CityBean> cityBeanList;
    private AddrBean.ProvinceBean provinceBean;

    String filter_search = "";

    String filter_property = "不限";

    String filter_gender = "不限";

    String filter_region = "不限";

    EditText editText,userRegion_et;

    int checkedItemFilter = 0;
    int checkedItem = 0;


    FloatingActionButton search_btn;
    public LuntanFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_luntan, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //首页初始化
        getDataGonggao(getString(R.string.luntan_url));
        getDataSlider(getString(R.string.luntan_url),"首页");

        initNavigateButton(view);
        initSubnavigationButton(view);
        initTieziRelease(view);


        search_btn = view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_region = "不限";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.drawable.ic_search);
                builder.setTitle("搜索相关");
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(getActivity(), R.layout.dialog_foot_luntan_search, null);
                dialog.setView(dialogView);
                dialog.show();
                editText =  dialogView.findViewById(R.id.editText);
                userRegion_et = dialogView.findViewById(R.id.userRegion);
                spCity = dialogView.findViewById(R.id.spinner_city);
                spProvince = dialogView.findViewById(R.id.spinner_province);
                loadAddressData();
                Spinner spinner_gender = dialogView.findViewById(R.id.spinner_gender);
                ArrayAdapter<CharSequence> adapter_gender = ArrayAdapter.createFromResource(getActivity(),R.array.sousuo_gender,android.R.layout.simple_spinner_item);
                adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_gender.setAdapter(adapter_gender);
                spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_property = parent.getItemAtPosition(position).toString();
                        filter_gender = selected_property;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        filter_gender = "不限";
                    }
                });
                Spinner spinner_property = dialogView.findViewById(R.id.spinner_property);
                ArrayAdapter<CharSequence> adapter_property = ArrayAdapter.createFromResource(getActivity(),R.array.sousuo_property,android.R.layout.simple_spinner_item);
                adapter_property.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_property.setAdapter(adapter_property);
                spinner_property.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_property = parent.getItemAtPosition(position).toString();
                        filter_property = selected_property;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        filter_property = "不限";
                    }
                });
                editText.setHint("填写内容关键词~");

                if (!filter_search.equals("")) {
                    editText.setText(filter_search);
                }
                TextView dialog_hint = dialogView.findViewById(R.id.dialog_hint);
                dialog_hint.setVisibility(View.GONE);
                Button dismissdialog_btn = dialogView.findViewById(R.id.cancel_btn);
                dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button confirm_btn = dialogView.findViewById(R.id.confirm_btn);
                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filter_search = editText.getText().toString();
                        filter_region = userRegion_et.getText().toString();
                        pageindex = 1;
                        getDataPostlist(getString(R.string.luntan_url),subtitle,pageindex+"");
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void loadAddressData() {
        adpProvince = new ProvinceAdapter(getActivity());
        adpCity = new CityAdapter(getActivity());
        try {
            InputStream inputStream = getActivity().getAssets().open("addr_china_selected.json");

            addrBean = new GsonBuilder().create().fromJson(new InputStreamReader(inputStream), AddrBean.class);

            spProvince.setAdapter(adpProvince);
            adpProvince.setProvinceBeanList(addrBean.getProvinceList());

            spCity.setAdapter(adpCity);
            adpCity.setCityBeanList(addrBean.getProvinceList().get(0).getCitylist());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spCity.setVisibility(View.VISIBLE);
                cityBeanList = addrBean.getProvinceList().get(position).getCitylist();
                provinceBean = addrBean.getProvinceList().get(position);
                adpCity.setCityBeanList(addrBean.getProvinceList().get(position).getCitylist());
                //选择省份后，只传递省份
                String selected_province = provinceBean.getProvince();
                userRegion_et.setText(selected_province);
                spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //选择城市后，只传递城市
                        String selected_province = provinceBean.getProvince();
                        String selected_city = cityBeanList.get(position).getCityName();
                        if (selected_city.equals("不限")){
                            userRegion_et.setText(selected_province);
                        }else {
                            userRegion_et.setText(selected_city);
                        }

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
    //三个按钮初始化
    private void initNavigateButton(View view){
        Button guangchang = view.findViewById(R.id.guangchang_btn);
        //Button guanzhu = view.findViewById(R.id.guanzhu_btn);
        //Button luntan = view.findViewById(R.id.luntan_btn);

        guangchang.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.luntan_guangchang_action));
        //guanzhu.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.guangchang_guanzhu_action));
        //luntan.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.guangchang_luntan_action));

    }

    //二级菜单
    private void initSubnavigationButton(View view){
        toggleButton1 = view.findViewById(R.id.toggleButton1);
        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex = 1;
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                toggleButton4.setChecked(false);
                toggleButton5.setChecked(false);
                getDataGonggao(getString(R.string.luntan_url));
                getDataSlider(getString(R.string.luntan_url),"首页");
            }
        });
        toggleButton2 = view.findViewById(R.id.toggleButton2);
        toggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex = 1;
                toggleButton1.setChecked(false);
                toggleButton3.setChecked(false);
                toggleButton4.setChecked(false);
                toggleButton5.setChecked(false);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDataSlider(getString(R.string.luntan_url),"自拍");
            }
        });
        toggleButton3 = view.findViewById(R.id.toggleButton3);
        toggleButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex = 1;
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton4.setChecked(false);
                toggleButton5.setChecked(false);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDataSlider(getString(R.string.luntan_url),"真实");
            }
        });
        toggleButton4 = view.findViewById(R.id.toggleButton4);
        toggleButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex = 1;
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                toggleButton5.setChecked(false);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDataSlider(getString(R.string.luntan_url),"情感");
            }
        });
        toggleButton5 = view.findViewById(R.id.toggleButton5);
        toggleButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex = 1;
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                toggleButton4.setChecked(false);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getVipinfo("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat");
            }
        });
    }
    //发布帖子
    private void initTieziRelease(View view) {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.luntanRelease);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.luntan_fabutiezi_action);
            }
        });
    }
    private void initGonggao(View view,JSONArray jsonArray) throws JSONException {
        if (jsonArray.length()>0){
            strs = new String[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                strs[i]=jsonObject.getString("noticeinfo");
            }
        }
        marqueeTv =  view.findViewById(R.id.marquee);
        marqueeTv.setTextArraysAndClickListener(strs, new MarqueeTextViewClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,AnotherActivity.class));
            }
        });
    }
    //TODO 幻灯片相关
    private void initSlider(View view,JSONArray jsonArray) throws JSONException {
//        mDemoSlider = view.findViewById(R.id.luntan_slider);
//        mDemoSlider.removeAllSliders();
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
//
//            mDemoSlider.addSlider(textSliderView);
//        }
//
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

    @SuppressLint("ClickableViewAccessibility")
    private void initPostList(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initPostList: start");
        if (pageindex>1){
            //不是第一页，不用清零，直接更新
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String[] postPicture = jsonObject.getString("postpicture").split(",");
                    LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                            jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                            jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                            jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                            jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                            jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"));
                    luntanLists.add(posts);
                }
                adapter.swapData(luntanLists);
            }

        }else {
            //不同板块，需要先清零
            luntanLists.clear();
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String[] postPicture = jsonObject.getString("postpicture").split(",");
                    LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                            jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                            jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                            jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                            jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                            jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"));
                    luntanLists.add(posts);
                }
            }

            final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.luntan_RecyclerView);
            adapter = new LuntanSliderAdapter(luntanLists,sliderJsonArray,view.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLinearLayout();
            recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onLoadMore: start");

                    recyclerView.postDelayed(()->recyclerView.setPullLoadMoreCompleted(),1000);
                }

                @Override
                public void onLoadMore() {
//                    if (pageindex == 1&& ConstantValue.myId==null){
//                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity())
//                                .setTitle("登录以查看更多内容！")
//                                .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Intent intent = new Intent(getActivity(), SigninActivity.class);
//                                        getActivity().startActivity(intent);
//                                    }
//                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                        builder.create().show();
//                    }else {
//                        pageindex = pageindex+1;
//                        getDataPostlist(getString(R.string.luntan_url),subtitle,pageindex+"");
//                    }
                    pageindex = pageindex+1;
                    getDataPostlist(getString(R.string.luntan_url),subtitle,pageindex+"");
                    Log.d(TAG, "论坛页码："+pageindex);
                    recyclerView.postDelayed(()->recyclerView.setPullLoadMoreCompleted(),1000);
                }

            });
        }
    }



    //网络数据部分
    //处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是公告，2是幻灯片，3是帖子
            switch (msg.what){
                case 1:
                    try {
                        Log.d(TAG, "handleMessage: 公告接收的值"+msg.obj.toString());
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initGonggao(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        Log.d(TAG, "handleMessage: 幻灯片接收的值"+msg.obj.toString());
                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        sliderJsonArray = jsonArray;
                        Log.d(TAG, "handleMessage: subtitle"+subtitle);
                        getDataPostlist(getString(R.string.luntan_url),subtitle,"1");
                        //initSlider(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {

                        Log.d(TAG, "handleMessage: 帖子接收的值"+msg.obj.toString());

                        JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                        initPostList(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    if (msg.obj.toString().equals("会员已到期")){
                        Toast.makeText(getActivity(), "此版块需要开通会员", Toast.LENGTH_LONG).show();
                    }else {
                        getDataSlider(getString(R.string.luntan_url),"精华");
                    }
                    break;
            }
        }
    };
    //TODO okhttp获取论坛信息  1.公告，2.幻灯片，3.帖子
    public void getDataGonggao(final String url){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getGonggao")
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
    public void getDataSlider(final String url, final String subNav){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getSlide")
                        .add("slidesort", subNav)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                subtitle = subNav;
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=2;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void getDataPostlist(final String url,final String subNav,final String pageindex){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper sh = new SharedHelper(getActivity());
                String myid = sh.readUserInfo().get("userID");

                Log.d(TAG, "run: 搜索帖子地址："+filter_region);

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("type", "getPostlist")
                        .add("myid", myid)
                        .add("platename", subNav)
                        .add("pageindex",pageindex)
                        .add("filter_gender", filter_gender)
                        .add("filter_search", filter_search)
                        .add("filter_property", filter_property)
                        .add("filter_region", filter_region)
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
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

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
                    message.what=4;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
