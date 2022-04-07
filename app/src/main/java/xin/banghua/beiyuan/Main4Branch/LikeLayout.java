package xin.banghua.beiyuan.Main4Branch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Random;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import xin.banghua.beiyuan.R;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 *2\u00020\u0001:\u0001*B\u0011\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0004B\u001b\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0002\u0010\u0007B#\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\u0018\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020\u001cH\u0002J\u0010\u0010#\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0002J\u0006\u0010$\u001a\u00020\u0012J\u0012\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010(H\u0016J\b\u0010)\u001a\u00020\u0012H\u0002R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R \u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0014\"\u0004\b\u0019\u0010\u0016¨\u0006+"},
        d2 = {"Lcom/yunlei/douyinlike/widget/LikeLayout;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "icon", "Landroid/graphics/drawable/Drawable;", "mClickCount", "mHandler", "Lcom/yunlei/douyinlike/widget/LikeLayout$Companion$LikeLayoutHandler;", "onLikeListener", "Lkotlin/Function0;", "", "getOnLikeListener", "()Lkotlin/jvm/functions/Function0;", "setOnLikeListener", "(Lkotlin/jvm/functions/Function0;)V", "onPauseListener", "getOnPauseListener", "setOnPauseListener", "addHeartView", "x", "", "y", "getHideAnimSet", "Landroid/animation/AnimatorSet;", "mView", "Landroid/widget/ImageView;", "getRandomRotate", "getShowAnimSet", "onPause", "onTouchEvent", "", "event", "Landroid/mView/MotionEvent;", "pauseClick", "Companion", "TouYin_DemoClient-master.app"}
)
public final class LikeLayout extends FrameLayout {
    private static final String TAG = "LikeLayout";
    @NotNull
    private Function0 onLikeListener;
    @NotNull
    private Function0 onPauseListener;
    private Drawable icon;
    private int mClickCount;
    private final Companion.LikeLayoutHandler mHandler;


    @NotNull
    public final Function0 getOnLikeListener() {
        return this.onLikeListener;
    }

    public final void setOnLikeListener(@NotNull Function0 var1) {
        Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
        this.onLikeListener = var1;
    }

    @NotNull
    public final Function0 getOnPauseListener() {
        return this.onPauseListener;
    }

    public final void setOnPauseListener(@NotNull Function0 var1) {
        Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
        this.onPauseListener = var1;
    }


    //为了不和viewpager的点击事件冲突，此组件需要放入viewpager中，根据抬起的位置是否移动判断是否调用暂停，否则viewpager翻页后也会触发暂停
    private int mStartX, mStartY;
    private int mScaledTouchSlop;
    public static boolean ifVideo = true;
    public boolean onTouchEvent(@Nullable MotionEvent event) {
        if (event != null) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouchEvent: 按下");
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    if (!ifVideo){
                        float x = event.getX();
                        float y = event.getY();
                        int var10001 = this.mClickCount++;
                        this.mHandler.removeCallbacksAndMessages((Object)null);
                        if (this.mClickCount >= 2) {
                            this.addHeartView(x, y);
                            this.onLikeListener.invoke();
                            this.mHandler.sendEmptyMessageDelayed(1, 500L);
                        } else {
                            this.mHandler.sendEmptyMessageDelayed(0, 500L);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent: 抬起");
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    if (Math.abs(endX - mStartX) < mScaledTouchSlop
                            && Math.abs(endY - mStartY) < mScaledTouchSlop) {
                        //上面是计算是否是viewpager的滚动，不是滚动，则判断是单击还是连击
                        float x = event.getX();
                        float y = event.getY();
                        int var10001 = this.mClickCount++;
                        this.mHandler.removeCallbacksAndMessages((Object)null);
                        if (this.mClickCount >= 2) {
                            this.addHeartView(x, y);
                            this.onLikeListener.invoke();
                            this.mHandler.sendEmptyMessageDelayed(1, 500L);
                        } else {
                            this.mHandler.sendEmptyMessageDelayed(0, 500L);
                        }
                    }
                    break;
            }
        }

        return true;
    }

    private final void pauseClick() {
        if (this.mClickCount == 1) {
            this.onPauseListener.invoke();
        }

        this.mClickCount = 0;
    }

    public final void onPause() {
        this.mClickCount = 0;
        this.mHandler.removeCallbacksAndMessages((Object)null);
    }

    private final void addHeartView(float x, float y) {
        LayoutParams lp = new LayoutParams(this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight());
        lp.leftMargin = (int)(x - (float)(this.icon.getIntrinsicWidth() / 2));
        lp.topMargin = (int)(y - (float)this.icon.getIntrinsicHeight() - 120);//比点击位置高120
        final ImageView img = new ImageView(this.getContext());
        img.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.postRotate(this.getRandomRotate());
        img.setImageMatrix(matrix);
        img.setImageDrawable(this.icon);
        img.setLayoutParams((android.view.ViewGroup.LayoutParams)lp);
        this.addView((View)img);
        AnimatorSet animSet = this.getShowAnimSet(img);
        final AnimatorSet hideSet = this.getHideAnimSet(img);
        animSet.start();
        animSet.addListener((Animator.AnimatorListener)(new AnimatorListenerAdapter() {
            public void onAnimationEnd(@Nullable Animator animation) {
                super.onAnimationEnd(animation);
                hideSet.start();
            }
        }));
        hideSet.addListener((Animator.AnimatorListener)(new AnimatorListenerAdapter() {
            public void onAnimationEnd(@Nullable Animator animation) {
                super.onAnimationEnd(animation);
                LikeLayout.this.removeView((View)img);
            }
        }));
    }

    private final AnimatorSet getShowAnimSet(ImageView view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.2F, 1.0F});
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.2F, 1.0F});
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(new Animator[]{(Animator)scaleX, (Animator)scaleY});
        animSet.setDuration(100L);
        return animSet;
    }

    private final AnimatorSet getHideAnimSet(ImageView view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0F, 0.1F});
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.0F, 2.0F});
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.0F, 2.0F});
        ObjectAnimator translation = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0F, -150.0F});
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(new Animator[]{(Animator)alpha, (Animator)scaleX, (Animator)scaleY, (Animator)translation});
        animSet.setDuration(800L);
        return animSet;
    }

    private final float getRandomRotate() {
        return (float)((new Random()).nextInt(20) - 10);
    }

    public LikeLayout(@Nullable Context context) {
        super(context);
        this.onLikeListener = (Function0) new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        this.onPauseListener = (Function0)(Function0) new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        Drawable var10001 = this.getResources().getDrawable(R.mipmap.ic_heart);
        Intrinsics.checkExpressionValueIsNotNull(var10001, "resources.getDrawable(R.mipmap.ic_heart)");
        this.icon = var10001;
        this.mHandler = new Companion.LikeLayoutHandler(this);
        this.setClipChildren(false);

        //触发移动事件的最小距离
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public LikeLayout(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.onLikeListener = (Function0)new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        this.onPauseListener = (Function0)new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        Drawable var10001 = this.getResources().getDrawable(R.mipmap.ic_heart);
        Intrinsics.checkExpressionValueIsNotNull(var10001, "resources.getDrawable(R.mipmap.ic_heart)");
        this.icon = var10001;
        this.mHandler = new Companion.LikeLayoutHandler(this);
        this.setClipChildren(false);

        //触发移动事件的最小距离
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public LikeLayout(@Nullable Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onLikeListener = (Function0)new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        this.onPauseListener = (Function0)new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        };
        Drawable var10001 = this.getResources().getDrawable(R.mipmap.ic_heart);
        Intrinsics.checkExpressionValueIsNotNull(var10001, "resources.getDrawable(R.mipmap.ic_heart)");
        this.icon = var10001;
        this.mHandler = new Companion.LikeLayoutHandler(this);
        this.setClipChildren(false);

        //触发移动事件的最小距离
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    @Metadata(
            mv = {1, 1, 18},
            bv = {1, 0, 3},
            k = 1,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0003B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0004"},
            d2 = {"Lcom/yunlei/douyinlike/widget/LikeLayout$Companion;", "", "()V", "LikeLayoutHandler", "TouYin_DemoClient-master.app"}
    )
    public static final class Companion {
        private Companion() {
        }

        @Metadata(
                mv = {1, 1, 18},
                bv = {1, 0, 3},
                k = 1,
                d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016R\u001c\u0010\u0005\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00030\u00030\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
                d2 = {"Lcom/yunlei/douyinlike/widget/LikeLayout$Companion$LikeLayoutHandler;", "Landroid/os/Handler;", "mView", "Lcom/yunlei/douyinlike/widget/LikeLayout;", "(Lcom/yunlei/douyinlike/widget/LikeLayout;)V", "mView", "Ljava/lang/ref/WeakReference;", "kotlin.jvm.PlatformType", "handleMessage", "", "msg", "Landroid/os/Message;", "TouYin_DemoClient-master.app"}
        )
        private static final class LikeLayoutHandler extends Handler {
            private final WeakReference mView;

            public void handleMessage(@Nullable Message msg) {
                super.handleMessage(msg);
                Integer var2 = msg != null ? msg.what : null;
                LikeLayout var10000;
                if (var2 != null) {
                    if (var2 == 0) {
                        var10000 = (LikeLayout)this.mView.get();
                        if (var10000 != null) {
                            var10000.pauseClick();
                        }

                        return;
                    }
                }

                if (var2 != null) {
                    if (var2 == 1) {
                        var10000 = (LikeLayout)this.mView.get();
                        if (var10000 != null) {
                            var10000.onPause();
                        }
                    }
                }

            }

            public LikeLayoutHandler(@NotNull LikeLayout view) {
                super();
                Intrinsics.checkParameterIsNotNull(view, "mView");
                this.mView = new WeakReference(view);
            }
        }
    }
}
