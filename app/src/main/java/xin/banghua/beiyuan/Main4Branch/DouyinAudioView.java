package xin.banghua.beiyuan.Main4Branch;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.R;


public class DouyinAudioView extends FrameLayout implements  MediaPlayer.OnPreparedListener, View.OnClickListener, MediaPlayer.OnCompletionListener{
    private static final String TAG = "DouyinAudioView";

    public DouyinAudioView(@NonNull Context context) {
        super(context);
    }

    public DouyinAudioView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DouyinAudioView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DouyinAudioView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;
    FrameLayout douyin_audio_layout;
    ShapeableImageView douyin_audio_cd;
    ShapeableImageView douyin_audio_portrait;
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.douyin_audio_view, this, true);

        douyin_audio_layout = mView.findViewById(R.id.douyin_audio_layout);
        douyin_audio_cd = mView.findViewById(R.id.douyin_audio_cd);
        douyin_audio_portrait = mView.findViewById(R.id.douyin_audio_portrait);



        //svga播放器播放

        animationView = mView.findViewById(R.id.svga_view);

    }

    LuntanList luntanList;
    public void initShow(LuntanList luntanList){
        //自定义部分
        this.luntanList = luntanList;
    }

    public void playAudio(){
        //动画
        Animation animation= AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat);
        LinearInterpolator lin=new LinearInterpolator();
        animation.setInterpolator(lin);
        douyin_audio_cd.startAnimation(animation);

        //动画
        Animation animation_25= AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat_25);
        LinearInterpolator lin_25=new LinearInterpolator();
        animation_25.setInterpolator(lin_25);
        douyin_audio_portrait.startAnimation(animation_25);

        startAnim();


    }
    /**
     * 初始化静态变量
     * @param mContext
     */
    public static void initRelativeSingleVar(Context mContext){
        Log.d(TAG, "initRelativeSingleVar: 初始化静态变量");
        animation = AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        animation_25 = AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat_25);
        LinearInterpolator lin_25=new LinearInterpolator();
        animation_25.setInterpolator(lin_25);



        SVGAParser.Companion.shareParser().init(mContext);
        svgaParser = SVGAParser.Companion.shareParser();
        svgaParser.setFrameSize(500, 500);
    }

    private static Animation animation;
    private static Animation animation_25;
    public void playAudioFromPerson(){

        //动画
        if (animation==null){
            Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat);
            LinearInterpolator lin = new LinearInterpolator();
            animation.setInterpolator(lin);
            douyin_audio_cd.startAnimation(animation);
        }else {
            douyin_audio_cd.startAnimation(animation);
        }



        //动画
        if (animation_25==null){
            Animation animation_25 = AnimationUtils.loadAnimation(mContext,R.anim.rotate_repeat_25);
            LinearInterpolator lin_25=new LinearInterpolator();
            animation_25.setInterpolator(lin_25);
            douyin_audio_portrait.startAnimation(animation_25);
        }else {
            douyin_audio_portrait.startAnimation(animation_25);
        }


        startAnim();

    }



    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onClick(View v) {

    }


    //播放器变量
    int currentIndex = 0;
    SVGAImageView animationView;
    static SVGAParser svgaParser;
    public void startAnim(){
        svgaParser.decodeFromAssets("note_animation.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                animationView.setVideoItem(videoItem);
                animationView.startAnimation();
                Log.d(TAG, "onComplete: loadAnimation1");
            }

            @Override
            public void onError() {

            }
        });
    }
}
