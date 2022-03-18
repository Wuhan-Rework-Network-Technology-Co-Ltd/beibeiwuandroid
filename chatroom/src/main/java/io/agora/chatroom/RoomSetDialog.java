package io.agora.chatroom;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.model.Constant;

public class RoomSetDialog {
    private static final String TAG = "MusicSettingDialog";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    public RoomSetDialog(Context mContext) {
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
        view = View.inflate(mContext, R.layout.room_set, null);

        ButterKnife.bind(this, view);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            //消失监听
            room_name = room_name_et.getText().toString();
            Toast.makeText(mContext, "正在保存并更新设置...", Toast.LENGTH_LONG).show();
            if (!TextUtils.isEmpty(room_name)||!TextUtils.isEmpty(room_cover)||!TextUtils.isEmpty(room_bg))
            OkHttpInstance.roomSet(room_name, room_cover, room_bg, new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    JSONObject jsonObject = null;//原生的
                    try {
                        jsonObject = new JSONObject(responseString);
                        Constant.sRoomName = jsonObject.getString("audioroomname");
                        Constant.sRoomCover = jsonObject.getString("audioroomcover");
                        Constant.sRoomBG = jsonObject.getString("audioroombackground");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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
                RoomSetDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


//        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static String room_name = "";
    public static String room_cover = "";
    public static String room_bg = "";

    @BindView(R2.id.close_tv)
    ImageView close_tv;
    @BindView(R2.id.room_cover_img)
    ImageView room_cover_img;
    @BindView(R2.id.room_bg_img)
    ImageView room_bg_img;
    @BindView(R2.id.room_name_et)
    EditText room_name_et;

    public final static int IMAGE_ROOM_COVER = 1012;  //图片选择返回码
    public final static int IMAGE_ROOM_BG = 1013;  //图片选择返回码

    public void initShow(AppCompatActivity activity){
        //自定义部分
        close_tv.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        room_name_et.setText(Constant.sRoomName);
        Glide.with(mContext).load(Common.getOssResourceUrl(Constant.sRoomCover)).into(room_cover_img);
        Glide.with(mContext).load(Common.getOssResourceUrl(Constant.sRoomBG)).into(room_bg_img);

        room_cover_img.setOnClickListener(v -> {
            ImageSelector.builder()
                    .useCamera(true) // 设置是否使用拍照
                    .setCrop(true)  // 设置是否使用图片剪切功能。会是正方形
                    .setSingle(true)  //设置是否单选
                    .setViewImage(true) //是否点击放大图片查看,，默认为true
                    .start(activity, IMAGE_ROOM_COVER); // 打开相册
        });
        room_bg_img.setOnClickListener(v -> {
            ImageSelector.builder()
                    .useCamera(true) // 设置是否使用拍照
                    .setCrop(false)  // 设置是否使用图片剪切功能。
                    .onlyImage(true) // 不剪切必须加
                    .setSingle(true)  //设置是否单选
                    .setViewImage(true) //是否点击放大图片查看,，默认为true
                    .start(activity, IMAGE_ROOM_BG); // 打开相册
        });

        slideOffset = 0;
        bottomSheetDialog.show();
    }

    public void setCover(String mPath){
        room_cover_img.setImageURI(Uri.fromFile(new File(mPath)));
    }

    public void setBG(String mPath){
        room_bg_img.setImageURI(Uri.fromFile(new File(mPath)));
    }
}
