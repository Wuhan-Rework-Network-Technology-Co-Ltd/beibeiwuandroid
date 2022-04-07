package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import xin.banghua.beiyuan.R;


public class PortraitFrameView extends FrameLayout {
    private static final String TAG = "PortraitFrameView";
    public PortraitFrameView(@NonNull Context context) {
        super(context);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PortraitFrameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    ImageView portrait_frame_image_view;


    String defaultPortraitFrame;


    public Boolean isDefault = false;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.portrait_frame_view, this, true);


        //
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PortraitFrameView);
        defaultPortraitFrame = attributes.getString(R.styleable.PortraitFrameView_defaultPortraitFrame);

        if (!TextUtils.isEmpty(defaultPortraitFrame)){
            isDefault = true;
            setPortraitFrame(defaultPortraitFrame);
        }
    }
    //播放器变量
    int currentFrame = 0;
    SVGAImageView animationView;
    SVGAParser svgaParser;

    public void setPortraitFrame(String name){
        portrait_frame_image_view = mView.findViewById(R.id.portrait_frame_image_view);
        animationView = mView.findViewById(R.id.portrait_frame_svga_view);
        if (name == null){
            animationView.setVisibility(GONE);
            portrait_frame_image_view.setVisibility(GONE);
            return;
        }
        if (name.contains("svga")){
            //svga播放器播放
            SVGAParser.Companion.shareParser().init(mContext);
            portrait_frame_image_view.setVisibility(GONE);
            animationView.setVisibility(VISIBLE);
            animationView.setBackgroundResource(R.color.transparent);
//            animationView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View mView) {
//                    animationView.stepToFrame(currentFrame++, true);
//                }
//            });
            animationView.setCallback(new SVGACallback() {
                @Override
                public void onPause() {
                    Log.d(TAG, "onPause: 动画暂停");
                }

                @Override
                public void onFinished() {
                    Log.d(TAG, "onFinished: 动画结束");
                }

                @Override
                public void onRepeat() {
                    //Log.d(TAG, "onRepeat: 动画重复");
                }

                @Override
                public void onStep(int i, double v) {
                    //Log.d(TAG, "onStep: 动画步骤"+i+"|"+v);
                }
            });
            svgaParser = SVGAParser.Companion.shareParser();


            Log.d("SVGA", "## name " + name);
            svgaParser.setFrameSize(500, 500);
            if (isDefault){
                svgaParser.decodeFromAssets(name+".svga", new SVGAParser.ParseCompletion() {
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
            }else {
                try {
                    svgaParser.decodeFromInputStream(new FileInputStream("data/data/" + "xin.banghua.beiyuan" + "/files/svga/"+name+".svga"),name,new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                            animationView.setVideoItem(videoItem);
                            animationView.startAnimation();
                            Log.d(TAG, "onComplete: loadAnimation1");
                        }

                        @Override
                        public void onError() {

                        }
                    },true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
