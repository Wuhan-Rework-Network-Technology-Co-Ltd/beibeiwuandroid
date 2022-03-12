package io.agora.chatroom.ktv;

import static io.agora.chatroom.ktv.KtvFrameLayout.ktvView;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.model.Channel;
import io.agora.chatroom.model.Constant;

public class KtvMusicListDialog {
    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    public KtvMusicListDialog(Context mContext) {
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
        view = View.inflate(mContext, R.layout.ktv_music_list_dialog, null);

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
                KtvMusicListDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


//        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }


    @BindView(R2.id.back_btn)
    Button back_btn;
    @BindView(R2.id.menu_scroll)
    HorizontalScrollView menu_scroll;
    @BindView(R2.id.viewpager_menu)
    TabLayout viewpager_menu;
    @BindView(R2.id.viewPager)
    ViewPager2 viewPager;
    @BindView(R2.id.search_film)
    SearchView search_film;
    @BindView(R2.id.clear_song_btn)
    Button clear_song_btn;

    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentStateAdapter mFragmentPagerAdapter;


    KtvMusicListFragment ktvMusicListFragment;
    KtvMusicListChoosedFragment ktvMusicListChoosedFragment;
    public void showDialog(){
        //自定义部分
        slideOffset = 0;
        bottomSheetDialog.show();
    }

    public void dismissDialog(){
        bottomSheetDialog.show();
    }

    private volatile List<MemberMusicModel> musics = new ArrayList<>();
    public void initView(AppCompatActivity context, Channel channel){

        back_btn.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });


        if (channel.getId().equals(Constant.sUserId+"")){
            clear_song_btn.setVisibility(View.VISIBLE);
        }else {
            clear_song_btn.setVisibility(View.INVISIBLE);
        }

        clear_song_btn.setOnClickListener(view1 -> {
            OkHttpInstance.deleteSongRoom(new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    ktvView.playNewSong();
                }
            });
        });

        ktvMusicListFragment = KtvMusicListFragment.newInstance("");
        ktvMusicListFragment.setCurrentChannel(channel);

        ktvMusicListChoosedFragment = KtvMusicListChoosedFragment.newInstance("");
        ktvMusicListChoosedFragment.setCurrentChannel(channel);

        menuViewPagerView_fragment.add(ktvMusicListFragment);
        menuViewPagerView_fragment.add(ktvMusicListChoosedFragment);


//        menuViewPagerView_menu.add("歌曲");
//        menuViewPagerView_menu.add("已点");


        viewpager_menu.addTab(viewpager_menu.newTab().setText("歌曲"));
        viewpager_menu.addTab(viewpager_menu.newTab().setText("已点"));

        viewpager_menu.setTabMode(TabLayout.MODE_SCROLLABLE);

        //配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentStateAdapter(context) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return menuViewPagerView_fragment.get(position);
            }

            @Override
            public int getItemCount() {
                return menuViewPagerView_fragment.size();
            }
        };

        int[] viewLocation = new int[2];

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

        search_film.onActionViewExpanded();
        search_film.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ktvMusicListFragment.searchName(newText);
                return false;
            }
        });
    }
}
