package xin.banghua.beiyuan.custom_ui.music_selector;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class MusicSelectorDialog {
    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public AppCompatActivity mContext;
    private BottomSheetDialog bottomSheetDialog;

    @BindView(R.id.iv_dialog_close)
    Button iv_dialog_close;


    public MusicSelectorDialog(AppCompatActivity mContext) {
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
        view = View.inflate(mContext, R.layout.music_selector_view, null);


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
                MusicSelectorDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentStateAdapter mFragmentPagerAdapter;

    @BindView(R.id.viewpager_menu)
    TabLayout viewpager_menu;
    @BindView(R.id.viewpager)
    ViewPager2 viewPager;

    Boolean ifInitialed = false;
    public void initShow(OkHttpResponseCallBack okHttpResponseCallBack){
        bottomSheetDialog.setOnDismissListener(dialog -> {
            //消失监听
            okHttpResponseCallBack.getResponseString("");
        });

        slideOffset = 0;
        bottomSheetDialog.show();

        if (ifInitialed)
            return;

        ifInitialed = true;
        //自定义部分
        menuViewPagerView_fragment.add(new OnlineMusicFragment());
        menuViewPagerView_fragment.add(new LocalMusicFragment());
        //menuViewPagerView_fragment.add(new RecordMusicFragment());

        //menuViewPagerView_menu.add("录音");
        viewpager_menu.addTab(viewpager_menu.newTab().setText("在线"));
        viewpager_menu.addTab(viewpager_menu.newTab().setText("本地"));
        viewpager_menu.setTabMode(TabLayout.MODE_SCROLLABLE);

        mFragmentPagerAdapter = new FragmentStateAdapter(mContext) {
            @Override
            public int getItemCount() {
                return menuViewPagerView_fragment.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return menuViewPagerView_fragment.get(position);
            }
        };
        viewpager_menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.setAdapter(mFragmentPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewpager_menu.setScrollPosition(position, 0, false);
            }
        });

        viewPager.setOffscreenPageLimit(2);

    }
}
