package io.agora.chatroom.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.R;

public class ReportDialog {
    private static final String TAG = "ReportDialog";


    /**
     * 0是个人作品举报
     */
    public static final String REPORT_TYPE_0 = "0";
    /**
     * 1是聊天举报
     */
    public static final String REPORT_TYPE_1 = "1";
    /**
     * 2是匹配举报
     */
    public static final String REPORT_TYPE_2 = "2";
    /**
     * 3是房间举报
     */
    public static final String REPORT_TYPE_3 = "3";

    /**
     * 4是评论1举报
     */
    public static final String REPORT_TYPE_4 = "4";

    /**
     * 5是评论2举报
     */
    public static final String REPORT_TYPE_5 = "5";


    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    List<String> list2 = new ArrayList<String>();

    public ReportDialog(Context mContext) {
        this.mContext = mContext;
        initSheetDialog();
    }



    private float slideOffset = 0;
    public void show(String type,String id){
        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());

        list2.add("恶意辱骂");
        list2.add("血腥暴力");
        list2.add("造谣传谣");
        list2.add("侮辱国家");
        list2.add("淫秽色情");
        list2.add("其他");

        tagcontainerLayout.setTags(list2);
        tagcontainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                informreason.setText(text+":");
                informreason.setSelection(informreason.getText().length());
            }

            @Override
            public void onTagLongClick(int position, String text) {
                informreason.setText(text+":");
                informreason.setSelection(informreason.getText().length());
            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        inform_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (informreason.getText().toString().trim().equals("")){
                    Toast.makeText(mContext, "请输入举报理由", Toast.LENGTH_LONG).show();
                }else {
                    String reason = informreason.getText().toString().trim();

                    OkHttpInstance.reportViolation(type,id,reason);
                    Toast.makeText(mContext, "举报成功", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                }
            }
        });

        slideOffset = 0;
        bottomSheetDialog.show();
    }


    View view;

    TagContainerLayout tagcontainerLayout;
    EditText informreason;
    Button inform_btn;
    ImageView iv_dialog_close;
    private void initSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }

        //mView
        view = View.inflate(mContext, R.layout.dialog_bottomsheet_report, null);


        tagcontainerLayout = view.findViewById(R.id.tagcontainerLayout);
        informreason = view.findViewById(R.id.informreason_text);
        inform_btn = view.findViewById(R.id.imformsubmit_btn);
        iv_dialog_close = view.findViewById(R.id.dialog_bottomsheet_iv_close);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
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
                ReportDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}

