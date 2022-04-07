package xin.banghua.beiyuan.custom_ui.music_selector;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.PopupWindow;
import android.widget.TextView;

import xin.banghua.beiyuan.R;


public class MusicCutPopupWindow extends PopupWindow {
	private static final String TAG = "MusicCutPopupWindow";
	private Button mButtonCancel; //取消按钮
	private Button mButtonSure;   //确定按钮
	private DoubleSlideSeekBar mDoubleSlideSeekBar; //自定义的双向滑杆进度条
	private TextView mTextViewName;    //音乐名称
	private TextView mTextViewDeltaRule;//差值
	private TextView mTextViewMusicTest;//试听按钮
	private View mMenuView;

	private Chronometer chronometer;//计时器

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

	public void startChronometer(Long cutMusicStartProgress){
		Log.d(TAG, "startChronometer: 音乐开始时间"+SystemClock.elapsedRealtime()+"|"+cutMusicStartProgress);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				chronometer.setBase(SystemClock.elapsedRealtime()-cutMusicStartProgress);
				chronometer.start();
			}
		});
	}

	public MusicCutPopupWindow(Context context,OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.music_cut_dialog, null);
		mButtonCancel = (Button) mMenuView.findViewById(R.id.popupWindow_music_cut_btn_cancel);
		mButtonSure = (Button) mMenuView.findViewById(R.id.popupWindow_music_cut_btn_sure);
		mDoubleSlideSeekBar=(DoubleSlideSeekBar) mMenuView.findViewById(R.id.popupWindow_music_cut_doubleSlideSeekBar);
		mTextViewDeltaRule= (TextView) mMenuView.findViewById(R.id.popupWindow_music_cut_tv_delta_rule);
		mTextViewName=(TextView) mMenuView.findViewById(R.id.popupWindow_music_cut_tv_musicName);
		mTextViewMusicTest=(TextView) mMenuView.findViewById(R.id.popupWindow_music_cut_tv_musicTest);

		chronometer=(Chronometer) mMenuView.findViewById(R.id.chronometer);
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
		//设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		//设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(false);
		//设置窗口外也能点击（点击外面时，窗口可以关闭）
		this.setOutsideTouchable(false);
		//设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.circleDialog);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x00000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
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
}
