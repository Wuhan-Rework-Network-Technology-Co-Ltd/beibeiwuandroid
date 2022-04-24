package xin.banghua.beiyuan.Main4Branch;


import static xin.banghua.onekeylogin.Constant.THEME_KEY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.GsonBuilder;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chatroom.activity.ChannelGridActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Adapter.LuntanSliderAdapter;
import xin.banghua.beiyuan.App;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.MarqueeTextView;
import xin.banghua.beiyuan.MarqueeTextViewClickListener;
import xin.banghua.beiyuan.ParseJSON.ParseJSONArray;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;
import xin.banghua.beiyuan.Signin.CityAdapter;
import xin.banghua.beiyuan.Signin.ProvinceAdapter;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.bean.AddrBean;
import xin.banghua.beiyuan.custom_ui.CustomVideoView;
import xin.banghua.beiyuan.publish.AllEffectActivity;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.ScreenUtils;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;


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
    TextView toggleButton11,toggleButton12,toggleButton13,toggleButton14,toggleButton15,toggleButton1,toggleButton2,toggleButton3,toggleButton4,toggleButton5;

    private View mView;

    private String subtitle = "首页";

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


    Button search_btn;
    public LuntanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        CustomVideoView.getInstance(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomVideoView.getInstance(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_guang_chang, container, false);
        return mView;
    }



    ImageView goToRoomTwo;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        marqueeTv =  view.findViewById(R.id.marquee);
        look_just_btn = view.findViewById(R.id.look_just_btn);

        goToRoomTwo = view.findViewById(R.id.goToRoomTwo);
        goToRoomTwo.setOnClickListener(v -> {
            if (Common.myID ==null){
                Intent intentSignin = new Intent(getContext(), SigninActivity.class);
                getContext().startActivity(intentSignin);
            }else {
                Intent intent = new Intent(getContext(), ChannelGridActivity.class);
                startActivity(intent);

//                Intent intent = new Intent(getActivity(), VideoChatViewActivity.class);
//                intent.putExtra("channel", "1");
//                intent.putExtra("targetId", Common.userInfoList.getId());
//                intent.putExtra("targetPortrait", Common.userInfoList.getPortrait());
//                intent.putExtra("myRole", "remote");
//                startActivity(intent);


//                Intent intent = new Intent(getActivity(), AllEffectActivity.class);
//                intent.putExtra("form_where","ZuopinTopicActivity");
//                startActivity(intent);
            }
        });
        //首页初始化
//        getDataGonggao(getString(R.string.luntan_url));
//        getDataSlider(getString(R.string.luntan_url),"首页");

        subtitle = "精华";
        getDataPostlist(getString(R.string.luntan_url_new),"精华","1");
        if (lookJust.get(subtitle)!=null){
            if (!lookJust.get(subtitle).equals("1")){
                look_just_btn.setVisibility(View.VISIBLE);
            }
        }


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
                        getDataPostlist(getString(R.string.luntan_url_new),subtitle,pageindex+"");
                        dialog.dismiss();
                    }
                });
            }
        });


        String menu = getActivity().getIntent().getStringExtra("menu");
        if (!TextUtils.isEmpty(menu)){
            if (menu.equals("关注")){
                subtitle = "关注";
                filter_region = "不限";
                pageindex = 1;
                toggleButton11.setTextSize(15);
                toggleButton12.setTextSize(15);
                toggleButton13.setTextSize(15);
                toggleButton14.setTextSize(15);
                toggleButton15.setTextSize(22);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
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
//        Button guangchang = view.findViewById(R.id.guangchang_btn);
//        //Button guanzhu = view.findViewById(R.id.guanzhu_btn);
//        //Button luntan = view.findViewById(R.id.luntan_btn);
//
//        guangchang.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.luntan_guangchang_action));
        //guanzhu.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.guangchang_guanzhu_action));
        //luntan.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.guangchang_luntan_action));

    }


    //刚刚看过
    Button look_just_btn;
    public static Map<String,String> lookJust = new HashMap<>();
    //二级菜单
    private void initSubnavigationButton(View view){

        look_just_btn.setOnClickListener(v -> {
            pageindex = 1;
            getDataPostlist(getString(R.string.luntan_url_new),subtitle,lookJust.get(subtitle));

            look_just_btn.setVisibility(View.GONE);
        });

        toggleButton11 = view.findViewById(R.id.toggleButton11);
        toggleButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "精华";
                filter_region = "不限";
                pageindex = 1;
                toggleButton11.setTextSize(22);
                toggleButton12.setTextSize(15);
                toggleButton13.setTextSize(15);
                toggleButton14.setTextSize(15);
                toggleButton15.setTextSize(15);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        toggleButton12 = view.findViewById(R.id.toggleButton12);
        toggleButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.myID==null){
                    Intent intent = new Intent(getActivity(), OneKeyLoginActivity.class);
                    intent.putExtra(THEME_KEY, 4);
                    getActivity().startActivity(intent);
                    return;
                }
                subtitle = "同城";
                if (Common.userInfoList!=null){
                    filter_region = Common.userInfoList.getRegion();
                }else {
                    filter_region = "北京-北京";
                }

                pageindex = 1;
                toggleButton11.setTextSize(15);
                toggleButton12.setTextSize(22);
                toggleButton13.setTextSize(15);
                toggleButton14.setTextSize(15);
                toggleButton15.setTextSize(15);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        toggleButton13 = view.findViewById(R.id.toggleButton13);
        toggleButton13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "首页";
                filter_region = "不限";
                pageindex = 1;
                toggleButton11.setTextSize(15);
                toggleButton12.setTextSize(15);
                toggleButton13.setTextSize(22);
                toggleButton14.setTextSize(15);
                toggleButton15.setTextSize(15);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        toggleButton14 = view.findViewById(R.id.toggleButton14);
        toggleButton14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "招募令";
                filter_region = "不限";
                pageindex = 1;
                toggleButton11.setTextSize(15);
                toggleButton12.setTextSize(15);
                toggleButton13.setTextSize(15);
                toggleButton14.setTextSize(22);
                toggleButton15.setTextSize(15);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        toggleButton15 = view.findViewById(R.id.toggleButton15);
        toggleButton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "关注";
                filter_region = "不限";
                pageindex = 1;
                toggleButton11.setTextSize(15);
                toggleButton12.setTextSize(15);
                toggleButton13.setTextSize(15);
                toggleButton14.setTextSize(15);
                toggleButton15.setTextSize(22);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");

                if (lookJust.get(subtitle)!=null){
                    if (!lookJust.get(subtitle).equals("1")){
                        look_just_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        toggleButton1 = view.findViewById(R.id.toggleButton1);
        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "首页";
                pageindex = 1;
                toggleButton1.setTextSize(22);
                toggleButton2.setTextSize(15);
                toggleButton3.setTextSize(15);
                toggleButton4.setTextSize(15);
                toggleButton5.setTextSize(15);
                //getDataGonggao(getString(R.string.luntan_url));
                //getDataSlider(getString(R.string.luntan_url),"首页");
                getDataPostlist(getString(R.string.luntan_url),"首页","1");
            }
        });
        toggleButton2 = view.findViewById(R.id.toggleButton2);
        toggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "自拍";
                pageindex = 1;
                toggleButton1.setTextSize(15);
                toggleButton2.setTextSize(22);
                toggleButton3.setTextSize(15);
                toggleButton4.setTextSize(15);
                toggleButton5.setTextSize(15);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //getDataSlider(getString(R.string.luntan_url),"自拍");
                getDataPostlist(getString(R.string.luntan_url),"自拍","1");
            }
        });
        toggleButton3 = view.findViewById(R.id.toggleButton3);
        toggleButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "真实";
                pageindex = 1;
                toggleButton1.setTextSize(15);
                toggleButton2.setTextSize(15);
                toggleButton3.setTextSize(22);
                toggleButton4.setTextSize(15);
                toggleButton5.setTextSize(15);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //getDataSlider(getString(R.string.luntan_url),"真实");
                getDataPostlist(getString(R.string.luntan_url),"真实","1");
            }
        });
        toggleButton4 = view.findViewById(R.id.toggleButton4);
        toggleButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "情感";
                pageindex = 1;
                toggleButton1.setTextSize(15);
                toggleButton2.setTextSize(15);
                toggleButton3.setTextSize(15);
                toggleButton4.setTextSize(22);
                toggleButton5.setTextSize(15);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //getDataSlider(getString(R.string.luntan_url),"情感");
                getDataPostlist(getString(R.string.luntan_url),"情感","1");
            }
        });
        toggleButton5 = view.findViewById(R.id.toggleButton5);
        toggleButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtitle = "精华";
                pageindex = 1;
                toggleButton1.setTextSize(15);
                toggleButton2.setTextSize(15);
                toggleButton3.setTextSize(15);
                toggleButton4.setTextSize(15);
                toggleButton5.setTextSize(22);
                try {
                    marqueeTv.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //getVipinfo("https://console.banghua.xin/app/index.php?i=999999&c=entry&a=webapp&do=viptimeinsousuo&m=socialchat");
                getDataPostlist(getString(R.string.luntan_url),"精华","1");
            }
        });
    }
    //发布帖子
    private void initTieziRelease(View view) {
        RelativeLayout floatingActionButton = view.findViewById(R.id.luntanRelease);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigation.findNavController(v).navigate(R.id.luntan_fabutiezi_action);
                if (Common.myID==null){
                    Intent intent = new Intent(getActivity(), OneKeyLoginActivity.class);
                    intent.putExtra(THEME_KEY, 4);
                    getActivity().startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), AllEffectActivity.class);
                    intent.putExtra("form_where","ZuopinTopicActivity");
                    startActivity(intent);
//                    Intent intent = new Intent(getActivity(), PublishPostActivity.class);
//                    getActivity().startActivity(intent);
                }

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

    //用户手动点击播放后，自动播放开始，
    // 除非用户手动点击停止，或者视频播放完毕，停止自动播放，
    private boolean isLooper = true;
    private int looperFlag = 1;//0,无自动播放，1.自动播放上一个，2自动播放下一个
    private int position_play = -1;//播放的位置
    int currentRecyclerPosition = 0;
    @SuppressLint("ClickableViewAccessibility")




    private void initPostList(View view, JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "initPostList: start");
        if (pageindex>1){
            //不是第一页，不用清零，直接更新
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (!Common.isBlackList(jsonObject.getString("myblacklist")) && !Common.isBlackListMe(jsonObject.getString("authid"))){
                        String[] postPicture = jsonObject.getString("postpicture").split(",");
                        LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                                jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                                jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                                jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                                jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                                jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"),jsonObject.getString("comment_sum"));
                        posts.setComment_forbid(jsonObject.getString("comment_forbid"));
                        posts.setPostvideo(jsonObject.getString("postvideo"));
                        posts.setWidth(jsonObject.getString("width"));
                        posts.setHeight(jsonObject.getString("height"));
                        posts.setCover(jsonObject.getString("cover"));
                        posts.setOnline(jsonObject.getString("online"));
                        posts.setPortraitframe(jsonObject.getString("portraitframe"));
                        posts.setMyfriends(jsonObject.getString("myfriends"));
                        posts.setMyblacklist(jsonObject.getString("myblacklist"));
                        posts.setPlay_once(jsonObject.getString("play_once"));
                        posts.setMore_five(jsonObject.getString("more_five"));
                        posts.setPlay_completed(jsonObject.getString("play_completed"));
                        posts.setPlay_time(jsonObject.getString("play_time"));
                        posts.setPostpicture(jsonObject.getString("postpicture"));
                        posts.setVitality(jsonObject.getString("vitality"));
                        posts.setPost(jsonObject.getString("post"));
                        posts.setComment(jsonObject.getString("comment"));
                        posts.setTopic(jsonObject.getString("topic"));
                        posts.setRp_verify_time(jsonObject.getString("rp_verify_time"));
                        posts.setAll_money(jsonObject.getString("all_money"));
                        posts.setAll_income(jsonObject.getString("all_income"));
                        luntanLists.add(posts);
                    }
                }
                adapter.swapData(luntanLists);
            }
        }else {
            //不同板块，需要先清零
            luntanLists.clear();
            isLooper = true;
            looperFlag = 1;//0,无自动播放，1.自动播放上一个，2自动播放下一个
            position_play = -1;//播放的位置
            if (jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (!Common.isBlackList(jsonObject.getString("myblacklist")) && !Common.isBlackListMe(jsonObject.getString("authid"))){
                        String[] postPicture = jsonObject.getString("postpicture").split(",");
                        LuntanList posts = new LuntanList(jsonObject.getString("age"),jsonObject.getString("gender"),jsonObject.getString("region"),
                                jsonObject.getString("property"),jsonObject.getString("id"),jsonObject.getString("plateid"),
                                jsonObject.getString("platename"),jsonObject.getString("authid"),jsonObject.getString("authnickname"),
                                jsonObject.getString("authportrait"),jsonObject.getString("posttip"),jsonObject.getString("posttitle"),
                                jsonObject.getString("posttext"),postPicture,jsonObject.getString("like"),jsonObject.getString("favorite"),
                                jsonObject.getString("time"),jsonObject.getString("vip"),jsonObject.getString("svip"),jsonObject.getString("comment_sum"));
                        posts.setComment_forbid(jsonObject.getString("comment_forbid"));
                        posts.setPostvideo(jsonObject.getString("postvideo"));
                        posts.setWidth(jsonObject.getString("width"));
                        posts.setHeight(jsonObject.getString("height"));
                        posts.setCover(jsonObject.getString("cover"));
                        posts.setOnline(jsonObject.getString("online"));
                        posts.setPortraitframe(jsonObject.getString("portraitframe"));
                        posts.setMyfriends(jsonObject.getString("myfriends"));
                        posts.setMyblacklist(jsonObject.getString("myblacklist"));
                        posts.setPlay_once(jsonObject.getString("play_once"));
                        posts.setMore_five(jsonObject.getString("more_five"));
                        posts.setPlay_completed(jsonObject.getString("play_completed"));
                        posts.setPlay_time(jsonObject.getString("play_time"));
                        posts.setPostpicture(jsonObject.getString("postpicture"));
                        posts.setVitality(jsonObject.getString("vitality"));
                        posts.setPost(jsonObject.getString("post"));
                        posts.setComment(jsonObject.getString("comment"));
                        posts.setTopic(jsonObject.getString("topic"));
                        posts.setRp_verify_time(jsonObject.getString("rp_verify_time"));
                        posts.setAll_money(jsonObject.getString("all_money"));
                        posts.setAll_income(jsonObject.getString("all_income"));
                        luntanLists.add(posts);
                    }
                }
            }

            final PullLoadMoreRecyclerView recyclerView = view.findViewById(R.id.luntan_RecyclerView);
            adapter = new LuntanSliderAdapter(luntanLists,view.getContext(),subtitle);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(App.getApplication(), DividerItemDecoration.VERTICAL));
            recyclerView.setLinearLayout();
            recyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollStateChanged: "+newState);
                    //滑动停止后，
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && isLooper && looperFlag != 0) {

                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        //刚刚看过
                        try {
                            lookJust.put(subtitle,luntanLists.get(layoutManager.findFirstVisibleItemPosition()).getId());
                            Log.d(TAG, "onScrollStateChanged: 停止滚蛋"+lookJust.get(subtitle));
                        }catch (Exception e){
                            Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
                        }




                        switch (looperFlag) {
                            case 1:
                                int position_lastVisible=layoutManager.findLastVisibleItemPosition();
                                if (position_lastVisible==position_play){
                                    //自动播放上一个
                                    position_play-=1;
                                }else {
                                    //最后一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放倒数第2个可见的item
                                    position_play=position_lastVisible-1;
                                }

                                break;
                            case 2:
                                int position_firstVisible=layoutManager.findFirstVisibleItemPosition();
                                if (position_firstVisible==position_play){
                                    //自动播放下一个

                                    position_play+=1;
                                }else {
                                    //第一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放第2个可见的item
                                    position_play=position_firstVisible+1;
                                }
                                break;
                        }



                        if (SharedHelper.getInstance(getActivity()).readAutoPlay()){
                            try {
                                if (!luntanLists.get(position_play-1).getPostvideo().equals("https://oss.banghua.xin/0") && (position_play-layoutManager.findFirstVisibleItemPosition())>=0){
                                    View view = recyclerView.getChildAt(position_play-layoutManager.findFirstVisibleItemPosition());
                                    if (null != recyclerView.getChildViewHolder(view)){
                                        LuntanSliderAdapter.ViewHolder viewHolder = (LuntanSliderAdapter.ViewHolder)recyclerView.getChildViewHolder(view);
                                        viewHolder.video_layout.addView(CustomVideoView.getInstance(mView.getContext(),luntanLists.get(position_play-1)),2);
                                    }
                                }
                            }catch (Exception e){
                                Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
                            }
                        }


                        //adapter.notifyItemChanged(position_play);

                        //注意
                        looperFlag=0;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (!isLooper) return;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    View view = layoutManager.findViewByPosition(position_play);
                    //说明播放的view还未完全消失
                    if (view != null) {

                        int y_t_rv = ScreenUtils.getViewScreenLocation(recyclerView)[1];//RV顶部Y坐标
                        int y_b_rv = y_t_rv + recyclerView.getHeight();//RV底部Y坐标

                        int y_t_view = ScreenUtils.getViewScreenLocation(view)[1];//播放的View顶部Y坐标
                        int height_view = view.getHeight();
                        int y_b_view = y_t_view + height_view;//播放的View底部Y坐标

                        //上滑
                        if (dy > 0) {
                            //播放的View上滑，消失了一半了,停止播放，
                            if ((y_t_rv > y_t_view) && ((y_t_rv - y_t_view) > height_view * 1f / 2)) {

                                CustomVideoView.getInstance(getActivity());
                                //adapter.notifyItemChanged(position_play);
                                looperFlag = 2;//自动播放下一个
                            }

                        } else if (dy < 0) {
                            //下滑

//                        LogUtils.log("y_t_rv", y_t_rv);
//                        LogUtils.log("y_b_rv", y_b_rv);
                            //播放的View下滑，消失了一半了,停止播放
                            if ((y_b_view > y_b_rv) && ((y_b_view - y_b_rv) > height_view * 1f / 2)) {

                                CustomVideoView.getInstance(getActivity());
                                //adapter.notifyItemChanged(position_play);
                                looperFlag = 1;//自动播放上一个
                            }
                        }
                    }
                }
            });
            recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                @Override
                public void onRefresh() {
                    Log.d(TAG, "onLoadMore: start");
                    pageindex = 1;
                    getDataPostlist(getString(R.string.luntan_url_new),subtitle,pageindex+"");
                    recyclerView.postDelayed(()->recyclerView.setPullLoadMoreCompleted(),1000);
                }

                @Override
                public void onLoadMore() {
//                    if (pageindex == 1&& Common.myID==null){
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
                    pageindex = pageindex + 1;
                    getDataPostlist(getString(R.string.luntan_url_new),subtitle,luntanLists.get(luntanLists.size()-1).getId());
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
                        getDataPostlist(getString(R.string.luntan_url_new),subtitle,"1");
                        //initSlider(mView,jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    String data = msg.obj.toString();
                    if (data.equals("false")){
//                        luntanLists.clear();
//                        adapter.swapData(luntanLists);
                        Toast.makeText(getActivity(),"没有更多内容了，先去其他地方看看吧！",Toast.LENGTH_LONG).show();
                    }else {
                        try {
                            Log.d(TAG, "handleMessage: 帖子接收的值"+subtitle+"|"+data);
                            JSONArray jsonArray = new ParseJSONArray(msg.obj.toString()).getParseJSON();
                            initPostList(mView,jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                OkHttpClient client = OkHttpInstance.getInstance();
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
                OkHttpClient client = OkHttpInstance.getInstance();
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

                Log.d(TAG, "run: 搜索帖子地址："+subNav);

                OkHttpClient client = OkHttpInstance.getInstance();
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
                    Log.d(TAG, "run: getDataPostlist发送的值"+message.obj.toString());
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

                OkHttpClient client = OkHttpInstance.getInstance();
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
