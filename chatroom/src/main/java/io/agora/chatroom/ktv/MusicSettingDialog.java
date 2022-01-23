package io.agora.chatroom.ktv;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;

public class MusicSettingDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "MusicSettingDialog";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    public MusicSettingDialog(Context mContext) {
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
        view = View.inflate(mContext, R.layout.ktv_dialog_music_setting, null);

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
                MusicSettingDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


//        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    private static final String TAG_EAR = "ear";
    private static final String TAG_MIC_VOL = "mic_vol";
    private static final String TAG_MUSIC_VOL = "music_vol";

    private boolean isEar;
    private int volMic;
    private int volMusic;

    @BindView(R2.id.switchEar)
    Switch switchEar;
    @BindView(R2.id.sbVol1)
    SeekBar sbVol1;
    @BindView(R2.id.sbVol2)
    SeekBar sbVol2;
    public void initShow(Boolean isEar,int volMic,int volMusic, Callback callback){
        //自定义部分
        switchEar.setChecked(isEar);
        sbVol1.setProgress(volMic);
        sbVol2.setProgress(volMusic);

        switchEar.setOnCheckedChangeListener(this);
        sbVol1.setOnSeekBarChangeListener(this);
        sbVol2.setOnSeekBarChangeListener(this);

        mCallback = callback;

        slideOffset = 0;
        bottomSheetDialog.show();
    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isEar = isChecked;
        this.mCallback.onEarChanged(isEar);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbVol1) {
            volMic = progress;
            this.mCallback.onMicVolChanged(progress);
        } else if (seekBar == sbVol2) {
            volMusic = progress;
            this.mCallback.onMusicVolChanged(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private Callback mCallback;

    public interface Callback {
        void onEarChanged(boolean isEar);

        void onMicVolChanged(int vol);

        void onMusicVolChanged(int vol);
    }
}
