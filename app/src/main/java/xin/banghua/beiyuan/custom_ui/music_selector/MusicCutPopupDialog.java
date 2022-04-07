package xin.banghua.beiyuan.custom_ui.music_selector;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import xin.banghua.beiyuan.R;


public class MusicCutPopupDialog {

    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;


    View view;
    private float slideOffset = 0;
    private void initSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }

        //view   需要设置中固定高度android:layout_height="844dp"
        view = View.inflate(mContext, R.layout.music_cut_dialog, null);

        ButterKnife.bind(this, view);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (timer!=null)
                timer.cancel();

            MusicPlayer.reset();
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
                MusicCutPopupDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });

    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }



    public void showAtLocation(){
        //自定义部分

        try {
            slideOffset = 0;
            bottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss(){
        bottomSheetDialog.dismiss();
    }



    private Button mButtonCancel; //取消按钮
    private Button mButtonSure;   //确定按钮
    private DoubleSlideSeekBar mDoubleSlideSeekBar; //自定义的双向滑杆进度条
    private TextView mTextViewName;    //音乐名称
    private TextView mTextViewDeltaRule;//差值
    private TextView mTextViewMusicTest;//试听按钮

    private Chronometer chronometer;//计时器
    private TextView chronometer_tv;//计时器文本


    private float minRule=0;
    private float maxRule=0;

    public float getMinRule() {
        return minRule;
    }
    public void setMinRule(float minRule) {
        this.minRule = minRule;
    }
    public float getMaxRule() {
        return maxRule;
    }
    public void setMaxRule(float maxRule) {
        this.maxRule = maxRule;
    }



    Timer timer;
    String seconds;
    public void startChronometer(Long cutMusicStartProgress){
        Log.d(TAG, "startChronometer: 音乐开始时间"+ SystemClock.elapsedRealtime()+"|"+cutMusicStartProgress);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                chronometer.setBase(SystemClock.elapsedRealtime()-cutMusicStartProgress);
//                chronometer.start();
//            }
//        });
        if (timer!=null)
            timer.cancel();

        seconds = String.valueOf(cutMusicStartProgress/1000);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        chronometer_tv.setText(seconds);
                        seconds = String.valueOf(Integer.parseInt(seconds)+1);
                    }
                });
            }
        }, 0, 1000);
    }

    public MusicCutPopupDialog(Context context, View.OnClickListener itemsOnClick) {
        this.mContext = context;
        initSheetDialog();

        mButtonCancel = (Button) view.findViewById(R.id.popupWindow_music_cut_btn_cancel);
        mButtonSure = (Button) view.findViewById(R.id.popupWindow_music_cut_btn_sure);
        mDoubleSlideSeekBar=(DoubleSlideSeekBar) view.findViewById(R.id.popupWindow_music_cut_doubleSlideSeekBar);
        mTextViewDeltaRule= (TextView) view.findViewById(R.id.popupWindow_music_cut_tv_delta_rule);
        mTextViewName=(TextView) view.findViewById(R.id.popupWindow_music_cut_tv_musicName);
        mTextViewMusicTest=(TextView) view.findViewById(R.id.popupWindow_music_cut_tv_musicTest);

        chronometer=(Chronometer) view.findViewById(R.id.chronometer);
        chronometer_tv=(TextView) view.findViewById(R.id.time_tv);
/*        //取消按钮
        mButtonCancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });*/
        //滑动控件
        mDoubleSlideSeekBar.setOnRangeListener(new DoubleSlideSeekBar.onRangeListener() {
            @Override
            public void onRange(float low, float big) {
                minRule=low;
                maxRule=big;
                float delta=big-low;
                mTextViewDeltaRule.setVisibility(View.VISIBLE);
                mTextViewDeltaRule.setText("已截取" + String.format("%.0f" , delta)+"秒");
            }
        });
        //设置按钮监听
        mButtonCancel.setOnClickListener(itemsOnClick);
        mButtonSure.setOnClickListener(itemsOnClick);
        mTextViewMusicTest.setOnClickListener(itemsOnClick);
    }
    /**
     * 设置音乐信息
     * @param name
     * @param duration
     */
    public void setMusicInfo(String name,long duration){
        mTextViewName.setText(name);
        int big=Integer.parseInt(String.valueOf(duration/1000));
        mDoubleSlideSeekBar.setBigValue(big);
        mDoubleSlideSeekBar.setBigRange(big);
        maxRule=big;
    }



    /**
     *
     * @param cmt  Chronometer控件
     * @return 小时+分钟+秒数  的所有秒数
     */
    public  static String getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();
        if(string.length()==7){

            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours =hour*3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[2]);
            totalss = Hours+Mins+SS;
            return String.valueOf(totalss);
        }

        else if(string.length()==5){

            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);

            totalss =Mins+SS;
            return String.valueOf(totalss);
        }
        return String.valueOf(totalss);


    }
}
