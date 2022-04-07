package xin.banghua.beiyuan.publish;


import static com.zhaoss.weixinrecorded.activity.RecordedActivity.INTENT_PATH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.faceunity.core.camera.FUCamera;
import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.enumeration.CameraFacingEnum;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.enumeration.FUTransformMatrixEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.VideoRecordHelper;
import com.faceunity.core.renderer.CameraRenderer;
import com.faceunity.core.utils.CameraUtils;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.nama.post.DemoConfig;
import com.faceunity.nama.post.base.BaseActivity;
import com.faceunity.nama.post.entity.FunctionConfigModel;
import com.faceunity.nama.post.utils.FileUtilsFU;
import com.faceunity.nama.post.utils.SystemUtil;
import com.faceunity.nama.post.view.SelectDataActivity;
import com.faceunity.nama.utils.VideoViewWithCover;
import com.faceunity.ui.button.RecordBtn;
import com.faceunity.ui.dialog.ToastHelper;
import com.faceunity.ui.widget.CameraFocus;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.zhaoss.weixinrecorded.util.MyVideoEditor;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import xin.banghua.beiyuan.Adapter.ScriptList;
import xin.banghua.beiyuan.custom_ui.music_selector.MusicSelectorDialog;
import xin.banghua.beiyuan.custom_ui.music_selector.SingletonMusicPlayer;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.faceunity.R;

/**
 * DESC：
 * Created on 2021/3/1
 */
public abstract class ScriptSingleFaceUnityActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BaseFaceUnityActivity";
    //region Activity生命周期绑定

    /**
     * 由于不知道什么原因，导致视频通话后，第一次进入美颜短视频拍摄页面，摄像头捕获显示黑屏
     * 所以如果刚从视频通话离开，则美颜短视频页面打开两次，暂时解决这个问题
     */
    public static Boolean isLeaveVideoChat = false;

    @Override
    public void onResume() {
        super.onResume();
        mCameraRenderer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRecording) {
            isRecording = false;
            onStopRecord();
        }
        mCameraRenderer.onPause();
    }


    @Override
    public void onDestroy() {
        mCameraRenderer.onDestroy();
        super.onDestroy();
    }

    //endregion 生命周期绑定


    //region Activity OnCreate
    protected ViewStub mStubBottom;
    protected View mStubView;
    protected GLSurfaceView mSurfaceView;
    protected TextView mTrackingView;
    protected CameraFocus mCameraFocusView;
    protected RecordBtn mTakePicView;
    protected TextView mDebugView;
    protected ImageButton mMoreView;
    protected TextView mEffectDescription;
    protected RelativeLayout mCustomView;
    protected FrameLayout mRootView;
    protected RadioGroup mRenderTypeView;
    protected ImageView mBackView;

    protected ImageView album_img;

    protected ImageView remove_music_img;

    protected VideoViewWithCover video_view_with_cover;


    public static TextView music_tv;
    private MusicSelectorDialog musicSelectorDialog;

    /* 更多弹框*/
    private PopupWindow mPopupWindow;

    protected Handler mMainHandler;
    /* 对焦光标消失*/
    private final Runnable cameraFocusDismiss = () -> {
        mCameraFocusView.layout(0, 0, 0, 0);
        findViewById(R.id.lyt_photograph_light).setVisibility(View.INVISIBLE);
    };

    private FunctionConfigModel mFunctionConfigModel;
    private boolean isSpecialDevice = false;


    @Override
    public int getLayoutResID() {
        return R.layout.activity_script_single;
    }

    @Override
    public void initData() {
        mMainHandler = new Handler();
        mVideoRecordHelper = new VideoRecordHelper(this, mOnVideoRecordingListener);
        mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
        mFunctionConfigModel = FunctionConfigModel.functionSwitchMap.get(getFunctionType());
    }

    Activity mContext;

    private MyVideoEditor myVideoEditor = new MyVideoEditor();
    ScriptList scriptList;


    private int executeCount = 1;//总编译次数
    private float executeProgress;//编译进度
    private TextView editorTextView;
    @Override
    public void initView() {

        mContext = this;

        SingletonMusicPlayer.getInstance().music = null;


        myVideoEditor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {

                float stepPro = 100f/executeCount;
                int temp = (int) (percent/100f*stepPro);
                if ((int)(executeProgress+temp) > 100){
                    editorTextView.setText("视频编辑中"+(int)(executeProgress+temp-100)+"%");
                }else {
                    editorTextView.setText("视频编辑中"+(int)(executeProgress+temp)+"%");
                }
                if(percent==100){
                    executeProgress += stepPro;
                }
            }
        });

        musicSelectorDialog = new MusicSelectorDialog(ScriptSingleFaceUnityActivity.this);
        music_tv = findViewById(R.id.music_tv);
        music_tv.setOnClickListener(v -> {
            musicSelectorDialog.initShow(new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (SingletonMusicPlayer.getInstance().music!=null){
                        remove_music_img.setVisibility(View.VISIBLE);
                    }else {
                        remove_music_img.setVisibility(View.GONE);
                    }
                }
            });
        });
        remove_music_img = findViewById(R.id.remove_music_img);
        remove_music_img.setOnClickListener(v -> {
            remove_music_img.setVisibility(View.GONE);
            SingletonMusicPlayer.getInstance().music = null;
            SingletonMusicPlayer.getInstance().reset();
            music_tv.setText("选择音乐");
        });
        album_img = findViewById(R.id.album_img);
        album_img.setOnClickListener(v -> {
            Intent intent = new Intent(ScriptSingleFaceUnityActivity.this,PublishPostActivity.class);
            intent.putExtra("isAlbum",true);
            startActivity(intent);
        });



        scriptList = (ScriptList)getIntent().getSerializableExtra("scriptList");
        video_view_with_cover = findViewById(R.id.video_view_with_cover);
        //video_view.setUrl("https://oss.banghua.xin/audios/99999/2022/03/ILn9G4L8pApmmY8Bdy1yp8P4pMf8a8.mp4"); //设置视频地址
        Log.d(TAG, "initView: 合音地址"+scriptList.getScript_video());
        video_view_with_cover.video_view.setUrl(scriptList.getScript_video());

        Glide.with(mContext).load(scriptList.getScript_cover()).into(video_view_with_cover.video_cover);


        isSpecialDevice = SystemUtil.isSpeDevice();
        mStubBottom = findViewById(R.id.stub_bottom);
        mStubBottom.setInflatedId(R.id.stub_bottom);

        mRootView = findViewById(R.id.fyt_root);
        mBackView = findViewById(R.id.iv_back);
        mCustomView = findViewById(R.id.cyt_custom_view);
        mSurfaceView = findViewById(R.id.gl_surface);

        mTrackingView = findViewById(R.id.tv_tracking);
        mCameraFocusView = findViewById(R.id.focus);
        mTakePicView = findViewById(R.id.btn_take_pic);
        mTakePicView.setMax(Long.parseLong(scriptList.getScript_during()));

        mEffectDescription = findViewById(R.id.tv_effect_description);
        mDebugView = findViewById(R.id.tv_debug);
        mDebugView.setText(String.format(getString(R.string.fu_base_debug), 0, 0, 0, 0));
        mMoreView = findViewById(R.id.btn_more);
        if (mFunctionConfigModel.isOpenResolutionChange) {
            mMoreView.setBackgroundResource(R.mipmap.icon_live_more);
        } else if (mFunctionConfigModel.isOpenPhotoVideo) {
            mMoreView.setBackgroundResource(R.mipmap.icon_live_photo);
        } else {
            mMoreView.setVisibility(View.INVISIBLE);
        }
        mRenderTypeView = findViewById(R.id.radio_render_input);
        mRenderTypeView.setVisibility(View.INVISIBLE);

    }

    private AlertDialog progressDialog;
    public TextView showProgressDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = View.inflate(this, com.zhaoss.weixinrecorded.R.layout.dialog_loading, null);
        builder.setView(view);
        ProgressBar pb_loading = (ProgressBar) view.findViewById(com.zhaoss.weixinrecorded.R.id.pb_loading);
        TextView tv_hint = (TextView) view.findViewById(com.zhaoss.weixinrecorded.R.id.tv_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pb_loading.setIndeterminateTintList(ContextCompat.getColorStateList(this, com.zhaoss.weixinrecorded.R.color.blue));
        }
        progressDialog = builder.create();
        progressDialog.show();

        return tv_hint;
    }

    @Override
    public void bindListener() {
        mCameraRenderer = new CameraRenderer(mSurfaceView, getCameraConfig(), mOnGlRendererListener);

        /* 亮度调整*/
        ((SeekBar) findViewById(R.id.seek_photograph_light)).setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        /* 切换前后摄像头*/
        findViewById(R.id.btn_camera_change).setOnClickListener(this);
        /* fps日志展示*/
        findViewById(R.id.btn_debug).setOnClickListener(this);
        /* 返回 */
        mBackView.setOnClickListener(this);
        /* 拍照*/
        mTakePicView.setOnRecordListener(mOnRecordListener);
        /* 更多 */
        mMoreView.setOnClickListener(this);
        /* 单双输入切换 */
        mRenderTypeView.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_render_dual) {
                cameraRenderType = 0;
            } else if (checkedId == R.id.rb_render_tex) {
                cameraRenderType = 1;
            }
        });
    }

    public void setmStubBottom(int layout_id){
        if (layout_id != 0) {
            mStubBottom.setLayoutResource(layout_id);
            mStubView = mStubBottom.inflate();
        }
    }

    /**
     * 底部功能栏id
     *
     * @return Int
     */
    protected abstract int getStubBottomLayoutResID();


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.btn_camera_change) {
            if (mCameraRenderer.getFUCamera() != null) {
                mCameraRenderer.getFUCamera().switchCamera();
            }
        } else if (id == R.id.btn_debug) {
            if (mDebugView.getVisibility() == View.VISIBLE) {
                isShowBenchmark = false;
                mDebugView.setVisibility(View.GONE);
            } else {
                isShowBenchmark = true;
                mDebugView.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btn_more) {
            if (mFunctionConfigModel.isOpenResolutionChange) {
                showMorePopupWindow();
            } else if (mFunctionConfigModel.isOpenPhotoVideo) {
                onSelectPhotoVideoClick();
            }
        }
    }


    //endregion Activity OnCreate

    //region CameraRenderer
    protected FURenderKit mFURenderKit = FURenderKit.getInstance();
    protected FUAIKit mFUAIKit = FUAIKit.getInstance();
    protected CameraRenderer mCameraRenderer;
    private int cameraRenderType = 0;

    /*Benchmark 开关*/
    private boolean isShowBenchmark = false;
    /*检测 开关*/
    protected boolean isAIProcessTrack = true;
    /*检测标识*/
    protected int aIProcessTrackStatus = 1;


    /**
     * 特效配置
     */
    protected void configureFURenderKit() {
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
    }

    /**
     * 配置相机参数
     *
     * @return CameraBuilder
     */
    protected FUCameraConfig getCameraConfig() {
        FUCameraConfig cameraConfig = new FUCameraConfig();
        return cameraConfig;
    }

    /**
     * 检测类型
     *
     * @return
     */
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        return FUAIProcessorEnum.FACE_PROCESSOR;
    }

    /**
     * 检测结果变更回调
     *
     * @param fuaiProcessorEnum
     * @param status
     */
    protected void onTrackStatusChanged(FUAIProcessorEnum fuaiProcessorEnum, int status) {
//        mTrackingView.setVisibility((status > 0) ? View.INVISIBLE : View.VISIBLE);
//        if (status <= 0) {
//            if (fuaiProcessorEnum == FUAIProcessorEnum.FACE_PROCESSOR) {
//                mTrackingView.setText(R.string.fu_base_is_tracking_text);
//            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
//                mTrackingView.setText(R.string.toast_not_detect_body);
//            }
//            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
//                mTrackingView.setText(R.string.toast_not_detect_gesture);
//            }
//        }
    }

    /**
     * 日志回调
     *
     * @param width
     * @param height
     * @param fps
     * @param renderTime
     */
    protected void onBenchmarkFPSChanged(int width, int height, double fps, double renderTime) {
        mDebugView.setText(String.format(getString(R.string.fu_base_debug), width, height, (int) fps, (int) renderTime));
    }


    /**
     * 获取SurfaceView视图窗口
     *
     * @param width
     * @param height
     */
    protected void onSurfaceChanged(int width, int height) {
    }

    protected void onSurfaceCreated() {
    }

    protected void onDrawFrameAfter() {

    }

    protected void onRenderBefore(FURenderInputData inputData) {

    }

    /* CameraRenderer 回调*/
    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {


        private int width;//数据宽
        private int height;//数据高
        private long mFuCallStartTime = 0; //渲染前时间锚点（用于计算渲染市场）


        private int mCurrentFrameCnt = 0;
        private int mMaxFrameCnt = 10;
        private long mLastOneHundredFrameTimeStamp = 0;
        private long mOneHundredFrameFUTime = 0;


        @Override
        public void onSurfaceCreated() {
            configureFURenderKit();
            ScriptSingleFaceUnityActivity.this.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            runOnUiThread(() -> ScriptSingleFaceUnityActivity.this.onSurfaceChanged(width, height));
        }

        @Override
        public void onRenderBefore(FURenderInputData inputData) {
            if (isSpecialDevice) {
                //目前这个是Nexus 6P
                if (inputData.getRenderConfig().getCameraFacing() == CameraFacingEnum.CAMERA_FRONT) {
                    inputData.getRenderConfig().setInputTextureMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
                    inputData.getRenderConfig().setInputBufferMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
                }
            }
            width = inputData.getWidth();
            height = inputData.getHeight();
            mFuCallStartTime = System.nanoTime();
            if (cameraRenderType == 1) {
                inputData.setImageBuffer(null);
            }
            ScriptSingleFaceUnityActivity.this.onRenderBefore(inputData);
        }


        @Override
        public void onRenderAfter(@NonNull FURenderOutputData outputData, @NotNull FURenderFrameData frameData) {
            recordingData(outputData, frameData.getTexMatrix());
        }

        @Override
        public void onDrawFrameAfter() {
            trackStatus();
            benchmarkFPS();
            ScriptSingleFaceUnityActivity.this.onDrawFrameAfter();
        }


        @Override
        public void onSurfaceDestroy() {
            mFURenderKit.release();
        }

        /*AI识别数目检测*/
        private void trackStatus() {
            if (!isAIProcessTrack) {
                return;
            }
            FUAIProcessorEnum fuaiProcessorEnum = getFURenderKitTrackingType();
            int trackCount;
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                trackCount = mFUAIKit.handProcessorGetNumResults();
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                trackCount = mFUAIKit.humanProcessorGetNumResults();
            } else {
                trackCount = mFUAIKit.isTracking();
            }
            if (aIProcessTrackStatus != trackCount) {
                aIProcessTrackStatus = trackCount;
                runOnUiThread(() -> onTrackStatusChanged(fuaiProcessorEnum, trackCount));
            }
        }

        /*渲染FPS日志*/
        private void benchmarkFPS() {
            if (!isShowBenchmark) {
                return;
            }
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
            if (++mCurrentFrameCnt == mMaxFrameCnt) {
                mCurrentFrameCnt = 0;
                double fps = ((double) mMaxFrameCnt) * 1000000000L / (System.nanoTime() - mLastOneHundredFrameTimeStamp);
                double renderTime = ((double) mOneHundredFrameFUTime) / mMaxFrameCnt / 1000000L;
                mLastOneHundredFrameTimeStamp = System.nanoTime();
                mOneHundredFrameFUTime = 0;
                runOnUiThread(() -> onBenchmarkFPSChanged(width, height, fps, renderTime));
            }
        }

        /*录制保存*/
        private void recordingData(FURenderOutputData outputData, float[] texMatrix) {
            if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
                return;
            }
            if (isRecordingPrepared) {
                mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX);
            }
            if (isTakePhoto) {
                isTakePhoto = false;
                mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight());
            }
        }
    };


    //endregion CameraRenderer

    //region  其他业务

    /**
     * 当前功能对应类别 FunctionEnum值
     *
     * @return Boolean
     */
    protected abstract int getFunctionType();


    /**
     * 更多按钮-加载文件
     *
     * @return Boolean
     */
    private void onSelectPhotoVideoClick() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        SelectDataActivity.startActivity(this, getFunctionType());
    }


    /**
     * 调整拍照按钮对齐方式
     *
     * @param margin Int
     */
    protected void changeTakePicButtonMargin(int margin, int width) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTakePicView.getLayoutParams();
        params.bottomMargin = margin;
        mTakePicView.setDrawWidth(width);
        mTakePicView.setLayoutParams(params);
    }

    /**
     * 调整拍照按钮位置大小
     *
     * @param width    Int
     * @param showRate Float
     * @param margin   Int
     * @param diff     Int
     */
    protected void updateTakePicButton(int width, Float showRate, int margin, int diff, Boolean changeSize) {
        int currentWidth = changeSize ? (int) (width * (1 - showRate * 0.265)) : width;
        int currentMargin = margin + (int) (diff * showRate);
        changeTakePicButtonMargin(currentMargin, currentWidth);
    }


    /**
     * 显示提示描述
     *
     * @param strRes Int
     * @param time   Int
     */
    public void showDescription(int strRes, long time) {
        if (strRes == 0) {
            return;
        }
        runOnUiThread(() -> showToast(strRes));
    }


    /**
     * 显示提示描述
     */
    public void showToast(String msg) {
        ToastHelper.showNormalToast(this, msg);
    }

    /**
     * 显示提示描述
     */
    public void showToast(int res) {
        ToastHelper.showWhiteTextToast(this, res);
    }

    /**
     * 更多按钮-点击事件绑定
     */
    private void showMorePopupWindow() {
        if (mPopupWindow == null) {
            int width = getResources().getDimensionPixelSize(R.dimen.x682);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_common_popup_more, null);
            RadioGroup rgSolution = view.findViewById(R.id.rg_resolutions);
            RelativeLayout clSelectPhoto = view.findViewById(R.id.rly_select_photo);
            rgSolution.setOnCheckedChangeListener(mMorePopupWindowCheckedChangeListener);
            clSelectPhoto.setOnClickListener(v -> onSelectPhotoVideoClick());

            rgSolution.setVisibility(mFunctionConfigModel.isOpenResolutionChange ? View.VISIBLE : View.GONE);
            clSelectPhoto.setVisibility(mFunctionConfigModel.isOpenPhotoVideo ? View.VISIBLE : View.GONE);
            mPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.photo_more_popup_anim_style);
            showHideMoreWindowView(view);
        }
        int xOffset = getResources().getDimensionPixelSize(R.dimen.x386);
        int yOffset = getResources().getDimensionPixelSize(R.dimen.x12);
        mPopupWindow.showAsDropDown(mMoreView, -xOffset + mMoreView.getWidth() / 2, yOffset);
    }

    public void showHideMoreWindowView(View view) {
    }

    @SuppressLint("NonConstantResourceId")
    private RadioGroup.OnCheckedChangeListener mMorePopupWindowCheckedChangeListener = (group, checkedId) -> {
        FUCamera fuCamera = mCameraRenderer.getFUCamera();
        if (fuCamera == null) return;
        if (checkedId == R.id.rb_resolution_480p) {
            fuCamera.changeResolution(640, 480);
        } else if (checkedId == R.id.rb_resolution_720p) {
            fuCamera.changeResolution(1280, 720);
        } else if (checkedId == R.id.rb_resolution_1080p) {
            fuCamera.changeResolution(1920, 1080);
        }
    };


    /*亮度调节  */
    private final SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            FUCamera camera = mCameraRenderer.getFUCamera();
            if (camera != null) {
                camera.setExposureCompensation(((float) progress) / 100);
            }
            mMainHandler.removeCallbacks(cameraFocusDismiss);
            mMainHandler.postDelayed(cameraFocusDismiss, CameraUtils.FOCUS_TIME);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    /* 拍照按钮事件回调  */
    private final RecordBtn.OnRecordListener mOnRecordListener = new RecordBtn.OnRecordListener() {
        @Override
        public void stopRecord() {
            if (isRecording) {
                isRecording = false;
                ScriptSingleFaceUnityActivity.this.onStopRecord();

                video_view_with_cover.video_view.pause();

                if (SingletonMusicPlayer.getInstance().isPlaying())
                    SingletonMusicPlayer.getInstance().stop();
            }
        }

        @Override
        public void startRecord() {
            if (!isRecording) {
                isRecording = true;
                ScriptSingleFaceUnityActivity.this.onStartRecord();

                video_view_with_cover.video_view.setVolume(0.2f,0.2f);
                video_view_with_cover.video_view.start();

                if (SingletonMusicPlayer.getInstance().getDuration()>0){
                    Log.d(TAG, "startRecord: 播放音乐");
                    SingletonMusicPlayer.getInstance().start();
                }
            }
        }

        @Override
        public void takePic() {
            isTakePhoto = true;
        }
    };

    /**
     * 触碰事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        if (!mFunctionConfigModel.isShowAutoFocus) {
            return false;
        }
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
            FUCamera fuCamera = mCameraRenderer.getFUCamera();
            findViewById(R.id.lyt_photograph_light).setVisibility(View.VISIBLE);
            int progress = (int) ((fuCamera == null) ? 0f : fuCamera.getExposureCompensation() * 100);
            ((SeekBar) findViewById(R.id.seek_photograph_light)).setProgress(progress);
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int focusRectSize = getResources().getDimensionPixelSize(R.dimen.x150);
            DisplayMetrics screenInfo = getScreenInfo();
            int screenWidth = screenInfo.widthPixels;
            int marginTop = getResources().getDimensionPixelSize(R.dimen.x280);
            int padding = getResources().getDimensionPixelSize(R.dimen.x44);
            int progressBarHeight = getResources().getDimensionPixelSize(R.dimen.x460);
            if (rawX > screenWidth - focusRectSize && rawY > marginTop - padding && rawY < marginTop + progressBarHeight + padding
            ) {
                return false;
            }
            if (fuCamera != null) {
                fuCamera.handleFocus(mSurfaceView.getWidth(), mSurfaceView.getHeight(), rawX, rawY, focusRectSize);
            }
            mCameraFocusView.showCameraFocus(rawX, rawY);

            mMainHandler.removeCallbacks(cameraFocusDismiss);
            mMainHandler.postDelayed(cameraFocusDismiss, CameraUtils.FOCUS_TIME);
            return true;
        }
        return false;
    }


    /**
     * 获取屏幕信息
     *
     * @return
     */
    private DisplayMetrics getScreenInfo() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics;
    }

    //endregion

    //region 视频录制

    private VideoRecordHelper mVideoRecordHelper;
    private volatile boolean isRecordingPrepared = false;
    private boolean isRecording = false;
    private volatile long recordTime = 0;


    protected void onStartRecord() {
        mVideoRecordHelper.startRecording(mSurfaceView, mCameraRenderer.getFUCamera().getCameraHeight(), mCameraRenderer.getFUCamera().getCameraWidth());
    }

    protected void onStopRecord() {
        mTakePicView.setSecond(0);
        mVideoRecordHelper.stopRecording();
    }

    private OnVideoRecordingListener mOnVideoRecordingListener = new OnVideoRecordingListener() {

        @Override
        public void onPrepared() {
            isRecordingPrepared = true;
        }

        @Override
        public void onProcess(Long time) {
            recordTime = time;
            runOnUiThread(() -> {
                if (isRecording) {
                    mTakePicView.setSecond(time);
                }
            });

        }

        @Override
        public void onFinish(File file) {
            Log.d(TAG, "onFinish: 1视频录制结束"+file.getAbsolutePath());
            isRecordingPrepared = false;

            if (recordTime < 1100) {
                runOnUiThread(() -> ToastHelper.showNormalToast(ScriptSingleFaceUnityActivity.this, R.string.save_video_too_short));
            } else {
                String filePath = FileUtilsFU.addVideoToAlbum(ScriptSingleFaceUnityActivity.this, file);
                if (filePath == null || filePath.trim().length() == 0) {
                    runOnUiThread(() -> ToastHelper.showNormalToast(ScriptSingleFaceUnityActivity.this, R.string.save_video_failed));
                } else {
                    runOnUiThread(() -> ToastHelper.showNormalToast(ScriptSingleFaceUnityActivity.this, R.string.save_video_success));

                    //编辑
//                    intent.putExtra("filePath",filePath);
//                    intent.putExtra("form_where","EditVideo");
//                    startActivity(intent);

                    Log.d(TAG, "onBindViewHolder: 合并后的视频地址   调整视频尺寸");
                    //String newScaleVideo = myVideoEditor.executeScaleVideo(filePath,780,1280);
                    Log.d(TAG, "onBindViewHolder: 合并后的视频地址   调整视频尺寸完毕");


                    runOnUiThread(() -> editorTextView = showProgressDialog());
                    if (recordTime < Integer.parseInt(scriptList.getScript_during())){
                        Log.d(TAG, "onFinish: 合并后的视频地址   裁剪视频");
                        scriptList.setScript_video(myVideoEditor.executeCutVideo(scriptList.getScript_video(),0f,Float.parseFloat((recordTime/1000)+"")));
                        Log.d(TAG, "onFinish: 合并后的视频地址   裁剪视频完毕");
                    }

                    Log.d(TAG, "onFinish: 合并后的视频地址   合并视频");

                    String newFilePath = myVideoEditor.executeCoverVideo(filePath,scriptList.getScript_video());



//                    List<String> list = new ArrayList<>();
//                    list.add(filePath);
//                    list.add(scriptList.getScript_video());
//                    String newFilePath = myVideoEditor.executeConcatDiffentMp4((ArrayList<String>) list,false);
                    Log.d(TAG, "onFinish: 合并后的视频地址   合并视频完毕");

                    Log.d(TAG, "onFinish: 合并后的视频地址"+newFilePath);

                    progressDialog.dismiss();
                    //开启视频编辑
                    Intent intent = new Intent(ScriptSingleFaceUnityActivity.this, EditVideoActivity.class);
                    intent.putExtra(INTENT_PATH, newFilePath);
                    startActivityForResult(intent, 4492);


                }

            }

//            if (file.exists()) {
//                file.delete();
//            }
        }

    };

    //endregion 视频录制
    //region 拍照

    private PhotoRecordHelper mPhotoRecordHelper;
    private volatile Boolean isTakePhoto = false;

    /**
     * 获取拍摄的照片
     */
    private final OnPhotoRecordingListener mOnPhotoRecordingListener = this::onReadBitmap;


    protected void onReadBitmap(Bitmap bitmap) {
        new Thread(() -> {
            String path = FileUtilsFU.addBitmapToAlbum(this, bitmap);
            if (path == null) return;
            runOnUiThread(() -> ToastHelper.showNormalToast(ScriptSingleFaceUnityActivity.this, R.string.save_photo_success));


            //拍照跳转
            Intent intent = new Intent(ScriptSingleFaceUnityActivity.this,PublishPostActivity.class);
            intent.putExtra("picture",path);
            startActivity(intent);

//            intent.putExtra("filePath",path);
//            intent.putExtra("form_where","SelectPicture");
//            startActivity(intent);
        }).start();
    }



    public static Intent intent;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 4492) {
                Log.d(TAG, "onFinish: 美颜文件1"+data.getStringExtra(INTENT_PATH));
//                Intent intent = new Intent();
//                intent.putExtra(INTENT_PATH, data.getStringExtra(INTENT_PATH));
//                intent.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
//                setResult(RESULT_OK, intent);
//                finish();


                intent.putExtra("filePath",data.getStringExtra(INTENT_PATH));
                intent.putExtra("form_where","EditVideo");
                startActivity(intent);

            }
        }
    }

    //endregion 拍照

}
