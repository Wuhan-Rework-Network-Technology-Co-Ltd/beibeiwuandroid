package xin.banghua.beiyuan.Main5Branch;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPersonalInfoFragment extends Fragment {
    private static final String TAG = "ResetPersonalInfoFragme";

    //button
    Button reset_portrait_btn;
    Button reset_nickname_btn;
    Button reset_age_btn;
    Button reset_gender_btn;
    Button reset_property_btn;
    Button reset_region_btn;
    Button reset_signature_btn;
    Button rp_verify_btn;
    TextView rp_verify_tv;

    public ResetPersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Common.userInfoList.getRp_verify_time().equals("0")){
            rp_verify_tv.setText("已认证");
            rp_verify_btn.setClickable(false);
        }
    }

    private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();


        CheckPermission.verifyPermissionCameraAndStorage(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_reset_personal_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("个人信息修改");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        reset_portrait_btn = view.findViewById(R.id.reset_portrait_btn);
        reset_portrait_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","头像设置");
                startActivity(intent);
            }
        });
        reset_nickname_btn = view.findViewById(R.id.reset_nickname_btn);
        reset_nickname_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","昵称设置");
                startActivity(intent);
            }
        });
        reset_age_btn = view.findViewById(R.id.reset_age_btn);
        reset_age_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","年龄设置");
                startActivity(intent);
            }
        });
        reset_gender_btn = view.findViewById(R.id.reset_gender_btn);
        reset_gender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","性别设置");
                startActivity(intent);
            }
        });
        reset_property_btn = view.findViewById(R.id.reset_property_btn);
        reset_property_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","属性设置");
                startActivity(intent);
            }
        });
        reset_region_btn = view.findViewById(R.id.reset_region_btn);
        reset_region_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","地区设置");
                startActivity(intent);
            }
        });
        reset_signature_btn = view.findViewById(R.id.reset_signature_btn);
        reset_signature_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ResetActivity.class);
                intent.putExtra("title","签名设置");
                startActivity(intent);
            }
        });

        rp_verify_btn = view.findViewById(R.id.rp_verify_btn);

        rp_verify_tv  = view.findViewById(R.id.rp_verify_tv);
        Log.d(TAG, "onViewCreated: 认证"+Common.userInfoList.getRp_verify_time());
        if (Common.userInfoList.getRp_verify_time().equals("0")){
            Log.d(TAG, "onViewCreated: 1未认证");
            rp_verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,RPVerifyActivity.class);
                    startActivity(intent);
                }
            });
        }else {

            rp_verify_tv.setText("已认证");
            rp_verify_btn.setClickable(false);
        }


        //ImageView back_btn = view.findViewById(R.id.iv_back_left);
        //back_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.reset_me_action));
    }

    @Override  //菜单的点击，其中返回键的id是android.R.id.home
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
