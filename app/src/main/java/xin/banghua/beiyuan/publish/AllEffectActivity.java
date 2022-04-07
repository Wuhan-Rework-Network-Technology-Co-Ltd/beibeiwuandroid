package xin.banghua.beiyuan.publish;


import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.faceunity.nama.post.data.BodyBeautyDataFactory;
import com.faceunity.nama.post.data.FaceBeautyDataFactory;
import com.faceunity.nama.post.data.PropDataFactory;
import com.faceunity.nama.post.entity.FunctionEnum;
import com.faceunity.ui.control.BodyBeautyControlView;
import com.faceunity.ui.control.FaceBeautyControlView;
import com.faceunity.ui.control.PropControlView;
import com.faceunity.ui.entity.PropBean;

import xin.banghua.beiyuan.CheckPermission;
import xin.banghua.faceunity.R;

/**
 * DESC：美颜
 * Created on 2021/3/1
 */
public class AllEffectActivity extends BaseFaceUnityActivity {
    private static final String TAG = "AllEffectActivity";


    public static AllEffectActivity allEffectActivity;
    //美颜
    private FaceBeautyControlView mFaceBeautyControlView;
    private FaceBeautyDataFactory mFaceBeautyDataFactory;
    FaceBeautyDataFactory.FaceBeautyListener mFaceBeautyListener = new FaceBeautyDataFactory.FaceBeautyListener() {

        @Override
        public void onFilterSelected(int res) {
            showToast(res);
        }

        @Override
        public void onFaceBeautyEnable(boolean enable) {
            mCameraRenderer.setFURenderSwitch(enable);
        }
    };

    //美体
    private BodyBeautyControlView mBodyBeautyControlView;
    private BodyBeautyDataFactory mBodyBeautyDataFactory;




    //贴纸面具共用view
    private PropControlView mPropControlView;
    //面具
    private PropDataFactory mPropDataFactory_mark;

    //贴纸
    private PropDataFactory mPropDataFactory_sticker;

    private PropDataFactory.PropListener mPropListener = new PropDataFactory.PropListener() {
        @Override
        public void onItemSelected(PropBean bean) {
            if (bean.getDescId() > 0) {
                mMainHandler.post(() -> showDescription(bean.getDescId(), 1500));
            }
        }
    };



    Button beauty_btn,beauty_body_btn, mark_btn,sticker_btn;



    public static boolean needBindDataFactory = false;

    @Override
    public void onResume() {
        allEffectActivity = this;
        if (needBindDataFactory) {
            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            needBindDataFactory = false;
        }
        super.onResume();
    }




    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_face_beauty;
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();

        mFaceBeautyDataFactory.bindCurrentRenderer();

        mBodyBeautyDataFactory.bindCurrentRenderer();

        mPropDataFactory_mark.bindCurrentRenderer();

        mPropDataFactory_sticker.bindCurrentRenderer();
    }

    @Override
    public void initData() {
        super.initData();

        CheckPermission.verifyPermissionCameraAndAudioAndStorage(AllEffectActivity.this);

        mFaceBeautyDataFactory = new FaceBeautyDataFactory(mFaceBeautyListener);

        mBodyBeautyDataFactory = new BodyBeautyDataFactory();

        mPropDataFactory_mark = new PropDataFactory(mPropListener, FunctionEnum.AR_MASK, 0);

        mPropDataFactory_sticker = new PropDataFactory(mPropListener, FunctionEnum.STICKER, 0);
    }



    @Override
    public void initView() {
        super.initView();
        stub_bottom_menu = findViewById(R.id.stub_bottom_menu);

        mFaceBeautyControlView = findViewById(R.id.faceBeautyControlView);
        mFaceBeautyControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate,
                    getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x256), true);
        });

        mBodyBeautyControlView = findViewById(R.id.bodyBeautyControlView);


        mPropControlView = findViewById(R.id.propControlView);




        beauty_btn = findViewById(R.id.beauty_btn);
        beauty_btn.setOnClickListener(v -> {
            stub_bottom_menu.setVisibility(View.VISIBLE);
            mFaceBeautyControlView.setVisibility(View.VISIBLE);
            mBodyBeautyControlView.setVisibility(View.GONE);
            mPropControlView.setVisibility(View.GONE);

            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x166));
        });
        beauty_body_btn = findViewById(R.id.beauty_body_btn);
        beauty_body_btn.setOnClickListener(v -> {
            stub_bottom_menu.setVisibility(View.VISIBLE);
            mFaceBeautyControlView.setVisibility(View.GONE);
            mBodyBeautyControlView.setVisibility(View.VISIBLE);
            mPropControlView.setVisibility(View.GONE);

            mBodyBeautyControlView.bindDataFactory(mBodyBeautyDataFactory);
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x298), getResources().getDimensionPixelSize(R.dimen.x122));
        });
        mark_btn = findViewById(R.id.mark_btn);
        mark_btn.setOnClickListener(v -> {
            stub_bottom_menu.setVisibility(View.VISIBLE);
            mFaceBeautyControlView.setVisibility(View.GONE);
            mBodyBeautyControlView.setVisibility(View.GONE);
            mPropControlView.setVisibility(View.VISIBLE);

            mPropControlView.bindDataFactory(mPropDataFactory_mark);
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x212), getResources().getDimensionPixelSize(R.dimen.x166));
        });

        sticker_btn = findViewById(R.id.sticker_btn);
        sticker_btn.setOnClickListener(v -> {
            stub_bottom_menu.setVisibility(View.VISIBLE);
            mFaceBeautyControlView.setVisibility(View.GONE);
            mBodyBeautyControlView.setVisibility(View.GONE);
            mPropControlView.setVisibility(View.VISIBLE);

            mPropControlView.bindDataFactory(mPropDataFactory_sticker);
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x212), getResources().getDimensionPixelSize(R.dimen.x166));
        });



    }

    @Override
    public void bindListener() {
        super.bindListener();

        if (isLeaveVideoChat){
            sticker_btn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "bindListener: 再次跳转");
                    isLeaveVideoChat = false;
                    finish();
                    Intent intent = new Intent(AllEffectActivity.this, AllEffectActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            },500);
        }
    }


    @Override
    protected int getFunctionType() {
        return FunctionEnum.FACE_BEAUTY;
    }



    FrameLayout stub_bottom_menu;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stub_bottom_menu.setVisibility(View.GONE);
        return super.onTouchEvent(event);
    }
}