package io.agora.chatroom;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.model.Constant;
import xin.banghua.pullloadmorerecyclerview.NiceImageView;

public class UserInfoDialog {
    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    public UserInfoDialog(Context mContext) {
        this.mContext = mContext;
        initSheetDialog();
    }
    View view;
    private float slideOffset = 0;
    private void initSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }

        //view   需要设置中固定高度android:layout_height="844dp"
        view = View.inflate(mContext, R.layout.user_info_dialog, null);

        ButterKnife.bind(this, view);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog_dim_false);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            //消失监听
        });
        BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        //dialog滑动监听
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    Log.d(TAG, "onStateChanged: slideOffset"+slideOffset);
                    if (slideOffset <= -0.12 && slideOffset != -1) {
                        //当向下滑动时 值为负数
                        bottomSheetDialog.dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //show的时候会先调用一次onSlide，导致slideOffset为-1，所以判断是否下滑隐藏时，加个判断slideOffset != -1
                UserInfoDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


//        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }


    @BindView(R2.id.portrait_niv)
    NiceImageView portrait_niv;
    @BindView(R2.id.gift_btn)
    Button gift_btn;
    @BindView(R2.id.nickname_tv)
    TextView nickname_tv;
    @BindView(R2.id.tv_age)
    TextView tv_age;
    @BindView(R2.id.tv_property)
    TextView tv_property;
    @BindView(R2.id.tv_sign)
    TextView tv_sign;
    @BindView(R2.id.tv_favoritenum)
    TextView tv_favoritenum;
    @BindView(R2.id.tv_fansnum)
    TextView tv_fansnum;
    @BindView(R2.id.follow_btn)
    Button follow_btn;
    @BindView(R2.id.at_btn)
    Button at_btn;
    @BindView(R2.id.person_btn)
    Button person_btn;

    public void initShow(String userId){
        OkHttpInstance.getUserAttributes(userId, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                UserInfoList userInfoList = JSON.parseObject(responseString,UserInfoList.class);

                //自定义部分
                Glide.with(mContext).load(Common.getOssResourceUrl(userInfoList.getPortrait())).into(portrait_niv);

//                gift_btn.setOnClickListener(v->{
//                    bottomSheetDialog.dismiss();
//                    GiftDialog giftDialog = new GiftDialog(mContext);
//                    giftDialog.show(userInfoList.getId());
//                });


                nickname_tv.setText(userInfoList.getNickname());

                if (userInfoList.getGender().equals("男")){
//                    tv_age.setText("♂ "+Common.getAge(Common.parseStringToDate(userInfoList.getAge())));
                    tv_age.setText("♂ "+userInfoList.getAge());
                    tv_age.setBackgroundResource(R.drawable.shape_age_male);
                }else {
//                    tv_age.setText("♀ "+Common.getAge(Common.parseStringToDate(userInfoList.getAge())));
                    tv_age.setText("♀ "+userInfoList.getAge());
                    tv_age.setBackgroundResource(R.drawable.shape_age_female);
                }

                tv_property.setText(userInfoList.getProperty());

                if (TextUtils.isEmpty(userInfoList.getSignature())){
                    tv_sign.setText("ta很懒，什么也没留下");
                }else {
                    tv_sign.setText(userInfoList.getSignature());
                }

//                tv_favoritenum.setText(userInfoList.getFavoritenum());
//                tv_fansnum.setText(userInfoList.getFansnum());

//                if (Common.myFollow.contains(userInfoList.getId())){
//                    follow_btn.setText("已关注");
//                    follow_btn.setClickable(false);
//                }else {
//                    follow_btn.setText("关注");
//                    follow_btn.setOnClickListener(v->{
//                        bottomSheetDialog.dismiss();
//                        OkHttpInstance.addFavorite(mContext,userInfoList.getId());
//                        follow_btn.setText("已关注");
//                        follow_btn.setClickable(false);
//                    });
//                }

//                at_btn.setOnClickListener(v->{
//                    bottomSheetDialog.dismiss();
//                    String at_user = "@"+userInfoList.getNickname()+" ";
//                    ChatRoomActivity.et_input.setText("@"+userInfoList.getNickname()+" ");
//                    ChatRoomActivity.et_input.requestFocus();
//                    ChatRoomActivity.et_input.setSelection(at_user.length());
//                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(ChatRoomActivity.et_input, SHOW_FORCED);
//                });

                person_btn.setOnClickListener(v->{
                    bottomSheetDialog.dismiss();
                    Constant.goToPersonalPage.putExtra("userID",userId);
                    mContext.startActivity(Constant.goToPersonalPage);
                });
            }
        });


        slideOffset = 0;
        bottomSheetDialog.show();
    }
}
