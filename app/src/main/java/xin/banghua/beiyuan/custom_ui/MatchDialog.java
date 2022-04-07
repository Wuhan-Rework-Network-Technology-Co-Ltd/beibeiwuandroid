package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class MatchDialog {
    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;

    @BindView(R.id.userGender)
    RadioGroup userGender;
    @BindView(R.id.userProperty)
    RadioGroup userProperty;
    @BindView(R.id.submit_btn)
    Button submit_btn;

    public MatchDialog(Context mContext) {
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
        view = View.inflate(mContext, R.layout.match_dialog, null);

        ButterKnife.bind(this, view);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
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
                MatchDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });

    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public void initShow(OkHttpResponseCallBack okHttpResponseCallBack){
        if (Common.userInfoList.getMatch_gender().equals("不限")) {
            userGender.check(R.id.unlimited_gender);
        } else if (Common.userInfoList.getMatch_gender().equals("男")) {
            userGender.check(R.id.male);
        } else if (Common.userInfoList.getMatch_gender().equals("女")) {
            userGender.check(R.id.female);
        }


        if (Common.userInfoList.getMatch_property().equals("双")) {
            userProperty.check(R.id.dProperty);
        } else if (Common.userInfoList.getMatch_gender().equals("Z")) {
            userProperty.check(R.id.zProperty);
        } else if (Common.userInfoList.getMatch_gender().equals("B")) {
            userProperty.check(R.id.bProperty);
        }

        //自定义部分
        userGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.unlimited_gender) {
                    OkHttpInstance.resetMatch("不限");
                } else if (checkedId == R.id.male) {
                    OkHttpInstance.resetMatch("男");
                } else if (checkedId == R.id.female) {
                    OkHttpInstance.resetMatch("女");
                }
            }
        });
        userProperty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dProperty) {
                    OkHttpInstance.resetMatch("双");
                } else if (checkedId == R.id.zProperty) {
                    OkHttpInstance.resetMatch("Z");
                } else if (checkedId == R.id.bProperty) {
                    OkHttpInstance.resetMatch("B");
                }
            }
        });
        submit_btn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            okHttpResponseCallBack.getResponseString("");
        });

        slideOffset = 0;
        bottomSheetDialog.show();
    }
}
