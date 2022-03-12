package io.agora.chatroom.ktv;

import static io.agora.chatroom.ktv.KtvFrameLayout.ktvView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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


public class OrderMusicFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public OrderMusicFrameLayout(@NonNull Context context) {
        super(context);
    }

    public OrderMusicFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OrderMusicFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public OrderMusicFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ktv_music_list_dialog, this, true);

        ButterKnife.bind(mView,this);
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
    public void initShow(){
        //自定义部分
    }


    private volatile List<MemberMusicModel> musics = new ArrayList<>();
    public void initView(AppCompatActivity context, Channel channel,OkHttpResponseCallBack okHttpResponseCallBack){
        if (viewpager_menu.getTabCount()>0){
            return;
        }
        back_btn.setOnClickListener(view -> {
            this.setVisibility(GONE);
            okHttpResponseCallBack.getResponseString("");
        });


        if (channel.getId().equals(Constant.sUserId+"")){
            clear_song_btn.setVisibility(View.VISIBLE);
        }else {
            clear_song_btn.setVisibility(View.GONE);
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

        //search_film.onActionViewExpanded();
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
